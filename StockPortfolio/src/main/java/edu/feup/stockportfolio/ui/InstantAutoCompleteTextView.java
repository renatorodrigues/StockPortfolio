package edu.feup.stockportfolio.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class InstantAutoCompleteTextView extends AutoCompleteTextView {

    public InstantAutoCompleteTextView(Context context) {
        super(context);
    }

    public InstantAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InstantAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previously_focused_rect) {
        super.onFocusChanged(focused, direction, previously_focused_rect);
        if (focused) {
            performFiltering(getText(), 0);
        }
    }
}
