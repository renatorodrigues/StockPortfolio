package edu.feup.stockportfolio.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import edu.feup.stockportfolio.R;

public class PortfolioActivity extends Activity implements QuotesListFragment.OnQuoteSelectedListener {
    private static final String TAG = "PortfolioActivity";

    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);
        setContentView(R.layout.quotes);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onQuoteSelected(int position) {
        QuoteDetailsFragment quote_fragment = (QuoteDetailsFragment) getFragmentManager().findFragmentById(R.id.quotes_details);

        if (quote_fragment != null) {
            quote_fragment.updateQuoteView(position);
        } else {
            Log.d(TAG, "Creating new Quote Information fragment");
            QuoteDetailsFragment new_quote_fragment = new QuoteDetailsFragment();
            Bundle args = new Bundle();
            args.putInt(QuoteDetailsFragment.ARG_POSITION, position);
            new_quote_fragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new_quote_fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
