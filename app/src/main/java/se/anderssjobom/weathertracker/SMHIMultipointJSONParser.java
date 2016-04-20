package se.anderssjobom.weathertracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import se.anderssjobom.weathertracker.model.WeatherDataPoint;

/**
 * Created by ander on 20/04/2016.
 */
public class SMHIMultipointJSONParser {

    public static List<WeatherDataPoint> parseFeed(String content) throws JSONException {
            JSONObject obj = new JSONObject(content);
            JSONArray ar = obj.getJSONArray("timeSeries").getJSONObject(0)
                    .getJSONArray("parameters").getJSONObject(0).getJSONArray("values");
            List<WeatherDataPoint> list = new ArrayList<>();
            for (int i = 0; i < 100; i++){
                WeatherDataPoint wdp = new WeatherDataPoint();
                wdp.setTemperature(ar.getInt(i));
                list.add(wdp);
            }
        return list;
    }
}
