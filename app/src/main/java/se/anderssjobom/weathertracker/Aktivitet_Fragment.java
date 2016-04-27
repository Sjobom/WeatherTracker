package se.anderssjobom.weathertracker;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    private RecyclerView.LayoutManager layoutManager;
    String[] a_name;

    int[] img_res = {R.drawable.ic_swim_black_48dp, R.drawable.bike_black, R.drawable.sail,R.drawable.hike};
    ArrayList<DataProvider> arrayList = new ArrayList<DataProvider>();



    private boolean isVisible = false;


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
      //      DataProvider dataProvider = new DataProvider(img_res[i],name);
      //      arrayList.add(dataProvider);
            i++;
        }

        adapter = new RecylerAdapter(arrayList);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        button1 = (Button)thisView.findViewById(R.id.DateButton);
        button2 = (Button)thisView.findViewById(R.id.DateButton2);

        if (!isVisible){
            someCode();
        }

        return thisView;

    }



    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null){
            isVisible = true;
            someCode();
        }
        else {
            isVisible = false;
        }
    }

    private void someCode(){
        String df1 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText1.getTime());
        String df2 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText2.getTime());
        button1.setText(df1);
        button2.setText(df2);
    }
}
