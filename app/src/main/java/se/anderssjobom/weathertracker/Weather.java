package se.anderssjobom.weathertracker;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.maps.android.PolyUtil;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import se.anderssjobom.weathertracker.model.WeatherParameters;

public class Weather {

    private List<JSONObject> jsonList;
    private ProgressBar pb;
    private GoogleMap map;
    private Map<String, Object> parametersToUseMap;
    private LocalDate start;
    private LocalDate end;
    private OnAnalysisReadyCallback callback;
    public static boolean listReady;


    public Weather(ProgressBar pb, GoogleMap map, OnAnalysisReadyCallback callback){
        this.map = map;
        this.pb = pb;
        this.callback = callback;
    }

    public void findWeather(Map<String,Object> parametersToUseMap, List<Polygon> pList, Calendar startDate, Calendar endDate){

        this.parametersToUseMap = parametersToUseMap;

        start = new LocalDate(startDate);
        end = new LocalDate(endDate);

        jsonList = new ArrayList<>();

        pb.setVisibility(View.VISIBLE);

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

        String uri;
        Double lon, lat;
        final AtomicInteger workCounter = new AtomicInteger(pointsInPolygon.size());
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.US);
        DecimalFormat df = new DecimalFormat("#.######", dfs);

        Executor exec = Executors.newFixedThreadPool(100);

        for (int i = 0; i < pointsInPolygon.size(); i++){
            tempLatLng = pointsInPolygon.get(i);
            lon = Double.valueOf(df.format(tempLatLng.longitude));
            lat = Double.valueOf(df.format(tempLatLng.latitude));

            uri = "http://opendata-download-metfcst.smhi.se/api/category/pmp2g/version/2/" +
                    "geotype/point/lon/" + lon + "/lat/" + lat + "/data.json";
            new Weather.WebTask(workCounter).executeOnExecutor(exec, uri);
        }

        Log.d("Weather, querys: ", Integer.toString(pointsInPolygon.size()));

    }


     class WebTask extends AsyncTask<String, String, JSONObject> {

        private final AtomicInteger workCounter;

        public WebTask(AtomicInteger workCounter){
            this.workCounter = workCounter;
        }

        @Override
        protected void onPreExecute(){
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            String content = HttpManager.getData(params[0]);
            JSONObject obj = null;
            try {
                obj = new JSONObject(content);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return obj;
        }

        @Override
        protected void onPostExecute(JSONObject obj){

            jsonList.add(obj);

            int tasksLeft = this.workCounter.decrementAndGet();
            if (tasksLeft == 0){
                Log.d("Fetching", "Done");
                //TODO - skapa tråd för analys!
                analyseResults();
                pb.setVisibility(View.INVISIBLE);
            }
            else {
                Log.d("Counting", Integer.toString(tasksLeft));
            }
        }
    }


    private void analyseResults() {
        Boolean dayNotFound = true;
        int timeSeriesIndex = 0;
        JSONObject obj;
        JSONObject timeSeriesObject;
        JSONArray timeSeries;
        JSONArray ar;
        WeatherParameters tempWeather;
        String strDate;
        String tempDate;
        LatLng tempLatLng;
        DateTimeFormatter fmt = DateTimeFormat.forPattern("y-MM-dd");

        int days = Days.daysBetween(start, end).getDays() + 1;

        PriorityQueue<WeatherParameters>[] tempQueues = new PriorityQueue[days+1];

        for (int i = 0; i < days; i++) {
            tempQueues[i] = new PriorityQueue<>(3, new pointReverseComparator());
            tempQueues[i].offer(new WeatherParameters());
            tempQueues[i].offer(new WeatherParameters());
            tempQueues[i].offer(new WeatherParameters());
        }

        try {
            //Kolla varje hämtat JSON-objekt (Position)
            for (int i = 0; i < jsonList.size(); i++) {
                obj = jsonList.get(i);
                ar = obj.getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
                tempLatLng = new LatLng(ar.getDouble(1),ar.getDouble(0));
                timeSeries = obj.getJSONArray("timeSeries");

                //Skapa ett objekt för varje dag på postionen!
                for (int day = 0; day < days; day++) {
                    dayNotFound = true;
                    while(dayNotFound){
                        timeSeriesObject = timeSeries.getJSONObject(timeSeriesIndex++);
                        strDate = timeSeriesObject.getString("validTime");
                        tempDate = (strDate.split("T")[0]);
                        if (fmt.print(start.plusDays(day)).equals(tempDate)) {
                            tempWeather = new WeatherParameters(obj, tempLatLng, parametersToUseMap, new LocalDate(tempDate));
                            Log.d(" Weather: " + tempDate, tempWeather.toString());
                            //Är tempDates poäng bättre än någon av de tre nuvarande på tempDates dags templista?

                            tempQueues[day].offer(tempWeather);

                            if (tempQueues[day].size() > 3) {
                                tempQueues[day].poll();
                            }
                            dayNotFound = false;
                        }
                    }
                    dayNotFound = false;
                    timeSeriesIndex = 0;
                }
            }
            //Skapa lista alla platser och dagar
            List<WeatherParameters> rList = new ArrayList<>();
            for (int day = 0; day < days; day++){
                rList.add(tempQueues[day].poll());
                rList.add(tempQueues[day].poll());
                rList.add(tempQueues[day].poll());
            }

            Collections.sort(rList, new pointComparator());
            Log.d("rList:", rList.toString());
            Log.d("rList size:", Integer.toString(rList.size()));

            jsonList = null; //Ta bort referensen för GarbageCollector


            listReady = true;

        callback.onAnalysisReady(rList);

        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class pointReverseComparator implements Comparator<WeatherParameters>{

        @Override
        public int compare(WeatherParameters lhs, WeatherParameters rhs) {
            if(lhs.getPoint() < rhs.getPoint()){
                return -1;
            }else if (lhs.getPoint() > rhs.getPoint()){
                return 1;
            }
            return 0;
        }
    }

    public class pointComparator implements Comparator<WeatherParameters>{

        @Override
        public int compare(WeatherParameters lhs, WeatherParameters rhs) {
            if(lhs.getPoint() < rhs.getPoint()){
                return 1;
            }else if (lhs.getPoint() > rhs.getPoint()){
                return -1;
            }
            return 0;
        }
    }




        // SKRIVER UT SMHIs koordinater
            /* for (int i = 0; i < jsonList.size(); i++){
                obj = jsonList.get(i);
                try {
                    ar = (JSONArray) obj.getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0);
                    lon = ar.getDouble(0);
                    lat = ar.getDouble(1);
                    options = new MarkerOptions()
                            .position(new LatLng(lat,lon));
                    map.addMarker(options);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }*/

}



/*    static class AnalysisTask extends AsyncTask<JSONObject, String, List<WeatherParameters>{

        private final AtomicInteger workCounter;

        public AnalysisTask(AtomicInteger workCounter){
            this.workCounter = workCounter;
        }

        @Override
        protected List<WeatherParameters> doInBackground(JSONObject... params) {
            int tasksLeft = this.workCounter.decrementAndGet();

            if (tasksLeft == 0){
                Log.d("Fetching", "Done");

            }
            else {
                Log.d("Counting", Integer.toString(tasksLeft));
            }
            return ;
        }
    }*/