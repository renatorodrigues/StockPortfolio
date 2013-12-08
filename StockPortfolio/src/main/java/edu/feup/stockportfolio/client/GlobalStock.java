package edu.feup.stockportfolio.client;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LinePoint;

import java.util.ArrayList;

public class GlobalStock {
    private Line line_graph_;
    private double range_max_;
    private double range_min_;
    private double total_own_quotes_;

    GlobalStock(){
        line_graph_ = new Line();
    }

    public boolean hasHistory() {
        return (line_graph_.getSize() != 0);
    }

    public void refresh(ArrayList<StockData> companies){
        total_own_quotes_ = 0.0;

        for(StockData company : companies){
            company.refresh_history();
            total_own_quotes_ += company.get_own_quotes_value();
        }

        range_max_=0;
        range_min_=Double.MAX_VALUE;

        Line l = new Line();
        for(int i=0; i<31; ++i){

            double y=0;
            for(StockData company : companies){
                LinePoint a = company.get_line().getPointByX((float) i);
                if(a!=null)
                    y+=a.getY();
            }
            if(y!=0){
                if(y>range_max_) range_max_=y;
                if(y<range_min_) range_min_=y;
                LinePoint p = new LinePoint(i,y);
                l.addPoint(p);
            }
        }

        double delta = range_max_-range_min_;
        double padding = delta*0.1;
        range_min_ -= padding;
        range_max_ += padding;
        if(range_min_<0)range_min_=0;

        line_graph_ = l;
    }

    public float get_range_max() {
        return (float)range_max_;
    }

    public float get_range_min() {
        return (float)range_min_;
    }

    public Line get_line() { return line_graph_; }

    public double getOwnedStock() {
        return total_own_quotes_;
    }
}
