package com.example.kirilrechanski.coinz;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Bank extends AppCompatActivity {
    double goldAvailable = 0;
    double sumGold = 0;
    int coinsLeft = 0;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        //Get the current user.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        //Read coins available and gold from Firestore
        FirebaseFirestore databaseReference = FirebaseFirestore.getInstance();
        assert user != null;
        databaseReference.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
            goldAvailable = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(task.getResult())
                    .get("goldAvailable")).toString());
            coinsLeft = Integer.parseInt(Objects.requireNonNull(task.getResult().get("coinsLeft")).toString());

            sumGold += goldAvailable;

            TextView gold = findViewById(R.id.textGold);
            TextView coins = findViewById(R.id.textCoinsLeft);
            gold.setText(String.format("Gold available: %.2f", goldAvailable));
            coins.setText(String.format("Coins left: %d",coinsLeft ));
        });
    }
}
