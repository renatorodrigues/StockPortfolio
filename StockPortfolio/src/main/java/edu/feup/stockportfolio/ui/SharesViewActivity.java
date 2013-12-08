package edu.feup.stockportfolio.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import edu.feup.stockportfolio.R;
import edu.feup.stockportfolio.client.Portfolio;
import edu.feup.stockportfolio.client.StockData;
import edu.feup.stockportfolio.network.StockNetworkUtilities;
import edu.feup.stockportfolio.network.WebServiceCallRunnable;

public class SharesViewActivity extends Activity {
    private static final String TAG = "SharesViewActivity";

    public static final String ARG_INDEX = "index";

    private int index_;
    private StockData shares_;

    private TextView formal_name_;
    private TextView company_;
    private ProgressBar progress_bar_;
    private LineGraph history_graph_;
    private TextView quote_;
    private TextView change_;

    private TextView open_;
    private TextView high_;
    private TextView low_;
    private TextView volume_;
    private TextView avg_vol_;
    private TextView mkt_cap_;

    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);
        setContentView(R.layout.shares_view);

        Bundle extras = getIntent().getExtras();
        index_ = extras.getInt(ARG_INDEX, -1);
        if (index_ == -1) {
            finish();
            return;
        }

        shares_ = Portfolio.getIstance().getShares().get(index_);


        formal_name_ = (TextView) findViewById(R.id.formal_name);
        company_ = (TextView) findViewById(R.id.company);
        progress_bar_ = (ProgressBar) findViewById(R.id.progress_bar);
        history_graph_ = (LineGraph) findViewById(R.id.history_graph);
        quote_ = (TextView) findViewById(R.id.quote);
        change_ = (TextView) findViewById(R.id.change);

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
        formal_name_.setText(shares_.get_formal_name());
        company_.setText(shares_.get_company());
        quote_.setText(shares_.get_quote_value());
        change_.setText(shares_.get_change() + " (" + shares_.get_change_percentage() + ")");

        int color;
        if (shares_.get_change().contains("+")) {
            color = android.R.color.holo_green_dark;
        } else {
            color = android.R.color.holo_red_dark;
        }
        change_.setTextColor(StockPortfolio.context.getResources().getColor(color));

        open_.setText(String.format("%.2f", Float.valueOf(shares_.get_open())));
        high_.setText(shares_.get_high());
        low_.setText(shares_.get_low());
        volume_.setText(shares_.get_volume());
        avg_vol_.setText(shares_.get_avg_volume());
        mkt_cap_.setText(shares_.get_market_cap());
    }

    public void loadHistory() {
        Thread history_thread = new Thread(new WebServiceCallRunnable(new Handler()) {
            @Override
            public void run() {
                shares_.refresh_history();

                handler_.post(new Runnable() {
                    @Override
                    public void run() {
                        history_graph_.addLine(shares_.get_line());
                        shares_.get_line().setShowingPoints(false);
                        shares_.get_line().setColor(Color.parseColor("#FFBB33"));
                        history_graph_.setRangeY(shares_.get_range_min(), shares_.get_range_max());
                        history_graph_.setLineToFill(0);

                        progress_bar_.setVisibility(View.GONE);
                        history_graph_.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        history_thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_shares, menu);
        return true;
    }
    
}
