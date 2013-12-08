package edu.feup.stockportfolio.client;


import java.util.ArrayList;


public class Portfolio {
    private static final Portfolio INSTANCE = new Portfolio();

    public static Portfolio getIstance() {
        return INSTANCE;
    }

    private ArrayList<StockData> shares_;


    private Portfolio() {
        shares_ = new ArrayList<StockData>();

        shares_.add(new StockData("GOOG", 2));
        shares_.add(new StockData("IBM", 3));
        shares_.add(new StockData("MSFT", 1));
    }

    public ArrayList<StockData> getShares() {
        return shares_;
    }
    public int getTotal() {
        return -1;
    }

    public void loadShares() {

    }

    public void saveShares() {

    }
}
