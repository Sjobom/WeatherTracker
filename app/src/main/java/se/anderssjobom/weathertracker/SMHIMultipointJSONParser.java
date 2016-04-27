package se.anderssjobom.weathertracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import se.anderssjobom.weathertracker.model.WeatherParameters;

/**
 * Created by ander on 20/04/2016.
 */
public class SMHIMultipointJSONParser {

    public static List<WeatherParameters> parseFeed(String content) throws JSONException {
            JSONObject obj = new JSONObject(content);
            JSONArray ar = obj.getJSONArray("timeSeries").getJSONObject(0)
                    .getJSONArray("parameters").getJSONObject(0).getJSONArray("values");
            List<WeatherParameters> list = new ArrayList<>();
            for (int i = 0; i < 100; i++){
                WeatherParameters wdp = new WeatherParameters();
                wdp.setTemperature(ar.getInt(i));
                list.add(wdp);
            }
        return list;
    }
}
