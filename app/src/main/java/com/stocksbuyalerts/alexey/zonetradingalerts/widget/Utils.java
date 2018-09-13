package com.stocksbuyalerts.alexey.zonetradingalerts.widget;

import android.content.Context;
import android.content.SharedPreferences;

import com.stocksbuyalerts.alexey.zonetradingalerts.R;

public final class Utils {
    public static final String PREFS_NAME = "prefs";

    private Utils(){}

    public static void saveSymbol(Context context, String symbol) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        prefs.putString(context.getString(R.string.widget_symbol_key), symbol);
        prefs.apply();
    }

    public static String loadSymbol(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String symbolBase64 = prefs.getString(context.getString(R.string.widget_symbol_key), "");

        return symbolBase64;
    }


}