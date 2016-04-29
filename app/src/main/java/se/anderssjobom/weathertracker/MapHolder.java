package se.anderssjobom.weathertracker;

import android.app.*;
import android.app.ListFragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rami on 2016-04-28.
 */
public class MapHolder extends AppCompatActivity {

    public static View view;
    public static List<Polygon> placePolygons;
    public static List<Marker> polygonTrashbins;
    public static List<Polyline> tempPolylines;
    public static LatLng curLatLng;
    public static LatLng prevLatLng;
    public static FrameLayout fram_map;
    public static FloatingActionButton enterDrawStateButton;
    public static FloatingActionButton exitDrawStateButton;
    public static FloatingActionButton doneButton;
    public static Boolean isMapMoveable = true; // to detect map is movable
    public static ArrayList<Marker> markers = new ArrayList<Marker>(3);
    public static boolean onResultScreen = false;




    public static TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerMapAdapter viewPagerMapAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout.setVisibility(View.GONE);
        viewPagerMapAdapter = new ViewPagerMapAdapter(getFragmentManager());

        //Lägger till fragment
        viewPagerMapAdapter.addFragments(new MapActivity(), "Karta");
        viewPagerMapAdapter.addFragments(new ListFragment(), "Lista");

        //viewPagerMapAdapter.addFragments(new Enkel_Fragment(), "Enkel Vy"); //Make room for table

        //Väljer vilken Adapter vår viewPager ska följa
        viewPager.setAdapter(viewPagerMapAdapter);
        //Sätter antal fragment viewPager håller "levande"
        viewPager.setOffscreenPageLimit(viewPagerMapAdapter.getCount());

        tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public void onBackPressed() {
        if (!onResultScreen){
            super.onBackPressed();
        } else {
            doneButton.show();  //TODO look at what happens when you press back button, also could i use tablout isvisible as a reference?
            enterDrawStateButton.show();
            findViewById(R.id.card_view).setVisibility(View.VISIBLE);
            //resultPopup.dismiss();

            for (Polygon poly : placePolygons) {
                poly.setVisible(true);
            }
            for (Marker trashCon : polygonTrashbins) {
                trashCon.setVisible(true);
            }
            for (Marker marker : markers) {
                marker.remove();
            }
            onResultScreen = false;
        }

    }
}

