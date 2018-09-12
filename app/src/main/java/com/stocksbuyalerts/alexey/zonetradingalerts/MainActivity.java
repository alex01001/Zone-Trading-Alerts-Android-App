package com.stocksbuyalerts.alexey.zonetradingalerts;

import android.app.ActionBar;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements AlertAdapter.AlertItemClickListener{

    public static final int RC_SIGN_IN = 1;

    public static final String CHART_URL = "chart_url";
    public static final String SYMBOL = "symbol";


    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;



    private RecyclerView mRecyclerView;
    private AlertAdapter adapter;
    public List<Alert> alertList;
//    TextView errorMessageTextView;
//    ProgressBar mLoadingIndicator;


    @BindView(R.id.tv_error_message_diaplay) TextView errorMessageTextView;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
//    @BindView(R.id.rv_alertsList) RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alertList = new ArrayList<Alert>();
        ButterKnife.bind(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_alertsList);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        actionBar.setLogo(R.mipmap.ic_launcher_round);

        FirebaseMessaging.getInstance().subscribeToTopic("alerts")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "msg_subscribed";
                        if (!task.isSuccessful()) {
                            msg = "msg_subscribe_failed";
                        }
                        Log.d("zzz", msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
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


// connecting to database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Alerts");

// managing authentication

        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
//                    Toast.makeText(MainActivity.this, "signed in",Toast.LENGTH_SHORT).show();

                    FirebaseDatabase database = mFirebaseDatabase;
                    DatabaseReference myRef = mMessagesDatabaseReference;

                    // Read from the database

                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Toast.makeText(MainActivity.this, "message event!",Toast.LENGTH_SHORT).show();

                            alertList = new ArrayList<Alert>();

                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                Alert alert = ds.getValue(Alert.class);
                                Log.w("zzz", alert.toString());
                                alertList.add(alert);

                            }
                            Collections.reverse(alertList);
                            adapter.setAlertData(alertList);

                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            Log.w("zzz", "Failed to read value.", error.toException());
                        }
                    });


                }
                else{

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .setTosAndPrivacyPolicyUrls("http://www.stocksbuyalerts.com/disclaimer/",
                                            "http://www.stocksbuyalerts.com/disclaimer/")
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

    }

    // check if we are connected to the network
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void makeSearchQuery() {
            if (isOnline()) {
//                String searchResults = null;
//                Bundle queryBundle = new Bundle();
//                queryBundle.putString("query", "LOAD_FROM_DB");
//                LoaderManager loaderManager = getSupportLoaderManager();
//                Loader<String> queryLoader = loaderManager.getLoader(FAVORITES_LOADER_ID);
//
//                if (queryLoader == null) {
//                    loaderManager.initLoader(FAVORITES_LOADER_ID, queryBundle, this);
//                } else {
//                    loaderManager.restartLoader(FAVORITES_LOADER_ID, queryBundle, this);
//                }
            }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode==RESULT_OK){
                Toast.makeText(MainActivity.this, "Signed in!",Toast.LENGTH_SHORT).show();
            }
            else if (resultCode==RESULT_CANCELED){
                Toast.makeText(MainActivity.this, "Signed in cancelled!",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                Toast.makeText(MainActivity.this, "Signed Out",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.add_widget_menu:
                Context context = MainActivity.this;
                Class detActivity = AddWidgetActivity.class;
                Intent intent = new Intent(getApplicationContext(),detActivity);
                startActivity(intent);


                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

     @Override
    public void onAlertItemClick(int ClickedItemIndex) {
        Context context = MainActivity.this;
        Class detActivity = ChartActivity.class;
        Intent intent = new Intent(getApplicationContext(),detActivity);
        intent.putExtra(CHART_URL, alertList.get(ClickedItemIndex).getChartURL());
        intent.putExtra(SYMBOL, alertList.get(ClickedItemIndex).getSymbol() + " - " + alertList.get(ClickedItemIndex).getTimeStr());
        startActivity(intent);
    }


    public void perform_action(View v)
    {
        String url = "http://www.stocksbuyalerts.com";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }
}
