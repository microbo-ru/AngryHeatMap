package com.example.angryheatmap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // All capitals names of the Russian regions
    private String[] regionsCapitals = new String[]{"moscow", "saint-petersburg", "rostov-na-donu", "maykop", "ufa", "omsk", "abakan", "samara",
            "yakutsk", "gorno-altaysk", "ulan-ude", "makhachkala", "magas", "nal-chik", "elista", "cherkessk", "petrozavodsk", "syktyvkar", "simferopol",
            "yoshkar-ola", "saransk", "vladikavkaz", "kazan", "kyzyl", "izhevsk", "groznyy", "cheboksary", "barnaul", "chita", "petropavlovsk-kamchatsky",
            "krasnodar", "krasnoyarsk", "perm", "vladivostok", "stavropol", "khabarovsk", "blagoveshchensk", "arkhangel-sk", "astrakhan", "belgorod",
            "bryansk", "vladimir", "volgograd", "vologda", "voronezh", "ivanovo", "irkutsk", "kaliningrad", "kaluga", "kemerovo", "kirov", "kostroma",
            "kurgan", "kursk", "lipetsk", "magadan", "murmansk", "nizhniy-novgorod", "velikiy-novgorod", "novosibirsk", "orenburg", "orel", "penza",
            "pskov", "ryazan", "saratov", "yuzhno-sakhalinsk", "yekaterinburg", "smolensk", "tambov", "tver", "tomsk", "tula", "tyumen", "ulyanovsk",
            "chelyabinsk", "yaroslavl", "sevastopol", "birobidzhan", "nar-yan-mar", "khanty-mansiysk", "anadyr", "salekhard"};
    // All capitals names of the Russian regions in Russian
    private String[] regionsCapitalsRus = new String[]{"Москва", "Санкт-Петербург", "Ростов-на-Дону", "Майкоп", "Уфа", "Омск", "Абакан", "Самара",
            "Якутск", "Горно-Алтайск", "Улан-Удэ", "Махачкала", "Магас", "Нальчик", "Элиста", "Черкесск", "Петрозаводск", "Сыктывкар", "Симферополь",
            "Йошкар-Ола", "Саранск", "Владикавказ", "Казань", "Кызыл", "Ижевск", "Грозный", "Чебоксары", "Барнаул", "Чита", "Петропавловск-Камчатский",
            "Краснодар", "Красноярск", "Пермь", "Владивосток", "Ставрополь", "Хабаровск", "Благовещенск", "Архангельск", "Астрахань", "Белгород",
            "Брянск", "Владимир", "Волгоград", "Вологда", "Воронеж", "Иваново", "Иркутск", "Калининград", "Калуга", "Кемерово", "Киров", "Кострома",
            "Курган", "Курск", "Липецк", "Магадан", "Мурманск", "Нижний-Новгород", "Великий-Новгород", "Новосибирск", "Оренбург", "Орёл", "Пенза",
            "Псков", "Рязань", "Саратов", "Южно-Сахалинск", "Екатеринбург", "Смоленск", "Тамбов", "Тверь", "Томск", "Тула", "Тюмень", "Ульяновск",
            "Челябинск", "Ярославль", "Севастополь", "Биробиджан", "Нарьян-мар", "Ханты-мансийск", "Анадырь", "Салехард"};
    // The constant part of the URL address
    private final String URL_STRING = "https://time-in.ru/coordinates/";
    //The timeout to connect the URL_STRING
    private final String URL_TIMEOUT = "6000";
    //File name for store the serialized data
    private final String FILE_NAME = "map_data.dat";
    //Output stream to save the serialized data
    private ObjectOutputStream objectOutputStream;
    //Input stream to load the serialized data
    private ObjectInputStream objectInputStream;
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
    private volatile HashMap<MapOverlay, Boolean> overlayMap;
    //ArrayList to keep the MapOverlay objects with markers nums
    private volatile ArrayList<MapOverlay> overlayListNums;
    //ArrayList to keep the MapOverlay objects with cities names
    private volatile ArrayList<MapOverlay> overlayListCities;

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
        overlayMap = new HashMap<>();
        overlayListNums = new ArrayList<>();
        overlayListCities = new ArrayList<>();
        initialize();
    }

    private void initialize() {
        // Search for the map fragment to finish setup by calling init().
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapfragment);
        // Set up disk cache path for the map service for this application
        // It is recommended to use a path under your application folder for storing the disk cache
        boolean success = MapSettings.setIsolatedDiskCacheRootPath(
                getApplicationContext().getExternalFilesDir(null) + File.separator + ".here-maps",
                "INTENT_NAME"); /* ATTENTION! Do not forget to update {YOUR_INTENT_NAME} */
        if (!success) {
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

    class TestTheInternetResource extends AsyncTask<String, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            mLogTextView.setText("Waiting for connection to be established.");
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL myUrl = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) myUrl.openConnection();
                urlConnection.setReadTimeout(Integer.parseInt(strings[1]));
                urlConnection.setConnectTimeout(Integer.parseInt(strings[1]));
                int responseCode = urlConnection.getResponseCode();
                urlConnection.disconnect();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                try {
                    objectOutputStream = new ObjectOutputStream(openFileOutput(FILE_NAME, Context.MODE_PRIVATE));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < regionsCapitals.length; i++) {
                    new ParsingPageTask().execute(URL_STRING + regionsCapitals[i], regionsCapitalsRus[i]);
                }
            } else {
                mLogTextView.setText("Connection with the resource is not established.");
                try {
                    objectInputStream = new ObjectInputStream(openFileInput(FILE_NAME));
                    for (int i = 0; i < regionsCapitals.length; i++) {
                        try {
                            DataForSerialization dfs = (DataForSerialization)objectInputStream.readObject();
                            String[] strParams = new String[] {dfs.getLatitude(), dfs.getLongitude(), dfs.getHeight(), dfs.getRusCityName() + "*"};
                            new ParsingPageTask().prepareDataForMap(strParams);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    objectInputStream.close();
                    mainLayer.removeView(mLogTextView);
                } catch (FileNotFoundException e) {
                    mLogTextView.setText("File Not Found Exception!");
                    mLogTextView.setTextColor(Color.RED);
                } catch (IOException e) {
                    mLogTextView.setText("End Of File Exception!");
                    mLogTextView.setTextColor(Color.RED);
                }
            }
        }
    }

    private void addArrayOfMarkers() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            new TestTheInternetResource().execute(URL_STRING, URL_TIMEOUT);
        } else {
            mLogTextView.setText("Connection with Internet is not established.");
            try {
                objectInputStream = new ObjectInputStream(openFileInput(FILE_NAME));
                for (int i = 0; i < regionsCapitals.length; i++) {
                    try {
                        DataForSerialization dfs = (DataForSerialization)objectInputStream.readObject();
                        String[] strParams = new String[] {dfs.getLatitude(), dfs.getLongitude(), dfs.getHeight(), dfs.getRusCityName() + "^"};
                        new ParsingPageTask().prepareDataForMap(strParams);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                objectInputStream.close();
                mainLayer.removeView(mLogTextView);
            } catch (FileNotFoundException e) {
                mLogTextView.setText("File Not Found Exception!");
                mLogTextView.setTextColor(Color.RED);
            } catch (IOException e) {
                mLogTextView.setText("End Of File Exception!");
                mLogTextView.setTextColor(Color.RED);
            }
        }
    }

    private void makeMarkersTouchable() {
        // Create a gesture listener and add it to the SupportMapFragment
        MapGesture.OnGestureListener listener = new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
            @Override
            public boolean onMapObjectsSelected(List<ViewObject> list) {
                if (list.size() > 1) {
                    for (int i = 0; i < list.size() - 1; ) {
                        list.remove(i);
                    }
                }
                for (ViewObject viewObj : list) {
                    if (viewObj.getBaseType() == ViewObject.Type.USER_OBJECT) {
                        if (((MapObject) viewObj).getType() == MapObject.Type.MARKER) {
                            MapMarker mapMarker = (MapMarker) viewObj;
                            GeoCoordinate coordinates = mapMarker.getCoordinate();
                            double latitude = coordinates.getLatitude();
                            double longitude = coordinates.getLongitude();
                            double altitude = coordinates.getAltitude();
                            for (java.util.Map.Entry<MapOverlay, Boolean> entry : overlayMap.entrySet()) {
                                MapOverlay overlay = entry.getKey();
                                Boolean isShown = entry.getValue();
                                GeoCoordinate overlayCoordinates = overlay.getCoordinate();
                                double overlayLatitude = overlayCoordinates.getLatitude();
                                double overlayLongitude = overlayCoordinates.getLongitude();
                                double overlayAltitude = overlayCoordinates.getAltitude();
                                if (latitude == overlayLatitude && longitude == overlayLongitude && altitude == overlayAltitude) {
                                    if (isShown == false) {
                                        map.addMapOverlay(overlay);
                                        overlayMap.put(overlay, true);
                                    } else if (isShown == true) {
                                        map.removeMapOverlay(overlay);
                                        overlayMap.put(overlay, false);
                                    }
                                    break;
                                }
                            }
                            for (int i = 0; i < overlayListNums.size(); i++) {
                                MapOverlay overlayNum = overlayListNums.get(i);
                                MapOverlay overlayCity = overlayListCities.get(i);
                                GeoCoordinate overlayCoordinates = overlayCity.getCoordinate();
                                double overlayLatitude = overlayCoordinates.getLatitude();
                                double overlayLongitude = overlayCoordinates.getLongitude();
                                double overlayAltitude = overlayCoordinates.getAltitude();
                                if (latitude == overlayLatitude && longitude == overlayLongitude && altitude == overlayAltitude) {
                                    map.removeMapOverlay(overlayNum);
                                    map.addMapOverlay(overlayCity);
                                    overlayListNums.set(i, overlayCity);
                                    overlayListCities.set(i, overlayNum);
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
            return new String[]{latitude, longitude, height, strings[1]};
        }

        @Override
        protected void onProgressUpdate(String... values) {
            synchronized (mLogTextView) {
                mLogTextView.setText(values[0]);
            }
        }

        @Override
        protected void onPostExecute(String[] strings) {
            DataForSerialization dataForSerialization = new DataForSerialization(strings[0], strings[1], strings[2], strings[3]);
            try {
                objectOutputStream.writeObject(dataForSerialization);
                objectOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            prepareDataForMap(strings);
            if (count == regionsCapitals.length) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void prepareDataForMap(String[] strings) {
            GeoCoordinate coordinates = new GeoCoordinate(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]), Double.parseDouble(strings[2]));
            MapMarker marker = new MapMarker();
            marker.setCoordinate(coordinates);
            title1.append(strings[0]);
            title2.append(strings[1]);
            title3.append(strings[2]);
            MapOverlay mapOverlay = new MapOverlay(layout, coordinates);
            MapOverlay titleOverlayNum = getNewOverlay("Маркер №" + (count + 1), coordinates);
            MapOverlay titleOverlayCity = getNewOverlay(strings[3], coordinates);
            synchronized (map) {
                map.addMapObject(marker);
                map.addMapOverlay(titleOverlayNum);
                overlayMap.put(mapOverlay, false);
                overlayListNums.add(titleOverlayNum);
                overlayListCities.add(titleOverlayCity);
                count++;
                if (count == regionsCapitals.length) {
                    mainLayer.removeView(mLogTextView);
                }
            }
        }

        private MapOverlay getNewOverlay(String name, GeoCoordinate coordinates) {
            LinearLayout linearLayout = new LinearLayout(getApplicationContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            TextView titleView = new TextView(getApplicationContext());
            TextView emptyText1 = new TextView(getApplicationContext());
            TextView emptyText2 = new TextView(getApplicationContext());
            TextView emptyText3 = new TextView(getApplicationContext());
            TextView emptyText4 = new TextView(getApplicationContext());
            titleView.setTextColor(Color.DKGRAY);
            titleView.setTypeface(null, Typeface.BOLD);
            titleView.setTag("titleView");
            titleView.setText(name);
            titleView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            linearLayout.addView(titleView);
            linearLayout.addView(emptyText1);
            linearLayout.addView(emptyText2);
            linearLayout.addView(emptyText3);
            linearLayout.addView(emptyText4);
            return new MapOverlay(linearLayout, coordinates);
        }
    }
}

class DataForSerialization implements Serializable {
    private static final long serialVersionUID = 1234567L;
    private String latitude;
    private String longitude;
    private String height;
    private String rusCityName;

    public DataForSerialization(String latitude, String longitude, String height, String rusCityName) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
        this.rusCityName = rusCityName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getHeight() {
        return height;
    }

    public String getRusCityName() {
        return rusCityName;
    }
}
