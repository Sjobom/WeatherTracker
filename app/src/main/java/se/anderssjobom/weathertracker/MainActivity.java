package se.anderssjobom.weathertracker;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;
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
    private GoogleApiClient mGoogleApiClient; //För AutoCompleteLocationSearch
    private Marker placeMarker;
    private Circle placeCircle;
    private Polyline placePolyline;
    private Polygon placePolygon;
    private List<LatLng> tempPolyLatLngs;
    private FrameLayout fram_map;
    private Button btn_draw_State;
    private Boolean Is_MAP_Moveable = true; // to detect map is movable


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (servicesOK()) {
            setContentView(R.layout.activity_map);
            //Initialisera kartan
            initMap();
            //Initialisera ritfunktionen
            initDrawFrame();
            //Initialisera GPS
            mLocationClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(AppIndex.API).build();
            mLocationClient.connect();

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
                    placeMarker = MainActivity.this.createMarker(latLng);
                }
                @Override
                public void onError(Status status) {
                    // TODO: Handle the error.
                }
            });

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
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if(placeMarker != null){placeMarker.remove();}
                    placeMarker = MainActivity.this.createMarker(latLng);
                }
            });
        }
    }

    private void initDrawFrame() {
        fram_map = (FrameLayout) findViewById(R.id.fram_map);
        btn_draw_State = (Button) findViewById(R.id.btn_draw_State);
        tempPolyLatLngs = new LinkedList<LatLng>();
        btn_draw_State.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Is_MAP_Moveable != true) {
                    Is_MAP_Moveable = true;
                    btn_draw_State.getBackground().setColorFilter(0xFFD3D3D3, PorterDuff.Mode.MULTIPLY); //Grå knapp
                    createOnTouchListener(false);
                    if (placePolyline != null) {placePolyline.remove();}

                } else {
                    Is_MAP_Moveable = false;
                    btn_draw_State.getBackground().setColorFilter(0xFF009688, PorterDuff.Mode.MULTIPLY); //Grön knapp
                    createOnTouchListener(true);

                    if (placePolygon != null) {
                        placePolygon.remove();
                    }
                    //Återställ listan
                    if (placePolyline != null) {
                        placePolyline.remove();
                        tempPolyLatLngs = new LinkedList<LatLng>();
                    }
                }
            }
        });
    }
    public void Draw_Polyline() {
        if (placePolyline != null) {placePolyline.remove();}
        PolylineOptions polylineOptions = new PolylineOptions();
        polylineOptions.addAll(tempPolyLatLngs);
        polylineOptions.color(0xff009688);
        polylineOptions.width(6);
        placePolyline = mMap.addPolyline(polylineOptions);
    }
    public void Draw_Polygon() {
        if (placePolygon != null) {placePolygon.remove();}
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.addAll(tempPolyLatLngs);
        polygonOptions.strokeColor(0xff009688);
        polygonOptions.strokeWidth(6);
        polygonOptions.fillColor(0x1A0000FF);
        placePolygon = mMap.addPolygon(polygonOptions);
    }

    //Om true - skapar en onTouchListener för framelayouten över kartan
    //Om false - återställer till en tom onTouchListener för framelayouten över kartan
    private void createOnTouchListener(boolean state){
        if(state){
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
                            tempPolyLatLngs.add(new LatLng(latitude, longitude));
                            break;

                        case MotionEvent.ACTION_MOVE:
                            // finger moves on the screen
                            tempPolyLatLngs.add(new LatLng(latitude, longitude));
                            Draw_Polyline();
                            break;

                        case MotionEvent.ACTION_UP:
                            // finger leaves the screen
                            if (!tempPolyLatLngs.isEmpty()){Draw_Polygon();}
                            //Återställ kartrörligheten, onTouchListenern och knappen när användaren släpper fingret
                            Is_MAP_Moveable = true;
                            btn_draw_State.getBackground().setColorFilter(0xFFD3D3D3, PorterDuff.Mode.MULTIPLY); //Grå knapp
                            createOnTouchListener(false);
                            break;
                    }
                    if (Is_MAP_Moveable == false) {return true;}
                    else {return false;}
                }
            });
        } else{
            fram_map.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }
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
