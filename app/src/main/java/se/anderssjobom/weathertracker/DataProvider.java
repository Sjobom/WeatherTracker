package se.anderssjobom.weathertracker;

/**
 * Created by ThimLohse on 2016-04-22.
 */
public class DataProvider {
    private int img_res;
    private String a_name;

    public DataProvider(int img_res, String a_name)
    {
        this.setImg_res(img_res);
        this.setA_name(a_name);
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
}
