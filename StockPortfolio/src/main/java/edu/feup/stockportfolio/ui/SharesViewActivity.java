package edu.feup.stockportfolio.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shares_view);

        Bundle extras = getIntent().getExtras();
        index_ = extras.getInt(ARG_INDEX, -1);
        if (index_ == -1) {
            finish();
            return;
        }

        Toast.makeText(this, "" + index_, Toast.LENGTH_SHORT).show();

        loadHistory();

    }


    public void loadHistory() {
        Thread history_thread = new Thread(new WebServiceCallRunnable(new Handler()) {
            @Override
            public void run() {
                Portfolio.getIstance().getShares().get(0).refresh_history();

                handler_.post(new Runnable() {
                    @Override
                    public void run() {
                        LineGraph li = (LineGraph) findViewById(R.id.graph);
                        li.addLine(Portfolio.getIstance().getShares().get(0).get_line());
                        Portfolio.getIstance().getShares().get(0).get_line().setShowingPoints(false);
                        Portfolio.getIstance().getShares().get(0).get_line().setColor(Color.parseColor("#FFBB33"));
                        li.setRangeY(1000, 1200);
                        li.setLineToFill(0);
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
