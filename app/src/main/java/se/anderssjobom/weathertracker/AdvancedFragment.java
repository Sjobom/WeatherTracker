package se.anderssjobom.weathertracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.appyvet.rangebar.RangeBar;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdvancedFragment extends Fragment implements View.OnClickListener {
    Button start_date_button;
    Button end_date_button;
    private boolean isVisible = false;


    public void onClick(View v) {

    }

    public AdvancedFragment()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_avancerad, container, false);
        start_date_button = (Button) thisView.findViewById(R.id.start_date_button_advanced);
        end_date_button = (Button) thisView.findViewById(R.id.end_date_button_advanced);
        EditText tempEditText = (EditText) thisView.findViewById(R.id.wind_direction_from_edittext);
        tempEditText.setEnabled(false);
        tempEditText = (EditText) thisView.findViewById(R.id.wind_direction_to_edittext);
        tempEditText.setEnabled(false);

        initSeekbars(thisView);
        initSwitches(thisView);

        if (!isVisible){
            writeDate();
        }

        // Inflate the layout for this fragment
        return thisView;
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    private void initSeekbars(final View thisView){
        SeekBar tempSeekBar = (SeekBar) thisView.findViewById(R.id.temperature_seekbar);
        tempSeekBar.setMax(62);
        tempSeekBar.incrementProgressBy(1);
        tempSeekBar.setEnabled(false);
        tempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                TextView tempText = (TextView) seekBar.getRootView().findViewById(R.id.temperatur_textview_response);
                if (progress == 0) {
                    tempText.setText("minimal");
                } else if(progress == 62){
                    tempText.setText("maximal");
                }
                else {
                    tempText.setText(Integer.toString(progress - 31) + "°C");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        tempSeekBar = (SeekBar) thisView.findViewById(R.id.wind_strength_seekbar);
        tempSeekBar.setMax(20);
        tempSeekBar.incrementProgressBy(1);
        tempSeekBar.setEnabled(false);
        tempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                TextView windText = (TextView) seekBar.getRootView().findViewById(R.id.wind_strength_textview_response);
                if (progress == 6) {
                    windText.setText("maximal");
                } else {
                    windText.setText(Integer.toString(progress) + " m/s");
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        tempSeekBar = (SeekBar) thisView.findViewById(R.id.cloud_cover_seekbar);
        tempSeekBar.setMax(8);
        tempSeekBar.incrementProgressBy(1);
        tempSeekBar.setEnabled(false);
        tempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                TextView cloudText = (TextView) seekBar.getRootView().findViewById(R.id.cloud_cover_textview_response);
                double result = progress * 12.5;
                cloudText.setText(Double.toString(result) + "%");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        tempSeekBar = (SeekBar) thisView.findViewById(R.id.rain_seekbar);
        tempSeekBar.setMax(100);
        tempSeekBar.incrementProgressBy(1);
        tempSeekBar.setEnabled(false);
        tempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView rainText = (TextView) seekBar.getRootView().findViewById(R.id.rain_textview_response);
                rainText.setText(Integer.toString(progress) + " mm");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        RangeBar timeBarAdv = (RangeBar) thisView.findViewById(R.id.time_bar_advanced);

        TextView timeBarResponse = (TextView) thisView.findViewById(R.id.time_bar_textview_response);
        timeBarResponse.setText(timeBarAdv.getLeftIndex() + "-" + timeBarAdv.getRightIndex());

        timeBarAdv.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex, int rightPinIndex, String leftPinValue, String rightPinValue) {
                TextView timeBarResponse = (TextView) rangeBar.getRootView().findViewById(R.id.time_bar_textview_response);
                timeBarResponse.setText(leftPinValue + "-" + rightPinValue);
                MainActivity.leftIndex = leftPinIndex;
                MainActivity.rightIndex = rightPinIndex;
            }
        });

    }

    private void initSwitches(View thisView){
        Switch tempSwitch = (Switch) thisView.findViewById(R.id.temperature_switch);
        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SeekBar tempSeekBar = (SeekBar) buttonView.getRootView().findViewById(R.id.temperature_seekbar);
                if(isChecked){tempSeekBar.setEnabled(true);}
                else{tempSeekBar.setEnabled(false);}
            }
        });

        tempSwitch = (Switch) thisView.findViewById(R.id.wind_strength_switch);
        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SeekBar tempSeekBar = (SeekBar) buttonView.getRootView().findViewById(R.id.wind_strength_seekbar);
                if(isChecked){tempSeekBar.setEnabled(true);}
                else{tempSeekBar.setEnabled(false);}
            }
        });

        tempSwitch = (Switch) thisView.findViewById(R.id.cloud_cover_switch);
        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SeekBar tempSeekBar = (SeekBar) buttonView.getRootView().findViewById(R.id.cloud_cover_seekbar);
                if(isChecked){tempSeekBar.setEnabled(true);}
                else{tempSeekBar.setEnabled(false);}
            }
        });

        tempSwitch = (Switch) thisView.findViewById(R.id.wind_direction_switch);
        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText tempEditText = (EditText) buttonView.getRootView().findViewById(R.id.wind_direction_from_edittext);
                EditText tempEditText2 = (EditText) buttonView.getRootView().findViewById(R.id.wind_direction_to_edittext);
                if(isChecked){
                    tempEditText.setEnabled(true);
                    tempEditText2.setEnabled(true);
                } else{
                    tempEditText.setEnabled(false);
                    tempEditText2.setEnabled(false);
                }
            }
        });

        tempSwitch = (Switch) thisView.findViewById(R.id.rain_switch);
        tempSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SeekBar tempSeekBar = (SeekBar) buttonView.getRootView().findViewById(R.id.rain_seekbar);
                if (isChecked) {tempSeekBar.setEnabled(true);}
                else {tempSeekBar.setEnabled(false);}
            }
        });

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
        String df1 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText1.getTime());
        String df2 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText2.getTime());
        //RangeBar bar = (RangeBar) getActivity().findViewById(R.id.time_bar_advanced);
        //bar.setLeft(MainActivity.leftIndex);
        //bar.setRight(MainActivity.rightIndex);
        start_date_button.setText(df1);
        end_date_button.setText(df2);
    }
}
