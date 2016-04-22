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
        // Använder dagens datum som standardvärde
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog d = new DatePickerDialog(getActivity(), this, year, month, day);   // Skapar en ny instans av DatePickerDialog objekt som ska returneras
        DatePicker dp = d.getDatePicker();   //Skapar en datepicker, som vi behöver när vi ska sätta min och max datum
        long currentDate = c.getTimeInMillis();

        dp.setMinDate(currentDate);  //Sätt mindatum till nuvarande datum
        dp.setMaxDate(currentDate + 864000000); //Sätt maxdatum till 10 dagar framåt
        return d;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {// När användaren har valt datum och tryckt på OK, kommer denna metod att anropas
        Button butt = (Button) MainActivity.view; //Hämtar knappen som användaren tryckte på

        Calendar cal = new GregorianCalendar();      //Skapar ett datumobjekt för datumet som användaren har valt
        cal.set(year, month, day);

        String df = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());//Formatet för datum blir yyyy-MM-dd
        switch(butt.getId()){  //Vi skriver ner datumet till en global variabel, för att kunna begränsa datum (se ovan)
            case R.id.DateButton:
                MainActivity.buttText1.set(year, month, day); //Beroende på vad för knapp som användaren tryckte på så sätts det nya datumet till
                break;                                        //en ny variabel
            case R.id.DateButton2:
                MainActivity.buttText2.set(year, month, day);
                break;
        }
        butt.setText(df);   //Skriver valt datum på knappen
    }

}
