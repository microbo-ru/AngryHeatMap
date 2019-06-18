package com.example.angryheatmap;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.common.MapSettings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    // All capitals names of the Russian regions
    private String[] regionsCapitals = new String[] {"moscow", "saint-petersburg", "rostov-na-donu", "maykop", "ufa", "omsk", "abakan", "samara",
            "yakutsk", "gorno-altaysk", "ulan-ude", "makhachkala", "magas", "nal-chik", "elista", "cherkessk", "petrozavodsk", "syktyvkar", "simferopol",
            "yoshkar-ola", "saransk", "vladikavkaz", "kazan", "kyzyl", "izhevsk", "groznyy", "cheboksary", "barnaul", "chita", "petropavlovsk-kamchatsky",
            "krasnodar", "krasnoyarsk", "perm", "vladivostok", "stavropol", "khabarovsk", "blagoveshchensk", "arkhangel-sk", "astrakhan", "belgorod",
            "bryansk", "vladimir", "volgograd", "vologda", "voronezh", "ivanovo", "irkutsk", "kaliningrad", "kaluga", "kemerovo", "kirov", "kostroma",
            "kurgan", "kursk", "lipetsk", "magadan", "murmansk", "nizhniy-novgorod", "velikiy-novgorod", "novosibirsk", "orenburg", "orel", "penza",
            "pskov", "ryazan", "saratov", "yuzhno-sakhalinsk", "yekaterinburg", "smolensk", "tambov", "tver", "tomsk", "tula", "tyumen", "ulyanovsk",
            "chelyabinsk", "yaroslavl", "sevastopol", "birobidzhan", "nar-yan-mar", "khanty-mansiysk", "anadyr", "salekhard"};
    // The constant part of the URL address
    private final String URL_STRING = "https://time-in.ru/coordinates/";
    // Logger
    private TextView mLogTextView;
    // main layout of the MainActivity
    private LinearLayout mainLayer;
    // map embedded in the map fragment
    private Map map = null;
    // map fragment embedded in this activity
    private SupportMapFragment mapFragment = null;
    //Counter of MapMarkers created
    private int count = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogTextView = findViewById(R.id.title);
        mainLayer = findViewById(R.id.linearLayOut);
        initialize();
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
                        map.setCenter(new GeoCoordinate(59.9386, 30.3141, 12.0), Map.Animation.NONE); //Set the center in Piter
                        // Set the zoom level to the average between min and max
                        map.setZoomLevel((map.getMaxZoomLevel() + map.getMinZoomLevel()) / 2);
                        // Set all markers on the map
                        addArrayOfMarkers();
                    } else {
                        mLogTextView.setText("ERROR: Cannot initialize Map Fragment");
                    }
                }
            });
        }
    }

    private void addArrayOfMarkers() {
        for (int i = 0; i < regionsCapitals.length; i++) {
            new ParsingPageTask().execute(URL_STRING + regionsCapitals[i]);
        }
    }

    class ParsingPageTask extends AsyncTask<String, String, String[]> {

        @Override
        protected String[] doInBackground(String... strings) {
            String latitude = null;
            String longitude = null;
            String height = null;
            Document doc = null;
            try {
                doc = Jsoup.connect(strings[0]).get();
                publishProgress("Добавляем на карту город: " + strings[0].substring(URL_STRING.length()));
                Element element = doc.select("div.coordinates-city-info").first();
                Elements divs = element.select("div");
                String s1 = divs.get(1).text();
                String s2 = divs.get(2).text();
                latitude = s1.substring(s1.indexOf(":") + 2, s1.lastIndexOf(",")); //широта
                longitude = s1.substring(s1.lastIndexOf(",") + 2);                 //долгота
                height = s2.substring(s2.indexOf(":") + 2, s2.indexOf(" метр"));      //высота
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new String[]{latitude, longitude, height};
        }

        @Override
        protected void onProgressUpdate(String... values) {
            synchronized (mLogTextView) {
                mLogTextView.setText(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            MapMarker marker = new MapMarker();
            marker.setCoordinate(new GeoCoordinate(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2])));
            synchronized (map) {
                map.addMapObject(marker);
                count++;
                if (count == regionsCapitals.length) {
                    mainLayer.removeView(mLogTextView);
                }
            }
        }
    }
}
