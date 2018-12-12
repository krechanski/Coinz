package com.example.kirilrechanski.coinz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.geojson.Feature;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class Wallet extends AppCompatActivity {

    //List which stores collected coins
    private List<Coin> coins = new ArrayList<>();
    private List<String> notifications = new ArrayList<>();
    static double gold = 0;
    public List<Feature> features = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    int coinsLeftNum = 0;
    double goldAvaiable = 0;


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get the current user and initialize Firebase.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseFirestore.getInstance();

        /* If the current date is the same as the download date, read the coins from Firestore,
           if not, clear all the coins from the wallet and Firestore
         */
        if (!MapActivity.downloadDate.equals(MapActivity.currentDate)) {
            databaseReference.collection("users").document(user.getUid()).update("wallet", "");
            databaseReference.collection("users").document(user.getUid()).update("coinsLeft", "25");
        }

        databaseReference.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
            coinsLeftNum = Integer.parseInt(task.getResult().get("coinsLeft").toString());
            TextView coinsLeft = findViewById(R.id.coinsLeftText);
            coinsLeft.setText(String.format("Coins left: %d", coinsLeftNum));
        });

        //Get the wallet string with all the coins, parse it in order to get the currency and value
        //and finally create a Coin object out of the two strings
        databaseReference.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String wallet = task.getResult().get("wallet").toString();
                if (!wallet.equals("[]") && !wallet.equals("")) {
                    String allCoins = wallet.replace("[", "").replace("]", "");
                    String[] coinsString = allCoins.split(", ");

                    for (String c : coinsString) {
                        String[] coinAttributes = c.split(" ");
                        String currency = coinAttributes[0];
                        double value = Double.valueOf(coinAttributes[1]);
                        value = round(value, 2);

                        Coin coin = new Coin(currency, value);
                        coins.add(coin);
                    }


                    //Used when selecting which coins to deposit to the bank
                    List<Coin> selectedCoins = new ArrayList<>();
                    int numCoins = coins.size();

                    //Calculate how much gold you have based on the collected coins
                    double sumGold = 0;
                    for (Coin coin1 : coins) {
                        switch (coin1.getCurrency()) {
                            case "QUID":
                                sumGold += coin1.getValue() * MapActivity.QUIDrate;
                                break;

                            case "PENY":
                                sumGold += coin1.getValue() * MapActivity.PENYrate;
                                break;

                            case "DOLR":
                                sumGold += coin1.getValue() * MapActivity.DLRrate;
                                break;

                            case "SHIL":
                                sumGold += coin1.getValue() * MapActivity.SHILrate;
                                break;
                        }
                    }

                    //Create a gridview with the collected coins and add buttons for markAll, unMarkAll
                    GridView gridview =  findViewById(R.id.gridview);
                    ImageAdapter imageAdapter = new ImageAdapter(Wallet.this, coins);
                    gridview.setAdapter(imageAdapter);
                    gridview.setOnItemClickListener((parent, v, position, id) -> {
                        int selectedIndex = imageAdapter.selectedPositions.indexOf(position);
                        if (selectedIndex > -1) {
                            imageAdapter.selectedPositions.remove(selectedIndex);
                            selectedCoins.remove(parent.getItemAtPosition(position));
                            v.setBackgroundResource(R.drawable.coin_notselected);
                        } else {
                            imageAdapter.selectedPositions.add(position);
                            selectedCoins.add((Coin) parent.getItemAtPosition(position));
                            v.setBackgroundResource(R.drawable.coin_selected);
                        }
                    });

                    Button markAll =  findViewById(R.id.markAll);
                    markAll.setOnClickListener(v -> {
                        imageAdapter.selectedPositions.clear();
                        selectedCoins.clear();
                        selectedCoins.addAll(coins);
                        for (int i = 0; i < numCoins; i++) {
                            imageAdapter.selectedPositions.add(i);
                            View view = gridview.getChildAt(i);
                            if (view != null) {
                                view.setBackgroundResource(R.drawable.coin_selected);
                            }
                        }
                    });


                    Button unmarkAll = findViewById(R.id.unmarkAll);
                    unmarkAll.setOnClickListener(v -> {
                        imageAdapter.selectedPositions.clear();
                        selectedCoins.clear();

                        for (int i = 0; i < numCoins; i++) {
                            View view = gridview.getChildAt(i);
                            if (view != null) {
                                view.setBackgroundResource(R.drawable.coin_notselected);
                            }
                        }
                    });

                    //Bank selected coins and remove them from the gridview
                    Button bankCoins = findViewById(R.id.bankCoins);
                    bankCoins.setOnClickListener(view -> {
                        //Check if the user wants to bank more coins than remaining ones
                        if (selectedCoins.size() > coinsLeftNum) {
                            Toast.makeText(Wallet.this,
                                    String.format("You can only bank 25 coins per day. Coins left: %d", coinsLeftNum),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            for (Coin c : selectedCoins) {
                                switch (c.getCurrency()) {
                                    case "QUID":
                                        gold += c.getValue() * MapActivity.QUIDrate;
                                        break;

                                    case "PENY":
                                        gold += c.getValue() * MapActivity.PENYrate;
                                        break;

                                    case "DOLR":
                                        gold += c.getValue() * MapActivity.DLRrate;
                                        break;

                                    case "SHIL":
                                        gold += c.getValue() * MapActivity.SHILrate;
                                        break;
                                }

                                coins.remove(c);
                                String currency = c.getCurrency();
                                double value = c.getValue();
                                databaseReference.collection("users").document(user.getUid())
                                        .update("wallet", FieldValue.arrayRemove(currency + " " + value));
                            }

                            /*
                            Update gridview when a coin is removed, save the remaining ones to local storage,
                            decrement coinsLeftNum based on the selected coins and update it in the Firestore
                             */
                            imageAdapter.notifyDataSetChanged();
                            coinsLeftNum -= selectedCoins.size();
                            databaseReference.collection("users").document(user.getUid()).update("coinsLeft", coinsLeftNum);
                            databaseReference.collection("users").document(user.getUid()).update("goldAvailable", gold);
                            TextView coinsLeft = findViewById(R.id.coinsLeftText);
                            coinsLeft.setText(String.format("Coins left: %d", coinsLeftNum));
                            selectedCoins.clear();
                            imageAdapter.selectedPositions.clear();
                            Toast.makeText(Wallet.this, String.format("Deposited: %.2f gold", gold), Toast.LENGTH_SHORT).show();
                        }
                    });

                    TextView sumCoins = findViewById(R.id.sumGold);
                    sumCoins.setText(String.format("Gold sum: %.2f", sumGold));
                }


                //Layout for each item in the gridView
                class GridItemView extends FrameLayout {

                    private TextView textView;
                    private ImageView imageView;

                    public GridItemView(Context context) {
                        super(context);
                        LayoutInflater.from(context).inflate(R.layout.coin_layout, this);
                        imageView = getRootView().findViewById(R.id.coinMarker);
                        textView = getRootView().findViewById(R.id.coinValue);

                    }
                }
            }

            public double round(double value, int places) {
                if (places < 0) {
                    return value;
                }
                BigDecimal bd = new BigDecimal(value);
                bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
                return bd.doubleValue();
            }
        });
    }
}



