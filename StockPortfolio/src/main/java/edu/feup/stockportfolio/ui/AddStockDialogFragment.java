package edu.feup.stockportfolio.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import edu.feup.stockportfolio.R;

public class AddStockDialogFragment extends DialogFragment {
    public interface AddStockDialogFragmentListener {
        public void onConfirmation();
    }

    private static final int STYLE = DialogFragment.STYLE_NO_TITLE;
    private static final int THEME = android.R.style.Theme_Holo_Light_Dialog;

    private Activity activity_;

    private EditText tick_;
    private EditText shares_;

    private Button confirm_button_;

    public static AddStockDialogFragment newInstance() {
        AddStockDialogFragment stock_fragment = new AddStockDialogFragment();

        return stock_fragment;
    }

    @Override
    public void onCreate(Bundle saved_instance_state) {
        super.onCreate(saved_instance_state);

        setStyle(STYLE, THEME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved_instance_state) {
        View v = inflater.inflate(R.layout.add_shares, container, false);

        tick_ = (EditText) v.findViewById(R.id.tick);
        shares_ = (EditText) v.findViewById(R.id.shares);

        confirm_button_ = (Button) v.findViewById(R.id.confirm_button);

        return v;
    }
}
