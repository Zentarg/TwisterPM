package com.ag.twisterpm.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class UtilMethods {

    public static void SoftHideKeyboard(Activity activity, View view) {
        InputMethodManager imp = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imp.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
