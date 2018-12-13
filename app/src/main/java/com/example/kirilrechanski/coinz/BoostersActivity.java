package com.example.kirilrechanski.coinz;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class BoostersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    private final int VALUE_BOOSTER_PRICE = 800;
    private final int COLLECTING_DISTANCE_BOOSTER_PRICE = 1500;

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
        collectingDistancePrice.setText(String.format("Price: %d gold", COLLECTING_DISTANCE_BOOSTER_PRICE));


        //Display info about the boosters when the Info button is pressed
        Button infoCoinValue = findViewById(R.id.infoValueBooster);
        infoCoinValue.setOnClickListener(view -> {
            Toast.makeText(this, "This booster doubles the gold value of 5 coins" +
                            " and stores them directly in the bank."
                    , Toast.LENGTH_LONG).show();
        });

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
                Double gold = Double.parseDouble(task.getResult().get("goldAvailable").toString());
                if (MapActivity.boosterEnabled) {
                    Toast.makeText(this, "You can only have one booster enabled", Toast.LENGTH_SHORT).show();
                }
                else if (gold >= VALUE_BOOSTER_PRICE) {
                    databaseReference.collection("users").document(user.getUid())
                            .update("goldAvailable", gold-VALUE_BOOSTER_PRICE);
                    databaseReference.collection("users").document(user.getUid())
                            .update("boosterEnabled", true);
                    databaseReference.collection("users").document(user.getUid())
                            .update("coinsLeftInBoosterMode", 5);
                    MapActivity.boosterEnabled = true;
                    Toast.makeText(this, "Purchase successful.", Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(this, "You do not have enough gold to purchase " +
                            "this booster.", Toast.LENGTH_SHORT).show();
                }
            });
        });



    }
}
