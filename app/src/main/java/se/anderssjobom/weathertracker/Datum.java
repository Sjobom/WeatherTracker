package se.anderssjobom.weathertracker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class Datum extends Fragment implements View.OnClickListener {
    Button button1;
    Button button2;


    public void onClick(View v) {

    }

    public Datum()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_datum, container, false);

        // Inflate the layout for this fragment
        button1 = (Button)thisView.findViewById(R.id.avancerad_vy_button1);
        button2 = (Button)thisView.findViewById(R.id.avancerad_vy_button2);
        return thisView;
    }


    @Override
    public void onStart() {

        button1.setText(MainActivity.buttText1);
        button2.setText(MainActivity.buttText2);
        super.onStart();



    }


}
