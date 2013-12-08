package edu.feup.stockportfolio.ui;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;

import java.util.ArrayList;

import edu.feup.stockportfolio.client.GlobalStock;
import edu.feup.stockportfolio.client.Portfolio;
import edu.feup.stockportfolio.R;
import edu.feup.stockportfolio.client.StockData;
import edu.feup.stockportfolio.network.NetworkUtilities;
import edu.feup.stockportfolio.network.StockNetworkUtilities;
import edu.feup.stockportfolio.network.WebServiceCallRunnable;

public class PortfolioActivity extends ListActivity implements AdapterView.OnItemClickListener, SwipeDismissListViewTouchListener.DismissCallbacks {
    private static final String TAG = "PortfolioActivity";

    private GlobalStock global_stock_;
    private ArrayList<StockData> shares_;
    private QuoteListAdapter list_adapter_;

    private ProgressBar progress_bar_;
    private View empty_view_;
    private ViewStub no_connection_stub_;
    private View no_connection_;

    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);
        setContentView(R.layout.portfolio);

        progress_bar_ = (ProgressBar) findViewById(R.id.progress_bar);
        empty_view_ = findViewById(android.R.id.empty);

        global_stock_ = Portfolio.getInstance().getGlobalStock();
        shares_ = Portfolio.getInstance().getShares();

        SwipeDismissListViewTouchListener touch_listener = new SwipeDismissListViewTouchListener(getListView(), this);

        getListView().setOnTouchListener(touch_listener);
        getListView().setOnScrollListener(touch_listener.makeScrollListener());
        getListView().setOnItemClickListener(this);

        refreshShares();
    }

    public void noConnection(View v) {
        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }

    public void refreshShares() {
        if (shares_.isEmpty()) {
            Log.v(TAG, "no shares");
            return;
        }

        getListView().getEmptyView().setVisibility(View.GONE);

        if (!NetworkUtilities.isNetworkAvailable()) {
            no_connection_stub_ = (ViewStub) findViewById(R.id.no_connection_stub);
            no_connection_ = no_connection_stub_.inflate();
            getListView().setEmptyView(no_connection_);
            NetworkUtilities.showNoConnectionDialog(PortfolioActivity.this);
            return;
        }

        //empty_view_.setVisibility(View.GONE);
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
        return (position != 0);
    }

    @Override
    public void onDismiss(ListView list_view, int[] reverse_sorted_positions) {
        for (int position : reverse_sorted_positions) {
            Portfolio.getInstance().getShares().remove(position - 1);
        }
        list_adapter_.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {

        } else {
            Intent intent = new Intent(PortfolioActivity.this, SharesViewActivity.class);
            intent.putExtra(SharesViewActivity.ARG_INDEX, position - 1);
            startActivity(intent);
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
                        break;
                    case TYPE_QUOTE:
                        convert_view = inflater_.inflate(R.layout.quote_list_item, null);
                        holder.formal_name = (TextView) convert_view.findViewById(R.id.formal_name);
                        holder.company = (TextView) convert_view.findViewById(R.id.company);
                        holder.quote = (TextView) convert_view.findViewById(R.id.quote);
                        holder.change = (TextView) convert_view.findViewById(R.id.change);
                        break;
                }
                convert_view.setTag(holder);
            } else {
                holder = (ViewHolder) convert_view.getTag();
            }

            if (type == TYPE_QUOTE) {
                StockData stock_data = shares_.get(position - 1);

                holder.formal_name.setText(stock_data.get_formal_name());
                holder.company.setText(stock_data.get_company());
                holder.quote.setText(stock_data.get_quote_value());
                holder.change.setText(stock_data.get_change() + " (" + stock_data.get_change_percentage() + ")");

                int color;
                if (stock_data.get_change().contains("+")) {
                    color = android.R.color.holo_green_dark;
                } else {
                    color = android.R.color.holo_red_dark;
                }
                holder.change.setTextColor(StockPortfolio.context.getResources().getColor(color));
            } else {
                final GlobalStock global_stock = Portfolio.getInstance().getGlobalStock();
                final ProgressBar progress_bar = (ProgressBar) convert_view.findViewById(R.id.progress_bar);
                final LineGraph global_graph = (LineGraph) convert_view.findViewById(R.id.graph);;
                if (global_stock.hasHistory()) {
                    progress_bar.setVisibility(View.GONE);

                    global_stock.get_line().setShowingPoints(false);
                    global_stock.get_line().setColor(Color.parseColor("#FFBB33"));
                    global_graph.addLine(global_stock.get_line());
                    global_graph.setRangeY(global_stock.get_range_min(), global_stock.get_range_max());
                    global_graph.setLineToFill(0);

                    global_graph.setVisibility(View.VISIBLE);

                    TextView total_quotes = (TextView) convert_view.findViewById(R.id.total_quotes);
                    total_quotes.setText("" + global_stock.getOwnedStock());
                } else {
                    progress_bar.setVisibility(View.VISIBLE);
                    global_graph.setVisibility(View.GONE);
                    Thread refresh_global = new Thread(new WebServiceCallRunnable(new Handler()) {
                        @Override
                        public void run() {
                            Log.d(TAG, "getting global chart");
                            global_stock.refresh(shares_);
                            handler_.post(new Runnable() {
                                @Override
                                public void run() {
                                    notifyDataSetChanged();
                                }
                            });
                        }
                    });
                    refresh_global.start();
                }
            }

            return convert_view;
        }
    }

    public static class ViewHolder {
        public TextView formal_name;
        public TextView company;
        public TextView quote;
        public TextView change;
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
                AddStockDialogFragment.newInstance().show(getFragmentManager(), "add_stock");
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
