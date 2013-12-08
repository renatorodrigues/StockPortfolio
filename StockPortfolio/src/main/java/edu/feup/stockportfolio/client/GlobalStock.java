package edu.test.networktest;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LinePoint;

import java.util.ArrayList;

public class GlobalStock {
    private Line line_graph_;
    private double range_max_;
    private double range_min_;

    GlobalStock(){
        line_graph_ = new Line();
    }

    public void refresh(ArrayList<StockData> companies){
        for(StockData company : companies){
            company.refresh_history();
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
        range_min_ -= delta;
        range_max_ += delta;
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
}
