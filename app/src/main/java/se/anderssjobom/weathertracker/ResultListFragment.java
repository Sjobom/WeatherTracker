package se.anderssjobom.weathertracker;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Fragment;
import android.widget.ProgressBar;

import se.anderssjobom.weathertracker.model.WeatherParameters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class ResultListFragment extends Fragment {

    public static RecyclerView recyclerView ;
    public static RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    public static ArrayList<WeatherParameters> wp = new ArrayList<>();
    String[] str;
    public static RecyclerView.Adapter mAdapter;
    public static RecyclerView mRecyclerView;


    public ResultListFragment() {
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        Log.d("ResultListFragment", "onCreateView");

        //Skapar recyclerView
        mRecyclerView = (RecyclerView) view.findViewById(R.id.result_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        //skapar och lägger till manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        return view;
    }

}
