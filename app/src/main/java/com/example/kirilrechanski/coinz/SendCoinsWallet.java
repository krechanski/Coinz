package com.example.kirilrechanski.coinz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.geojson.Feature;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class SendCoinsWallet extends AppCompatActivity {

    //List which stores collected coins
    private List<Coin> coins = new ArrayList<>();
    static double gold = 0;
    public List<Feature> features = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    int coinsLeftNum = 0;
    double goldAvaiable = 0;
    String sendersUsername = "";


    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_coins_wallet);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get the current user.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseFirestore.getInstance();


        databaseReference.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
            sendersUsername = task.getResult().get("username").toString();
            coinsLeftNum = Integer.parseInt(task.getResult().get("coinsLeft").toString());
            TextView coinsLeft = findViewById(R.id.coinsLeftTextSendCoins);
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
                    GridView gridview = findViewById(R.id.gridViewSendCoins);
                    ImageAdapter imageAdapter = new ImageAdapter(SendCoinsWallet.this, coins);
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

                    Button markAll =  findViewById(R.id.markAllSendCoins);
                    markAll.setOnClickListener(v -> {
                        imageAdapter.selectedPositions.clear();
                        selectedCoins.clear();
                        selectedCoins.addAll(coins);
                        for (int i = 0; i < numCoins; i++) {
                            imageAdapter.selectedPositions.add(i);
                            View view =  gridview.getChildAt(i);
                            if (view != null) {
                                view.setBackgroundResource(R.drawable.coin_selected);
                            }
                        }
                    });


                    Button unmarkAll = findViewById(R.id.unmarkAllSendCoins);
                    unmarkAll.setOnClickListener(v -> {
                        imageAdapter.selectedPositions.clear();
                        selectedCoins.clear();

                        for (int i = 0; i < numCoins; i++) {
                            View view =  gridview.getChildAt(i);
                            if (view != null) {
                                view.setBackgroundResource(R.drawable.coin_notselected);
                            }
                        }
                    });


                    //Send selected coins to another user
                    Button sendCoins = findViewById(R.id.sendCoinsWallet);
                    sendCoins.setOnClickListener(view -> databaseReference.collection("users").document(user.getUid()).get()
                            .addOnCompleteListener(task1 -> {
                                Integer coinsRemaining = Integer.parseInt(String.valueOf(task1.getResult().get("coinsLeft")));
                                if (coinsRemaining != 0) {
                                    Toast.makeText(SendCoinsWallet.this, "You need to bank all 25 coins, " +
                                            "before sending one", Toast.LENGTH_SHORT).show();
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

                                        //Removes the selected coin from the senders' wallet
                                        databaseReference.collection("users").document(user.getUid())
                                                .update("wallet", FieldValue.arrayRemove(currency + " " + value));

                                        /*
                                        Get a collectionReference to users, after that get the document
                                        which matches the recpients' username, get the goldAvaiable field
                                        and update it with the selected coins' value
                                         */
                                        CollectionReference cf = databaseReference.collection("users");

                                        Query firstQuery = cf.whereEqualTo("username", SendCoinsActivity.usernameString);

                                        firstQuery.get().addOnCompleteListener(task11 -> {
                                            String docId = "";
                                            QuerySnapshot name = task11.getResult();
                                            List<DocumentSnapshot> snapshots = name.getDocuments();
                                            for (DocumentSnapshot ds : snapshots) {
                                                docId = ds.getId();
                                                Double goldValue = Double.parseDouble(snapshots.get(0).get("goldAvailable").toString());
                                                goldValue += gold;
                                                databaseReference.collection("users")
                                                        .document(docId).update("goldAvailable", goldValue);
                                                databaseReference.collection("users").document(docId)
                                                        .update("notifications",
                                                                FieldValue.arrayUnion(sendersUsername + " sent you " + goldValue + " gold! :)"));
                                                databaseReference.collection("users")
                                                        .document(docId).update("hasNotification", true);
                                            }
                                        });
                                    }

                                    imageAdapter.notifyDataSetChanged();
                                }
                            }));

                    TextView sumCoins = findViewById(R.id.sumGoldSendCoins);
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
                        textView =  getRootView().findViewById(R.id.coinValue);

                    }
                }
            }

            double round(double value, int places) {
                if (places < 0) {
                    return value;
                }
                BigDecimal bd = new BigDecimal(value);
                bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
                return bd.doubleValue();
            }

            public void onBackPressed() {
                startActivity(new Intent(SendCoinsWallet.this, MapActivity.class));
            }
        });
    }
}



