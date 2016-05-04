package se.anderssjobom.weathertracker;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import static android.support.v4.app.ActivityCompat.startActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by ThimLohse on 2016-04-19.
 */
public class MainActivity extends AppCompatActivity
{
    public static View view;
    public static Calendar buttText1 = GregorianCalendar.getInstance();
    public static Calendar buttText2 = GregorianCalendar.getInstance();
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        JodaTimeAndroid.init(this); //Importerat tidsbibliotek!

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);


        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        //Lägger till fragment
        viewPagerAdapter.addFragments(new Aktivitet_Fragment(), "Aktiviteter");
        viewPagerAdapter.addFragments(new Enkel_Fragment(), "Enkel Vy");
        viewPagerAdapter.addFragments(new AdvancedFragment(), "Avancerad Vy");

        //Väljer vilken Adapter vår viewPager ska följa
        viewPager.setAdapter(viewPagerAdapter);
        //Sätter antal fragment viewPager håller "levande"
        viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        tabLayout.setupWithViewPager(viewPager);

    }
    public void showDateFragment(View v) {//När vi klickar på knappen, gör den hit
        view = v;  //Behöver detta för att senare veta vilken knapp som vi har tryckt när vi ska skriva ner datum som användaren har valt
        showDatePickerDialog();
    }

    public void showMap(View v){ //TODO Make different cases depending on which of the tab buttons user pressed
        HashMap<String, Object> parametersToUseList = new HashMap<String, Object>();
        switch(v.getId()){
            case R.id.button_map:
                break;

            case R.id.go_to_map_button_simple:
                SeekBar seekTemp = (SeekBar) findViewById(R.id.tempSeekBar);
                SeekBar seekCloud = (SeekBar) findViewById(R.id.cloudSeekBar);
                SeekBar seekWind = (SeekBar) findViewById(R.id.windSeekBar);
                parametersToUseList.put("temperature", seekTemp.getProgress());
                parametersToUseList.put("windSpeed", seekCloud.getProgress());
                parametersToUseList.put("cloudCover", seekWind.getProgress());
                break;

            case R.id.go_to_map_button_advanced:
                SeekBar seekadvTemp = (SeekBar) findViewById(R.id.temperature_seekbar);
                SeekBar seekadvCloud = (SeekBar) findViewById(R.id.cloud_cover_seekbar);
                SeekBar seekadvWind = (SeekBar) findViewById(R.id.wind_strength_seekbar);
                Switch switchTempAdv = (Switch) findViewById(R.id.temperature_switch);
                Switch switchWindAdv = (Switch) findViewById(R.id.wind_strength_switch);
                Switch switchCloudAdv = (Switch) findViewById(R.id.cloud_cover_switch);
                if (switchTempAdv.isChecked()) {
                    parametersToUseList.put("temperature", (seekadvTemp.getProgress() - 31));
                }
                if(switchWindAdv.isChecked()) {
                    parametersToUseList.put("windSpeed", seekadvCloud.getProgress());
                }
                if (switchCloudAdv.isChecked()) {
                    parametersToUseList.put("cloudCover", seekadvWind.getProgress());
                }
                break;
        }

        Intent intent = new Intent(MainActivity.this, MapHolder.class);
        intent.putExtra("map", parametersToUseList);
        startActivity(intent);
    }

    public void showDatePickerDialog() {
        android.support.v4.app.DialogFragment newFragment = new DatePickerFragment(); //Nu skapas fragmenterobjektet
        newFragment.show(getSupportFragmentManager(), "datePicker"); //Nu visas kalenderfragmentet
    }

}