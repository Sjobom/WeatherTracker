package se.anderssjobom.weathertracker;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

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
    public static int leftIndex = 0;
    public static int rightIndex = 23;
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
        viewPagerAdapter.addFragments(new Aktivitet_Fragment(), "Favoriter");
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

        View popupView = View.inflate(v.getContext(), R.layout.checkdatepopup,null);
        PopupWindow checkDateWindow =  new PopupWindow(popupView, ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT,true);
        checkDateWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
        TextView tw = (TextView) popupView.findViewById(R.id.checkDateText);

        RangeBar timeBarFav = (RangeBar) findViewById(R.id.time_bar_favourites);

        Switch switchTempAdv = (Switch) findViewById(R.id.temperature_switch);
        Switch switchWindAdv = (Switch) findViewById(R.id.wind_strength_switch);
        Switch switchCloudAdv = (Switch) findViewById(R.id.cloud_cover_switch);
        Switch switchRainAdv = (Switch) findViewById(R.id.rain_switch);

        if (buttText1.getTimeInMillis() > buttText2.getTimeInMillis())
        {
            tw.setText("Slutdatum kan inte vara före startdatum!");
            checkDateWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL,0,0);
            return;
        }
        if(!switchTempAdv.isChecked() && !switchCloudAdv.isChecked() && !switchWindAdv.isChecked() && !switchRainAdv.isChecked() && v.getId() == R.id.go_to_map_button_advanced)
        {
            tw.setText("Välj minst en parameter!");
            checkDateWindow.showAtLocation(popupView, Gravity.CENTER_HORIZONTAL,0,0);
            return;
        }

        switch(v.getId()) {

            case R.id.go_to_map_button_simple:
                RangeBar timeBarSimple = (RangeBar) findViewById(R.id.time_bar_simple);

                parametersToUse.put("requestedStartTime", new LocalTime(timeBarSimple.getLeftIndex(), 0));
                parametersToUse.put("requestedEndTime", new LocalTime(timeBarSimple.getRightIndex(), 0));

                SeekBar enkelParameter = (SeekBar) findViewById(R.id.enkelSeekBar);
                int pickerData = Enkel_Fragment.picker.getValue();
                switch (pickerData) {
                    case 0:
                        parametersToUse.put("temperature", -100 + 200 * enkelParameter.getProgress());
                        break;
                    case 1:
                        int windSpeed = 0;
                        switch (enkelParameter.getProgress()){
                            case 0:
                                windSpeed = 0;
                                break;
                            case 1:
                                windSpeed = 3;
                                break;
                            case 2:
                                windSpeed = 6;
                                break;
                            case 3:
                                windSpeed = 10;
                                break;
                        }
                        parametersToUse.put("windSpeed", windSpeed);
                        break;
                    case 2:
                        parametersToUse.put("cloudCover",  enkelParameter.getProgress() * 4);
                        break;
                    case 3:
                        parametersToUse.put("rain", 100 * enkelParameter.getProgress()); //LÄGG TILL NEDERBÖRD HÄR
                        break;
                }
                break;

            case R.id.go_to_map_button_advanced:
                SeekBar seekadvTemp = (SeekBar) findViewById(R.id.temperature_seekbar);
                SeekBar seekadvCloud = (SeekBar) findViewById(R.id.cloud_cover_seekbar);
                SeekBar seekadvWind = (SeekBar) findViewById(R.id.wind_strength_seekbar);
                SeekBar seekadvRain = (SeekBar) findViewById(R.id.rain_seekbar);
                RangeBar timeBarAdv = (RangeBar) findViewById(R.id.time_bar_advanced);

                parametersToUse.put("requestedStartTime", new LocalTime(timeBarAdv.getLeftIndex(), 0));
                parametersToUse.put("requestedEndTime", new LocalTime(timeBarAdv.getRightIndex(), 0));

                if (switchTempAdv.isChecked()) {
                    parametersToUse.put("temperature", (seekadvTemp.getProgress() - 31));
                }
                if (switchWindAdv.isChecked()) {
                    parametersToUse.put("windSpeed", seekadvWind.getProgress());
                }
                if (switchCloudAdv.isChecked()) {
                    parametersToUse.put("cloudCover", seekadvCloud.getProgress());
                }
                if (switchRainAdv.isChecked()) {
                    parametersToUse.put("rain", seekadvRain.getProgress());
                }
                break;

            case R.id.go_to_map_popup_favoriter:

                Log.d("TIMEBAR", timeBarFav.toString());
                parametersToUse.put("requestedStartTime", new LocalTime(timeBarFav.getLeftIndex(), 0));
                parametersToUse.put("requestedEndTime", new LocalTime(timeBarFav.getRightIndex(), 0));

                //Beroende på vilket card man valt lägger man till olika parametrar till parametersToUseList
                switch (Aktivitet_Fragment.currentCard)
                {
                    case "Sol":
                        Log.d("VALT CARD", "Sol");
                        //parametersToUseList.put("temperature", Aktivitet_Fragment.currentTempValue);
                        parametersToUse.put("cloudCover", Aktivitet_Fragment.currentCloudValue);
                        parametersToUse.put("rain",Aktivitet_Fragment.currentRainValue);
                        break;
                    case "Sol och moln":
                        Log.d("VALT CARD", "Sol och moln");
                        parametersToUse.put("cloudCover", Aktivitet_Fragment.currentCloudValue);
                        //parametersToUseList.put("temperature", Aktivitet_Fragment.currentTempValue);
                        //parametersToUseList.put("windSpeed", Aktivitet_Fragment.currentWindValue);
                        break;
                    case "Dis":
                        Log.d("VALT CARD", "Dis");
                        parametersToUse.put("rain",Aktivitet_Fragment.currentRainValue);
                        parametersToUse.put("temperature", Aktivitet_Fragment.currentTempValue);
                        parametersToUse.put("cloudCover", Aktivitet_Fragment.currentCloudValue);
                        //parametersToUseList.put("windSpeed", Aktivitet_Fragment.currentWindValue);
                        break;
                    case "Moln":
                        Log.d("VALT CARD", "Moln");
                        parametersToUse.put("cloudCover", Aktivitet_Fragment.currentCloudValue);
                        break;
                    case "Sol och regn":
                        Log.d("VALT CARD", "Sol och regn");
                        parametersToUse.put("rain",Aktivitet_Fragment.currentRainValue);
                        parametersToUse.put("temperature", Aktivitet_Fragment.currentTempValue);
                        parametersToUse.put("cloudCover", Aktivitet_Fragment.currentCloudValue);
                        //parametersToUseList.put("windSpeed", Aktivitet_Fragment.currentWindValue);
                        break;
                    case "Regn":
                        Log.d("VALT CARD", "Regn");
                        parametersToUse.put("rain",Aktivitet_Fragment.currentRainValue);
                        parametersToUse.put("temperature", Aktivitet_Fragment.currentTempValue);
                        parametersToUse.put("cloudCover", Aktivitet_Fragment.currentCloudValue);
                        //parametersToUseList.put("windSpeed", Aktivitet_Fragment.currentWindValue);
                        break;
                    case "Åska":
                        Log.d("VALT CARD", "Åska");
                        parametersToUse.put("rain",Aktivitet_Fragment.currentRainValue);
                        parametersToUse.put("temperature", Aktivitet_Fragment.currentTempValue);
                        parametersToUse.put("cloudCover", Aktivitet_Fragment.currentCloudValue);
                        parametersToUse.put("windSpeed", Aktivitet_Fragment.currentWindValue);

                        break;
                    case "Snö":
                        Log.d("VALT CARD", "Snö");
                        parametersToUse.put("rain",Aktivitet_Fragment.currentRainValue);
                        parametersToUse.put("temperature", Aktivitet_Fragment.currentTempValue);
                        parametersToUse.put("cloudCover", Aktivitet_Fragment.currentCloudValue);
                        //parametersToUseList.put("windSpeed", Aktivitet_Fragment.currentWindValue);
                        break;
                    /*
                    //Om vi använder användares egna card?
                    case "User":
                        parametersToUseList.put("rain",Aktivitet_Fragment.currentRainValue);
                        parametersToUseList.put("temperature", Aktivitet_Fragment.currentTempValue);
                        parametersToUseList.put("cloudCover", Aktivitet_Fragment.currentCloudValue);
                        parametersToUseList.put("windSpeed", Aktivitet_Fragment.currentWindValue);
                        break;*/
                }
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