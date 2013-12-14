package edu.feup.stockportfolio.ui;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.echo.holographlibrary.LineGraph;

import edu.feup.stockportfolio.R;
import edu.feup.stockportfolio.client.Portfolio;
import edu.feup.stockportfolio.client.StockData;
import edu.feup.stockportfolio.network.WebServiceCallRunnable;

public class StockViewActivity extends Activity {
    private static final String TAG = "StockViewActivity";

    public static final int RESULT_STOCK_REMOVED = 1;
    public static final int RESULT_STOCK_UPDATED = 2;

    public static final String EXTRA_STOCK_INDEX = "stock_index";

    public static final String ARG_INDEX = "index";

    private int index_;
    private StockData stock_;

    private TextView formal_name_;
    private TextView company_;
    private ProgressBar progress_bar_;
    private LineGraph history_graph_;
    private TextView quote_;
    private TextView change_;

    private ViewFlipper shares_flipper_;

    private TextView shares_num_;
    private TextView shares_value_;

    private Button decrement_shares_;
    private Button decrement_ten_shares_;
    private EditText shares_edit_;
    private Button increment_shares_;
    private Button increment_ten_shares_;

    private TextView open_;
    private TextView high_;
    private TextView low_;
    private TextView volume_;
    private TextView avg_vol_;
    private TextView mkt_cap_;

    private int previous_shares_;

    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);
        setContentView(R.layout.stock_view);

        Bundle extras = getIntent().getExtras();
        index_ = extras.getInt(ARG_INDEX, -1);
        if (index_ == -1) {
            finish();
            return;
        }

        ActionBar action_bar = getActionBar();
        action_bar.setDisplayHomeAsUpEnabled(true);

        stock_ = Portfolio.getInstance().getStocks().get(index_);

        previous_shares_ = stock_.get_quantity();

        formal_name_ = (TextView) findViewById(R.id.formal_name);
        company_ = (TextView) findViewById(R.id.company);
        progress_bar_ = (ProgressBar) findViewById(R.id.progress_bar);
        history_graph_ = (LineGraph) findViewById(R.id.history_graph);
        quote_ = (TextView) findViewById(R.id.quote);
        change_ = (TextView) findViewById(R.id.change);

        shares_flipper_ = (ViewFlipper) findViewById(R.id.shares_flipper);

        shares_num_ = (TextView) findViewById(R.id.shares_num);
        shares_value_ = (TextView) findViewById(R.id.shares_value);

        decrement_shares_ = (Button) findViewById(R.id.decrement_shares);
        decrement_ten_shares_ = (Button) findViewById(R.id.decrement_ten_shares);
        shares_edit_ = (EditText) findViewById(R.id.shares_edit);
        increment_shares_ = (Button) findViewById(R.id.increment_shares);
        increment_ten_shares_ = (Button) findViewById(R.id.increment_ten_shares);

        open_ = (TextView) findViewById(R.id.open);
        high_ = (TextView) findViewById(R.id.high);
        low_ = (TextView) findViewById(R.id.low);
        volume_ = (TextView) findViewById(R.id.volume);
        avg_vol_ = (TextView) findViewById(R.id.avg_vol);
        mkt_cap_ = (TextView) findViewById(R.id.mkt_cap);

        populate();

        loadHistory();

    }

    public void populate() {
        formal_name_.setText(stock_.get_formal_name());
        company_.setText(stock_.get_company());
        quote_.setText(stock_.get_quote_value());
        change_.setText(stock_.get_change() + " (" + stock_.get_change_percentage() + ")");

        int color;
        if (stock_.get_change().contains("+")) {
            color = android.R.color.holo_green_dark;
        } else {
            color = android.R.color.holo_red_dark;
        }
        change_.setTextColor(StockPortfolio.context.getResources().getColor(color));

        shares_num_.setText("" + stock_.get_quantity());
        shares_value_.setText("" + stock_.get_own_quotes_value());

        shares_edit_.setText(Integer.toString(stock_.get_quantity()));

        open_.setText(stock_.get_open());
        high_.setText(stock_.get_high());
        low_.setText(stock_.get_low());
        volume_.setText(stock_.get_volume());
        avg_vol_.setText(stock_.get_avg_volume());
        mkt_cap_.setText(stock_.get_market_cap());
    }

    public void loadHistory() {
        if (stock_.hasHistory()) {
            populateGraph();
        }

        Thread history_thread = new Thread(new WebServiceCallRunnable(new Handler()) {
            @Override
            public void run() {
                stock_.refresh_history();

                handler_.post(new Runnable() {
                    @Override
                    public void run() {
                        populateGraph();
                    }
                });
            }
        });
        history_thread.start();
    }

    public void populateGraph() {
        history_graph_.addLine(stock_.get_line());
        stock_.get_line().setShowingPoints(false);
        stock_.get_line().setColor(Color.parseColor("#FFBB33"));
        history_graph_.setRangeY(stock_.get_range_min(), stock_.get_range_max());
        history_graph_.setLineToFill(0);

        progress_bar_.setVisibility(View.GONE);
        history_graph_.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_stock, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_remove:
                remove();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void editShares(View v) {
        shares_flipper_.setInAnimation(StockViewActivity.this, android.R.anim.slide_in_left);
        shares_flipper_.setOutAnimation(StockViewActivity.this, android.R.anim.slide_out_right);
        shares_flipper_.showNext();
    }

    public void saveShares(View v) {
        if (shares_edit_.length() != 0) {
            int shares = Integer.parseInt(shares_edit_.getText().toString());
            Portfolio.getInstance().updateStock(index_, shares);

            shares_num_.setText(Integer.toString(shares));
            shares_value_.setText(Double.toString(stock_.get_own_quotes_value()));
        }


        shares_flipper_.setInAnimation(StockViewActivity.this, android.R.anim.slide_in_left);
        shares_flipper_.setOutAnimation(StockViewActivity.this, android.R.anim.slide_out_right);
        shares_flipper_.showPrevious();
    }

    public void updateShares(View v) {
        int shares;
        if (shares_edit_.length() != 0) {
            shares = Integer.parseInt(shares_edit_.getText().toString());
        } else {
            shares = stock_.get_quantity();
        }

        switch (v.getId()) {
            case R.id.decrement_shares:
                --shares;
                break;
            case R.id.decrement_ten_shares:
                shares -= 10;
                break;
            case R.id.increment_shares:
                ++shares;
                break;
            case R.id.increment_ten_shares:
                shares += 10;
                break;
        }

        if (shares < 0) {
            shares = 0;
        }

        shares_edit_.setText(Integer.toString(shares));
    }

    private void remove() {
        Intent return_intent = new Intent();
        return_intent.putExtra(EXTRA_STOCK_INDEX, index_);
        setResult(RESULT_STOCK_REMOVED, return_intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        int shares = Integer.parseInt(shares_num_.getText().toString());
        if (previous_shares_ != shares) {
            setResult(RESULT_STOCK_UPDATED);
        }

        super.onBackPressed();
    }
}
