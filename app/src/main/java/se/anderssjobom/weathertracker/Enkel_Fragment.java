package se.anderssjobom.weathertracker;



import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class Enkel_Fragment extends Fragment implements View.OnClickListener {

    Button start_date_button;
    Button end_date_button;
    SeekBar tempSeekBar;
    SeekBar cloudSeekBar;
    SeekBar windSeekBar;
    private boolean isVisible = false;

    public void onClick(View v) {

    }
    public Enkel_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_enkel, container, false);

        start_date_button = (Button) thisView.findViewById(R.id.start_date_button_simple);
        end_date_button = (Button) thisView.findViewById(R.id.end_date_button_simple);

        tempSeekBar = (SeekBar) thisView.findViewById(R.id.tempSeekBar);
        tempSeekBar.setMax(2);
        tempSeekBar.incrementProgressBy(1);

        cloudSeekBar = (SeekBar) thisView.findViewById(R.id.cloudSeekBar);
        cloudSeekBar.setMax(3);
        cloudSeekBar.incrementProgressBy(1);

        windSeekBar = (SeekBar) thisView.findViewById(R.id.windSeekBar);
        windSeekBar.setMax(3);
        windSeekBar.incrementProgressBy(1);

        final TextView tempBarValue = (TextView)thisView.findViewById(R.id.tempSelectText);
        final TextView cloudBarValue = (TextView)thisView.findViewById(R.id.cloudSelectText);
        final TextView windBarValue = (TextView)thisView.findViewById(R.id.windSelectText);

        //Temperaturslider med states
        tempSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress)
                {
                    case 2:
                        tempBarValue.setText("Max temp");
                        break;
                    case 1:
                        tempBarValue.setText("Kallast temp");
                        break;
                    case 0:
                        tempBarValue.setText("Av");
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
        cloudSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress)
                {
                    case 3:
                        cloudBarValue.setText("Mycket");
                        break;
                    case 2:
                        cloudBarValue.setText("Mellan");
                        break;
                    case 1:
                        cloudBarValue.setText("Lite");
                        break;
                    case 0:
                        cloudBarValue.setText("Inget");
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
        windSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch (progress)
                {
                    case 3:
                        windBarValue.setText("Stark");
                        break;
                    case 2:
                        windBarValue.setText("Medel");
                        break;
                    case 1:
                        windBarValue.setText("Svag");
                        break;
                    case 0:
                        windBarValue.setText("Av");
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



