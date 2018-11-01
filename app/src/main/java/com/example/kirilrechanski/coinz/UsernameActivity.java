package com.example.kirilrechanski.coinz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UsernameActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        findViewById(R.id.createUserName).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
                mAuth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = mAuth.getCurrentUser();


                TextView username = findViewById(R.id.usernameField);

                if(currentUser != null) {
                    mDatabase.collection("users").document(currentUser.getUid())
                            .update("username", username.getText().toString());
                    Toast.makeText(UsernameActivity.this, "Username created.",
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(UsernameActivity.this, MapActivity.class));
                    finish();
                }
            }
        });


    }

}
