package com.stocksbuyalerts.alexey.zonetradingalerts;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.stocksbuyalerts.alexey.zonetradingalerts.widget.MyWidgetService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddWidgetActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    private static final String TAG = "SBA_msg";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;

    @BindView(R.id.et_symbol)
    EditText vSymbol;
    @BindView(R.id.btn_addWidget)
    Button btnAddWidget;
    @BindView(R.id.tv_wrong_symbol)
    TextView tvErrorMessage;

    @OnClick(R.id.btn_addWidget)
    public void submit() {
        String symbol = vSymbol.getText().toString().trim();
        symbol = symbol.toUpperCase();

        if(symbol.equals("")){
            tvErrorMessage.setText(R.string.invalidSymbol);
            tvErrorMessage.setVisibility(View.VISIBLE);
            return;
        }

        if(symbolList.contains(symbol)){
            tvErrorMessage.setVisibility(View.INVISIBLE);
            MyWidgetService.updateWidget(getBaseContext(), symbol);
            Toast.makeText(AddWidgetActivity.this, String.format(getResources().getString(R.string.symbol_added_to_widget), symbol), Toast.LENGTH_SHORT).show();
        }
        else{
            tvErrorMessage.setText(R.string.invalidSymbol);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }


    private List<String> symbolList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!isOnline()){
            finish();
        }

        setContentView(R.layout.activity_add_widget);
        ButterKnife.bind(this);

// connecting to database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Prices");

// managing authentication

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = mFirebaseDatabase;
        DatabaseReference myRef = mMessagesDatabaseReference;

// Read from the database

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                symbolList = new ArrayList<String>();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String s = ds.getKey();
//                    Log.w(TAG, s);
                    symbolList.add(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = Objects.requireNonNull(cm).getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
