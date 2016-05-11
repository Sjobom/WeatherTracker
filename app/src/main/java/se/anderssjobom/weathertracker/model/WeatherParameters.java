package se.anderssjobom.weathertracker.model;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ander on 20/04/2016.
 */
public class WeatherParameters {
    private JSONObject data;
    private int point;
    private LatLng latLng;
    private LocalDate date;
    private LocalTime foundStartTime, foundEndTime;
    private List<JSONArray> dataInFoundTimeSpan;

    public WeatherParameters(JSONObject data, LatLng latLng, Map<String,Object> parametersToUseMap, LocalDate date) throws JSONException {
        this.data = data;
        this.latLng = latLng;
        this.date = date;

        //TODO - sätt foundStartTime och endStartTime!
        findPossibleTimes(parametersToUseMap);

        this.point = calculatePoints(parametersToUseMap);
    }

    //DO NOT USE REGULARLY!
    public WeatherParameters(){
        point = -1;
    }

    public int getPoint() {
        return point;
    }

    public double getTemperature() throws JSONException {
        double tempTemp = 0;
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            tempTemp += (double) dataInFoundTimeSpan.get(i).getJSONObject(1).getJSONArray("values").get(0);
        }

        return tempTemp / dataInFoundTimeSpan.size();
    }

    public LocalDate getDate() throws JSONException {
        return date;
    }

    public int getCloudCover() throws JSONException {
        int tempCloud = 0;
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            tempCloud += (int) dataInFoundTimeSpan.get(i).getJSONObject(7).getJSONArray("values").get(0);
        }

        return tempCloud / dataInFoundTimeSpan.size();
    }

    public double getWindspeed() throws JSONException {
        int tempWindSpeed = 0;
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            tempWindSpeed += (double) dataInFoundTimeSpan.get(i).getJSONObject(4).getJSONArray("values").get(0);
        }

        return tempWindSpeed / dataInFoundTimeSpan.size();
    }

    public int getWindDirection() throws JSONException {
        int tempWindDirection = 0;
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            tempWindDirection += (double) dataInFoundTimeSpan.get(i).getJSONObject(3).getJSONArray("values").get(0);
        }

        return tempWindDirection / dataInFoundTimeSpan.size();
    }

    public LocalTime getFoundEndTime() {
        return foundEndTime;
    }

    public LocalTime getFoundStartTime() {
        return foundStartTime;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    private int calculatePoints(Map<String,Object> parametersToUseMap) throws JSONException {
        int point = 0;

        if(parametersToUseMap.get("temperature") != null){
            point += pointFormula((int) parametersToUseMap.get("temperature"), getTemperature(), 40);
        }

        if(parametersToUseMap.get("windSpeed") != null){
            point += pointFormula((int) parametersToUseMap.get("windSpeed"), getWindspeed(), 20);
        }

        if(parametersToUseMap.get("cloudCover") != null){
            point += pointFormula((( (int) parametersToUseMap.get("cloudCover"))), getCloudCover(), 9);
        }

        return point;
    }

    private int pointFormula(int requested, double found, int parameterDelta){
        return (int) ( (1 - ( Math.abs((requested - found)) / parameterDelta ) ) * 1000 );
    }


    private int pointFormula(int requested, int found, int parameterDelta){
        return ( (1 - ( Math.abs((requested - found)) / parameterDelta ) ) * 1000 );
    }

    private void findPossibleTimes(Map<String,Object> parametersToUseMap) throws JSONException {

        LocalTime requestedStartTime = (LocalTime) parametersToUseMap.get("requestedStartTime");
        LocalTime requestedEndTime = (LocalTime) parametersToUseMap.get("requestedEndTime");
        JSONArray timeSeries = data.getJSONArray("timeSeries");
        JSONObject timeSeriesObject;
        Boolean thisDateNotFound = true;
        Boolean lookingAtDay = true;
        String strDate;
        LocalDate tempDate;
        LocalTime tempTime;
        int timeSeriesIndex = 0;
        Hours startHourDiff;
        Hours endHourDiff;
        dataInFoundTimeSpan = new ArrayList<>();

        while(thisDateNotFound){
            timeSeriesObject = timeSeries.getJSONObject(timeSeriesIndex++);
            strDate = timeSeriesObject.getString("validTime");
            tempDate = new LocalDate(strDate.split("T")[0]);
            if (tempDate.isEqual(date)) {
                thisDateNotFound = false;
                foundStartTime = new LocalTime(strDate.split("T")[1].split("Z")[0]);
                foundEndTime = foundStartTime;
                lookingAtDay = true;
                while(lookingAtDay){
                    timeSeriesObject = timeSeries.getJSONObject(timeSeriesIndex++);
                    strDate = timeSeriesObject.getString("validTime");
                    tempTime = new LocalTime(strDate.split("T")[1].split("Z")[0]);
                    startHourDiff = Hours.hoursBetween(requestedStartTime, tempTime);
                    Log.d("StartHourDiff", startHourDiff.toString());
                    endHourDiff = Hours.hoursBetween(requestedEndTime, tempTime);
                    Log.d("EndHourDiff", endHourDiff.toString());

                    //TODO - Blir fel på hour-differens!!!

                    Log.d("Start Difference", (Hours.hoursBetween(requestedStartTime, foundStartTime).toString()));
                    if (startHourDiff.isLessThan(Hours.hoursBetween(requestedStartTime, foundStartTime))){
                        Log.d("StartHourDiff", "was smaller");
                        foundStartTime = tempTime;
                    }
                    Log.d("End Difference", (Hours.hoursBetween(requestedEndTime, foundEndTime).toString()));
                    if (endHourDiff.isLessThan(Hours.hoursBetween(requestedEndTime, foundEndTime))){
                        Log.d("EndHourDiff", "was smaller");
                        foundEndTime = tempTime;
                    }

                    // Om nästa dag, sluta jämför tider!
                    if(!new LocalDate(timeSeries.getJSONObject((timeSeriesIndex)).getString("validTime").split("T")[0]).isEqual(date)){
                        while(lookingAtDay) {
                            timeSeriesObject = timeSeries.getJSONObject(--timeSeriesIndex);
                            strDate = timeSeriesObject.getString("validTime");
                            tempTime = new LocalTime(strDate.split("T")[1].split("Z")[0]);

                            // Lägg till den data som finns mellan tidsspannet!
                            if (tempTime.isEqual(foundEndTime) || tempTime.isBefore(foundEndTime)
                                    && tempTime.isEqual(foundStartTime) || tempTime.isBefore(foundStartTime)){
                                    dataInFoundTimeSpan.add(timeSeriesObject.getJSONArray("parameters"));
                                if (tempTime.isEqual(foundStartTime)) {
                                    lookingAtDay = false;
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    public int compareTo(WeatherParameters wp){
        if(wp.getPoint() > getPoint()){
            return 1;
        } else if (wp.getPoint() < getPoint()){
            return -1;
        }
        return 0;
    }

    @Override
    public String toString() {
        try {
            return "[WeatherParameters, Point: " + point + " FoundTimeSpan: "
                    + getFoundStartTime().toString() + "-" + getFoundEndTime().toString() + ", Temp: " + Double.toString(getTemperature())
                    + ", WindSpeed: " + Double.toString(getWindspeed()) + ", CloudCover: " + Double.toString(getCloudCover()) + "]";
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

