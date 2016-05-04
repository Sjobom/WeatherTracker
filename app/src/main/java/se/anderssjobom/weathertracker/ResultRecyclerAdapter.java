package se.anderssjobom.weathertracker;


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
import java.util.ArrayList;
import java.util.List;

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
            return;
        } else {
            Log.d("Size" , Integer.toString(list.size()));
            android.location.Address address = list.get(0);
            if (address.getLocality() != null) {
                holder.resultText.setText(address.getLocality());//Vi sätter text som ska förekomma i markör-fönster
            } else {
                holder.resultText.setText(address.getSubLocality());
            }
        }

       if (MapHolder.parametersToUse.containsKey("temperature")){
            try {
                //TextView tvTemp = new TextView(MapHolder.con);
                holder.tempText.setText("Temperature: " + Double.toString(weatherData.getTemperature()) + "°C");
                //holder.tempText.setText(tvTemp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else { holder.tempText.setVisibility(View.GONE);}
        if (MapHolder.parametersToUse.containsKey("windSpeed")){
            Log.d("Contains" , "Windspeed");
            try {
                //TextView tvWind = new TextView(MapHolder.con);
                holder.windText.setText("WindSpeed : " + Double.toString(weatherData.getWindspeed()) + " m/s");
                //holder.addView(tvWind);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else { holder.windText.setVisibility(View.GONE);}

        if (MapHolder.parametersToUse.containsKey("cloudCover")){
            try {
                //TextView tvWind = new TextView(MapHolder.con);
                holder.cloudText.setText("Cloud coverage : " + Double.toString(weatherData.getCloudCover() * 12.5) + "%");
                //holder.addView(tvWind);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else { holder.cloudText.setVisibility(View.GONE);}
        try {
            int cloud = weatherData.getCloudCover();
            if (cloud <= 2){
                holder.weatherIcon.setImageResource(R.drawable.ic_sunny);
            } else if (cloud > 2 && cloud <= 5){
                holder.weatherIcon.setImageResource(R.drawable.ic_mostly_cloudy);
            } else if (cloud > 5){
                holder.weatherIcon.setImageResource(R.drawable.ic_cloudy);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //testar skriver ut poängen
        //holder.resultText.setText(Integer.toString(dataProvider.getPoint()));
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

