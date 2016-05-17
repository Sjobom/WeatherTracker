package se.anderssjobom.weathertracker;

/**
 * Created by ThimLohse on 2016-04-22.
 */
public class DataProvider {
    private int img_res;
    private String a_name;
    private int tempValue;
    private int windValue;
    private int cloudValue;
    public double rainValue;

    public DataProvider(int img_res, String a_name)
    {
        this.setImg_res(img_res);
        this.setA_name(a_name);
    }
    //Skapade extra konstruktor med temperaturvärde temp
    public DataProvider(int img_res, String a_name, int temp)
    {
        this.tempValue = temp;
        this.a_name = a_name;
        this.img_res = img_res;

    }
    public DataProvider(int img_res, String a_name, int temp, int wind)
    {
        this.tempValue = temp;
        this.a_name = a_name;
        this.img_res = img_res;
        this.windValue = wind;
    }
    public DataProvider(int img_res, String a_name, int temp, int wind, int cloud)
    {
        this.tempValue = temp;
        this.a_name = a_name;
        this.img_res = img_res;
        this.windValue = wind;
        this.cloudValue = cloud;

    }
    public DataProvider(int img_res, String a_name, int temp, int wind, int cloud, double rain)
    {
        this.tempValue = temp;
        this.a_name = a_name;
        this.img_res = img_res;
        this.windValue = wind;
        this.cloudValue = cloud;
        this.rainValue = rain;

    }


    public int getImg_res() {
        return img_res;
    }

    public void setImg_res(int img_res) {
        this.img_res = img_res;
    }

    public String getA_name() {
        return a_name;
    }

    public void setA_name(String a_name) {
        this.a_name = a_name;
    }

    public void setTempValue(int val)
    {
        tempValue = val;
    }

    public void setCloudValue(int cloudValue) {
        this.cloudValue = cloudValue;
    }

    public void setRainValue(int rainValue) {
        this.rainValue = rainValue;
    }

    public void setWindValue(int windValue) {
        this.windValue = windValue;
    }

    public int getTempValue()
    {
        return tempValue;
    }

    public int getCloudValue() {
        return cloudValue;
    }

    public double getRainValue() {
        return rainValue;
    }

    public int getWindValue() {
        return windValue;
    }
}
