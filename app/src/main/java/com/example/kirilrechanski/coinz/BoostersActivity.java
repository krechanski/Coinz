package com.example.kirilrechanski.coinz;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class BoostersActivity extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    private double gold;
    private final int VALUE_BOOSTER_PRICE = 800;
    private final int VALUE_COLLECTING_DISTANCE_PRICE = 2500;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boosters);

        //Get the current user and initialize Firebase.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseFirestore.getInstance();


        //Set the price for the boosters
        TextView valueBoosterPrice = findViewById(R.id.currencyBoosterPriceText);
        valueBoosterPrice.setText(String.format("Price: %d gold", VALUE_BOOSTER_PRICE));

        TextView collectingDistancePrice = findViewById(R.id.distanceBoosterPriceText);
        collectingDistancePrice.setText(String.format("Price: %d gold", VALUE_COLLECTING_DISTANCE_PRICE));

        //Get users' gold
        databaseReference.collection("users").document(user.getUid()).get().addOnCompleteListener(task ->
                gold = Double.parseDouble(Objects.requireNonNull(Objects.requireNonNull(task.getResult())
                        .get("goldAvailable")).toString()));

        //Display info about the boosters when the Info button is pressed
        Button infoCoinValue = findViewById(R.id.infoValueBooster);
        infoCoinValue.setOnClickListener(view ->
                Toast.makeText(this, "This booster doubles the gold value of 5 coins" +
                        " and stores them directly in the bank."
                , Toast.LENGTH_LONG).show());

        Button infoCollectingDistance = findViewById(R.id.infoDistanceBooster);
        infoCollectingDistance.setOnClickListener(view -> {
            Toast.makeText(this, "This booster increases the collecting radius to 40m for " +
                            "the duration of the day."
                    , Toast.LENGTH_LONG).show();
        });


        /*Get the users' gold from Firestore and check if it is enough to purchase the booster.
        If so, purchase the booster and create a boolean field in Firestore and set it to true,
        as well as create an int field with coins left to collect in booster mode and set it to 5
        */
        Button buyValueBooster = findViewById(R.id.buyValueBooster);
        buyValueBooster.setOnClickListener(view -> {
            databaseReference.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
                if (MapActivity.valueBoosterEnabled || MapActivity.distanceBoosterEnabled) {
                    Toast.makeText(this, "You can only have one booster enabled", Toast.LENGTH_SHORT).show();
                }
                else if (gold >= VALUE_BOOSTER_PRICE) {
                    databaseReference.collection("users").document(user.getUid())
                            .update("goldAvailable", gold-VALUE_BOOSTER_PRICE);
                    databaseReference.collection("users").document(user.getUid())
                            .update("valueBoosterEnabled", true);
                    databaseReference.collection("users").document(user.getUid())
                            .update("coinsLeftInBoosterMode", 5);
                    MapActivity.valueBoosterEnabled = true;
                    Toast.makeText(this, "Purchase successful.", Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(this, "You do not have enough gold to purchase " +
                            "this booster.", Toast.LENGTH_SHORT).show();
                }
            });
        });



        Button buyDistanceBooster = findViewById(R.id.buyDistanceBooster);
        buyDistanceBooster.setOnClickListener(view -> {
            if (MapActivity.valueBoosterEnabled || MapActivity.distanceBoosterEnabled) {
                Toast.makeText(this, "You can only have one booster enabled", Toast.LENGTH_SHORT).show();
            }

            else if (gold >= VALUE_COLLECTING_DISTANCE_PRICE) {
                databaseReference.collection("users").document(user.getUid())
                        .update("goldAvailable", gold-VALUE_COLLECTING_DISTANCE_PRICE);
                databaseReference.collection("users").document(user.getUid())
                        .update("distanceBoosterEnabled", true);
                MapActivity.COLLECTING_DISTANCE = 40;
                MapActivity.distanceBoosterEnabled = true;
                Toast.makeText(this, "Purchase successful.", Toast.LENGTH_SHORT).show();
            }

            else {
                Toast.makeText(this, "You do not have enough gold to purchase " +
                        "this booster.", Toast.LENGTH_SHORT).show();
            }

        });



    }
}
