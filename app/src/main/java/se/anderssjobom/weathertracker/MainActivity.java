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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (servicesOK()) {
            setContentView(R.layout.activity_map);
            //Initialisera kartan
            initMap();
            //Initialisera GPS
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
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
        if (currentLocation == null) {
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
                    Log.d("PERMISSION", "GRANTED");
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

            if (mMap != null){
                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        Geocoder gc = new Geocoder(MainActivity.this);
                        List<android.location.Address> list = null;
                        try {
                            list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        placeMarker = MainActivity.this.createMarker(latLng, list);
                    }
                });
            }
        }
    }

    private Marker createMarker(LatLng latlng, List<android.location.Address> address) {
     /*   if (marker != null){
            removeEverything();
        }
*/       MarkerOptions options = new MarkerOptions()
                              .position(latlng)
                              .title(String.valueOf(address.get(0)));


        return mMap.addMarker(options);

    }
   /* private void removeEverything() {
        marker.remove();
        marker = null;
    }*/
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    // De tre funktionerna nedan krävs för GPS
    @Override
    public void onConnected(@Nullable Bundle bundle) {
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

        LatLng latLng = new LatLng(
                currentLocation.getLatitude(),
                currentLocation.getLongitude()
        );
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                latLng, 15
        );
        mMap.animateCamera(update);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        mLocationClient.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://se.anderssjobom.weathertracker/http/host/path")
        );
        AppIndex.AppIndexApi.start(mLocationClient, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://se.anderssjobom.weathertracker/http/host/path")
        );
        AppIndex.AppIndexApi.end(mLocationClient, viewAction);
        mLocationClient.disconnect();
    }
}
