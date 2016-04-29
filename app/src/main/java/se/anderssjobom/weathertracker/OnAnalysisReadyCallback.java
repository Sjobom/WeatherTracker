package se.anderssjobom.weathertracker;

import java.util.List;

import se.anderssjobom.weathertracker.model.WeatherParameters;

/**
 * Created by ander on 29/04/2016.
 */
public interface OnAnalysisReadyCallback {
    void onAnalysisReady (List<WeatherParameters> resultList);
}
