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

import org.json.JSONException;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.anderssjobom.weathertracker.model.WeatherParameters;

class ResultRecyclerAdapter extends RecyclerView.Adapter<ResultRecyclerAdapter.ResultRecyclerViewHolder> {

    private static ArrayList<WeatherParameters> arrayList = new ArrayList<>();

    public ResultRecyclerAdapter(ArrayList<WeatherParameters> weatherParameters)
    {
        this.arrayList = weatherParameters;
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

        Geocoder gc = new Geocoder(MapHolder.con); //namnet på staden som markören pekar på
        List<android.location.Address> list = null;        //Få namn på område
        try {
            LatLng latLng = weatherData.getLatLng();
            list = gc.getFromLocation(latLng.latitude, latLng.longitude, 1); //TODO Maybe implement a better way to get locality name
        } catch (IOException e) {     //TODO Improve the info window
            e.printStackTrace();
        }

        if (list.isEmpty()){   //Ifall vi inte får någon information om området
            holder.resultText.setText("No information");
        } else {
            Log.d("Size" , Integer.toString(list.size()));
            android.location.Address address = list.get(0);
            if (address.getLocality() != null) {
                holder.resultText.setText(address.getLocality());//Vi sätter text som ska förekomma i markör-fönster
            } else {
                holder.resultText.setText(address.getSubLocality());
            }
        }

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);

       if (MapHolder.parametersToUse.containsKey("temperature")){
                //TextView tvTemp = new TextView(MapHolder.con);
                holder.tempText.setText("Temperatur: " + df.format(weatherData.getTemperature()) + "°C");
                //holder.tempText.setText(tvTemp);
        } else { holder.tempText.setVisibility(View.GONE);}
        if (MapHolder.parametersToUse.containsKey("windSpeed")){
            Log.d("Contains" , "Windspeed");
                holder.windText.setText("Vindhastighet: " + df.format(weatherData.getWindspeed()) + " m/s");
        }
        else { holder.windText.setVisibility(View.GONE);}

        if (MapHolder.parametersToUse.containsKey("cloudCover")){
                holder.cloudText.setText("Molntäcke: " + df.format(weatherData.getCloudCover() * 12.5) + "%");
        }
        else { holder.cloudText.setVisibility(View.GONE);}
                holder.weatherIcon.setImageDrawable(weatherData.getWeatherImage());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }




    public static class ResultRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //ImageView Im_Activity;
        TextView resultText;
        TextView tempText;
        TextView windText;
        TextView cloudText;
        ImageView weatherIcon;

        public ResultRecyclerViewHolder(View v)
        {
            super(v);
            v.setOnClickListener(this);
            resultText = (TextView) v.findViewById(R.id.result_list_text);
            tempText = (TextView) v.findViewById(R.id.result_list_temp);
            windText = (TextView) v.findViewById(R.id.result_list_wind);
            cloudText = (TextView) v.findViewById(R.id.result_list_cloud);
            weatherIcon = (ImageView) v.findViewById(R.id.result_list_image);

        }

        @Override
        public void onClick(View v)
        {
            //När man klickar på ett card så händer något här


        }

    }


}

