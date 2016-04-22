package se.anderssjobom.weathertracker;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Button;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by ThimLohse on 2016-04-19.
 */
public class MainActivity extends AppCompatActivity
{
    public static View view;
    public static Calendar buttText1 = GregorianCalendar.getInstance();
    public static Calendar buttText2 = GregorianCalendar.getInstance();
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
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(viewPagerAdapter);


        tabLayout.setupWithViewPager(viewPager);

        viewPager.setPageTransformer(true,new ZoomOutPageTransformer());





    }
    public void showDateFragment(View v) {//När vi klickar på knappen, gör den hit
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
