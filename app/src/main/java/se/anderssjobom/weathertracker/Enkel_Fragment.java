package se.anderssjobom.weathertracker;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class Enkel_Fragment extends Fragment{

    Button start_date_button;
    Button end_date_button;
    SeekBar parameterSeekBar;
    public static NumberPicker picker;
    TextView valueText;
    TextView paramText;

    private boolean isVisible = false;

    public Enkel_Fragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_enkel, container, false);

        //skapar knappar
        start_date_button = (Button) thisView.findViewById(R.id.start_date_button_simple);
        end_date_button = (Button) thisView.findViewById(R.id.end_date_button_simple);

        //skapar text-vyer
        valueText = (TextView) thisView.findViewById(R.id.valueText);
        valueText.setText("Kallast");
        paramText = (TextView) thisView.findViewById(R.id.paramTextView);
        paramText.setText("Temperatur: ");

        //skapar seekbar
        parameterSeekBar = (SeekBar) thisView.findViewById(R.id.enkelSeekBar);
        parameterSeekBar.setMax(1);
        parameterSeekBar.setProgress(0);



        //skapar numberpicker
        picker = (NumberPicker) thisView.findViewById(R.id.parameterPicker);
        picker.setMinValue(0);
        picker.setMaxValue(3);
        picker.setValue(0);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);//ta bort keyboard
        picker.setDisplayedValues( new String[] { "Temperatur", "Vindhastighet", "Molnighet", "Nederbörd" } );
        picker.setWrapSelectorWheel(false); //hindra alternativen från att loopa runt på pickern
        picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                Log.d("PICKER", Integer.toString(picker.getValue()));

                switch (picker.getValue())
                {
                    case 0:
                        parameterSeekBar.setMax(1);
                        parameterSeekBar.setProgress(0);
                        paramText.setText("Temperatur: ");
                        break;

                    case 1:
                        paramText.setText("Vind: ");
                        parameterSeekBar.setProgress(0);
                        parameterSeekBar.setMax(2);
                        break;

                    case 2:
                        paramText.setText("Molnighet: ");
                        parameterSeekBar.setMax(2);
                        parameterSeekBar.setProgress(0);
                        break;

                    case 3:
                        paramText.setText("Nederbörd: ");
                        parameterSeekBar.setMax(1);
                        parameterSeekBar.setProgress(0);
                        break;
                }
            }
        });

        //Rangebar till tid-väljarN
        RangeBar timeBarSimple = (RangeBar) thisView.findViewById(R.id.time_bar_simple);

        TextView timeBarResponse = (TextView) thisView.findViewById(R.id.time_bar_textview_simple_response);
        timeBarResponse.setText(timeBarSimple.getLeftIndex() + "-" + timeBarSimple.getRightIndex());

        timeBarSimple.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                TextView timeBarResponse = (TextView) rangeBar.getRootView().findViewById(R.id.time_bar_textview_simple_response);
                timeBarResponse.setText(leftPinValue + "-" + rightPinValue);
                MainActivity.leftIndex = leftPinIndex;
                MainActivity.rightIndex = rightPinIndex;
            }
        });

        //Ändrar värden på valueText beroende på seeBarProgress
        parameterSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (picker.getValue())
                {
                    case 0 :
                        if(progress == 0)
                            valueText.setText("Kallast");
                        else
                            valueText.setText("Varmast");
                        break;
                    case 1:
                        if(progress == 0)
                            valueText.setText("Svag");
                        else if (progress == 1)
                            valueText.setText("Mellan");
                        else
                            valueText.setText("Stark");
                        break;
                    case 2:
                        if(progress == 0)
                            valueText.setText("Lite");
                        else if(progress == 1)
                            valueText.setText("Mellan");
                        else
                            valueText.setText("Mycket");
                        break;
                    case 3:
                        if(progress == 0)
                            valueText.setText("Av");
                        else
                            valueText.setText("På");
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        return thisView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null){
            isVisible = true;
            writeDate();
        }
        else {
            isVisible = false;
        }
    }

    private void writeDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String df1 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText1.getTime());
        Log.d("Toime", sdf.format(MainActivity.buttText1.getTime()));
        Log.d("Toime", Long.toString(MainActivity.buttText1.getTimeInMillis()));
        String df2 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText2.getTime());
        start_date_button.setText(df1);
        end_date_button.setText(df2);
    }

}


