package se.anderssjobom.weathertracker;

import android.content.Intent;
import android.location.Geocoder;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

import se.anderssjobom.weathertracker.model.WeatherParameters;

/**
 * Created by ander on 12/05/2016.
 */
public class DetailActivity extends AppCompatActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        WeatherParameters wp = intent.getParcelableExtra("weatherParameter");
        LinearLayout layout = (LinearLayout) this.findViewById(R.id.detail_layout);
        TextView tempText;

        ImageView img = (ImageView) this.findViewById(R.id.weather_picture_detail);
        String s = "ic_" + wp.getWeatherType();
        img.setImageDrawable(wp.getWeatherImage());

        tempText = (TextView) this.findViewById(R.id.locality_detail);
        Geocoder gc = new Geocoder(getApplicationContext());
        tempText.setText(MapActivity.geocoder(wp.getLatLng(), gc));

        DateTimeFormatter fmtTime = DateTimeFormat.forPattern("HH:mm");
        DateTimeFormatter fmtDate = DateTimeFormat.forPattern("dd/M");

       // LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.)

        tempText = (TextView) this.findViewById(R.id.date_detail);
        tempText.setText(wp.getDate().toString(fmtDate) + " " +
                wp.getFoundStartTime().toString(fmtTime) + "-" +
                wp.getFoundEndTime().toString(fmtTime));

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);

        tempText = new TextView(this);
        tempText.setText("Vädertyp: " + wp.getWeatherType());
        tempText.setTextSize(21f);
        layout.addView(tempText);

        tempText = new TextView(this);
        tempText.setText("Temperatur: " + df.format(wp.getTemperature()) + "°C");
        tempText.setTextSize(21f);
        layout.addView(tempText);

        tempText = new TextView(this);
        tempText.setText("Vindstyrka: " + df.format(wp.getWindspeed()) + " m/s");
        tempText.setTextSize(21f);
        layout.addView(tempText);

        tempText = new TextView(this);
        tempText.setText("Molnighet: " + df.format(wp.getCloudCover() * 12.5) + "%");
        tempText.setTextSize(21f);
        layout.addView(tempText);

        tempText = new TextView(this);
        tempText.setText("Vindriktning: " + df.format(wp.getWindDirection()) + "°");
        tempText.setTextSize(21f);
        layout.addView(tempText);

        tempText = new TextView(this);
        tempText.setText("Nederbörd: " + df.format(wp.getRain()) + " mm");
        tempText.setTextSize(21f);
        layout.addView(tempText);

        tempText = new TextView(this);
        tempText.setText("Lufttryck: " + df.format(wp.getAirPressure()) + " hPa");
        tempText.setTextSize(21f);
        layout.addView(tempText);

        tempText = new TextView(this);
        tempText.setText("Luftfuktighet: " + df.format(wp.getRelativeHumidity()) + "%");
        tempText.setTextSize(21f);
        layout.addView(tempText);

        super.onCreate(savedInstanceState);



    }
}
