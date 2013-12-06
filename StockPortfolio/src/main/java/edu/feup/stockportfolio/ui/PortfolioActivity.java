package edu.feup.stockportfolio.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

import edu.feup.stockportfolio.R;

public class PortfolioActivity extends Activity implements QuotesFragment.OnQuoteSelectedListener {
    private static final String TAG = "PortfolioActivity";

    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);
        setContentView(R.layout.quotes_and_shares);

        if (findViewById(R.id.fragment_container) != null && saved_instance_state == null) {
            if (getFragmentManager().findFragmentById(R.id.quotes_information_fragment) != null) {
                getFragmentManager().beginTransaction().add(R.id.fragment_container, getFragmentManager().findFragmentById(R.id.quotes_information_fragment)).commit();
            } else {
                QuotesFragment quotes_fragment = new QuotesFragment();
                quotes_fragment.setArguments(getIntent().getExtras());
                getFragmentManager().beginTransaction().add(R.id.fragment_container, quotes_fragment).commit();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onQuoteSelected(int position) {
        QuoteInformationFragment quote_fragment = (QuoteInformationFragment) getFragmentManager().findFragmentById(R.id.quotes_information_fragment);

        if (quote_fragment != null) {
            quote_fragment.updateQuoteView(position);
        } else {
            Log.d(TAG, "Creating new Quote Information fragment");
            QuoteInformationFragment new_quote_fragment = new QuoteInformationFragment();
            Bundle args = new Bundle();
            args.putInt(QuoteInformationFragment.ARG_POSITION, position);
            new_quote_fragment.setArguments(args);
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new_quote_fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}
