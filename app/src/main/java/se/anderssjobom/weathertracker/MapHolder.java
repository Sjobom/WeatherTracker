package se.anderssjobom.weathertracker;

import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;

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
    //public static FloatingActionButton filterButton;



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
        viewPager.setOffscreenPageLimit(0);
        //filterButton = (FloatingActionButton) this.findViewById(R.id.filter_button);
        //Log.d("IDER" , Integer.toString(findViewById(R.id.filter_button).getId()));

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
            MapActivity.filterButton.setVisibility(View.INVISIBLE);

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
        //Visa tablayouten
        tabLayout.setVisibility(View.VISIBLE);

        ArrayList<WeatherParameters> topthreeList = new ArrayList<WeatherParameters>();
        for (int i = 0; i < 3; i++){
            topthreeList.add(resultList.get(i));
        }
        RecyclerView.Adapter mAdapter = new ResultRecyclerAdapter((ArrayList) topthreeList);
        ResultListFragment.mRecyclerView.setAdapter(mAdapter);
        MapActivity.filterButton.setVisibility(View.VISIBLE);
    }



    public void filter (View v){
        PopupMenu popup = new PopupMenu(this, v);
        final List<LocalDate> dateList = new ArrayList<LocalDate>();
        LocalDate date1 = new LocalDate(MainActivity.buttText1);
        LocalDate date2 = new LocalDate(MainActivity.buttText2);
        popup.getMenu().add("Alla datum");
        dateList.add(date1.minusDays(1));
        Log.d("WELP", "WELP");
        LocalDate currentDate = date1;
        while (!currentDate.equals(date2.plusDays(1))){
            popup.getMenu().add(currentDate.toString());
            dateList.add(currentDate);
            currentDate = currentDate.plusDays(1);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                List<WeatherParameters> dayWeatherList = new ArrayList<WeatherParameters>();
                String dateText = (String) item.getTitle();

                if (dateText.equals("Alla datum")){
                    Log.d("Date", "All dates");
                    mapActivity.createTopMarkers(resultList);

                    ArrayList<WeatherParameters> topthreeList = new ArrayList<WeatherParameters>();
                    for (int i = 0; i < 3; i++){
                        topthreeList.add(resultList.get(i));
                    }

                    RecyclerView.Adapter mAdapter = new ResultRecyclerAdapter((ArrayList) topthreeList);
                    ResultListFragment.mRecyclerView.setAdapter(mAdapter);
                } else {
                     LocalDate usedDate = LocalDate.parse(dateText, DateTimeFormat.forPattern("yyyy-MM-dd"));




                    for (int i = 0; i < resultList.size(); i++) {
                        try {
                            Log.d("ControlDate", resultList.get(i).getDate().toString());
                            if (dateText.equals(resultList.get(i).getDate().toString())) {
                                dayWeatherList.add(resultList.get(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    mapActivity.createTopMarkers(dayWeatherList);

                    RecyclerView.Adapter mAdapter = new ResultRecyclerAdapter((ArrayList) dayWeatherList);
                    ResultListFragment.mRecyclerView.setAdapter(mAdapter);
                }
                return false;
            }
        });

        popup.show();
    }
}

