package com.example.kirilrechanski.coinz;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Statistics extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    private float walkedDistance;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //Get the current user.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseFirestore.getInstance();

        TextView distanceWalkedToday = findViewById(R.id.distanceToday);
        TextView totalDistanceWalked = findViewById(R.id.totalDistance);


        float distanceToday = getDistanceRun(MapActivity.steps);
        distanceWalkedToday.setText(String.format("Distance walked today: %.2f km", distanceToday ));

        //Get the totaldistancewalked from firestore, add the distance walked today to it,
        //display it and finally write back totalDistanceWalked to Firestore
        databaseReference.collection("users").document(user.getUid()).get().addOnCompleteListener(task -> {
            Float walkedDistance = Float.valueOf(task.getResult().get("totalDistanceWalked").toString());
            walkedDistance+=distanceToday;
            totalDistanceWalked.setText(String.format("Total distance walked: %.2f km", walkedDistance));
            databaseReference.collection("users").document(user.getUid())
                    .update("totalDistanceWalked", walkedDistance);
        });


    }

    //Function to determine the distance walked in kilometers
    // using average step length and number of steps
    public float getDistanceRun(long steps){
        float distance = (float)(steps*78)/(float)100000;
        return distance;
    }
}
