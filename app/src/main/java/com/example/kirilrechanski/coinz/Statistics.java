package com.example.kirilrechanski.coinz;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Statistics extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    private float totalDistanceWalked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //Get the current user.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseFirestore.getInstance();

        float distanceToday = getDistanceRun(MapActivity.steps);



        TextView distanceWalkedToday = findViewById(R.id.distanceToday);
        TextView totalDistanceWalked = findViewById(R.id.totalDistance);
        distanceWalkedToday.setText(String.format("Distance walked today: %.2f km", distanceToday ));

    }

    //Function to determine the distance walked in kilometers
    // using average step length and number of steps
    public float getDistanceRun(long steps){
        float distance = (float)(steps*78)/(float)100000;
        return distance;
    }
}
