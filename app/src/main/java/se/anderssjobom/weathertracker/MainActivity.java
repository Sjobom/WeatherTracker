package se.anderssjobom.weathertracker;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by ThimLohse on 2016-04-19.
 */
public class MainActivity extends AppCompatActivity
{
    public static View view;
    public static String buttText1 = "default1";
    public static String buttText2 = "default2";
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new Aktivitet_Fragment(), "Aktiviteter");
        viewPagerAdapter.addFragments(new Enkel_Fragment(), "Enkel Vy");
        viewPagerAdapter.addFragments(new Datum(), "Avancerad Vy");
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        viewPager.setPageTransformer(true,new ZoomOutPageTransformer());





    }


    public void onClick(View v) {//När vi klickar på knappen, gör den hit
        view = v;  //Behöver detta för att senare veta vilken knapp som vi har tryckt när vi ska skriva ner datum som användaren har valt
        showDatePickerDialog();
    }

    public void showMap(View v){
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment(); //Nu skapas fragmenterobjektet
        newFragment.show(getSupportFragmentManager(), "datePicker"); //Nu visas kalenderfragmentet
    }


}
