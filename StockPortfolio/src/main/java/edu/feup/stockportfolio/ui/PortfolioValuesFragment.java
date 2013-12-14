package edu.feup.stockportfolio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

import java.util.ArrayList;

import edu.feup.stockportfolio.R;
import edu.feup.stockportfolio.client.Portfolio;

public class PortfolioValuesFragment extends Fragment {
    private static final String TAG = "PortfolioValuesFragment";

    private ExpandableHeightGridView labels_grid_;
    private LabelAdapter label_adapter_;
    private PieGraph pie_graph_;

    public static PortfolioValuesFragment newInstance() {
        PortfolioValuesFragment portfolio_fragment = new PortfolioValuesFragment();
        return portfolio_fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved_instance_state) {
        View v = inflater.inflate(R.layout.portfolio_values, container, false);

        ArrayList<PieSlice> slices = Portfolio.getInstance().getGlobalStock().get_slices_prices();

        label_adapter_ = new LabelAdapter(getActivity(), slices);

        labels_grid_ = (ExpandableHeightGridView) v.findViewById(R.id.labels_grid);
        labels_grid_.setExpanded(true);
        labels_grid_.setAdapter(label_adapter_);

        pie_graph_ = (PieGraph) v.findViewById(R.id.prices_pie_graph);
        pie_graph_.setSlices(slices);

        return v;
    }


}
