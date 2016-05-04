package se.anderssjobom.weathertracker;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
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
        final WeatherParameters dataProvider = arrayList.get(position);

        //testar skriver ut poängen
        holder.resultText.setText(Integer.toString(dataProvider.getPoint()));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }




    public static class ResultRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //ImageView Im_Activity;
        TextView resultText;
        ImageView weatherIcon;

        public ResultRecyclerViewHolder(View v)
        {
            super(v);
            v.setOnClickListener(this);
            resultText = (TextView) v.findViewById(R.id.result_text_view);

            weatherIcon = (ImageView) v.findViewById(R.id.weatherIcon);

        }

        @Override
        public void onClick(View v)
        {
            //När man klickar på ett card så händer något här
        }
    }


}

