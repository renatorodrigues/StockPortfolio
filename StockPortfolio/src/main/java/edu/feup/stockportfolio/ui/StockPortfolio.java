package edu.feup.stockportfolio.ui;

import android.app.Application;
import android.content.Context;

public class StockPortfolio extends Application {
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }

    public static class Constants {

    }
}
