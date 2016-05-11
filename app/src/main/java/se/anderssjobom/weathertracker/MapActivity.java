package se.anderssjobom.weathertracker;

import android.Manifest;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import org.json.JSONException;

import org.joda.time.LocalTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import se.anderssjobom.weathertracker.model.WeatherParameters;

public class MapActivity extends Fragment //TODO
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback{

    private String LOG = "MapActivity";
    private static final int GPS_ZOOM_PERMISSION_CODE = 1; //Application specific request code tmapo match with a result reported to onRequestPermissionsResult(int, String[], int[]).
    GoogleMap mMap; //Kartreferens, initialiseras i initMap
    private static final int ERROR_DIALOG_REQUEST = 9001;
    private GoogleApiClient mLocationClient; //För GPS
    private GoogleApiClient mGoogleApiClient; //För AutoCompleteLocationSearch
    private List<Marker> topResultMarkers;
    private Circle placeCircle;
    public static List<Polygon> placePolygons;
    public static List<Marker> polygonTrashbins;
    private List<Polyline> tempPolylines;
    private LatLng curLatLng;
    private LatLng prevLatLng;
    private FrameLayout fram_map;
    public static FloatingActionButton enterDrawStateButton;
    private FloatingActionButton exitDrawStateButton;
    public static FloatingActionButton doneButton;
    private Boolean isMapMoveable = true; // to detect map is movable
    public static ArrayList<Marker> markers = new ArrayList<Marker>(3);
    public static boolean onResultScreen = false;
    private View thisView;
  //  private PopupWindow resultPopup;


    public MapActivity(){
        //Required empty constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
    //    if (servicesOK()) {
            thisView = inflater.inflate(R.layout.activity_map, container, false);
            //Initialisera kartan
            initMap();
            //Initialisera ritfunktionen
            initDrawFrame();
            //Initialisera GPS
            if (savedInstanceState == null) {
                mLocationClient = new GoogleApiClient.Builder(getActivity())
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(AppIndex.API).build();
                mLocationClient.connect();
                //Initialisera sökfunktionen
                initSearch();
   /*      //   } else{
                Log.d(LOG, Thread.currentThread().getStackTrace().toString());
                String s = savedInstanceState.getString("STRING");
                //Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
                CameraPosition position = savedInstanceState.getParcelable("MAP_POSITION");
                CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
                mMap.moveCamera(update);
         //   }*/

        }
        FloatingActionButton button = (FloatingActionButton) thisView.findViewById(R.id.done_button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Intent intent = new Intent(MapActivity.this, MainActivity.class);
                //startActivity(intent);

                doneButton.hide();
                enterDrawStateButton.hide();
                exitDrawStateButton.hide();
                thisView.findViewById(R.id.card_view).setVisibility(View.INVISIBLE);

                //Flyttade "setTablayoutVisible" till onAnalysisReady

                for (Polygon poly: placePolygons){
                    poly.setVisible(false);
                }
                for (Marker marker : polygonTrashbins){
                    marker.setVisible(false);
                }
               onResultScreen = true;
 //               View popView = View.inflate(v.getContext(), R.layout.resultpopup,null);

                ProgressBar pb = (ProgressBar) getActivity().findViewById(R.id.weatherSearchProgressBar);

                Log.d("ParametersToUse", MapHolder.parametersToUse.toString());

                MapHolder.parametersToUse.put("requestedStartTime", new LocalTime("10:00:00"));
                MapHolder.parametersToUse.put("requestedEndTime", new LocalTime("14:00:00"));


                new Weather(pb, mMap, (OnAnalysisReadyCallback) getActivity()).findWeather(MapHolder.parametersToUse, placePolygons,
                        MainActivity.buttText1, MainActivity.buttText2);
            }
        });


        return thisView;
    }

