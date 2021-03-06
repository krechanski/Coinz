package com.example.kirilrechanski.coinz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class    UsernameActivity extends AppCompatActivity {

    /*
    The user sets his username which is linked with his email
    and it is saved in the FireStore Database.
     */
    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private boolean usernameExtist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        databaseReference = FirebaseFirestore.getInstance();
        findViewById(R.id.createUserName).setOnClickListener(v -> {
            FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();

            /*
            Check if the username exists in the database and make a Toast if it does.
            If it doesn't create it and link it with the users' email
             */
            TextView username = findViewById(R.id.usernameField);
            CollectionReference cf = databaseReference.collection("users");
            Query query = cf.whereEqualTo("username", username.getText().toString());
            query.get().addOnCompleteListener(task -> {
                QuerySnapshot qs = task.getResult();
                if (qs.isEmpty()) {
                    if (currentUser != null) {
                        mDatabase.collection("users").document(currentUser.getUid())
                                .update("username", username.getText().toString());
                        Toast.makeText(UsernameActivity.this, "Username created.",
                                Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(UsernameActivity.this, MapActivity.class));
                        finish();
                    }
                } else {
                    Toast.makeText(UsernameActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                }
            });


        });
    }

    //Don't allow the user to go to the map when creating a username
    @Override
    public void onBackPressed() {
    }
}
