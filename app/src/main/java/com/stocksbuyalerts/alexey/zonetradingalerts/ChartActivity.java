package com.stocksbuyalerts.alexey.zonetradingalerts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
//import android.widget.ShareActionProvider;

//import android.support.v4.view.MenuItemCompat;
//import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;

public class ChartActivity extends AppCompatActivity {
    private ShareActionProvider mShareActionProvider;

    public static final String CHART_URL = "chart_url";
    public static final String SYMBOL = "symbol";

    private String url;
    private String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart2);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        // Show the Up button in the action bar.
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        url ="";
        symbol="";

        Intent intent = getIntent();
        if (intent.hasExtra(CHART_URL) && intent.hasExtra(SYMBOL)) {
            url = getIntent().getStringExtra(CHART_URL);
            symbol = getIntent().getStringExtra(SYMBOL);

            if (actionBar != null) {
                actionBar.setTitle(symbol);
            }

            toolbar.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            int widthTb = toolbar.getMeasuredWidth();
            int heightTb = toolbar.getMeasuredHeight();

            WebView myWebView = (WebView) findViewById(R.id.wv_chart);

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x / 2;
            int height = (int) ((size.y - heightTb) / 2.1);


            Log.i("mmm x", String.valueOf(width));
            Log.i("mmm y", String.valueOf(height));

            Log.i("mmm TB", String.valueOf(heightTb));
            Log.i("mmm TB", String.valueOf(widthTb));

            myWebView.loadUrl(url+"?w=" + String.valueOf(width) + "&h=" + String.valueOf(height));
            //  myWebView.loadUrl("http://www.stocksbuyalerts.com/wp-content/themes/themify-corporate/js/charting_library-master/2018_07_10_14_42_MNK.html");
            WebSettings webSettings = myWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            myWebView.setInitialScale(getScale(width));
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private int getScale(int w){

        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int width = size. x;
        int height = size. y;

//        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//        int width = display.getWidth();

        Double val = new Double(width)/new Double(w);
        val = val * 100d;
        return val.intValue();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        MenuItem item = menu.findItem(R.id.mybutton);
//        mShareActionProvider = (ShareActionProvider) item.getActionProvider();
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //create the sharing intent
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType(".html -> text/html");
        String shareBody = url;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, symbol+ " Day Trading Alert from StocksBuyAlerts.com");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        //then set the sharingIntent
        mShareActionProvider.setShareIntent(sharingIntent);


        return true;
    }

    // handle button activities
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.mybutton) {
//            // do something here
//        }
//        return super.onOptionsItemSelected(item);
//    }

    // Call to update the share intent
//    private void setShareIntent(Intent shareIntent) {
//        if (mShareActionProvider != null) {
//            mShareActionProvider.setShareIntent(shareIntent);
//        }
//    }
}
