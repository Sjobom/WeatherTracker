package se.anderssjobom.weathertracker;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
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
    public static List<WeatherParameters> resultList;
    MapActivity mapActivity;
    ResultListFragment listFragment;
    public static Context con;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        parametersToUse = (HashMap<String, Object>) intent.getSerializableExtra("map");

        setContentView(R.layout.activity_menu);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout.setVisibility(View.GONE);
        viewPagerMapAdapter = new ViewPagerMapAdapter(getFragmentManager());
        viewPager.setOffscreenPageLimit(0);

        //Lägger till fragment
        mapActivity = new MapActivity();
        listFragment = new ResultListFragment();
        viewPagerMapAdapter.addFragments(mapActivity, "Karta");
        viewPagerMapAdapter.addFragments(listFragment, "Lista");

        //viewPagerMapAdapter.addFragments(new Enkel_Fragment(), "Enkel Vy"); //Make room for table

        //Väljer vilken Adapter vår viewPager ska följa
        viewPager.setAdapter(viewPagerMapAdapter);
        tabLayout.setupWithViewPager(viewPager);
        con = getApplicationContext();

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

    public void showDetails(View v, LatLng latLng){
        WeatherParameters wp = new WeatherParameters();
        Intent intent = new Intent(MapHolder.this, DetailActivity.class);
        for(int i = 0; i < resultList.size(); i++){
            if(resultList.get(i).getLatLng().equals(latLng)){
                wp = resultList.get(i);
            }
        }
        intent.putExtra("weatherParameter", wp);
        Log.d(LOG,intent.toString() );
        startActivity(intent);
    }

    @Override
    public void onAnalysisReady(List<WeatherParameters> resultList) {
        this.resultList = resultList;
        mapActivity.createTopMarkers(resultList);
        //Visa tablayouten
        tabLayout.setVisibility(View.VISIBLE);

        ArrayList<WeatherParameters> xtra = new ArrayList<>();
        int i =0;
        for (WeatherParameters point :resultList)
        {
            xtra.add(i,point);
            i++;
        }
        RecyclerView.Adapter mAdapter = new ResultRecyclerAdapter(xtra);
        ResultListFragment.mRecyclerView.setAdapter(mAdapter);
    }
}

