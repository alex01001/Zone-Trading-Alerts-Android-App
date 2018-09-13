package com.stocksbuyalerts.alexey.zonetradingalerts;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.stocksbuyalerts.alexey.zonetradingalerts.widget.MyWidgetService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private List<String> symbolList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_widget);
        ButterKnife.bind(this);

        btnAddWidget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String symbol = vSymbol.getText().toString().trim();
                symbol = symbol.toUpperCase();

                if(symbol==""){
                    tvErrorMessage.setText(R.string.invalidSymbol);
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }

                if(symbolList.contains(symbol)){
                    tvErrorMessage.setVisibility(View.INVISIBLE);
                    MyWidgetService.updateWidget(getBaseContext(), symbol);
                    Toast.makeText(AddWidgetActivity.this, "Symbol " + symbol +" added to homescreen widget!", Toast.LENGTH_SHORT).show();
                }
                else{
                    tvErrorMessage.setText(R.string.invalidSymbol);
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    return;
                }
            }
        });


// connecting to database
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("Prices");

// managing authentication

        mFirebaseAuth = FirebaseAuth.getInstance();
        Toast.makeText(AddWidgetActivity.this, R.string.signed_in,Toast.LENGTH_SHORT).show();
        FirebaseDatabase database = mFirebaseDatabase;
        DatabaseReference myRef = mMessagesDatabaseReference;

// Read from the database

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                symbolList = new ArrayList<String>();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    String s = ds.getKey();
                    Log.w(TAG, s);
                    symbolList.add(s);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}
