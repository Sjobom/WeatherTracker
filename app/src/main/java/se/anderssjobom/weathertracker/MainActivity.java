package se.anderssjobom.weathertracker;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int GPS_ZOOM_PERMISSION_CODE = 1; //Application specific request code to match with a result reported to onRequestPermissionsResult(int, String[], int[]).
    GoogleMap mMap; //Kartreferens, initialiseras i initMap
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private GoogleApiClient mLocationClient; //För GPS
    private Marker placeMarker;
    private Circle placeCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (servicesOK()) {
            setContentView(R.layout.activity_map);
            //Initialisera kartan
            initMap();
            //Initialisera GPS
            mLocationClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(AppIndex.API).build();
            mLocationClient.connect();
        } else {
            setContentView(R.layout.activity_main);
        }
    }

    protected void onSaveInstanceState (Bundle outState){
        //TODO - spara kartans nuvarande position
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
        mMap.animateCamera(update);
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
                    mMap.animateCamera(update);
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
        //Lägger till cirkel och marker vid långklick
        if (mMap != null){

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
                    Geocoder gc = new Geocoder(MainActivity.this);
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
                    placeMarker = MainActivity.this.createMarker(latLng);
                }
            });
        }
    }

    // De tre funktionerna nedan krävs för GPS
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
