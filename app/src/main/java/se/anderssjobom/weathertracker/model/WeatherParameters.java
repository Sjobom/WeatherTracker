package se.anderssjobom.weathertracker.model;

/**
 * Created by ander on 20/04/2016.
 */
public class WeatherParameters {
    private double temperature;
    private double windSpeed;
    private int windDirection; //grader 0-360
    private int totalCloudCover; //åttondelar 1-8

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public void setWindDirection(int windDirection) {
        this.windDirection = windDirection;
    }

    public int getTotalCloudCover() {
        return totalCloudCover;
    }

    public void setTotalCloudCover(int totalCloudCover) {
        this.totalCloudCover = totalCloudCover;
    }
}
