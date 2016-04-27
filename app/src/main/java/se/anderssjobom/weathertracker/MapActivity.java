package se.anderssjobom.weathertracker;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import se.anderssjobom.weathertracker.model.WeatherParameters;

public class MapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private String LOG = "MapActivity";
    private static final int GPS_ZOOM_PERMISSION_CODE = 1; //Application specific request code to match with a result reported to onRequestPermissionsResult(int, String[], int[]).
    GoogleMap mMap; //Kartreferens, initialiseras i initMap
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private GoogleApiClient mLocationClient; //För GPS
    private GoogleApiClient mGoogleApiClient; //För AutoCompleteLocationSearch
    private Marker placeMarker;
    private Circle placeCircle;
    private List<Polygon> placePolygons;
    private List<Marker> polygonTrashbins;
    private List<Polyline> tempPolylines;
    private LatLng curLatLng;
    private LatLng prevLatLng;
    private FrameLayout fram_map;
    private FloatingActionButton enterDrawStateButton;
    private FloatingActionButton exitDrawStateButton;
    private FloatingActionButton doneButton;
    private Boolean isMapMoveable = true; // to detect map is movable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG, "onCreate");

        if (servicesOK()) {
            setContentView(R.layout.activity_map);
            //Initialisera kartan
            initMap();
            //Initialisera ritfunktionen
            initDrawFrame();
            //Initialisera GPS
            if (savedInstanceState == null) {
                Log.d(LOG, "NULL");
                mLocationClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(AppIndex.API).build();
                mLocationClient.connect();
                //Initialisera sökfunktionen
                initSearch();
            } else{
                Log.d(LOG, Thread.currentThread().getStackTrace().toString());
                String s = savedInstanceState.getString("STRING");
                Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                CameraPosition position = savedInstanceState.getParcelable("MAP_POSITION");
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                mMap.moveCamera(update);
            }

        }
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.done_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MapActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        Log.d(LOG, "onStart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(LOG, "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(LOG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(LOG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        Log.d(LOG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onSaveInstanceState (Bundle savedInstanceState){
        savedInstanceState.putString("STRING", "test");
        savedInstanceState.putParcelable("MAP_POSITION", mMap.getCameraPosition());
        super.onSaveInstanceState(savedInstanceState);
        Log.d("onSaveInstanceState", "SAVED THE STATE");
    }
    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        //TODO - återställ kartans nuvarande position
    }

    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog =
                    GooglePlayServicesUtil.getErrorDialog(isAvailable, this, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public void setCurrentLocation() {
        if(ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    GPS_ZOOM_PERMISSION_CODE);
            return;
        }
        Location currentLocation = LocationServices.FusedLocationApi
                .getLastLocation(mLocationClient);
        LatLng latLng;
        int zoomLevel;
        //TODO - Ibland blir currentLocation null, kanske använda annan lösning?
        if (currentLocation != null) {
            latLng = new LatLng(
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude()
            );
            zoomLevel = 12;
        } else {
            latLng = new LatLng(62.386504, 16.320447);
            zoomLevel = 5;
        }
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                latLng, zoomLevel
        );
        mMap.moveCamera(update);
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GPS_ZOOM_PERMISSION_CODE: {
                if (permissions.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setCurrentLocation();
                }else{
                    LatLng latLng = new LatLng(62.386504, 16.320447);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                            latLng, 5
                    );
                    mMap.moveCamera(update);
                }
            }
        }
    }

    private void initMap(){
        if (mMap == null){
            MapFragment mapFragment =
                    (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this); //skapar kartan och anropar onMapReady när kartan är klar
        }
    }

    // Skapar en generell marker på kartan
    private Marker createMarker(LatLng latlng) {
        MarkerOptions options = new MarkerOptions()
                              .position(latlng);
        return mMap.addMarker(options);
    }

    //Skapar specifik cirkel utifrån placeMarker och given radie
    private void createCircle(int rad) {
        CircleOptions options = new CircleOptions()
                .center(placeMarker.getPosition())
                .radius(rad)
                .fillColor(0x330000FF);
        if(placeCircle != null){placeCircle.remove();}
        placeCircle = mMap.addCircle(options);
    }

    //Körs när mMap har initialierats klart i initMap
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (polygonTrashbins.contains(marker)) {
                    int index = polygonTrashbins.indexOf(marker);
                    placePolygons.get(index).remove();
                    marker.remove();
                    placePolygons.remove(index);
                    polygonTrashbins.remove(index);
                    if(placePolygons.isEmpty()){
                        doneButton.setVisibility(View.INVISIBLE);
                    }
                    return true;
                } else if (marker == marker) {
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View v = getLayoutInflater().inflate(R.layout.info_window, null);

                            String weather = "Sunny";
                            TextView tvLocal = (TextView) v.findViewById(R.id.tvLocality);//Länka till XML filen info_window
                            TextView tvTemp = (TextView) v.findViewById(R.id.tvTemp);
                            TextView tvWind = (TextView) v.findViewById(R.id.tvWind);
                            ImageView tvImage = (ImageView) v.findViewById(R.id.imageView1);

                            LatLng latLng = marker.getPosition();         //Vi tar markörerns koordinater och använder geocoder för att få
                            Geocoder gc = new Geocoder(MapActivity.this); //namnet på staden som markören pekar på
                            List<android.location.Address> list = null;        //Få namn på område
                            try {
                                list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            if (list.isEmpty()){   //Ifall vi inte får någon information om området
                                tvLocal.setText("No information"); //TODO maybe improve this to make it look better
                                return v;
                            }
                            android.location.Address address = list.get(0);

                            switch(weather){ //Beroende på vad för väder vi har så skriver vi motsvarande ikon till markörfönstret
                                case "Sunny":
                                    tvImage.setImageResource(R.drawable.ic_sunny);
                                    break;
                                case "Cloudy":
                                    tvImage.setImageResource(R.drawable.ic_cloudy);
                                    break;
                                case "Haze":
                                    tvImage.setImageResource(R.drawable.ic_haze);
                                    break;
                                case "Rain":
                                    tvImage.setImageResource(R.drawable.ic_rain);
                                    break;
                                case "Slight rain":
                                    tvImage.setImageResource(R.drawable.ic_slight_rain);
                                    break;
                                case "Snow":
                                    tvImage.setImageResource(R.drawable.ic_snow);
                                    break;
                                case "Storm":
                                    tvImage.setImageResource(R.drawable.ic_thunderstorms);
                                    break;
                                case "Slight cloud":
                                    tvImage.setImageResource(R.drawable.ic_mostly_cloudy);
                                    break;
                            }
                            tvLocal.setText(address.getLocality());//Vi sätter text som ska förekomma i markör-fönster
                            tvTemp.setText("Temperature: 5 C"); //Vill vi har mer än 4 linjer av text kan vi ändra det i XML filen
                            tvWind.setText("WindSpeed: 5 m/s East");

                            return v;
                        }
                    });
                    //TODO - implementera hantering av resultat eller väder!
                    return false;
                } else {
                    return false;
                }
            }
        });
    }

    private void initDrawFrame() {
        fram_map = (FrameLayout) findViewById(R.id.fram_map);
        enterDrawStateButton = (FloatingActionButton) findViewById(R.id.enter_draw_state_button);
        exitDrawStateButton = (FloatingActionButton) findViewById(R.id.exit_draw_state_button);
        doneButton = (FloatingActionButton) findViewById(R.id.done_button);
        tempPolylines = new ArrayList<Polyline>();
        placePolygons = new ArrayList<Polygon>();
        polygonTrashbins = new ArrayList<Marker>();
        createOnTouchListener();
        enterDrawStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    isMapMoveable = false;
                    enterDrawStateButton.hide();
                    exitDrawStateButton.show();
                    tempPolylines = new ArrayList<Polyline>();
            }
        });
        exitDrawStateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMapMoveable = true;
                exitDrawStateButton.hide();
                enterDrawStateButton.show();
                tempPolylines = null;
            }
        });
    }

    private void initSearch(){
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                        latLng, 12
                );
                mMap.animateCamera(update);
                if(placeMarker != null){placeMarker.remove();}
                placeMarker = MapActivity.this.createMarker(latLng);
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
    }

    public void drawPolyline() {
        PolylineOptions polylineOptions = new PolylineOptions();
        if (prevLatLng == null){prevLatLng = curLatLng;}
        polylineOptions.add(prevLatLng, curLatLng);
        polylineOptions.color(0xff009688);
        polylineOptions.width(5);
        tempPolylines.add(mMap.addPolyline(polylineOptions));
    }

    public void drawPolygon() {
        PolygonOptions polygonOptions = new PolygonOptions();
        List tempPolyLatLngs = new LinkedList<LatLng>();
        for (int i = 0; i < tempPolylines.size(); i++){
            tempPolyLatLngs.add(tempPolylines.get(i).getPoints().get(0));
        }
        polygonOptions.addAll(tempPolyLatLngs);
        polygonOptions.strokeColor(0xff009688);
        polygonOptions.strokeWidth(7);
        polygonOptions.fillColor(0x1A0000FF);
        placePolygons.add(mMap.addPolygon(polygonOptions));
        Marker marker = createMarker((LatLng) tempPolyLatLngs.get(0));
        marker.setIcon(BitmapDescriptorFactory.fromBitmap
                (resizeMapIcons("ic_delete_white_green_48px", 300, 300)));
        marker.setAnchor(0.5f, 0.5f);
        polygonTrashbins.add(marker);
        if (placePolygons.size() == 1){
            doneButton.setVisibility(View.VISIBLE);
        }
        List<LatLng> list = Weather.findWeather(new WeatherParameters(), placePolygons,
                GregorianCalendar.getInstance(), GregorianCalendar.getInstance());
        for(int i = 0; i < list.size(); i++){
            createMarker(list.get(i));
        }
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource
                (getResources(),getResources().getIdentifier(iconName, "drawable", getPackageName()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private void removePolylines(){
        if (!tempPolylines.isEmpty()){
            for (int i = 0; i < tempPolylines.size(); i++){
                tempPolylines.get(i).remove();
            }
        }
    }

    //Skapar en onTouchListener för framelayouten över kartan
    private void createOnTouchListener(){
            fram_map.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    float x = event.getX();
                    float y = event.getY();

                    int x_co = Math.round(x);
                    int y_co = Math.round(y);

                    Point x_y_points = new Point(x_co, y_co);

                    LatLng latLng = mMap.getProjection().fromScreenLocation(x_y_points);
                    double latitude = latLng.latitude;
                    double longitude = latLng.longitude;

                    int eventaction = event.getAction();
                    switch (eventaction) {
                        case MotionEvent.ACTION_DOWN:
                            // finger touches the screen
                            prevLatLng = curLatLng;
                            curLatLng = new LatLng(latitude, longitude);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            // finger moves on the screen
                            prevLatLng = curLatLng;
                            curLatLng = new LatLng(latitude, longitude);
                            drawPolyline();
                            break;

                        case MotionEvent.ACTION_UP:
                            // finger leaves the screen
                            if (!tempPolylines.isEmpty()){
                                drawPolygon();}
                            removePolylines();
                            //Återställ kartrörligheten, onTouchListenern och knappen när användaren släpper fingret
                            isMapMoveable = true;
                            exitDrawStateButton.hide();
                            enterDrawStateButton.show();
                            break;
                    }
                    if (isMapMoveable == false) {return true;}
                    else {return false;}
                }
            });
    }

    // De tre funktionerna nedan kan användas för GPS
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        setCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

