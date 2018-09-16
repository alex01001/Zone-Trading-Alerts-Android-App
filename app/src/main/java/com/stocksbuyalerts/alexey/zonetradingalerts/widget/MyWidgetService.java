package com.stocksbuyalerts.alexey.zonetradingalerts.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;

public class MyWidgetService  {
    public static void updateWidget(Context context, String symbol) {
        Utils.saveSymbol(context, symbol);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, PriceTrackingWidget.class));
        PriceTrackingWidget.updateAppWidgets(context, appWidgetManager, appWidgetIds);
    }
}