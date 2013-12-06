package edu.feup.stockportfolio.ui;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.feup.stockportfolio.Portfolio;
import edu.feup.stockportfolio.R;

public class QuoteInformationFragment extends Fragment {
    final static String ARG_POSITION = "position";

    private int current_position_ = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved_instance_state) {
        if (saved_instance_state != null) {
            current_position_ = saved_instance_state.getInt(ARG_POSITION);
        }

        return inflater.inflate(R.layout.quote_view, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            updateQuoteView(args.getInt(ARG_POSITION));
        } else if (current_position_ != -1) {
            updateQuoteView(current_position_);
        }
    }

    public void updateQuoteView(int position) {
        current_position_ = position;

        Activity activity = getActivity();

        TextView company = (TextView) activity.findViewById(R.id.company_text);
        TextView shares = (TextView) activity.findViewById(R.id.shares_text);

        company.setText(Portfolio.Companies[position]);
        shares.setText(Portfolio.Shares[position]);
    }

    @Override
    public void onSaveInstanceState(Bundle out_state) {
        super.onSaveInstanceState(out_state);

        out_state.putInt(ARG_POSITION, current_position_);
    }
}
