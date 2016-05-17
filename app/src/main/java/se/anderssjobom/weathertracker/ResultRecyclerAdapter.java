package se.anderssjobom.weathertracker;


import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;

import java.io.IOException;
import java.text.DecimalFormat;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.anderssjobom.weathertracker.model.WeatherParameters;

class ResultRecyclerAdapter extends RecyclerView.Adapter<ResultRecyclerAdapter.ResultRecyclerViewHolder> {

    private static ArrayList<WeatherParameters> arrayList = new ArrayList<>();
    private static MapHolder mapHolder;

    public ResultRecyclerAdapter(ArrayList<WeatherParameters> weatherParameters, MapHolder mapHolder)
    {
        this.arrayList = weatherParameters;
        this.mapHolder = mapHolder;
    }

    @Override
    public  ResultRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Skapar en view som ska "inflate" dvs visa vår result_row_layout som definerar formen på våra element,
        //inuti vår recyclerview.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_row_layout, parent, false);
        ResultRecyclerViewHolder recyclerViewHolder = new ResultRecyclerViewHolder(view);

        return recyclerViewHolder;
    }
    //Kopplar ihop en posistion i vår RecyclerView till varje Viewholder-element.
    @Override
    public void onBindViewHolder(final ResultRecyclerAdapter.ResultRecyclerViewHolder holder, final int position) {
        final WeatherParameters weatherData = arrayList.get(position);
        holder.position = weatherData.getLatLng();

        Geocoder gc = new Geocoder(MapHolder.con); //namnet på staden som markören pekar på
        holder.resultText.setText(MapActivity.geocoder(weatherData.getLatLng(), gc));

        DateTimeFormatter dtfDate = DateTimeFormat.forPattern("dd/M");
        DateTimeFormatter dtfTime = DateTimeFormat.forPattern("HH:mm");
        holder.dateText.setText(weatherData.getDate().toString() + " "
                + dtfTime.print(weatherData.getFoundStartTime()) + "-"
                + dtfTime.print(weatherData.getFoundEndTime()));

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);

       if (MapHolder.parametersToUse.containsKey("temperature")){
           holder.tempText.setText("Temperatur: " + df.format(weatherData.getTemperature()) + "°C");
        } else { holder.tempText.setVisibility(View.GONE);}

        if (MapHolder.parametersToUse.containsKey("windSpeed")){
            holder.windText.setText("Vindhastighet: " + df.format(weatherData.getWindspeed()) + " m/s");
        } else { holder.windText.setVisibility(View.GONE);}

        if (MapHolder.parametersToUse.containsKey("cloudCover")){
                holder.cloudText.setText("Molntäcke: " + df.format(weatherData.getCloudCover() * 12.5) + "%");
        } else { holder.cloudText.setVisibility(View.GONE);}

        if (MapHolder.parametersToUse.containsKey("rain")){
            holder.rainText.setText("Nederbörd: " + df.format(weatherData.getRain()) +  " mm");
        }else{holder.rainText.setVisibility(View.GONE);}
        holder.weatherIcon.setImageDrawable(weatherData.getWeatherImage());
        }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }




    public static class ResultRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //ImageView Im_Activity;
        TextView dateText;
        TextView resultText;
        TextView tempText;
        TextView windText;
        TextView cloudText;
        TextView rainText;
        ImageView weatherIcon;
        LatLng position;

        public ResultRecyclerViewHolder(View v)
        {
            super(v);
            v.setOnClickListener(this);
            dateText = (TextView) v.findViewById(R.id.result_date_text);
            resultText = (TextView) v.findViewById(R.id.result_list_text);
            tempText = (TextView) v.findViewById(R.id.result_list_temp);
            windText = (TextView) v.findViewById(R.id.result_list_wind);
            cloudText = (TextView) v.findViewById(R.id.result_list_cloud);
            rainText = (TextView) v.findViewById(R.id.result_list_rain);
            weatherIcon = (ImageView) v.findViewById(R.id.result_list_image);


        }

        @Override
        public void onClick(View v)
        {
            mapHolder.showDetails(v, position);
        }

    }
}

