package com.example.kirilrechanski.coinz;

import android.content.Context;

import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.FileOutputStream;
import java.util.List;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

public class DownloadCompleteRunner {
    static String result;
    static String geoJsonString;

    /*
       Initially save the downloaded string map into geoJsonString. After that create a FeatureCollection,
       List of feature and list of coordinates. Start looping through each feature and save its geometry, coordinates,
       value and currency. After each iteration place a marker on the map with the title, snippet, position and rounded
       to %.2f value.
     */

    public static void downloadComplete(String result) {
        DownloadCompleteRunner.result = result;
        geoJsonString = result;

        //Save the coinzMap if it hasn't been already
        if (MapActivity.mapDownloaded == false) {
            saveFile(result);
        }


        FeatureCollection featureCollection = FeatureCollection.fromJson(geoJsonString);
        List<Feature> features;
        features = featureCollection.features();
        List<Double> coordinates;

        for (Feature feature : features) {
            Geometry geometry = feature.geometry();
            if (geometry.type().equals("Point")) {
                Point point = (Point) geometry;
                coordinates = point.coordinates();
                JsonObject property = feature.properties();
                String currency = property.get("currency").toString();
                Double value = Math.round(property.get("value").getAsDouble()*100)/100.0;
                MarkerOptions markerOptions = new MarkerOptions();


                // Place custom markers on the currencies
                switch (currency) {
                    case "\"DOLR\"":
                        MapActivity.map.addMarker(markerOptions.title(currency)
                                .snippet(value.toString())
                                .position(new LatLng(coordinates.get(1), coordinates.get(0)))
                                .icon(MapActivity.markerDLR));
                        break;

                    case "\"SHIL\"":
                        MapActivity.map.addMarker(markerOptions.title(currency)
                                .snippet(value.toString())
                                .position(new LatLng(coordinates.get(1), coordinates.get(0)))
                                .icon(MapActivity.markerSHIL));
                        break;

                    case "\"PENY\"":
                        MapActivity.map.addMarker(markerOptions.title(currency)
                                .snippet(value.toString())
                                .position(new LatLng(coordinates.get(1), coordinates.get(0)))
                                .icon(MapActivity.markerPENY));
                        break;

                    case "\"QUID\"":
                        MapActivity.map.addMarker(markerOptions.title(currency)
                                .snippet(value.toString())
                                .position(new LatLng(coordinates.get(1), coordinates.get(0)))
                                .icon(MapActivity.markerQUID));
                        break;

                }

            }


        }
    }


    public static void saveFile(String currentMap) {
        FileOutputStream outputStream;
        try {
            outputStream = getApplicationContext().openFileOutput("coinzmap.geojson", Context.MODE_PRIVATE);
            outputStream.write(currentMap.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
