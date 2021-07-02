package com.reddigitalentertainment.sathijivanko;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.FragmentActivity;

public class HideKeyboard {
    public static void hide(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideFragment(FragmentActivity fa, View v){
        final InputMethodManager imm = (InputMethodManager) fa.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public static void showSoftKeyboard(View view, FragmentActivity fa) {
        InputMethodManager inputMethodManager = (InputMethodManager)fa.getSystemService(Activity.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }
}
