package com.example.kirilrechanski.coinz;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Wallet extends AppCompatActivity {

    //List which stores collected coins
    static List<Coin> coins = new ArrayList<>();
    static double gold = 0;
    public List<Feature> features = new ArrayList<>();
    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    int coinsLeftNum = 0;
    double goldAvaiable = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Get the current user.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseFirestore.getInstance();
        databaseReference.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                coinsLeftNum = Integer.parseInt(task.getResult().get("coinsLeft").toString());
                TextView coinsLeft = findViewById(R.id.coinsLeftText);
                coinsLeft.setText(String.format("Coins left: %d",coinsLeftNum));
            }
        });


        String walletCoins = "";
        if (coins.isEmpty()) {

            //Read the collected coins from local storage and show them in the gridview
            try {
                FileInputStream fileInputStream = openFileInput("walletcoins.geojson");
                walletCoins = readStream(fileInputStream);

                FeatureCollection featureCollection = FeatureCollection.fromJson(walletCoins);
                features = featureCollection.features();
                List<Double> coordinates;
                for (Feature feature : features) {
                    Geometry geometry = feature.geometry();
                    if (geometry.type().equals("Point")) {
                        Point point = (Point) geometry;
                        coordinates = point.coordinates();
                        JsonObject property = feature.properties();
                        String currency = property.get("currency").toString().replaceAll("^\"|\"$", "");
                        double value = property.get("value").getAsDouble();

                        Coin coin = new Coin(currency, value);
                        coins.add(coin);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        //Used when selecting which coins to deposit to the bank
        List<Coin> selectedCoins = new ArrayList<>();
        int numCoins = coins.size();

        //Calculate how much gold you have based on the collected coins
        double sumGold = 0;
        for (Coin c : coins) {
            switch (c.getCurrency()) {
                case "QUID":
                    sumGold += c.getValue() * MapActivity.QUIDrate;
                    break;

                case "PENY":
                    sumGold += c.getValue() * MapActivity.PENYrate;
                    break;

                case "DOLR":
                    sumGold += c.getValue() * MapActivity.DLRrate;
                    break;

                case "SHIL":
                    sumGold += c.getValue() * MapActivity.SHILrate;
                    break;
            }
        }

        //Create a gridview with the collected coins and add buttons for markAll, unMarkAll
        GridView gridview = (GridView) findViewById(R.id.gridview);
        ImageAdapter imageAdapter = new ImageAdapter(this, coins);
        gridview.setAdapter(imageAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                int selectedIndex = imageAdapter.selectedPositions.indexOf(position);
                if (selectedIndex > -1) {
                    imageAdapter.selectedPositions.remove(selectedIndex);
                    selectedCoins.remove((Coin) parent.getItemAtPosition(position));
                    v.setBackgroundResource(R.drawable.coin_notselected);
                } else {
                    imageAdapter.selectedPositions.add(position);
                    selectedCoins.add((Coin) parent.getItemAtPosition(position));
                    v.setBackgroundResource(R.drawable.coin_selected);
                }
            }
        });

        Button markAll = (Button) findViewById(R.id.markAll);
        markAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageAdapter.selectedPositions.clear();
                selectedCoins.clear();
                selectedCoins.addAll(coins);
                for (int i = 0; i < numCoins; i++) {
                    imageAdapter.selectedPositions.add(i);
                    View view = (View) gridview.getChildAt(i);
                    if (view != null) {
                        view.setBackgroundResource(R.drawable.coin_selected);
                    }
                }
            }
        });


        Button unmarkAll = findViewById(R.id.unmarkAll);
        unmarkAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageAdapter.selectedPositions.clear();
                selectedCoins.clear();

                for (int i = 0; i < numCoins; i++) {
                    View view = (View) gridview.getChildAt(i);
                    if (view != null) {
                        view.setBackgroundResource(R.drawable.coin_notselected);
                    }
                }
            }
        });

        //Bank selected coins and remove them from the gridview
        Button bankCoins = findViewById(R.id.bankCoins);
        bankCoins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Check if the user wants to bank more coins than remaining ones
                if (selectedCoins.size()> coinsLeftNum) {
                    Toast.makeText(Wallet.this,
                            String.format("You can only bank 25 coins per day. Coins left: %d", coinsLeftNum),
                            Toast.LENGTH_SHORT).show();
                }

                else {

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

                        //Iterate through the selected coins, remove them and
                        //overwrite the walletcoins.geojson with the unbanked coins
                        for (int i = 0; i < features.size(); i++) {
                            Feature f = features.get(i);
                            if (f.hasProperty("currency")) {
                                Double value = Double.valueOf(f.getStringProperty("value"));
                                if ((f.getStringProperty("currency").equals(c.getCurrency())) && value == c.getValue()) {
                                    features.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                    /*
                    Update gridview when a coin is removed, save the remaining ones to local storage,
                    decrement coinsLeftNum based on the selected coins and update it in the Firestore
                     */
                    imageAdapter.notifyDataSetChanged();
                    FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);
                    String remainingCoins = featureCollection.toJson();
                    saveWalletCoins(remainingCoins);
                    coinsLeftNum -= selectedCoins.size();
                    databaseReference.collection("users").document(user.getUid()).update("coinsLeft", coinsLeftNum);
                    databaseReference.collection("users").document(user.getUid()).update("goldAvailable", gold);
                    TextView coinsLeft = findViewById(R.id.coinsLeftText);
                    coinsLeft.setText(String.format("Coins left: %d", coinsLeftNum));
                    selectedCoins.clear();
                    imageAdapter.selectedPositions.clear();
                    Toast.makeText(Wallet.this, String.format("Deposited: %.2f gold", gold), Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView sumCoins = findViewById(R.id.sumGold);
        sumCoins.setText(String.format("Gold sum: %.2f", sumGold));
    }

    //Read input function used to read walletCoins.geojson
    @NonNull
    private String readStream(InputStream stream)
            throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(stream))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    //Method used to overwrite the local file for coins with non-banked coins
    public void saveWalletCoins(String currentCoins) {
        FileOutputStream outputStream;
        try {
            outputStream = getApplicationContext().openFileOutput("walletcoins.geojson", Context.MODE_PRIVATE);
            outputStream.write(currentCoins.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Layout for each item in the gridView
    public static class GridItemView extends FrameLayout {

        private TextView textView;
        private ImageView imageView;

        public GridItemView(Context context) {
            super(context);
            LayoutInflater.from(context).inflate(R.layout.coin_layout, this);
            imageView = getRootView().findViewById(R.id.coinMarker);
            textView = (TextView) getRootView().findViewById(R.id.coinValue);

        }
    }
}

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

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



