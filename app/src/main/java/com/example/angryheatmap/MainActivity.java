package com.example.angryheatmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.Image;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.common.MapSettings;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private TextView mLogTextView;
    // map embedded in the map fragment
    private Map map = null;
    //private Map mapClone = null;
    // map fragment embedded in this activity
    private SupportMapFragment mapFragment = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

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
                        /*
                        MapMarker defaultMarker = new MapMarker();
                        defaultMarker.setCoordinate(new GeoCoordinate(59.9386, 30.3141, 12.0));
                        map.addMapObject(defaultMarker);
                        */
                        /*
                        try {
                            Image image = new Image();
                            image.setImageResource(R.drawable.marker);
                            MapMarker customMarker = new MapMarker();
                            customMarker.setCoordinate(new GeoCoordinate(58.0, 32.0, 12.0));
                            customMarker.setIcon(image);
                            map.addMapObject(customMarker);
                        } catch (IOException e) { }
                        */
                        //addSomeMarker();
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
            if (map != null) {
                map.addMapObject(customMarker);
            } else {
                mLogTextView.setText("ERROR: map has NULL..");
            }
        } catch (IOException e) { }
    }

    private void addArrayOfMarkers() {
        ArrayList<MapObject> customMarkers = new ArrayList<>();

        MapMarker marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(59.9386, 30.3141));  //Saint Petersburg
        marker.setTitle("Санкт-Петербург");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(55.7522, 37.6156));  //Moscow
        marker.setTitle("Москва");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(68.9792, 33.0925));  //Murmansk
        marker.setTitle("Мурманск");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(47.2313, 39.7233));  //Rostov-on-Don
        marker.setTitle("Ростов-на-Дону");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(55.0415, 82.9346));  //Novosibirsk
        marker.setTitle("Новосибирск");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(54.9924, 73.3686));  //Omsk
        marker.setTitle("Омск");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(56.0184, 92.8672));  //Krasnoyarsk
        marker.setTitle("Красноярск");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(62.0339, 129.733));  //Yakutsk
        marker.setTitle("Якутск");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(58.0105, 56.2502));  //Perm
        marker.setTitle("Пермь");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(54.7431, 55.9678));  //Ufa
        marker.setTitle("Уфа");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(45.0448, 38.976));  //Krasnodar
        marker.setTitle("Краснодар");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(43.1056, 131.874));  //Vladivostok
        marker.setTitle("Владивосток");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(53.2001, 50.15));  //Samara
        marker.setTitle("Самара");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(46.3497, 48.0408));  //Astrakhan
        marker.setTitle("Астрахань");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(51.672, 39.1843));  //Voronej
        marker.setTitle("Воронеж");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(45.0428, 41.9734));  //Stavropol
        marker.setTitle("Ставрополь");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        marker = new MapMarker();
        marker.setCoordinate(new GeoCoordinate(52.0317, 113.501));  //Chita
        marker.setTitle("Чита");
        marker.setDescription("Информация");
        customMarkers.add(marker);

        map.addMapObjects(customMarkers);
    }
}
