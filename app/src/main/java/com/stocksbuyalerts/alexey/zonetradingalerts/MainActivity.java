package com.stocksbuyalerts.alexey.zonetradingalerts;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.transition.Explode;
import android.support.transition.Transition;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import butterknife.BindView;
import butterknife.ButterKnife;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class MainActivity extends AppCompatActivity implements AlertAdapter.AlertItemClickListener, NewsAdapter.NewsItemClickListener{

    private static final int RC_SIGN_IN = 1;
    private static final String CHART_URL = "chart_url";
    private static final String SYMBOL = "symbol";
    private static final String TAG = "SBA_msg";
    private static final String LIST_STATE_KEY = "list_pos";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;

    private AlertAdapter adapter;
    private List<Alert> alertList;

    private static NewsAdapter newsAdapter;
    private static List<News> newsList;
    private LinearLayoutManager layoutManager;
    public Parcelable mListState;

    @BindView(R.id.tv_error_message_diaplay) TextView errorMessageTextView;
    @BindView(R.id.rv_alertsList) RecyclerView mRecyclerView;
    @BindView(R.id.rv_news_list) RecyclerView mRecyclerViewNews;
    @BindView(R.id.tv_link) TextView tvLink;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertList = new ArrayList<Alert>();
        ButterKnife.bind(this);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher_round);
        }

//subscribing to notifications
        FirebaseMessaging.getInstance().subscribeToTopic("alerts")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "msg_subscribed";
                        if (!task.isSuccessful()) {
                            msg = "msg_subscribe_failed";
                            //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

// setting up adapter
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        if(width>1600){
            adapter = new AlertAdapter(getBaseContext(), this, true);
        }
        else {
            adapter = new AlertAdapter(getBaseContext(), this, false);
        }


        adapter.setAlertData(alertList);
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        newsAdapter = new NewsAdapter(getBaseContext(), this, true);
        newsAdapter.setNewsData(newsList);
        mRecyclerViewNews.setAdapter(newsAdapter);
        layoutManager = new LinearLayoutManager(getBaseContext());
        mRecyclerViewNews.setLayoutManager(layoutManager);


// connecting to database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Alerts");


        if(isOnline()) {
            makeNewsQuery();
            errorMessageTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            tvLink.setVisibility(View.VISIBLE);

            // managing authentication
            mFirebaseAuth = FirebaseAuth.getInstance();
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        FirebaseDatabase database = mFirebaseDatabase;
                        DatabaseReference myRef = mMessagesDatabaseReference;

                        // Read from the database
                        myRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                alertList = new ArrayList<Alert>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Alert alert = ds.getValue(Alert.class);
                                    alertList.add(alert);
                                }
                                Collections.reverse(alertList);
                                adapter.setAlertData(alertList);

                                if (savedInstanceState != null) {
                                    mListState = savedInstanceState.getParcelable(LIST_STATE_KEY);
                                    ((LinearLayoutManager) mRecyclerView.getLayoutManager()).onRestoreInstanceState(mListState);
                                }
                                LayoutAnimationController controller;
                                controller = AnimationUtils.loadLayoutAnimation(getBaseContext(), R.anim.layout_fall_down);
                                mRecyclerView.setLayoutAnimation(controller);
                                mRecyclerView.getAdapter().notifyDataSetChanged();
                                mRecyclerView.scheduleLayoutAnimation();
                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.w(TAG, "Failed to read value.", error.toException());
                            }
                        });
                    } else {
                        startActivityForResult(
                                AuthUI.getInstance()
                                        .createSignInIntentBuilder()
                                        .setIsSmartLockEnabled(false)
                                        .setAvailableProviders(Arrays.asList(
                                                new AuthUI.IdpConfig.EmailBuilder().build()))
                                        .setTosAndPrivacyPolicyUrls(getString(R.string.privacy_policy_url),
                                                getString(R.string.privacy_policy_url))
                                        .build(),
                                RC_SIGN_IN);
                    }
                }
            };
        }
        else{
            errorMessageTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            tvLink.setVisibility(View.GONE);
        }
    }

    // check if we are connected to the network
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(isOnline()) {
            if (requestCode == RC_SIGN_IN) {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this, R.string.signed_in, Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(MainActivity.this, R.string.error_signing_in, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
//        mScrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
//        Log.i(TAG, "onPause:" + String.valueOf(mScrollPosition));
        mListState = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).onSaveInstanceState();

        if(mFirebaseAuth!=null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mFirebaseAuth!=null) {
            mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(!isOnline()){
            return false;
        }
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                Toast.makeText(MainActivity.this, R.string.signed_out,Toast.LENGTH_SHORT).show();
                return true;

            case R.id.add_widget_menu:
                Class detActivity = AddWidgetActivity.class;
                Intent intent = new Intent(getApplicationContext(),detActivity);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void makeNewsQuery() {
        if(isOnline()) {
            URL newsURL = null;
            try{
                newsURL = new URL(getString(R.string.news_feed_url));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            if(newsURL!=null) {
                new NewsQueryTask().execute(newsURL);
            }
        }
    }

     @Override
    public void onAlertItemClick(int ClickedItemIndex, ImageView tumbnail) {
        Class detActivity = ChartActivity.class;
        Intent intent = new Intent(getApplicationContext(),detActivity);
        intent.putExtra(CHART_URL, alertList.get(ClickedItemIndex).getChartURL());
        intent.putExtra(SYMBOL, alertList.get(ClickedItemIndex).getSymbol() + " - " + alertList.get(ClickedItemIndex).getTimeStr());
        View clickedView = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findViewByPosition(ClickedItemIndex);
        startActivity(intent);
    }

    public void perform_action(View v)
    {
        String url = "http://www.stocksbuyalerts.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    @Override
    public void onNewsItemClick(int ClickedItemIndex) {

        String url = newsList.get(ClickedItemIndex).getLink();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }


    public static class NewsQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            URL searchUrl = urls[0];
            String searchResults = null;
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) searchUrl.openConnection();
                try {
                    InputStream in = urlConnection.getInputStream();
                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");
                    boolean hasInput = scanner.hasNext();
                    if (hasInput) {
                        return scanner.next();
                    } else {
                        return null;
                    }
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        }

        @Override
        protected void onPostExecute(String s) {
            if(s==null){
                return;
            }
             // parsing the response.

            XmlPullParserFactory parserFactory;
            try {
                parserFactory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = parserFactory.newPullParser();
                InputStream is = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(is, null);

                 processParsing(parser);

            } catch (XmlPullParserException e) {

            }
             catch (IOException e) {
            }
        }
    }

    private static void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException{
        newsList = new ArrayList<>();
        int eventType = parser.getEventType();
        News current = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName;

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();

                    if ("item".equals(eltName)) {
                        current = new News();
                        newsList.add(current);
                    } else if (current != null) {
                        if ("description".equals(eltName)) {
                            current.setDescription(parser.nextText());
                        } else if ("pubDate".equals(eltName)) {
                            current.setPubDate(parser.nextText());
                        } else if ("link".equals(eltName)) {
                            current.setLink(parser.nextText());
                        } else if ("title".equals(eltName)) {
                            current.setTitle(parser.nextText());
                        }
                        Log.i(TAG, current.toString());
                    }
                    break;
            }

            eventType = parser.next();
        }
        newsAdapter.setNewsData(newsList);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE_KEY, mListState);
    }
}
