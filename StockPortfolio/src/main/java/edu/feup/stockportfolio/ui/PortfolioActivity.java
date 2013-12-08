package edu.feup.stockportfolio.ui;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import java.util.ArrayList;

import edu.feup.stockportfolio.client.Portfolio;
import edu.feup.stockportfolio.R;
import edu.feup.stockportfolio.client.StockData;
import edu.feup.stockportfolio.network.StockNetworkUtilities;
import edu.feup.stockportfolio.network.WebServiceCallRunnable;

public class PortfolioActivity extends ListActivity implements AdapterView.OnItemClickListener, SwipeDismissListViewTouchListener.DismissCallbacks {
    private static final String TAG = "PortfolioActivity";

    private ArrayList<StockData> shares_;
    private QuoteListAdapter list_adapter_;

    private ProgressBar progress_bar_;
    private View empty_view_;

    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);
        setContentView(R.layout.portfolio);

        progress_bar_ = (ProgressBar) findViewById(R.id.progress_bar);
        empty_view_ = findViewById(android.R.id.empty);

        shares_ = Portfolio.getIstance().getShares();

        SwipeDismissListViewTouchListener touch_listener = new SwipeDismissListViewTouchListener(getListView(), this);

        getListView().setOnTouchListener(touch_listener);
        getListView().setOnScrollListener(touch_listener.makeScrollListener());
        getListView().setOnItemClickListener(this);

        refreshShares();
    }

    public void refreshShares() {
        if (shares_.isEmpty()) {
            Log.v(TAG, "no shares");
            return;
        }

        empty_view_.setVisibility(View.GONE);
        progress_bar_.setVisibility(View.VISIBLE);
        getListView().setEmptyView(progress_bar_);

        Thread refresh_shares_thread = new Thread(new WebServiceCallRunnable(new Handler()) {
            @Override
            public void run() {
                StockNetworkUtilities.refresh_all_today(shares_);

                handler_.post(new Runnable() {
                    @Override
                    public void run() {
                        empty_view_.setVisibility(View.VISIBLE);
                        progress_bar_.setVisibility(View.GONE);
                        getListView().setEmptyView(empty_view_);
                        list_adapter_ = new QuoteListAdapter(PortfolioActivity.this, shares_);
                        setListAdapter(list_adapter_);
                    }
                });
            }
        });
        refresh_shares_thread.start();
    }

    @Override
    public boolean canDismiss(int position) {
        if (position == 0) {
            return false;
        }

        return true;
    }

    @Override
    public void onDismiss(ListView list_view, int[] reverse_sorted_positions) {
        for (int position : reverse_sorted_positions) {
            Portfolio.getIstance().getShares().remove(position - 1);
        }
        list_adapter_.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {

        } else {
            Toast.makeText(this, "" + (position - 1), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PortfolioActivity.this, SharesViewActivity.class));
        }

    }

    private static class QuoteListAdapter extends BaseAdapter {
        private static final int TYPE_PORTFOLIO = 0;
        private static final int TYPE_QUOTE = 1;

        private ArrayList<StockData> shares_ = new ArrayList<StockData>();
        private LayoutInflater inflater_;

        public QuoteListAdapter(Context context, ArrayList<StockData> shares) {
            shares_ = shares;
            inflater_ = LayoutInflater.from(context);
        }

        @Override
        public int getItemViewType(int position) {
            return (position == 0 ? TYPE_PORTFOLIO : TYPE_QUOTE);
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getCount() {
            return shares_.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return shares_.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convert_view, ViewGroup parent) {
            ViewHolder holder = null;
            int type = getItemViewType(position);
            if (convert_view == null) {
                holder = new ViewHolder();
                switch (type) {
                    case TYPE_PORTFOLIO:
                        convert_view = inflater_.inflate(R.layout.portfolio_list_item, null);
                        holder.quote = (TextView) convert_view.findViewById(R.id.title);
                        break;
                    case TYPE_QUOTE:
                        convert_view = inflater_.inflate(R.layout.quote_list_item, null);
                        holder.quote = (TextView) convert_view.findViewById(R.id.quote);
                        break;
                }
                convert_view.setTag(holder);
            } else {
                holder = (ViewHolder) convert_view.getTag();
            }

            if (type == TYPE_QUOTE) {
                StockData stock_data = shares_.get(position - 1);
                holder.quote.setText(stock_data.get_company());

            } else {
                Line l = new Line();
                LinePoint p = new LinePoint(0, 5);
                l.addPoint(p);
                p = new LinePoint(8, 8);
                l.addPoint(p);
                p = new LinePoint(10, 4);
                l.addPoint(p);
                l.setColor(Color.parseColor("#FFBB33"));

                LineGraph li = (LineGraph) convert_view.findViewById(R.id.graph);
                li.addLine(l);
                li.setRangeY(0, 10);
                li.setLineToFill(0);
            }


            return convert_view;
        }
    }

    public static class ViewHolder {
        public TextView quote;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.portfolio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_shares:
                startActivity(new Intent(PortfolioActivity.this, AddSharesActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
