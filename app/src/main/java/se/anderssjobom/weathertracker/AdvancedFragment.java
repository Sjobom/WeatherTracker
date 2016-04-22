package se.anderssjobom.weathertracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdvancedFragment extends Fragment implements View.OnClickListener {
    Button button1;
    Button button2;


    public void onClick(View v) {

    }

    public AdvancedFragment()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_avancerad, container, false);

        // Inflate the layout for this fragment
        return thisView;
    }


    @Override
    public void onStart() {
        super.onStart();



    }


}
