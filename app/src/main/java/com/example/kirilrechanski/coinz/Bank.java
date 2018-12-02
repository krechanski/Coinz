package com.example.kirilrechanski.coinz;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Bank extends AppCompatActivity {
    static List<Coin> coinsBank = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    double goldAvailable = 0;
    double sumGold = 0;
    int coinsLeft = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        //Get the current user.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Read coins available and gold from Firestore
        databaseReference = FirebaseFirestore.getInstance();
        databaseReference.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                goldAvailable = Double.parseDouble(task.getResult().get("goldAvailable").toString());
                coinsLeft = Integer.parseInt(task.getResult().get("coinsLeft").toString());

                sumGold += goldAvailable;

                TextView gold = findViewById(R.id.textGold);
                TextView coins = findViewById(R.id.textCoinsLeft);
                gold.setText(String.format("Gold available: %.2f", goldAvailable));
                coins.setText(String.format("Coins left: %d",coinsLeft ));
            }
        });
    }
}
