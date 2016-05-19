package se.anderssjobom.weathertracker;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;

import java.util.ArrayList;

import java.text.SimpleDateFormat;


public class Aktivitet_Fragment extends Fragment //implements View.OnClickListener
{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private GridLayoutManager layoutManager;
    String[] a_name;
    int[] img_res =
            {R.drawable.ic_sunny, R.drawable.ic_mostly_cloudy,
            R.drawable.ic_haze,R.drawable.ic_cloudy,
            R.drawable.ic_slight_rain,R.drawable.ic_rain,
            R.drawable.ic_thunderstorms,R.drawable.ic_snow};
    ArrayList<DataProvider> DataArray = new ArrayList<DataProvider>();

    //väderparametrar beroende på vilket card som man klickat på
    public static int currentTempValue;
    public static int currentWindValue;
    public static int currentCloudValue;
    public static double currentRainValue;
    public static String currentCard;

    //Förbestämda, tillfälliga väder-värden till varje card
    //värde [0] i varje array tillhör "sol"-cardet osv...
    //Ändra till listor om vi lägger till funktion för att lägga till egna cards
    private int[] tempValues = {20,20,31,-1000,-1000,31,31,-5}; //temp i grader (-1000 är "don't care")
    private int[] cloudValues ={ 0, 3, 6, 8, 4, 8, 4, 3}; //molnighet *1/8
    private int[] windValues = {-1,-1,-1,-1,-1,-1, 3,-1}; //vind i m/s
    private double[] rainValues = {0,0,1,0,20,100,50,50}; //regn 0-100mm


    public Aktivitet_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_aktivitet, container, false);

        //Strängar/namn till varje card finns i en string-array i Strings-xml:en
        a_name = getResources().getStringArray(R.array.activity_name);

        //lägger till varje card till arrayen som sedan används av adaptern
        int i = 0;
        for(String name : a_name)
        {
            DataProvider dataProvider = new DataProvider(img_res[i],name, tempValues[i], windValues[i],
                    cloudValues[i], rainValues[i]);
            DataArray.add(dataProvider);
            i++;
        }

        //skapa recyclerview
        recyclerView = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        adapter = new RecylerAdapter(DataArray);
        layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        return thisView;
    }
}
