package se.anderssjobom.weathertracker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ThimLohse on 2016-04-22.
 */
public class RecylerAdapter extends RecyclerView.Adapter<RecylerAdapter.RecyclerViewHolder> {

    private ArrayList<DataProvider> arrayList = new ArrayList<DataProvider>();



    public RecylerAdapter(ArrayList<DataProvider> dataProviders)
    {
        this.arrayList = dataProviders;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Skapar en view som ska "inflate" dvs visa vår row_layout som definerar formen på våra element,
        //inuti vår recyclerview.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);

        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);



        return recyclerViewHolder;
    }

    //Kopplar ihop en posistion i vår RecyclerView till varje Viewholder-element.
    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position)
    {
       final DataProvider dataProvider = arrayList.get(position);
        holder.Im_Activity.setImageResource(dataProvider.getImg_res());
        holder.Tx_Activity.setText(dataProvider.getA_name());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ImageView Im_Activity;
        TextView Tx_Activity;

        public RecyclerViewHolder(View view)
        {
            super(view);
            Tx_Activity = (TextView) view.findViewById(R.id.activity_name);
            Im_Activity = (ImageView) view.findViewById(R.id.activity_image);
            Im_Activity.setOnClickListener(this);



        }

        @Override
        public void onClick(View v) {

            Toast.makeText(v.getContext(),"Du har valt " +Tx_Activity.getText().toString(),Toast.LENGTH_SHORT).show();



        }
    }
}