/*    @Override
    public void onCreate(Bundle savedInstanceState) {
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

                //Intent intent = new Intent(MapActivity.this, MainActivity.class);
                //startActivity(intent);

                doneButton.hide();
                enterDrawStateButton.hide();
                exitDrawStateButton.hide();
                findViewById(R.id.card_view).setVisibility(View.INVISIBLE);

                for (Polygon poly: placePolygons){
                    poly.setVisible(false);
                }
                for (Marker marker : polygonTrashbins){
                    marker.setVisible(false);
                }

                onResultScreen = true;

                ProgressBar pb = (ProgressBar) findViewById(R.id.weatherSearchProgressBar);
                pb.setVisibility(View.INVISIBLE);

                Map<String, Object> parametersToUse = new HashMap<String, Object>();

                parametersToUse.put("temperature", 10.0);
                parametersToUse.put("windSpeed", 20.0);
                parametersToUse.put("cloudCover", 8);

                new Weather(pb, mMap).findWeather(parametersToUse, placePolygons,
                        MainActivity.buttText1, MainActivity.buttText2);

            }
        });
}*/

    protected void onRestoreInstanceState (Bundle savedInstanceState) {
        //TODO - återställ kartans nuvarande position
    }

    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(MapHolder.view.getContext()); //TODO

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog =
                    GooglePlayServicesUtil.getErrorDialog(isAvailable, super.getActivity(), ERROR_DIALOG_REQUEST); //TODO
            dialog.show();
        } else {
            Toast.makeText(MapHolder.view.getContext(), "Can't connect to mapping service", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public void setCurrentLocation() {
        if(ActivityCompat.checkSelfPermission(
                super.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                        super.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(super.getActivity(), //TODO
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
            MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map); //TODO
            mapFragment.getMapAsync(this); //skapar kartan och anropar onMapReady när kartan är klar
        }
    }

    // Skapar en generell marker på kartan
    public Marker createMarker(LatLng latlng) {
        MarkerOptions options = new MarkerOptions()
                              .position(latlng);
        return mMap.addMarker(options);
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
                } else if (true) {
                    mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View v = getActivity().getLayoutInflater().inflate(R.layout.info_window, null); //TODO

                           // LinearLayout layer = (LinearLayout) v.findViewById(R.id.layoutWindow);
                            TextView tvTemp= (TextView)v.findViewById(R.id.marker_temp_text);
                            TextView tvCloud= (TextView)v.findViewById(R.id.marker_cloud_text);
                            TextView tvWind= (TextView)v.findViewById(R.id.marker_wind_text);

                        /*    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);*/
                            int currentMarker;
                            LatLng latLng = marker.getPosition();

                            if (latLng.equals(topResultMarkers.get(0).getPosition())){ currentMarker = 0;}
                            else if (latLng.equals(topResultMarkers.get(1).getPosition())){currentMarker = 1;}
                            else {currentMarker = 2;}


                            TextView tvLocal = (TextView) v.findViewById(R.id.tvLocality);
                            ImageView tvImage = (ImageView) v.findViewById(R.id.imageView1);
                                //Vi tar markörerns koordinater och använder geocoder för att få
                            Geocoder gc = new Geocoder(getActivity()); //namnet på staden som markören pekar på
                            List<android.location.Address> list = null;        //Få namn på område
                            try {
                                list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1); //TODO Maybe implement a better way to get locality name
                            } catch (IOException e) {     //TODO Improve the info window
                                e.printStackTrace();
                            }

                            if (list.isEmpty()){   //Ifall vi inte får någon information om området
                                tvLocal.setText("No information");
                                return v;
                            } else {
                                Log.d("Size" , Integer.toString(list.size()));
                                android.location.Address address = list.get(0);
                                if (address.getLocality() != null) {
                                    tvLocal.setText(address.getLocality());//Vi sätter text som ska förekomma i markör-fönster
                                } else {
                                    tvLocal.setText(address.getSubLocality());
                                }
                            }
                            if (MapHolder.parametersToUse.containsKey("temperature")){
                                try {

                                    tvTemp.setText("Temperatur: " + Double.toString(MapHolder.resultList.get(currentMarker).getTemperature()) + "°C");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{tvTemp.setVisibility(View.GONE);}
                            if (MapHolder.parametersToUse.containsKey("windSpeed")){
                                Log.d("Contains" , "Windspeed");
                                 try {
                                    tvWind.setText("Vindhastighet: " + Double.toString(MapHolder.resultList.get(currentMarker).getWindspeed()) + " m/s");
                                  } catch (JSONException e) {
                                      e.printStackTrace();
                                 }
                            }else{tvWind.setVisibility(View.GONE);}
                            if (MapHolder.parametersToUse.containsKey("cloudCover")){
                                try {
                                    tvCloud.setText("Molntäcke: " + Double.toString(MapHolder.resultList.get(currentMarker).getCloudCover() * 12.5) + "%");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else{tvWind.setVisibility(View.GONE);}

                                try {
                                    int cloud = MapHolder.resultList.get(currentMarker).getCloudCover();
                                    if (cloud <= 2){
                                        tvImage.setImageResource(R.drawable.ic_sunny);
                                    } else if (cloud > 2 && cloud <= 5){
                                        tvImage.setImageResource(R.drawable.ic_mostly_cloudy);
                                    } else if (cloud > 5){
                                        tvImage.setImageResource(R.drawable.ic_cloudy);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            return v;
                        }
                    });
                    return false;
                } else {
                    return false;
                }
            }
        });
    }

    private void initDrawFrame() {
        fram_map = (FrameLayout) thisView.findViewById(R.id.fram_map);
        enterDrawStateButton = (FloatingActionButton) thisView.findViewById(R.id.enter_draw_state_button);
        exitDrawStateButton = (FloatingActionButton) thisView.findViewById(R.id.exit_draw_state_button);
        doneButton = (FloatingActionButton) thisView.findViewById(R.id.done_button);
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
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(
                        latLng, 12
                );
                mMap.animateCamera(update);
                /*if(placeMarker != null){placeMarker.remove();}
                placeMarker = MapActivity.this.createMarker(latLng);*/
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
    }

    public Bitmap resizeMapIcons(String iconName, int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource
                (getResources(),getResources().getIdentifier(iconName, "drawable", getActivity().getPackageName())); //TODO
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

    public void createTopMarkers(List<WeatherParameters> resultList){
        if(topResultMarkers == null){
            topResultMarkers = new ArrayList<>();
        }
        if(topResultMarkers != null) {
            for (int i = 0; i < topResultMarkers.size(); i++) {
                topResultMarkers.get(i).remove();
            }
            topResultMarkers.clear();
        }

        for(int i = 0; i < 3; i++){
            topResultMarkers.add(createMarker(resultList.get(i).getLatLng()));
        }
    }

}

/*                            switch(weather){ //Beroende på vad för väder vi har så skriver vi motsvarande ikon till markörfönstret
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
                            }*/