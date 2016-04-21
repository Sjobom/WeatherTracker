package se.anderssjobom.weathertracker;


import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class Aktivitet_Fragment extends Fragment implements View.OnClickListener
{
    Button button1;
    Button button2;

    public Aktivitet_Fragment() {
        // Required empty public constructor
    }


    public void onClick(View v) {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_aktivitet, container, false);

        // Inflate the layout for this fragment
        button1 = (Button)thisView.findViewById(R.id.DateButton);
        button2 = (Button)thisView.findViewById(R.id.DateButton2);
        return thisView;


    }




    @Override
    public void onStart() {

        button1.setText(MainActivity.buttText1);
        button2.setText(MainActivity.buttText2);
        super.onStart();



    }
}
