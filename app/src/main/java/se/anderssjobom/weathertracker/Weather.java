package se.anderssjobom.weathertracker;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import se.anderssjobom.weathertracker.model.WeatherParameters;

/**
 * Created by ander on 20/04/2016.
 */
public class Weather {

    public static List<LatLng> findWeather(WeatherParameters wp, List<Polygon> pList, Calendar startDate, Calendar endDate){

        List<LatLng> poly = pList.get(0).getPoints();
        LatLng tempLatLng = poly.get(0);

        Double maxLat = tempLatLng.latitude, minLat = tempLatLng.latitude;
        Double maxLong = tempLatLng.longitude, minLong = tempLatLng.longitude;

        for (int i = 1; i < poly.size(); i++){
            if (poly.get(i).latitude < minLat){
                minLat = poly.get(i).latitude;
            } else if (poly.get(i).latitude > maxLat){
                maxLat = poly.get(i).latitude;
            }

            if (poly.get(i).longitude < minLong){
                minLong = poly.get(i).longitude;
            } else if (poly.get(i).longitude > maxLong){
                maxLong = poly.get(i).longitude;
            }
        }

        Double deltaLat = (maxLat - minLat);
        Double deltaLong = (maxLong - minLong);

        Double deltaLatRatio = deltaLat / (deltaLat + deltaLong);
        Double deltaLongRatio = deltaLong / (deltaLat + deltaLong);

        int x = 1;
        Double querys = deltaLatRatio * deltaLongRatio;

        if(querys < 100) {
            while(querys < 100){
                x++;
                querys = deltaLatRatio * x * deltaLongRatio * x;
            }
            deltaLat = deltaLat / (deltaLatRatio * x);
            deltaLong = deltaLong / (deltaLongRatio * x);
            Log.d("Weather deltaLat: ", String.valueOf(deltaLatRatio * x));
            Log.d("Weather deltaLong: ", String.valueOf(deltaLongRatio * x));
        }



        List <LatLng> pointsInPolygon = new ArrayList<>();

        for (Double i = minLat; i <= maxLat; i += deltaLat ){
            for (Double j = minLong; j <= maxLong; j += deltaLong){
                tempLatLng = new LatLng(i, j);
                if(PolyUtil.containsLocation(tempLatLng, poly, true)){
                    pointsInPolygon.add(tempLatLng);
                }
            }
        }
        Log.d("Weather, querys: ", Integer.toString(pointsInPolygon.size()));
        return pointsInPolygon;
    }


    class WebTask extends AsyncTask<String, String, String> {

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
            Log.d("MAIN.onPostExecute", result);
        }
    }


}
