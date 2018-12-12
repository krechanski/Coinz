package com.example.kirilrechanski.coinz;

import android.annotation.SuppressLint;

import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DownloadCompleteRunner {
    static String geoJsonString;
    static String mapRates;

    /*
       Initially save the downloaded string map into geoJsonString. After that create a FeatureCollection,
       List of feature and list of coordinates. Start looping through each feature and save its geometry, coordinates,
       value and currency. After each iteration place a marker on the map with the title, snippet, position and rounded
       to %.2f value.
     */

    @SuppressLint("DefaultLocale")
    public static void downloadComplete(String result) {
        geoJsonString = result;

        int ratesM = geoJsonString.indexOf("features") - 1;
        mapRates = geoJsonString.substring(0, ratesM);

        //Extracting the exchange rate for every coin
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject rates = jsonObject.getJSONObject("rates");
            MapActivity.DLRrate = rates.getDouble("DOLR");
            MapActivity.PENYrate = rates.getDouble("PENY");
            MapActivity.QUIDrate = rates.getDouble("QUID");
            MapActivity.SHILrate = rates.getDouble("SHIL");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        FeatureCollection featureCollection = FeatureCollection.fromJson(geoJsonString);
        List<Feature> features;
        features = featureCollection.features();
        List<Double> coordinates;

        assert features != null;
        for (Feature feature : features) {
            Geometry geometry = feature.geometry();
            assert geometry != null;
            if (geometry.type().equals("Point")) {
                Point point = (Point) geometry;
                coordinates = point.coordinates();
                JsonObject property = feature.properties();
                String currency = property != null ? property.get("currency").toString().replaceAll("^\"|\"$", "") : null;
                Double value = property != null ? property.get("value").getAsDouble() : 0;
                MarkerOptions markerOptions = new MarkerOptions();

                // Place custom markers on the currencies
                assert currency != null;
                switch (currency) {
                    case "DOLR":
                        MapActivity.map.addMarker(markerOptions.title(currency)
                                .snippet(String.format("%.2f", value))
                                .position(new LatLng(coordinates.get(1), coordinates.get(0)))
                                .icon(MapActivity.markerDLR));
                        break;

                    case "SHIL":
                        MapActivity.map.addMarker(markerOptions.title(currency)
                                .snippet(String.format("%.2f", value))
                                .position(new LatLng(coordinates.get(1), coordinates.get(0)))
                                .icon(MapActivity.markerSHIL));
                        break;

                    case "PENY":
                        MapActivity.map.addMarker(markerOptions.title(currency)
                                .snippet(String.format("%.2f", value))
                                .position(new LatLng(coordinates.get(1), coordinates.get(0)))
                                .icon(MapActivity.markerPENY));
                        break;

                    case "QUID":
                        MapActivity.map.addMarker(markerOptions.title(currency)
                                .snippet(String.format("%.2f", value))
                                .position(new LatLng(coordinates.get(1), coordinates.get(0)))
                                .icon(MapActivity.markerQUID));
                        break;

                }

            }
        }
    }
}
