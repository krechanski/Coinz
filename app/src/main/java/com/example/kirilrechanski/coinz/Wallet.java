package com.example.kirilrechanski.coinz;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Wallet extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    private String coinsCollected = "0";
    private String sumCoins = "0";

    /*Get the current user and his data fields for collected coins
    and sumCoins*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(Wallet.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        mAuth = FirebaseAuth.getInstance();
//        user = mAuth.getCurrentUser();
//
//        databaseReference = FirebaseFirestore.getInstance();
//        DocumentReference docRef = databaseReference.collection("users").document(user.getUid());
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                coinsCollected = task.getResult().get("coinsCollected").toString();
//                sumCoins = task.getResult().get("sumCoins").toString();
//
//
//                TextView coinsCollectedValue = findViewById(R.id.collectedCoinsValue);
//                coinsCollectedValue.setText(coinsCollected);
//
//                TextView moneyAvaiableText = findViewById(R.id.moneyAvaiableText);
//                TextView moneyAvaiableValue = findViewById(R.id.moneyAvaiableValue);
//                moneyAvaiableValue.setText(sumCoins);
//            }
//        });
    }

}
