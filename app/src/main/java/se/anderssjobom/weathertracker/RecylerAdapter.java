package se.anderssjobom.weathertracker;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;



import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by ThimLohse on 2016-04-22.
 */
public class RecylerAdapter extends RecyclerView.Adapter<RecylerAdapter.RecyclerViewHolder> {

    private static ArrayList<DataProvider> arrayList = new ArrayList<>();
    public static boolean local_pressed = false;
    public static PopupWindow popupWindow;
    public static Button button1;
    public static Button button2;
    public static String ID_ACTIVITY_BTN;



    public RecylerAdapter(ArrayList<DataProvider> dataProviders) {
        this.arrayList = dataProviders;
    }


    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //Skapar en view som ska "inflate" dvs visa vår row_layout som definerar formen på våra element,
        //inuti vår recyclerview.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);



        return recyclerViewHolder;
    }

    //Kopplar ihop en posistion i vår RecyclerView till varje Viewholder-element.
    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
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


        public RecyclerViewHolder(View Gridview)
        {
            super(Gridview);
            Gridview.setOnClickListener(this);


            Tx_Activity = (TextView) Gridview.findViewById(R.id.activity_name);
            Im_Activity = (ImageView) Gridview.findViewById(R.id.activity_image);

        }


        @Override
        public void onClick(View v) {
            // vibrator för knapptryck
            Vibrator vibrator = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(20);

            View popView = View.inflate(v.getContext(), R.layout.popup,null);
            popupWindow = new PopupWindow(popView, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT,true);
            popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
            popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,0);
            popView.setAlpha((float)0.8);

            button1 = (Button)popView.findViewById(R.id.DateButton);
            button2 = (Button)popView.findViewById(R.id.DateButton2);


            //Lägg till eller ta bort denna kod om man vill ha startdatum på båda datumknappar istället för "hints" om start och slutdatum
            someCode();


            //om tryckt - ändra alpha
            if (!local_pressed) {
                Im_Activity.setImageAlpha(100);
                Tx_Activity.setAlpha((float) 0.6);
                local_pressed = true;
            }

            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    Im_Activity.setImageAlpha(255);
                    Tx_Activity.setAlpha((float) 1);
                    local_pressed = false;
                    someCode();
                }
            });


        }

    }

    public static void someCode(){
        String df1 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText1.getTime());
        String df2 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText2.getTime());
        button1.setText(df1);
        button2.setText(df2);
    }


}

