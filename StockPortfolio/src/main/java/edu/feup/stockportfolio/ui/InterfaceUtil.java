package edu.feup.stockportfolio.ui;

import android.widget.EditText;

public class InterfaceUtil {
    public static boolean isEmpty(EditText edit_text) {
        return (edit_text.getText().toString().trim().length() == 0);
    }
}
