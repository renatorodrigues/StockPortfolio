package edu.feup.stockportfolio.client;


import java.util.ArrayList;


public class Portfolio {
    private static final Portfolio INSTANCE = new Portfolio();

    public static Portfolio getInstance() {
        return INSTANCE;
    }

    private GlobalStock global_stock_;
    private ArrayList<StockData> shares_;


    private Portfolio() {
        global_stock_ = new GlobalStock();

        shares_ = new ArrayList<StockData>();

        shares_.add(new StockData("GOOG", 2));
        shares_.add(new StockData("IBM", 3));
        shares_.add(new StockData("MSFT", 1));
        shares_.add(new StockData("TWTR", 1));
    }

    public ArrayList<StockData> getShares() {
        return shares_;
    }

    public GlobalStock getGlobalStock() {
        return global_stock_;
    }

    public void addShares(String company, int shares) {
        shares_.add(new StockData(company, shares));
    }

    public void removeShares(int index) {
        shares_.remove(index);
    }

    public void removeShares(StockData shares) {
        shares_.remove(shares);
    }

    public int getTotal() {
        return -1;
    }

    public void loadShares() {

    }

    public void saveShares() {

    }
}