/*if (mMap != null){

            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                @Override
                public View getInfoWindow(Marker marker) {//Definera markör-fönsters egenskaper, vi returnerar null
                    return null;                           //eftersom vi nöjer oss med default
                }

                @Override
                public View getInfoContents(Marker marker) {//Definera innehållet i markör-fönster
                    View v = getLayoutInflater().inflate(R.layout.info_window, null);

                    TextView tvLocal = (TextView) v.findViewById(R.id.tvLocality);//Länka till XML filen info_window
                    TextView tvLat = (TextView) v.findViewById(R.id.tvLat);
                    TextView tvLng = (TextView) v.findViewById(R.id.tvLng);
                    TextView tvSnipp = (TextView) v.findViewById(R.id.tvSnippet);

                    LatLng latLng = marker.getPosition();
                    Geocoder gc = new Geocoder(MapActivity.this);
                    List<android.location.Address> list = null;        //Få namn på område
                    try {
                        list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (list.isEmpty()){   //Ifall vi inte får någon information om området
                        tvLocal.setText("No information"); //TODO maybe improve this to make it look better
                        return v;
                    }
                    android.location.Address address = list.get(0);

                    tvLocal.setText("You shant escape my chungus");//Vi sätter text som ska förekomma i markör-fönster
                    tvLat.setText(address.getAddressLine(0)); //Vill vi har mer än 4 linjer av text kan vi ändra det i XML filen
                    tvLng.setText(address.getLocality());
                    tvSnipp.setText(address.getCountryName());

                    return v;
            }
            });
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(placeMarker != null){placeMarker.remove();}
                    placeMarker = MapActivity.this.createMarker(latLng);
                }
            });
        }*/

