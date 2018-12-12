package com.example.kirilrechanski.coinz;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {
    ListView listView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        //Get the current user.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseFirestore.getInstance();


        //Get the notifications array from Firestore
        databaseReference.collection("users")
                .document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String notifications = task.getResult().get("notifications").toString();
                        if (!notifications.equals("[]") && !notifications.equals("")) {
                            String allnotifications = notifications.replace("[", "").replace("]", "");
                            //Get listview object from xml
                            listView = findViewById(R.id.list);

                            // Defined Array values to show in ListView
                            String[] values = allnotifications.split(", ");

                            //first create a list from String array
                            List<String> list = Arrays.asList(values);

                            //next, reverse the list using Collections.reverse method
                            Collections.reverse(list);

                            //next, convert the list back to String array
                            values = (String[]) list.toArray();

                            // Define a new Adapter
                            // First parameter - Context
                            // Second parameter - Layout for the row
                            // Third parameter - ID of the TextView to which the data is written
                            // Forth - the Array of data

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(NotificationsActivity.this,
                                    android.R.layout.simple_list_item_1, android.R.id.text1, values);


                            // Assign adapter to ListView
                            listView.setAdapter(adapter);
                        }
                    }
                });
    }
}
