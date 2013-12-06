package edu.feup.stockportfolio.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;

public class QuoteDetailsActivity extends Activity {
    @Override
    protected void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish();
            return;
        }

        if (saved_instance_state == null) {
            QuoteDetailsFragment quote_details_fragment = new QuoteDetailsFragment();
            quote_details_fragment.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, quote_details_fragment).commit();
        }
    }
}
