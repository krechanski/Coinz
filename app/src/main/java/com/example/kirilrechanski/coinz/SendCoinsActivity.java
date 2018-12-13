package com.example.kirilrechanski.coinz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class SendCoinsActivity extends AppCompatActivity {
    private EditText usernameText;
    private Button next;
    static String usernameString;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_coins);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Get the email of current user.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseFirestore.getInstance();
        usernameText = findViewById(R.id.fieldUsername);
        CollectionReference cf = databaseReference.collection("users");




        next = findViewById(R.id.sendButton);
        next.setOnClickListener(v -> cf.document(user.getUid()).get().addOnCompleteListener(task -> {
            DocumentSnapshot ds = task.getResult();

            usernameString = usernameText.getText().toString();
            Query firstQuery = cf.whereEqualTo("username", SendCoinsActivity.usernameString);
            firstQuery.get().addOnCompleteListener(task1 -> {
                String docId = "";
                QuerySnapshot name = task1.getResult();
                List<DocumentSnapshot> snapshots = name.getDocuments();
                if (snapshots.isEmpty()) {
                    Toast.makeText(this, "Username does not exist.", Toast.LENGTH_SHORT).show();
                }
                else if(snapshots.contains(ds)) {
                    Toast.makeText(this, "You sneaky devil, trying to send coins to yourself, ah?", Toast.LENGTH_LONG).show();
                }

                else {
                    startActivity(new Intent(SendCoinsActivity.this, SendCoinsWallet.class));
                    finish();
                }

            });
        }));
    }



}
