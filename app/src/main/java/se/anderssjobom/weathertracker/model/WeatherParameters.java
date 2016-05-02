package se.anderssjobom.weathertracker.model;

import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * Created by ander on 20/04/2016.
 */
public class WeatherParameters {
    private JSONObject data;
    private int point;
    private LatLng latLng;

    public WeatherParameters(JSONObject data, LatLng latLng, Map<String,Object> parametersToUseMap) throws JSONException {
        this.data = data;
        this.latLng = latLng;
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
        return (double) data.getJSONArray("parameters").getJSONObject(1).getJSONArray("values").get(0);
    }

    public LocalDate getDate() throws JSONException {
        //KAN VARA FELAKTIG KONSTRUKTION!!
        return new LocalDate(data.getString("validTime"));
    }

    public int getCloudCover() throws JSONException {
        return (int) data.getJSONArray("parameters").getJSONObject(7).getJSONArray("values").get(0);
    }

    public double getWindspeed() throws JSONException {
        return (double) data.getJSONArray("parameters").getJSONObject(4).getJSONArray("values").get(0);
    }

    public int getWindDirection() throws JSONException {
        return (int) data.getJSONArray("parameters").getJSONObject(3).getJSONArray("values").get(0);
    }

    public LatLng getLatLng() {
        return latLng;
    }

    private int calculatePoints(Map<String,Object> parametersToUseMap) throws JSONException {
        int point = 0;
        double temp;

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
            return "[WeatherParameters, Point: " + point + ", Temp: " + Double.toString(getTemperature())
                    + ", WindSpeed: " + Double.toString(getWindspeed()) + ", CloudCover: " + Double.toString(getCloudCover()) + "]";
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

