package com.example.kirilrechanski.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        usernameText = findViewById(R.id.fieldUsername);

        next = findViewById(R.id.sendButton);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameString = usernameText.getText().toString();
                startActivity(new Intent(SendCoinsActivity.this, Wallet.class));
            }
        });


    }



}
