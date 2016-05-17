package se.anderssjobom.weathertracker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


import com.appyvet.rangebar.RangeBar;

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

        //sätter in värden på varje card (denna metod loopas igenom beroende på hur många cards som finns)
        holder.Im_Activity.setImageResource(dataProvider.getImg_res());
        holder.Tx_Activity.setText(dataProvider.getA_name());
        holder.tempVal = dataProvider.getTempValue();
        holder.windVal = dataProvider.getWindValue();
        holder.cloudVal = dataProvider.getCloudValue();
        holder.rainVal = dataProvider.getRainValue();
        holder.cardName = dataProvider.getA_name();


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        //Varje cards innehåll
        ImageView Im_Activity;
        TextView Tx_Activity;
        int tempVal;
        int windVal;
        int cloudVal;
        String cardName;
        double rainVal;


        public RecyclerViewHolder(View Gridview)
        {
            super(Gridview);
            Gridview.setOnClickListener(this);

            //sätter bild och namn på rätt plats
            Tx_Activity = (TextView) Gridview.findViewById(R.id.activity_name);
            Im_Activity = (ImageView) Gridview.findViewById(R.id.activity_image);


        }

        @Override
        public void onClick(View v) {
            // vibrator för knapptryck
            Vibrator vibrator = (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(20);

            //skriver väder-värdet från card till variabler som ska användas
            Aktivitet_Fragment.currentTempValue = tempVal;
            Aktivitet_Fragment.currentWindValue =windVal;
            Aktivitet_Fragment.currentCloudValue = cloudVal;
            Aktivitet_Fragment.currentRainValue = rainVal;
            Log.d("onClickCard",cardName);
            Aktivitet_Fragment.currentCard = cardName;

            //popup när man klickar på ett card
            Resources resources = v.getContext().getResources();
            int size = 0;
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            if (resourceId > 0) {
                size = resources.getDimensionPixelSize(resourceId);
            }
            View popView = View.inflate(v.getContext(), R.layout.popup,null);
            popupWindow = new PopupWindow(popView, ActionBar.LayoutParams.MATCH_PARENT,ActionBar.LayoutParams.WRAP_CONTENT,true);
            popupWindow.setAnimationStyle(android.R.style.Animation_InputMethod);
            
            popupWindow.setBackgroundDrawable(new ShapeDrawable());

            popupWindow.showAtLocation(popView, Gravity.BOTTOM,0,size);



            popView.setAlpha((float)0.8); //lite transparent

            RangeBar timeBarFav = (RangeBar) popView.findViewById(R.id.time_bar_favourites);

            TextView timeBarResponse = (TextView) popView.findViewById(R.id.time_bar_textview_response_favourites);
            timeBarResponse.setText(timeBarFav.getLeftIndex() + "-" + timeBarFav.getRightIndex());

            timeBarFav.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
                @Override
                public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                    TextView timeBarResponse = (TextView) rangeBar.getRootView().findViewById(R.id.time_bar_textview_response_favourites);
                    timeBarResponse.setText(leftPinValue + "-" + rightPinValue);
                    MainActivity.leftIndex = leftPinIndex;
                    MainActivity.rightIndex = rightPinIndex;

                }
            });

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
                    Log.d("SNELL", "HEEEEEEST");
                    Im_Activity.setImageAlpha(255);
                    Tx_Activity.setAlpha((float) 1);
                    local_pressed = false;
                    someCode();
                }
            });
            //Log.d("Ramiiiii", "asd");

        }

    }

    public static void someCode(){
        String df1 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText1.getTime());
        String df2 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText2.getTime());
        button1.setText(df1);
        button2.setText(df2);
    }


}

