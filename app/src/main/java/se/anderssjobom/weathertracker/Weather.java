package se.anderssjobom.weathertracker;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        private final AtomicInteger workCounter;

        public WebTask(AtomicInteger workCounter){
            this.workCounter = workCounter;
        }

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
            int tasksLeft = this.workCounter.decrementAndGet();

            if (tasksLeft == 0){
                Log.d("Counting", "Done");
            }
            else {
                Log.d("Counting", Integer.toString(tasksLeft));
            }
            //Log.d("MAIN.onPostExecute", result);
        }
    }


}
