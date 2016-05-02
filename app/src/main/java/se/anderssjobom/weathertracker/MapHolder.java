package se.anderssjobom.weathertracker;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

import java.util.HashMap;
import java.util.List;

import se.anderssjobom.weathertracker.model.WeatherParameters;

/**
 * Created by Rami on 2016-04-28.
 */
public class MapHolder extends AppCompatActivity implements OnAnalysisReadyCallback {

    private String LOG = "MapActivity";
    public static View view;
    public static TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerMapAdapter viewPagerMapAdapter;
    public static HashMap<String, Object> parametersToUse;
    List<WeatherParameters> resultList;
    MapActivity mapActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        parametersToUse = (HashMap<String, Object>) intent.getSerializableExtra("map");

        if (parametersToUse.containsKey("cloudCover")) {
            Log.d("TaaAAG", parametersToUse.get("cloudCover").toString());
        }

        setContentView(R.layout.activity_menu);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout.setVisibility(View.GONE);
        viewPagerMapAdapter = new ViewPagerMapAdapter(getFragmentManager());

        //Lägger till fragment
        mapActivity = new MapActivity();
        viewPagerMapAdapter.addFragments(mapActivity, "Karta");
        viewPagerMapAdapter.addFragments(new ListFragment(), "Lista");

        //viewPagerMapAdapter.addFragments(new Enkel_Fragment(), "Enkel Vy"); //Make room for table

        //Väljer vilken Adapter vår viewPager ska följa
        viewPager.setAdapter(viewPagerMapAdapter);
        //Sätter antal fragment viewPager håller "levande"
        viewPager.setOffscreenPageLimit(viewPagerMapAdapter.getCount());

        tabLayout.setupWithViewPager(viewPager);

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

    public void onResult(List<WeatherParameters> resultList){
        this.resultList = resultList;
    }


    @Override
    public void onBackPressed() {
        if (!MapActivity.onResultScreen){
            super.onBackPressed();
        } else {
            Log.d("Onlist" , "onList");
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
            Log.d("Onlist", "NOT");
            MapActivity.doneButton.show();  //TODO look at what happens when you press back button, also could i use tablout isvisible as a reference?
            MapActivity.enterDrawStateButton.show();
            findViewById(R.id.card_view).setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.GONE);

            for (Polygon poly : MapActivity.placePolygons) {
                poly.setVisible(true);
            }
            for (Marker trashCon : MapActivity.polygonTrashbins) {
                trashCon.setVisible(true);
            }
            for (Marker marker : MapActivity.markers) {
                marker.remove();
            }
            MapActivity.onResultScreen = false;
        }

    }

    @Override
    public void onAnalysisReady(List<WeatherParameters> resultList) {
        this.resultList = resultList;
        mapActivity.createTopMarkers(resultList);
    }
}

