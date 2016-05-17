package se.anderssjobom.weathertracker;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Range;
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

import com.appyvet.rangebar.RangeBar;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalTime;

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
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        JodaTimeAndroid.init(this); //Importerat tidsbibliotek!
        MainActivity.context = getApplicationContext();

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
        HashMap<String, Object> parametersToUse = new HashMap<String, Object>();
        switch(v.getId()){
            case R.id.button_map:
                break;

            case R.id.go_to_map_button_simple:
                SeekBar seekTemp = (SeekBar) findViewById(R.id.tempSeekBar);
                SeekBar seekCloud = (SeekBar) findViewById(R.id.cloudSeekBar);
                SeekBar seekWind = (SeekBar) findViewById(R.id.windSeekBar);
                parametersToUse.put("temperature", seekTemp.getProgress());
                parametersToUse.put("windSpeed", seekCloud.getProgress());
                parametersToUse.put("cloudCover", seekWind.getProgress());
                break;

            case R.id.go_to_map_button_advanced:
                SeekBar seekadvTemp = (SeekBar) findViewById(R.id.temperature_seekbar);
                SeekBar seekadvCloud = (SeekBar) findViewById(R.id.cloud_cover_seekbar);
                SeekBar seekadvWind = (SeekBar) findViewById(R.id.wind_strength_seekbar);
                SeekBar seekadvRain = (SeekBar) findViewById(R.id.rain_seekbar);
                Switch switchTempAdv = (Switch) findViewById(R.id.temperature_switch);
                Switch switchWindAdv = (Switch) findViewById(R.id.wind_strength_switch);
                Switch switchCloudAdv = (Switch) findViewById(R.id.cloud_cover_switch);
                Switch switchRainAdv = (Switch) findViewById(R.id.rain_switch);
                RangeBar timeBarAdv = (RangeBar) findViewById(R.id.time_bar_advanced);

                parametersToUse.put("requestedStartTime", new LocalTime(timeBarAdv.getLeftIndex(), 0));
                parametersToUse.put("requestedEndTime", new LocalTime(timeBarAdv.getRightIndex(), 0));

                if (switchTempAdv.isChecked()) {
                    parametersToUse.put("temperature", (seekadvTemp.getProgress() - 31));
                }
                if(switchWindAdv.isChecked()) {
                    parametersToUse.put("windSpeed", seekadvCloud.getProgress());
                }
                if (switchCloudAdv.isChecked()) {
                    parametersToUse.put("cloudCover", seekadvWind.getProgress());
                }
                if (switchRainAdv.isChecked()) {
                    parametersToUse.put("rain", seekadvRain.getProgress());
                }
                break;
        }

        Intent intent = new Intent(MainActivity.this, MapHolder.class);
        intent.putExtra("map", parametersToUse);
        startActivity(intent);
    }

    public void showDatePickerDialog() {
        android.support.v4.app.DialogFragment newFragment = new DatePickerFragment(); //Nu skapas fragmenterobjektet
        newFragment.show(getSupportFragmentManager(), "datePicker"); //Nu visas kalenderfragmentet
    }

    public static Context getAppContext(){
        return MainActivity.context;
    }

}