package com.stocksbuyalerts.alexey.zonetradingalerts.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stocksbuyalerts.alexey.zonetradingalerts.MainActivity;
import com.stocksbuyalerts.alexey.zonetradingalerts.R;

/**
 * Implementation of App Widget functionality.
 */
public class PriceTrackingWidget extends AppWidgetProvider {

    static void updateAppWidget(final Context context, final AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.price_tracking_widget);
//        views.setTextViewText(R.id.appwidget_text, widgetText);




        String symbol = Utils.loadSymbol(context);
        Log.i("xxx", symbol);

        if (symbol != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.price_tracking_widget);

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

            Query query = reference.child("Prices").orderByKey().equalTo(symbol);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.i("xxxds", ds.toString());

                            String ask = ds.child("ask").getValue().toString();
                            String open = ds.child("open").getValue().toString();
                            String time = ds.child("time").getValue().toString();

                            Log.i("xxx ask", ask);

                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, PriceTrackingWidget.class));

                            PriceTrackingWidget.updateAppWidgets(context, appWidgetManager, appWidgetIds, ask, open, time);

                          //  views.setTextViewText(R.id.tv_price, ask);


                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            views.setTextViewText(R.id.tv_symbol, symbol);
            views.setTextViewText(R.id.tv_price, "XXX");

            views.setOnClickPendingIntent(R.id.tv_symbol, pendingIntent);

            Intent intent = new Intent(context, MyWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

//            views.setRemoteAdapter(R.id.recipe_widget_listview, intent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.recipe_widget_listview);
        }







        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views);
    }



    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String tAsk, String tOpen, String tTime) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);

        final String symbol = Utils.loadSymbol(context);

        if (symbol != null) {
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.price_tracking_widget);

            float ask = Float.valueOf(tAsk);
            float open= Float.valueOf(tOpen);
            float change = 100*(ask-open)/open;

            views.setTextViewText(R.id.tv_symbol, symbol);
            views.setTextViewText(R.id.tv_price, "$"+ String.format("%.2f", ask));
            if (change>0){
                views.setTextViewText(R.id.tv_percent_change, "(+"+ String.format("%.2f", change)+"%)");
                views.setTextColor(R.id.tv_percent_change,  ContextCompat.getColor(context, R.color.colorGreen));
            }
            else{
                views.setTextViewText(R.id.tv_percent_change, "("+String.format("%.2f", change)+"%)");
                views.setTextColor(R.id.tv_percent_change,  ContextCompat.getColor(context, R.color.colorError));
            }

//            views.setTextViewText(R.id.tv_price, "$"+ String.format("%.2g%n", ask));
//            views.setTextViewText(R.id.tv_percent_change, String.format("%.2g%n", change)+"%");
            views.setTextViewText(R.id.tv_update_time, "as of " + tTime);



            views.setOnClickPendingIntent(R.id.tv_symbol, pendingIntent);

            Intent intent = new Intent(context, MyWidgetService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Log.i("xxx", "updateAppWidgets: ");
        }
    }

    public static void updateAppWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, String tAsk, String tOpen, String tTime) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, tAsk, tOpen, tTime);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

