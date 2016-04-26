package se.anderssjobom.weathertracker;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import se.anderssjobom.weathertracker.model.WeatherParameters;

/**
 * Created by ander on 20/04/2016.
 */
public class Weather {

    public List<Marker> findWeather(WeatherParameters wp, List<Polygon> pList, Calendar startDate, Calendar endDate){



        List<Marker> markerList = new ArrayList<>();
        return markerList;
    }

    public void test (){

    }

    static class WebTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){

        }

        @Override
        protected String doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result){

            //Log.d("MAIN.onPostExecute", result);
        }
    }


}
