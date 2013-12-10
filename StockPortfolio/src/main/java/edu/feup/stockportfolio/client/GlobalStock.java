package edu.feup.stockportfolio.client;

import android.graphics.Color;
import android.util.Log;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.util.ArrayList;
import java.util.List;

public class GlobalStock {
    private static final String TAG = "GlobalStock";

    private Line line_graph_;
    private ArrayList<PieSlice> slice_quantity_;
    private ArrayList<PieSlice> slice_prices_;
    private double range_max_;
    private double range_min_;
    private double total_own_quotes_ = 0.0;
    private boolean has_history_ = false;
    private boolean refreshing_ = false;

    GlobalStock(){
        line_graph_ = new Line();
        slice_quantity_ = new ArrayList<PieSlice>();
        slice_prices_ = new ArrayList<PieSlice>();
    }

    public boolean hasHistory() {
        return has_history_;
    }

    public boolean isRefreshing() {
        return refreshing_;
    }

    public void refresh(ArrayList<StockData> companies) {
        refreshing_ = true;
        Log.d(TAG, "refresh");
        total_own_quotes_ = 0.0;

        slice_quantity_ = new ArrayList<PieSlice>();
        slice_prices_ = new ArrayList<PieSlice>();

        for (StockData company : companies){
            if(!company.hasHistory())
                company.refresh_history();

            total_own_quotes_ += company.get_own_quotes_value();

            PieSlice pq = new PieSlice();
            pq.setTitle(company.get_formal_name());
            pq.setValue(company.get_quantity());
            slice_quantity_.add(pq);

            PieSlice pp = new PieSlice();
            pp.setTitle(company.get_formal_name());
            pp.setValue((float)company.get_own_quotes_value());
            slice_prices_.add(pp);
        }
        colorize_slices(slice_prices_);
        colorize_slices(slice_quantity_);

        range_max_ = 0;
        range_min_ = Double.MAX_VALUE;

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
        if (range_min_ < 0) {
            range_min_ = 0;
        }

        line_graph_ = l;

        has_history_ = true;
        refreshing_ = false;
    }

    public float get_range_max() {
        return (float) range_max_;
    }

    public float get_range_min() {
        return (float) range_min_;
    }

    public Line get_line() {
        return line_graph_;
    }

    public ArrayList<PieSlice> get_slices_quantities(){
        return slice_quantity_;
    }

    public ArrayList<PieSlice> get_slices_prices(){
        return slice_prices_;
    }

    public double getOwnedStock() {
        return total_own_quotes_;
    }

    private void colorize_slices(ArrayList<PieSlice> slices){

        int n = slices.size();
        if(n==0) return;

        //int baseColor = Color.rgb(138, 86, 226);
        int baseColor = Color.parseColor("#FFBB33");

        float[] baseColorHSV = new float[3];
        float baseHue;
        Color.colorToHSV(baseColor, baseColorHSV);
        baseHue = baseColorHSV[0];

        slices.get(0).setColor(baseColor);

        double step = (240.0 / (double)n);

        for (int i = 1; i < n; ++i)
        {
            float[] nextColor = baseColorHSV;
            nextColor[0] = (float)((baseHue + step * ((double)i)) % 240.0);
            slices.get(i).setColor(Color.HSVToColor(nextColor));
        }

    }
}
