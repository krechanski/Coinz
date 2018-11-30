package com.example.kirilrechanski.coinz;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Wallet extends AppCompatActivity {

    //List which stores collected coins
    static List<Coin> coins = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Create a gridview with the collected coins and added buttons for markAll, unMarkAll
        List<Coin> selectedCoins = new ArrayList<>();
        int numCoins = coins.size();
        double sumMoney = 0;
        for (Coin c: coins) {
            sumMoney += c.getValue();
        }
        TextView sumCoins = findViewById(R.id.sumCoins);
        sumCoins.setText(String.format("Coins sum: %.2f", sumMoney));



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


