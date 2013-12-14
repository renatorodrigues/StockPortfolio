package edu.feup.stockportfolio.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.echo.holographlibrary.PieGraph;

import edu.feup.stockportfolio.R;
import edu.feup.stockportfolio.client.GlobalStock;
import edu.feup.stockportfolio.client.Portfolio;

public class PortfolioHistoryFragment extends Fragment implements LineGraph.OnPointClickedListener {
    private static final String TAG = "PortfolioHistoryFragment";

    private GlobalStock global_stock_;
    private LineGraph line_graph_;
    private TextView price_;

    public static PortfolioHistoryFragment newInstance() {
        PortfolioHistoryFragment portfolio_fragment = new PortfolioHistoryFragment();
        return portfolio_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved_instance_state) {
        View v = inflater.inflate(R.layout.portfolio_history, container, false);

        global_stock_ = Portfolio.getInstance().getGlobalStock();

        line_graph_ = (LineGraph) v.findViewById(R.id.line_graph);
        price_ = (TextView) v.findViewById(R.id.price);

        global_stock_.get_line().setShowingPoints(true);
        //global_stock_.get_line().setColor(Color.parseColor("#FFBB33"));
        line_graph_.removeAllLines();
        line_graph_.addLine(global_stock_.get_line());
        line_graph_.setRangeY(global_stock_.get_range_min(), global_stock_.get_range_max());
        line_graph_.setLineToFill(0);

        line_graph_.setOnPointClickedListener(this);

        return v;
    }

    @Override
    public void onStop() {
        global_stock_.get_line().setShowingPoints(false);
        super.onStop();
    }

    @Override
    public void onClick(int line_index, int point_index) {
        Line line = line_graph_.getLine(line_index);
        LinePoint point = line.getPoint(point_index);
        float day = point.getX();
        float value = point.getY();

        price_.setText("" + value);
    }
}
