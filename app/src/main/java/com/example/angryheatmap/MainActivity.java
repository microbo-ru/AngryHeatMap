package com.example.angryheatmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.common.MapSettings;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView mLogTextView;
    // map embedded in the map fragment
    private Map map = null;
    private Map mapClone = null;
    // map fragment embedded in this activity
    private SupportMapFragment mapFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogTextView = findViewById(R.id.title);
        initialize();
        //addSomeMarkers();
    }

    private void initialize() {
        // Search for the map fragment to finish setup by calling init().
        mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapfragment);
        // Set up disk cache path for the map service for this application
        // It is recommended to use a path under your application folder for storing the disk cache
        boolean success = MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "INTENT_NAME"); /* ATTENTION! Do not forget to update {YOUR_INTENT_NAME} */

        if (!success) {
            //Toast.makeText(getApplicationContext(), "Unable to set isolated disk cache path.", Toast.LENGTH_LONG);
            mLogTextView.setText("Unable to set isolated disk cache path.");
        } else {
            mapFragment.init(new OnEngineInitListener() {
                @Override
                public void onEngineInitializationCompleted(OnEngineInitListener.Error error) {
                    if (error == OnEngineInitListener.Error.NONE) {
                        // retrieve a reference of the map from the map fragment
                        map = mapFragment.getMap();
                        // Set the map center to the St. Petersburg region (no animation)
                        map.setCenter(new GeoCoordinate(59.9386, 30.3141, 12.0), Map.Animation.NONE);
                        // Set the zoom level to the average between min and max
                        map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);

                        MapMarker defaultMarker = new MapMarker();
                        defaultMarker.setCoordinate(new GeoCoordinate(59.9386, 30.3141, 12.0));
                        map.addMapObject(defaultMarker);

                        try {
                            Image image = new Image();
                            image.setImageResource(R.drawable.marker);
                            MapMarker customMarker = new MapMarker();
                            customMarker.setCoordinate(new GeoCoordinate(58.0, 32.0, 12.0));
                            customMarker.setIcon(image);
                            map.addMapObject(customMarker);
                        } catch (IOException e) { }
                        //initMap(map);

                        addSomeMarker();
                        addArrayOfMarkers();
                        //mLogTextView.setVisibility(View.INVISIBLE);
                        LinearLayout mainLayer = findViewById(R.id.linearLayOut);
                        mainLayer.removeView(mLogTextView);

                    } else {
                        //System.out.println("ERROR: Cannot initialize Map Fragment");
                        mLogTextView.setText("ERROR: Cannot initialize Map Fragment");
                    }
                }
            });
        }
    }

    private void addSomeMarker() {     //Doesn't work. Wrong picture for marker.
        try {
            Image image = new Image();
            image.setImageResource(R.drawable.ttv);
            MapMarker customMarker = new MapMarker();
            customMarker.setCoordinate(new GeoCoordinate(58.0, 30.0, 12.0));
            customMarker.setIcon(image);
            if (mapClone != null) {
                mapClone.addMapObject(customMarker);
            } else {
                mLogTextView.setText("ERROR: map has NULL..");
            }
        } catch (IOException e) { }
    }

    private void addArrayOfMarkers() {
        for (int i = 0; i < 10; i++) {
            MapMarker customMarker = new MapMarker();
            double x = new Random().nextInt(10) / 10.0d;
            double y = new Random().nextInt(10) / 10.0d;
            customMarker.setCoordinate(new GeoCoordinate(59.9386 - x, 30.3141 - y, 12.0));
            map.addMapObject(customMarker);
        }
    }

    private void initMap(Map map) {
        mapClone = map;
    }
}
