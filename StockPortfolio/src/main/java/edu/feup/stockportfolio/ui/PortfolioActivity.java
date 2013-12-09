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
import android.widget.Toast;

import com.echo.holographlibrary.LineGraph;

import java.util.ArrayList;

import edu.feup.stockportfolio.client.GlobalStock;
import edu.feup.stockportfolio.client.Portfolio;
import edu.feup.stockportfolio.R;
import edu.feup.stockportfolio.client.StockData;
import edu.feup.stockportfolio.network.NetworkUtilities;
import edu.feup.stockportfolio.network.StockNetworkUtilities;
import edu.feup.stockportfolio.network.WebServiceCallRunnable;

public class PortfolioActivity extends ListActivity
        implements AdapterView.OnItemClickListener, SwipeDismissListViewTouchListener.DismissCallbacks, AddStockDialogFragment.AddStockDialogFragmentListener {
    private static final String TAG = "PortfolioActivity";

    private Portfolio portfolio_;

    private GlobalStock global_stock_;
    private ArrayList<StockData> stocks_;
    private QuoteListAdapter list_adapter_;

    private ListView list_view_;
    private ProgressBar progress_bar_;
    private View empty_view_;
    private ViewStub no_connection_stub_;
    private View no_connection_;

    private AddStockDialogFragment add_stock_fragment_ = null;

    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);
        setContentView(R.layout.portfolio);

        portfolio_ = Portfolio.getInstance();
        global_stock_ = portfolio_.getGlobalStock();
        stocks_ = portfolio_.getStocks();

        list_view_ = getListView();
        progress_bar_ = (ProgressBar) findViewById(R.id.progress_bar);
        empty_view_ = findViewById(android.R.id.empty);

        SwipeDismissListViewTouchListener touch_listener = new SwipeDismissListViewTouchListener(getListView(), this);

        list_view_.setOnTouchListener(touch_listener);
        list_view_.setOnScrollListener(touch_listener.makeScrollListener());
        list_view_.setOnItemClickListener(this);

        refreshStocksToday();
    }

    public void noConnection(View v) {
        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
    }

    public void refreshStocksHistory() {
        Thread refresh_global = new Thread(new WebServiceCallRunnable(new Handler()) {
            @Override
            public void run() {
                Log.d(TAG, "Refreshing stocks history");
                global_stock_.refresh(stocks_);
                handler_.post(new Runnable() {
                    @Override
                    public void run() {
                        list_adapter_.notifyDataSetChanged();
                    }
                });
            }
        });
        refresh_global.start();
    }

    public void refreshStocksToday() {
        if (!NetworkUtilities.isNetworkAvailable()) {
            list_view_.getEmptyView().setVisibility(View.GONE);
            no_connection_stub_ = (ViewStub) findViewById(R.id.no_connection_stub);
            no_connection_ = no_connection_stub_.inflate();
            list_view_.setEmptyView(no_connection_);
            NetworkUtilities.showNoConnectionDialog(PortfolioActivity.this);
            return;
        }

        if (stocks_.isEmpty()) {
            Log.v(TAG, "no shares");
            return;
        }

        list_view_.getEmptyView().setVisibility(View.GONE);

        //progress_bar_.setVisibility(View.VISIBLE);
        list_view_.setEmptyView(progress_bar_);

        Thread refresh_shares_thread = new Thread(new WebServiceCallRunnable(new Handler()) {
            @Override
            public void run() {
                StockNetworkUtilities.refresh_all_today(stocks_);

                handler_.post(new Runnable() {
                    @Override
                    public void run() {
                        empty_view_.setVisibility(View.VISIBLE);
                        progress_bar_.setVisibility(View.GONE);
                        list_view_.setEmptyView(empty_view_);
                        list_adapter_ = new QuoteListAdapter(PortfolioActivity.this, stocks_);
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
            portfolio_.removeStock(position - 1);
        }

        if (stocks_.isEmpty()) {
            list_view.setAdapter(null);
            list_adapter_ = null;
        } else {
            refreshStocksHistory();
        }
    }

    public void addStock(View v) {
        add_stock_fragment_ = AddStockDialogFragment.newInstance();
        add_stock_fragment_.show(getFragmentManager(), "add_stock");
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

    @Override
    public void onStockAdded(final String tick, final int shares) {
        Thread new_stock_thread = new Thread(new WebServiceCallRunnable(new Handler()) {
            @Override
            public void run() {
                final StockData stock = StockNetworkUtilities.new_stock(tick, shares);
                if (stock != null) {
                    portfolio_.addStock(stock);
                }

                handler_.post(new Runnable() {
                    @Override
                    public void run() {
                        if (stock != null) {
                            if (list_adapter_ == null) {
                                list_view_.setEmptyView(empty_view_);
                                list_adapter_ = new QuoteListAdapter(PortfolioActivity.this, stocks_);
                                setListAdapter(list_adapter_);
                            } else {
                                list_adapter_.notifyDataSetChanged();
                            }

                            if (add_stock_fragment_ != null) {
                                add_stock_fragment_.dismiss();
                                add_stock_fragment_ = null;
                            }

                            refreshStocksHistory();
                        } else {
                            Toast.makeText(PortfolioActivity.this, "Company not found", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        new_stock_thread.start();
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
                final LineGraph global_graph = (LineGraph) convert_view.findViewById(R.id.graph);
                final View portfolio_value = convert_view.findViewById(R.id.portfolio_value);
                final TextView total_quotes = (TextView) convert_view.findViewById(R.id.total_quotes);
                if (!global_stock.isRefreshing() && global_stock.hasHistory()) {
                    progress_bar.setVisibility(View.GONE);

                    global_stock.get_line().setShowingPoints(false);
                    global_stock.get_line().setColor(Color.parseColor("#FFBB33"));
                    global_graph.removeAllLines();
                    global_graph.addLine(global_stock.get_line());
                    global_graph.setRangeY(global_stock.get_range_min(), global_stock.get_range_max());
                    global_graph.setLineToFill(0);

                    global_graph.setVisibility(View.VISIBLE);
                    portfolio_value.setVisibility(View.VISIBLE);
                    total_quotes.setText(String.format("%.2f",global_stock.getOwnedStock()));
                } else {
                    progress_bar.setVisibility(View.VISIBLE);
                    global_graph.setVisibility(View.GONE);
                    portfolio_value.setVisibility(View.INVISIBLE);
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
                addStock(null);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
