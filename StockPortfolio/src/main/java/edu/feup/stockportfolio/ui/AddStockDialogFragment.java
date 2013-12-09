package edu.feup.stockportfolio.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import edu.feup.stockportfolio.R;
import edu.feup.stockportfolio.client.Portfolio;
import edu.feup.stockportfolio.network.WebServiceCallRunnable;

public class AddStockDialogFragment extends DialogFragment {
    public interface AddStockDialogFragmentListener {
        public void onStockAdded(String tick, int shares);
    }

    private static final int VALIDATE_TICK = 1;
    private static final int VALIDATE_SHARES = 1 << 1;

    private static final int STYLE = DialogFragment.STYLE_NO_TITLE;
    private static final int THEME = android.R.style.Theme_Holo_Light_Dialog;

    private AddStockDialogFragmentListener callback_;

    private InstantAutoCompleteTextView tick_;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            callback_ = (AddStockDialogFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement AddStockDialogFragmentListener");
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        InputMethodManager input_manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        input_manager.hideSoftInputFromWindow(tick_.getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saved_instance_state) {
        View v = inflater.inflate(R.layout.add_stock, container, false);

        tick_ = (InstantAutoCompleteTextView) v.findViewById(R.id.tick);
        shares_ = (EditText) v.findViewById(R.id.shares);

        String[] popular_stocks = getResources().getStringArray(R.array.popular_stocks);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, popular_stocks) {
            @Override
            public View getView(int position, View convert_view, ViewGroup parent) {
                View v = super.getView(position, convert_view, parent);

                v.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            InputMethodManager input_manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            input_manager.hideSoftInputFromWindow(tick_.getWindowToken(), 0);
                        }

                        return false;
                    }
                });

                return v;
            }
        };
        tick_.setAdapter(adapter);
        tick_.setDropDownHeight(LinearLayout.LayoutParams.MATCH_PARENT);

        tick_.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                InputMethodManager input_manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                input_manager.toggleSoftInputFromWindow(tick_.getWindowToken(), InputMethodManager.SHOW_IMPLICIT, 0);
                shares_.requestFocus();
                //validate();
            }
        });

        tick_.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validate(VALIDATE_TICK);
            }
        });

        shares_.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validate(VALIDATE_SHARES);
            }
        });
        shares_.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int action_id, KeyEvent event) {
                boolean handled = false;

                if (action_id == EditorInfo.IME_ACTION_DONE) {
                    newStock(v);
                    handled = true;
                }

                return handled;
            }
        });

        confirm_button_ = (Button) v.findViewById(R.id.confirm_button);
        confirm_button_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newStock(v);
            }
        });

        return v;
    }

    public boolean validate(int flags) {
        boolean errors = false;

        if ((flags & VALIDATE_TICK) == VALIDATE_TICK) {
            if (InterfaceUtil.isEmpty(tick_)) {
                tick_.setError("Missing value");
                errors = true;
            } else if (Portfolio.getInstance().contains(tick_.getText().toString())) {
                tick_.setError("Stock already in portfolio");
                errors = true;
            } else {
                tick_.setError(null);
            }
        }

        if ((flags & VALIDATE_SHARES) == VALIDATE_SHARES) {
            if (InterfaceUtil.isEmpty(shares_)) {
                shares_.setError("Missing value");
                errors = true;
            } else {
                shares_.setError(null);
            }
        }

        return !errors;
    }

    public void newStock(View v) {
        if (!validate(VALIDATE_TICK | VALIDATE_SHARES)) {
            return;
        }

        String tick = tick_.getText().toString();
        int shares = Integer.parseInt(shares_.getText().toString());

        callback_.onStockAdded(tick, shares);
    }
}
