package se.anderssjobom.weathertracker;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.text.SimpleDateFormat;


/**
 * A simple {@link Fragment} subclass.
 *
 */
public class Enkel_Fragment extends Fragment implements View.OnClickListener {

    Button button1;
    Button button2;
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

        // Inflate the layout for this fragment
        button1 = (Button)thisView.findViewById(R.id.Enkel_vy_button1);
        button2 = (Button)thisView.findViewById(R.id.Enkel_vy_button2);

        if (!isVisible){
            someCode();
        }
        return thisView;
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
        button1.setText(df1);
        button2.setText(df2);
    }
}

