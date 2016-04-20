package se.anderssjobom.weathertracker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {
    //Skapar en instans av dialogfragment
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Anv�nder dagens datum som standardv�rde
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog d = new DatePickerDialog(getActivity(), this, year, month, day);   // Skapar en ny instans av DatePickerDialog objekt som ska returneras
        DatePicker dp = d.getDatePicker();   //Skapar en datepicker, som vi beh�ver n�r vi ska s�tta min och max datum

        switch (MenuActivity.view.getId()) {//Kollar vilken knapp vi tryckte p�
            case R.id.DateButton:  //Vi tyrckte p� knapp 1
                dp.setMinDate(c.getTimeInMillis()); //S�tter mindatum som dagens datum

                if(MenuActivity.buttText2 != "default2") { //Om ett datum har valts p� den andra knappen, s� anv�nder vi det datumet som valbara maxdatum
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = new GregorianCalendar();

                    try {
                        cal.setTime(dateFormat.parse(MenuActivity.buttText2));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dp.setMaxDate(cal.getTimeInMillis());

                }
                else{//�r det inget datum p� det andra knappen s� s�tter vi maxDate till 10 dagar fram�t
                    dp.setMaxDate(c.getTimeInMillis() + 864000000); //10 dagar = 864000000 ms (seriously...)
                }

                break;

            case R.id.DateButton2://Om vi tryckte p� knapp 2
                dp.setMaxDate(c.getTimeInMillis() + 864000000); //S�tter maxdatum som dagens datum + 10 dagar

                if(MenuActivity.buttText1 != "default1") { //Om ett datum har valts p� den andra knappen, s� anv�nder vi det datumet som valbara mindatum
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = new GregorianCalendar();

                    try {
                        cal.setTime(dateFormat.parse(MenuActivity.buttText1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dp.setMinDate(cal.getTimeInMillis());

                }
                else{//�r det inget datum p� det andra knappen s� s�tter vi minDate till dagens datum
                    dp.setMinDate(c.getTimeInMillis());
                }

                break;
        }
        return d;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {// N�r anv�ndaren har valt datum och tryckt p� OK, kommer denna metod att anropas
        Button butt = (Button) MenuActivity.view; //Get the button that was pressed

        Calendar cal = Calendar.getInstance();      //Skapar ett datumobjekt f�r datumet som anv�ndaren har valt
        cal.set(year, month + 1, day);

        String df = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());//Formatet f�r datum blir yyyy-MM-dd

        switch(butt.getId()){  //Vi skriver ner datumet till en global variabel, f�r att kunna begr�nsa datum (se ovan)
            case R.id.DateButton:
                MenuActivity.buttText1 = Integer.toString(year) + "-" + Integer.toString(month + 1) + "-" + Integer.toString(day);
                break;
            case R.id.DateButton2:
                MenuActivity.buttText2 = Integer.toString(year) + "-" + Integer.toString(month + 1) + "-" + Integer.toString(day);
                break;
        }
        butt.setText(df);   //Skriver valt datum p� knappen
    }
}
