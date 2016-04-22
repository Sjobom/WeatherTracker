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
            someCode();
        }

        // Inflate the layout for this fragment
        return thisView;
    }



    @Override
    public void onStart() {
        super.onStart();

    }

    private void initSeekbars(View thisView){
        SeekBar tempSeekBar = (SeekBar) thisView.findViewById(R.id.temperature_seekbar);
        tempSeekBar.setMax(14);
        tempSeekBar.incrementProgressBy(1);
        tempSeekBar.setEnabled(false);
        tempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                TextView tempText = (TextView) seekBar.getRootView().findViewById(R.id.temperatur_textview_response);
                if (progress == 0) {
                    tempText.setText("minimal");
                } else if(progress == 14){
                    tempText.setText("maximal");
                }
                else {
                    tempText.setText(Integer.toString((progress - 7) * 5) + "°C");
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
        tempSeekBar.setMax(6);
        tempSeekBar.incrementProgressBy(1);
        tempSeekBar.setEnabled(false);
        tempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                TextView windText = (TextView) seekBar.getRootView().findViewById(R.id.wind_strength_textview_response);
                if (progress == 6) {
                    windText.setText("maximal");
                } else {
                    windText.setText(Integer.toString(progress * 5) + " m/s");
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
        tempSeekBar.setMax(3);
        tempSeekBar.incrementProgressBy(1);
        tempSeekBar.setEnabled(false);
        tempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                TextView cloudText = (TextView) seekBar.getRootView().findViewById(R.id.cloud_cover_textview_response);
                switch (progress){
                    case 0:
                        cloudText.setText("NEIN");
                        break;
                    case 1:
                        cloudText.setText("Lite");
                        break;
                    case 2:
                        cloudText.setText("Mellan");
                        break;
                    case 3:
                        cloudText.setText("Mycket");
                        break;
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null){
            isVisible = true;
            someCode();
        }
        else {
            isVisible = false;
        }
    }

    private void someCode(){
        String df1 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText1.getTime());
        String df2 = new SimpleDateFormat("yyyy-MM-dd").format(MainActivity.buttText2.getTime());
        start_date_button.setText(df1);
        end_date_button.setText(df2);
    }
}
