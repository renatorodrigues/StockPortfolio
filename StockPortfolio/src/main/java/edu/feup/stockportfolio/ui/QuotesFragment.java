package edu.feup.stockportfolio.ui;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import edu.feup.stockportfolio.Portfolio;
import edu.feup.stockportfolio.R;

public class QuotesFragment extends ListFragment {
    public interface OnQuoteSelectedListener {
        public void onQuoteSelected(int position);
    }

    OnQuoteSelectedListener callback_;

    @Override
    public void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);

        setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_activated_1, Portfolio.Companies));
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getFragmentManager().findFragmentById(R.id.quotes_information_fragment) != null) {
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback_ = (OnQuoteSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnQuoteSelectedListener");
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        callback_.onQuoteSelected(position);

        getListView().setItemChecked(position, true);
    }
}
