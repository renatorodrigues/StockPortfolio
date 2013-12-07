package edu.feup.stockportfolio.ui;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import edu.feup.stockportfolio.Portfolio;
import edu.feup.stockportfolio.R;
import edu.feup.stockportfolio.SwipeDismissListViewTouchListener;

public class PortfolioActivity extends ListActivity implements AdapterView.OnItemClickListener, SwipeDismissListViewTouchListener.DismissCallbacks {
    private static final String TAG = "PortfolioActivity";

    QuoteListAdapter list_adapter_;

    // temp
    ArrayList<String> quotes_ = new ArrayList<String>(Arrays.asList(Portfolio.Companies));

    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);
        setContentView(R.layout.portfolio);

        list_adapter_ = new QuoteListAdapter(this, quotes_);
        setListAdapter(list_adapter_);

        SwipeDismissListViewTouchListener touch_listener = new SwipeDismissListViewTouchListener(getListView(), this);

        getListView().setOnTouchListener(touch_listener);
        getListView().setOnScrollListener(touch_listener.makeScrollListener());
        getListView().setOnItemClickListener(this);
    }

    public void loadQuotes() {

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
            quotes_.remove(position);
        }
        list_adapter_.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(PortfolioActivity.this, ViewSharesActivity.class));
    }

    private static class QuoteListAdapter extends BaseAdapter {
        private static final int TYPE_PORTFOLIO = 0;
        private static final int TYPE_QUOTE = 1;

        private ArrayList<String> quotes_ = new ArrayList<String>();
        private LayoutInflater inflater_;

        public QuoteListAdapter(Context context, ArrayList<String> quotes) {
            quotes_ = quotes;
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
            return quotes_.size();
        }

        @Override
        public Object getItem(int position) {
            return quotes_.get(position);
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

            String quote = quotes_.get(position);
            holder.quote.setText(quote);

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
