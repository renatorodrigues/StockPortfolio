package edu.feup.stockportfolio.ui;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import edu.feup.stockportfolio.R;

public class ViewSharesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_shares_activity);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_shares, menu);
        return true;
    }
    
}
