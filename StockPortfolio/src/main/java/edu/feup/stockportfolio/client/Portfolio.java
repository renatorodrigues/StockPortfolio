package edu.feup.stockportfolio.client;


import java.util.ArrayList;
import java.util.Arrays;


public class Portfolio {
    private static final Portfolio INSTANCE = new Portfolio();

    public static Portfolio getInstance() {
        return INSTANCE;
    }

    private GlobalStock global_stock_;
    private ArrayList<StockData> stocks_;


    private Portfolio() {
        global_stock_ = new GlobalStock();

        stocks_ = new ArrayList<StockData>();

        /*stocks_.add(new StockData("GOOG", 2));
        stocks_.add(new StockData("IBM", 3));
        stocks_.add(new StockData("MSFT", 1));
        stocks_.add(new StockData("TWTR", 1));*/
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
    }

    public void addStock(String company, int shares) {
        stocks_.add(new StockData(company, shares));
    }

    public void removeStock(int index) {
        stocks_.remove(index);
    }

    public void removeStock(StockData stock) {
        stocks_.remove(stock);
    }

    public void loadStocks() {

    }

    public void saveStocks() {

    }
}
