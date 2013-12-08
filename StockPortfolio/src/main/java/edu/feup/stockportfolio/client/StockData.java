package edu.feup.stockportfolio.client;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LinePoint;

import edu.feup.stockportfolio.network.StockNetworkUtilities;

public class StockData{

    private Line line_graph_;
    private String company_;
    private String formal_name_;
    private String quote_value_;
    private String change_;
    private String change_percentage_;
    private String open_;
    private String high_;
    private String low_;
    private String volume_;
    private String avg_volume_;
    private String market_cap_;
    private int quantity_;

    private double range_max_;
    private double range_min_;

    StockData(String company, int quantity){
        set_company(company);
        set_quantity(quantity);
        line_graph_ = new Line();
    }

    public void refresh_today() {
        StockNetworkUtilities.refresh_today(this);
    }

    public void refresh_history() {
        StockNetworkUtilities.refresh_history(this);
    }

    public void populate_today(String[] data) {
        //TODO store as string or convert to double
        formal_name_ = data[1].replace("\"","");
        quote_value_ = data[2];
        change_ = data[3];
        change_percentage_ = data[4].replace("\"","");
        open_ = data[5];
        high_ = data[6];
        low_ = data[7];
        volume_ = data[8];
        avg_volume_ = data[9];
        market_cap_ = data[10].split("\r")[0];
    }

    public void populate_history(String[] data) {
        //first line is the header, data beings on the second!
        Line l = new Line();

        range_max_=0;
        range_min_=Double.MAX_VALUE;

        int c=0;
        for(int i=data.length-1; i>0; --i, ++c){

            //Date,Open,High,Low,Close,Volume,Adj Close
            String[] split = data[i].split(",");

            double x = c;
            double y = Double.valueOf(split[4]);

            if(y>range_max_) range_max_=y;
            if(y<range_min_) range_min_=y;

            LinePoint p = new LinePoint(x,y);
            l.addPoint(p);
        }

        //add today to graph
        LinePoint p = new LinePoint(c,get_actual_quote());
        l.addPoint(p);
        if(get_actual_quote()>range_max_) range_max_=get_actual_quote();
        if(get_actual_quote()<range_min_) range_min_=get_actual_quote();

        double delta = range_max_-range_min_;
        double padding = delta*0.1;
        range_min_ -= delta;
        range_max_ += delta;
        if(range_min_<0)range_min_=0;

        line_graph_ = l;
    }

    public double get_actual_quote() {
        return Double.valueOf(quote_value_);
    }

    public double get_own_quotes_value(){
        return get_actual_quote()*get_quantity();
    }

    public Line get_line() { return line_graph_; }

    public void set_company(String company) {
        company_ = company;
    }

    public void set_quantity(int quantity) { quantity_ = quantity; }

    public String get_company() {
        return company_;
    }

    public String get_formal_name() {
        return formal_name_;
    }

    public String get_quote_value() {
        return quote_value_;
    }

    public String get_change() {
        return change_;
    }

    public String get_change_percentage() {
        return change_percentage_;
    }

    public String get_open() {
        return open_;
    }

    public String get_high() {
        return high_;
    }

    public String get_low() {
        return low_;
    }

    public String get_volume() {
        return volume_;
    }

    public String get_avg_volume() {
        return avg_volume_;
    }

    public String get_market_cap() {
        return market_cap_;
    }

    public int get_quantity() {
        return quantity_;
    }

    @Override
    public String toString() {
        return get_company() + "\n"
                + get_formal_name() + "\n"
                + get_quote_value() + "\n"
                + get_change() + "\n"
                + get_change_percentage() + "\n"
                + get_open() + "\n"
                + get_high() + "\n"
                + get_low() + "\n"
                + get_volume() + "\n"
                + get_avg_volume() + "\n"
                + get_market_cap();
    }

    public float get_range_max() {
        return (float)range_max_;
    }

    public float get_range_min() {
        return (float)range_min_;
    }
}