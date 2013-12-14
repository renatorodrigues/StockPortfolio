package edu.feup.stockportfolio.client;


import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import edu.feup.stockportfolio.ui.StockPortfolio;

public class Portfolio {
    private static final String TAG = "Portfolio";

    private static final Portfolio INSTANCE = new Portfolio();

    public static Portfolio getInstance() {
        return INSTANCE;
    }

    private PortfolioDbHelper portfolio_db_helper_;

    private GlobalStock global_stock_;
    private ArrayList<StockData> stocks_;


    private Portfolio() {
        portfolio_db_helper_ = new PortfolioDbHelper(StockPortfolio.context);

        global_stock_ = new GlobalStock();
        stocks_ = new ArrayList<StockData>();
    }

    public String[] getCompanies() {
        String[] companies = new String[stocks_.size()];
        int i = 0;
        for (StockData stock : stocks_) {
            companies[i++] = stock.get_company();
        }

        return companies;
    }

    public boolean contains(String company) {
        return Arrays.asList(getCompanies()).contains(company);
    }

    public ArrayList<StockData> getStocks() {
        return stocks_;
    }

    public GlobalStock getGlobalStock() {
        return global_stock_;
    }

    public void addStock(StockData stock) {
        stocks_.add(stock);
        portfolio_db_helper_.addStock(stock);
    }

    public void addStock(String company, int shares) {
        StockData stock = new StockData(company, shares);
        stocks_.add(stock);
        portfolio_db_helper_.addStock(stock);
    }

    public void updateStock(int index, int shares) {
        StockData stock = stocks_.get(index);
        stock.set_quantity(shares);
        portfolio_db_helper_.updateStock(stock);
    }

    public void removeStock(int index) {
        String company = stocks_.get(index).get_company();
        stocks_.remove(index);
        portfolio_db_helper_.deleteStock(company);
    }

    public void removeStock(StockData stock) {
        String company = stock.get_company();
        stocks_.remove(stock);
        portfolio_db_helper_.deleteStock(company);
    }

    public void loadStocks() {
        stocks_  = portfolio_db_helper_.getAllStocks();

        for (StockData stock : stocks_) {
            Log.d(TAG, stock.get_company() + " " + stock.get_quantity());
        }
    }

    public void saveStocks() {

    }
}
