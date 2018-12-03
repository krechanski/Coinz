package com.example.kirilrechanski.coinz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.JsonParser;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.GeoJson;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.kirilrechanski.coinz.DownloadCompleteRunner.geoJsonString;


public class MapActivity extends AppCompatActivity implements
        OnMapReadyCallback, LocationEngineListener, PermissionsListener, NavigationView.OnNavigationItemSelectedListener{

    private final String TAG = "MapActivity";
    private final String PREFERENCE_FILE = "MyPrefsFile"; //For storing preferences
    private final float COLLECTING_DISTANCE = 25;
    static int steps = 0;

    private MapView mapView;
    static MapboxMap map;
    static Boolean mapDownloaded = false;
    private PermissionsManager permissionsManager;
    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private Location originLocation;
    static String downloadDate = ""; //Format: yyy/mm/dd
    private FirebaseAuth mAuth;
    private FirebaseFirestore databaseReference;
    private FirebaseUser user;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;
    private Sensor mStepDetectorSensor;
    static String currentDate;
    static Date date = new Date();
    static List<Feature> coinFeatures = new ArrayList<>();


    static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");


    //Initialize custom icons
    static Icon markerDLR;
    static Icon markerSHIL;
    static Icon markerPENY;
    static Icon markerQUID;


    //Exchange rates for coins
    static double DLRrate;
    static double SHILrate;
    static double PENYrate;
    static double QUIDrate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mapbox access token is configured here. This needs to be called either in your application
        // object or in the same activity which contains the mapview.
        Mapbox.getInstance(this, "pk.eyJ1Ijoia3JlY2hhbnNraSIsImEiOiJjam1ld203djAxbjg2M2tueTBkODBuam9jIn0.omVBD6pPwrgcllwIotvEMg");

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_nav_drawer);

        //Get the email of current user.
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        //Code below is used to create the NavDrawer
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);

        //Set the navDrawer email to the user's email
        View headerView = navigationView.getHeaderView(0);
        TextView email = headerView.findViewById(R.id.navEmail);
        email.setText(user.getEmail());
        navigationView.setNavigationItemSelectedListener(this);

        //Set the profile name in navDrawer to user's Username
        databaseReference = FirebaseFirestore.getInstance();
        DocumentReference docRef = databaseReference.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String usernameGet = document.getString("username");
                        TextView usernameNavDrawer = headerView.findViewById(R.id.navUsername);
                        usernameNavDrawer.setText(usernameGet);
                    } else {
                        Log.d("Error", "get username failed with ", task.getException());
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        switch (item.getItemId()) {

            case R.id.nav_signOut: {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LogInActivity.class));
                finish();
                break;
            }

            case R.id.nav_wallet: {
                startActivity(new Intent(this, Wallet.class));
                break;
            }

            case R.id.nav_bank: {
                startActivity(new Intent(this, Bank.class));
                break;
            }
            case R.id.nav_stats: {
                startActivity(new Intent(this, Statistics.class));
                break;
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        Toast.makeText(MapActivity.this, "Hello there! ;)",
                Toast.LENGTH_SHORT).show();
        map = mapboxMap;
        map.getUiSettings().setCompassEnabled(true);
        enableLocationPlugin();

        //Get the custom marker images
        markerDLR = getIcon(R.drawable.green_marker);
        markerSHIL = getIcon(R.drawable.blue_marker);
        markerPENY = getIcon(R.drawable.red_marker);
        markerQUID = getIcon(R.drawable.yellow_marker);

        //Get the current date
        currentDate = dateFormat.format(date);
        String url = "http://homepages.inf.ed.ac.uk/stg/coinz/" + currentDate + "/coinzmap.geojson";

        //Start downloading the map if the download date is different than the current one
        if (!downloadDate.equals(currentDate)) {

            //Increment collected coins and update the field in the FireStore Database
            databaseReference.collection("users").document(user.getUid())
                    .update("coinsLeft", 25);
            databaseReference.collection("users").document(user.getUid())
                    .update("steps", 0);

            Wallet.coins.clear();
            DownloadFileTask downloadFileTask = new DownloadFileTask();
            downloadDate = currentDate;
            downloadFileTask.execute(url);
        }


        //If the map is already downloaded locally, read it and call DownloadCompleteRunner
        else {
            mapDownloaded = true;
            String geoJsonString = "";
            try {
                FileInputStream fileInputStream = openFileInput("coinzmap.geojson");
                geoJsonString = readStream(fileInputStream);
                DownloadCompleteRunner.downloadComplete(geoJsonString);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Read input file used to read coinzmap.geojson
    @NonNull
    private String readStream(InputStream stream)
            throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(stream))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }


    // Fetches the custom marker icon image from drawable folder
    private Icon getIcon(int resource) {
        IconFactory iconFactory = IconFactory.getInstance(MapActivity.this);
        BitmapDrawable iconDrawable = (BitmapDrawable) ResourcesCompat.getDrawable(getResources(), resource, null);
        assert iconDrawable != null;
        Bitmap bitmap = iconDrawable.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
        Icon icon = iconFactory.fromBitmap(smallMarker);
        return icon;
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationPlugin() {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            initializeLocationEngine();
            // Create an instance of the plugin. Adding in LocationLayerOptions is also an optional
            // parameter
            initializeLocationLayer();

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }


    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setFastestInterval(1000);
        locationEngine.setInterval(5000);
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
        } else {
            locationEngine.addLocationEngineListener(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void initializeLocationLayer() {
        locationLayerPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(),
                location.getLongitude())));
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    //Remove markers which are less than 25 metres of the user's current location
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);

            steps++;
            Toast.makeText(this, String.format("Steps: %d", steps), Toast.LENGTH_SHORT).show();
            databaseReference.collection("users").document(user.getUid()).update("steps", steps);

            List<Marker> markerList = map.getMarkers();
            List<Double> coordinates;
            for (Marker marker : markerList) {

                if (getDistanceFromCurrentPosition(location.getLatitude(), location.getLongitude(),
                        marker.getPosition().getLatitude(), marker.getPosition().getLongitude()) <= COLLECTING_DISTANCE) {

                    //Create coin object from the markers and save it to a List in Wallet activity
                    Coin coin = new Coin(marker.getTitle(), Double.parseDouble(marker.getSnippet()));
                    Wallet.coins.add(coin);
                    map.removeMarker(marker);

                    //Saving collected coins in a local file for the Wallet Gridview
                    String currency = marker.getTitle();
                    String value = marker.getSnippet();
                    Double latitude = marker.getPosition().getLatitude();
                    Double longitude = marker.getPosition().getLongitude();


                    Point point = Point.fromLngLat(longitude, latitude);
                    coordinates = point.coordinates();
                    Geometry geometry = (Geometry) point;

                    Feature feature = Feature.fromGeometry(geometry);
                    feature.addStringProperty("value", value);
                    feature.addStringProperty("currency", currency);
                    coinFeatures.add(feature);
                    FeatureCollection featureCollection = FeatureCollection.fromFeatures(coinFeatures);
                    String coinWallet = featureCollection.toJson();
                    saveWalletCoins(coinWallet);
                }
            }
        }
    }


    //Used to calculate distance between the user and a target
    public static float getDistanceFromCurrentPosition(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000;

        double dLat = Math.toRadians(lat2 - lat1);

        double dLng = Math.toRadians(lng2 - lng1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = earthRadius * c;

        return new Float(dist).floatValue();

    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        //Present toast or dialog
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        // use ”” as the default value (this might be the first time the app is run)
        downloadDate = settings.getString("lastDownloadDate", "");
        Log.d(TAG, "[onStart] Recalled lastDownloadDate is ’" + downloadDate + "’");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationLayerPlugin != null) {
            locationLayerPlugin.onStart();
        }

        //Save the uncollected markers in a local file so
        //you don't have to pick the same marker twice on resume

        List<Marker> remainedMarkers = map.getMarkers();
        List<Feature> features = new ArrayList<>();
        List<Double> coordinatesMarker;

        for (Marker m : remainedMarkers) {
            String currency = m.getTitle();
            String value = m.getSnippet();
            Double latitude = m.getPosition().getLatitude();
            Double longitude = m.getPosition().getLongitude();


            Point point = Point.fromLngLat(longitude, latitude);
            coordinatesMarker = point.coordinates();
            Geometry geometry = (Geometry) point;

            Feature feature = Feature.fromGeometry(geometry);
            feature.addStringProperty("value", value);
            feature.addStringProperty("currency", currency);
            features.add(feature);

        }

        FeatureCollection featureCollection = FeatureCollection.fromFeatures(features);

        String GEOJsonString = DownloadCompleteRunner.mapRates + featureCollection.toJson().substring(1);
        saveFile(GEOJsonString);

        SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE,
                Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lastDownloadDate", downloadDate);
        editor.apply();
        mapView.onStop();
    }


    //Method used to save the coinz map in local storage
    public void saveFile(String currentMap) {
        FileOutputStream outputStream;
        try {
            outputStream = getApplicationContext().openFileOutput("coinzmap.geojson", Context.MODE_PRIVATE);
            outputStream.write(currentMap.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method used to save the coinz map in local storage
    public void saveWalletCoins(String currentCoins) {
        FileOutputStream outputStream;
        try {
            outputStream = getApplicationContext().openFileOutput("walletcoins.geojson", Context.MODE_PRIVATE);
            outputStream.write(currentCoins.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Minimize the app on back-pressed
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences settings = getSharedPreferences(PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lastDownloadDate", downloadDate);
        editor.apply();
        mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }
}
