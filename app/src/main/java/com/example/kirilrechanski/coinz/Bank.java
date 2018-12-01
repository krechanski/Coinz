package com.example.kirilrechanski.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Bank extends AppCompatActivity {
    static List<Coin> coinsBank = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        TextView goldText = findViewById(R.id.textGold);
        goldText.setText(String.format("Gold available: %.2f ",Wallet.gold ));


    }
}
