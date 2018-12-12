package com.example.kirilrechanski.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

public class SendCoinsActivity extends AppCompatActivity {
    private EditText usernameText;
    private Button next;
    static String usernameString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_coins);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameText = findViewById(R.id.fieldUsername);

        next = findViewById(R.id.sendButton);
        next.setOnClickListener(v -> {
            usernameString = usernameText.getText().toString();
            startActivity(new Intent(SendCoinsActivity.this, SendCoinsWallet.class));
        });


    }



}
