package se.anderssjobom.weathertracker.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.MarkerManager;

import org.joda.time.Hours;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.lang.Integer;

import se.anderssjobom.weathertracker.MainActivity;
import se.anderssjobom.weathertracker.R;

/**
 * Created by ander on 20/04/2016.
 */
public class WeatherParameters implements Parcelable {
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

    public double getTemperature(){
        double tempTemp = 0;
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            try {
                tempTemp += dataInFoundTimeSpan.get(i).getJSONObject(1).getJSONArray("values").getDouble(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return tempTemp / dataInFoundTimeSpan.size();
    }

    public LocalDate getDate() {
        return date;
    }

    public int getCloudCover() {
        int tempCloud = 0;
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            try {
                tempCloud += dataInFoundTimeSpan.get(i).getJSONObject(7).getJSONArray("values").getInt(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return (int) tempCloud / dataInFoundTimeSpan.size();
    }

    public double getAirPressure(){
        double tempAirPressure = 0;
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            try {
                tempAirPressure += dataInFoundTimeSpan.get(i).getJSONObject(0).getJSONArray("values").getDouble(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tempAirPressure / dataInFoundTimeSpan.size();
    }

    public int getRelativeHumidity(){
        int tempRelativeHumidity = 0;
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            try {
                tempRelativeHumidity += dataInFoundTimeSpan.get(i).getJSONObject(5).getJSONArray("values").getInt(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return (int) tempRelativeHumidity / dataInFoundTimeSpan.size();
    }

    public double getWindspeed(){
        double tempWindSpeed = 0;
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            try {
                tempWindSpeed += dataInFoundTimeSpan.get(i).getJSONObject(4).getJSONArray("values").getInt(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return tempWindSpeed / dataInFoundTimeSpan.size();
    }

    public int getWindDirection() {
        double tempWindDirection = 0;
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            try {
                tempWindDirection += dataInFoundTimeSpan.get(i).getJSONObject(3).getJSONArray("values").getDouble(0);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return (int) tempWindDirection / dataInFoundTimeSpan.size();
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

    public double getRain(){
        double rain = 0;
        JSONArray timeSeries;
        try {
            timeSeries = data.getJSONArray("timeSeries");
            JSONObject timeSeriesObject;
            Boolean thisDateNotFound = true;
            Boolean lookingAtDay = true;
            String strDate;
            LocalDate tempDate;
            LocalTime tempTime;
            int timeSeriesIndex = 0;
            int hourDiff;

            while (thisDateNotFound) {
                timeSeriesObject = timeSeries.getJSONObject(timeSeriesIndex++);
                strDate = timeSeriesObject.getString("validTime");
                tempDate = new LocalDate(strDate.split("T")[0]);
                if (tempDate.isEqual(date)) {
                    thisDateNotFound = false;
                    lookingAtDay = true;
                    while (lookingAtDay) {
                        timeSeriesObject = timeSeries.getJSONObject(timeSeriesIndex++);
                        strDate = timeSeriesObject.getString("validTime");
                        tempTime = new LocalTime(strDate.split("T")[1].split("Z")[0]);

                        if (tempTime.isEqual(foundEndTime) || tempTime.isBefore(foundEndTime)
                                && tempTime.isEqual(foundStartTime) || tempTime.isAfter(foundStartTime)) {
                            hourDiff = Math.abs(Hours.hoursBetween(tempTime,
                                    new LocalTime(timeSeries.getJSONObject(timeSeriesIndex).getString("validTime").split("T")[1].split("Z")[0])).getHours());
                            rain += timeSeriesObject.getJSONArray("parameters").getJSONObject(16).getJSONArray("values").getDouble(0) * hourDiff;
                        }

                        if (!new LocalDate(timeSeries.getJSONObject((timeSeriesIndex)).getString("validTime").split("T")[0]).isEqual(date)) {
                            lookingAtDay = false;
                        }
                    }

                }
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        // Värdet är i mm för hela perioden
        return rain;
    }

    private int getWeatherSymbol(){
        List<Integer> weatherSymbols = new ArrayList<>();

        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            try {
                weatherSymbols.add(dataInFoundTimeSpan.get(i).getJSONObject(18).getJSONArray("values").getInt(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int weatherSymbol = 1;
        int currentFrequency = Collections.frequency(weatherSymbols, 1);
        int tempFrequency;

        for(int i = 2; i < 16; i++){
            tempFrequency = Collections.frequency(weatherSymbols, i);
            if(tempFrequency >= currentFrequency){
                weatherSymbol = i;
                currentFrequency = tempFrequency;
            }
        }
        return weatherSymbol;
    }

    public String getWeatherType(){
        switch (getWeatherSymbol()){
            case 1:
                return "Clear Sky";
            case 2:
                return "Nearly clear sky";
            case 3:
                return "Variable cloudiness";
            case 4:
                return "Halfclear sky";
            case 5:
                return "Cloudy sky";
            case 6:
                return "Overcast";
            case 7:
                return "Fog";
            case 8:
                return "Rain showers";
            case 9:
                return "Thunderstorm";
            case 10:
                return "Light sleet";
            case 11:
                return "Snow showers";
            case 12:
                return "Rain";
            case 13:
                return "Thunder";
            case 14:
                return "Sleet";
            case 15:
                return "Snowfall";
            default:
                return "Invalid Number";
        }
    }

    public Drawable getWeatherImage(){
        switch (getWeatherSymbol()){
            case 1:
                //Clear sky
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_sunny);
            case 2:
                //Nearly clear sky
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_sunny);
            case 3:
                //Variable cloudiness
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_mostly_cloudy);
            case 4:
                //Halfclear sky
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_mostly_cloudy);
            case 5:
                //Cloudy sky
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_cloudy);
            case 6:
                //Overcast
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_cloudy);
            case 7:
                //Fog
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_haze);
            case 8:
                //Rain showers
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_slight_rain);
            case 9:
                //Thunderstorm
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_thunderstorms);
            case 10:
                //Light sleet
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_snow);
            case 11:
                //Snow showers
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_snow);
            case 12:
                //Rain
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_rain);
            case 13:
                //Thunder
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_thunderstorms);
            case 14:
                //Sleet
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_snow);
            case 15:
                //Snowfall
                return ContextCompat.getDrawable(MainActivity.getAppContext(),R.drawable.ic_snow);
            default:
                return null;
        }
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
        if(parametersToUseMap.get("rain") != null){

            // Om vi inte vill ha regn men det kommer regna, ge 0 poäng, annars addera som vanligt
            // TODO - kanske ändra så att det får vara lite regn trots att användaren inte vill ha något...
            if (!((int)(parametersToUseMap.get("rain")) == 0 && getRain() != 0)){
                point += pointFormula((( (int) parametersToUseMap.get("rain"))), getRain(),
                        Math.abs(Hours.hoursBetween(foundStartTime, foundEndTime).getHours()) + 6);;
            }
        }

        return point;
    }

    private int pointFormula(int requested, double found, int parameterDelta){
        Double grade = Math.abs((requested - found));
        if(grade < 0){ grade = 0.0;}

        return (int) ( (1 - ( grade / parameterDelta ) ) * 1000 );
    }


    private int pointFormula(int requested, int found, int parameterDelta){
        int grade = Math.abs((requested - found));
        if(grade < 0){ grade = 0;}
        return ( (1 - ( grade / parameterDelta ) ) * 1000 );
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
                    endHourDiff = Hours.hoursBetween(requestedEndTime, tempTime);

                    if (Math.abs(startHourDiff.getHours()) < Math.abs(Hours.hoursBetween(requestedStartTime, foundStartTime).getHours())){
                        foundStartTime = tempTime;
                    }
                    if (Math.abs(endHourDiff.getHours()) < Math.abs((Hours.hoursBetween(requestedEndTime, foundEndTime)).getHours())){
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
                                    && tempTime.isEqual(foundStartTime) || tempTime.isAfter(foundStartTime)){
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
        Log.d("Found times: ", foundStartTime.toString() + "-" +  foundEndTime.toString());
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
            return "[WeatherParameters, Point: " + point + " FoundTimeSpan: "
                    + getFoundStartTime().toString() + "-" + getFoundEndTime().toString()
                    + ", Temp: " + Double.toString(getTemperature())
                    + ", WindSpeed: " + Double.toString(getWindspeed())
                    + ", CloudCover: " + Double.toString(getCloudCover())
                    + ", Rain: " + Double.toString(getRain()) + "]";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data.toString());
        List<String> dataStringList = new ArrayList();
        for(int i = 0; i < dataInFoundTimeSpan.size(); i++){
            dataStringList.add(dataInFoundTimeSpan.get(i).toString());
        }
        dest.writeList(dataStringList);
        dest.writeInt(point);
        dest.writeDouble(latLng.latitude);
        dest.writeDouble(latLng.longitude);
        dest.writeString(date.toString());
        dest.writeString(foundEndTime.toString());
        dest.writeString(foundStartTime.toString());
    }

    // De-parcel object
    public WeatherParameters(Parcel in){
        try {
            data = new JSONObject(in.readString());

            List<String> dataStringList = in.readArrayList(null);
            dataInFoundTimeSpan = new ArrayList<>();
            for(int i = 0; i < dataStringList.size(); i++){
                dataInFoundTimeSpan.add(new JSONArray(dataStringList.get(i)));
            }

            point = in.readInt();
            Double lat = in.readDouble();
            Double lon = in.readDouble();
            latLng = new LatLng(lat, lon);
            date = new LocalDate(in.readString());
            foundEndTime = new LocalTime(in.readString());
            foundStartTime = new LocalTime(in.readString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Creator

    public static final Parcelable.Creator<WeatherParameters> CREATOR = new Parcelable.Creator<WeatherParameters>() {
        @Override
        public WeatherParameters createFromParcel(Parcel in) {
            return new WeatherParameters(in);
        }

        @Override
        public WeatherParameters[] newArray(int size) {
            return new WeatherParameters[size];
        }
    };

}

