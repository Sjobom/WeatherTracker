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

import java.util.ArrayList;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class Aktivitet_Fragment extends Fragment implements View.OnClickListener
{
    Button button1;
    Button button2;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private GridLayoutManager layoutManager;
    String[] a_name;
    int[] img_res = {R.drawable.swimcolorsmall, R.drawable.bikecolorsmall, R.drawable.sailcolorsmall,R.drawable.hikecolorsmall, R.drawable.horsebackcolorsmall,R.drawable.potatoes,R.drawable.running,R.drawable.loningsmall};
    ArrayList<DataProvider> DataArray = new ArrayList<DataProvider>();



    public Aktivitet_Fragment() {
        // Required empty public constructor
    }

    public void onClick(View v) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_aktivitet, container, false);


        recyclerView = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        a_name = getResources().getStringArray(R.array.activity_name);
        int i = 0;
        for(String name : a_name)
        {
            DataProvider dataProvider = new DataProvider(img_res[i],name);
            DataArray.add(dataProvider);
            i++;
        }

        recyclerView = (RecyclerView) thisView.findViewById(R.id.recycler_view);
        adapter = new RecylerAdapter(DataArray);
        layoutManager = new GridLayoutManager(getActivity(),2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);



        return thisView;

    }



}
