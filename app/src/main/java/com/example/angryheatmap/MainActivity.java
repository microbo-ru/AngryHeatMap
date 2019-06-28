package com.example.angryheatmap;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.here.android.mpa.common.GeoCoordinate;
import com.here.android.mpa.common.OnEngineInitListener;
import com.here.android.mpa.common.ViewObject;
import com.here.android.mpa.mapping.Map;
import com.here.android.mpa.mapping.MapGesture;
import com.here.android.mpa.mapping.MapMarker;
import com.here.android.mpa.mapping.MapObject;
import com.here.android.mpa.mapping.MapOverlay;
import com.here.android.mpa.mapping.SupportMapFragment;
import com.here.android.mpa.common.MapSettings;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
    // All capitals names of the Russian regions in Russian
    private String[] regionsCapitalsRus = new String[] {"Москва", "Санкт-Петербург", "Ростов-на-Дону", "Майкоп", "Уфа", "Оиск", "Абакан", "Самара",
            "Якутск", "Горно-Алтайск", "Улан-Удэ", "Махачкала", "Магас", "Нальчик", "Элиста", "Черкесск", "Петрозаводск", "Сыктывкар", "Симферополь",
            "Йошкар-Ола", "Саранск", "Владикавказ", "Казань", "Кызыл", "Ижевск", "Грозный", "Чебоксары", "Барнаул", "Чита", "Петропавловск-Камчатский",
            "Краснодар", "Красноярск", "Пермь", "Владивосток", "Ставрополь", "Хабаровск", "Благовещенск", "Архангельск", "Астрахань", "Белгород",
            "Брянск", "Владимир", "Волгоград", "Вологда", "Воронеж", "Иваново", "Иркутск", "Калининград", "Калуга", "Кемерово", "Киров", "Кострома",
            "Курган", "Курск", "Липецк", "Магадан", "Мурманск", "Нижний-Новгород", "Великий-Новгород", "Новосибирск", "Оренбург", "Орёл", "Пенза",
            "Псков", "Рязань", "Саратов", "Южно-Сахалинск", "Екатеринбург", "Смоленск", "Тамбов", "Тверь", "Томск", "Тула", "Тюмень", "Ульяновск",
            "Челябинск", "Ярославль", "Севастополь", "Биробиджан", "Нарьян-мар", "Ханты-мансийск", "Анадырь", "Салехард"};
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
    //HashMap to keep the MapOverlay objects references
    private volatile HashMap<MapOverlay, Boolean> overlayList;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;
        switch (id) {
            case R.id.actionSettings:
                intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.actionVolume:
                intent = new Intent(getApplicationContext(), VolumeActivity.class);
                startActivity(intent);
                return true;
            case R.id.actionFire:
                intent = new Intent(getApplicationContext(), FireActivity.class);
                startActivity(intent);
                return true;
            case R.id.actionActions:
                intent = new Intent(getApplicationContext(), ActionsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onSettingsMenuClick(MenuItem item) {
        Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLogTextView = findViewById(R.id.title);
        mainLayer = findViewById(R.id.linearLayOut);
        overlayList = new HashMap<>();
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
                        makeMarkersTouchable();
                    } else {
                        mLogTextView.setText("ERROR: Cannot initialize Map Fragment");
                    }
                }
            });
        }
    }

    private void addArrayOfMarkers() {
        for (int i = 0; i < regionsCapitals.length; i++) {
            new ParsingPageTask().execute(URL_STRING + regionsCapitals[i], regionsCapitalsRus[i]);
        }
    }

    private void makeMarkersTouchable() {
        // Create a gesture listener and add it to the SupportMapFragment
        MapGesture.OnGestureListener listener = new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
            @Override
            public boolean onMapObjectsSelected(List<ViewObject> list) {
                if (list.size() > 1) {
                    for (int i = 0; i < list.size() - 1;) {
                        list.remove(i);
                    }
                }
                for (ViewObject viewObj : list) {
                    if (viewObj.getBaseType() == ViewObject.Type.USER_OBJECT) {
                        if (((MapObject)viewObj).getType() == MapObject.Type.MARKER) {
                            MapMarker mapMarker = (MapMarker)viewObj;
                            GeoCoordinate coordinates = mapMarker.getCoordinate();
                            double latitude = coordinates.getLatitude();
                            double longitude = coordinates.getLongitude();
                            double altitude = coordinates.getAltitude();
                            for (java.util.Map.Entry<MapOverlay, Boolean> entry : overlayList.entrySet()) {
                                MapOverlay overlay = entry.getKey();
                                Boolean isShown = entry.getValue();
                                GeoCoordinate overlayCoordinate = overlay.getCoordinate();
                                double overlayLatitude = overlayCoordinate.getLatitude();
                                double overlayLongitude = overlayCoordinate.getLongitude();
                                double overlayAltitude = overlayCoordinate.getAltitude();
                                if (latitude == overlayLatitude && longitude == overlayLongitude && altitude == overlayAltitude) {
                                    if (isShown == false) {
                                        //overlay.getView().findViewById(R.id.textOverlayCenter).setVisibility(View.INVISIBLE);
                                        map.addMapOverlay(overlay);
                                        overlayList.put(overlay, true);
                                    } else if (isShown == true) {
                                        map.removeMapOverlay(overlay);
                                        overlayList.put(overlay, false);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
                // return false to allow the map to handle this callback also
                return false;
            }
        };
        //adding the listener it to the SupportMapFragment (mapFragment)
        if (mapFragment != null) {
            mapFragment.getMapGesture().addOnGestureListener(listener, Integer.MAX_VALUE, true);
        } else {
            mLogTextView.setText("ERROR: Map Fragment equals NULL..");
        }
    }

    class ParsingPageTask extends AsyncTask<String, String, String[]> {
        private LayoutInflater inflater;
        private View layout;
        private TextView title1;
        private TextView title2;
        private TextView title3;
        {
            inflater = getLayoutInflater();
            layout = inflater.inflate(R.layout.map_overlay_layout, (ConstraintLayout) findViewById(R.id.overlay_layout));
            title1 = layout.findViewById(R.id.textOverlayUp);
            title2 = layout.findViewById(R.id.textOverlayCenter);
            title3 = layout.findViewById(R.id.textOverlayDown);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            String latitude = null;
            String longitude = null;
            String height = null;
            Document doc;
            try {
                doc = Jsoup.connect(strings[0]).get();
                publishProgress("Добавляем на карту город: " + strings[1]);
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
            GeoCoordinate coordinates = new GeoCoordinate(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]));
            MapMarker marker = new MapMarker();
            marker.setCoordinate(coordinates);
            title1.append(strings[0]);
            title2.append(strings[1]);
            title3.append(strings[2]);
            MapOverlay mapOverlay = new MapOverlay(layout, coordinates);
            LinearLayout linearLayout = new LinearLayout(getApplicationContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView emptyText1 = new TextView(getApplicationContext());
            TextView emptyText2 = new TextView(getApplicationContext());
            TextView emptyText3 = new TextView(getApplicationContext());
            TextView emptyText4 = new TextView(getApplicationContext());
            TextView titleView = new TextView(getApplicationContext());
            titleView.setTextColor(Color.DKGRAY);
            titleView.setTypeface(null, Typeface.BOLD);
            titleView.setText("Маркер №" + (count + 1));
            linearLayout.addView(titleView);
            linearLayout.addView(emptyText1);
            linearLayout.addView(emptyText2);
            linearLayout.addView(emptyText3);
            linearLayout.addView(emptyText4);
            MapOverlay titleOverlay = new MapOverlay(linearLayout, coordinates);
            synchronized (map) {
                map.addMapObject(marker);
                map.addMapOverlay(titleOverlay);
                overlayList.put(mapOverlay, false);
                count++;
                if (count == regionsCapitals.length) {
                    mainLayer.removeView(mLogTextView);
                }
            }
        }
    }
}

