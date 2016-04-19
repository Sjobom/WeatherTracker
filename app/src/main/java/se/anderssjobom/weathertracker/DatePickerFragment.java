/*
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

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {//Skapar en instans av dialogfragment
        // Använder dagens datum som standardvärde
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        ​
        DatePickerDialog d = new DatePickerDialog(getActivity(), this, year, month, day);   // Skapar en ny instans av DatePickerDialog objekt som ska returneras
        DatePicker dp = d.getDatePicker();   //Skapar en datepicker, som vi behöver när vi ska sätta min och max datum
        ​
        switch (MainActivity.view.getId()) {//Kollar vilken knapp vi tryckte på
            case R.id.button:  //Vi tyrckte på knapp 1
                dp.setMinDate(c.getTimeInMillis()); //Sätter mindatum som dagens datum
                ​
                if(MainActivity.buttText2 != "default2") { //Om ett datum har valts på den andra knappen, så använder vi det datumet som valbara maxdatum
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = new GregorianCalendar();
                    ​
                    try {
                        cal.setTime(dateFormat.parse(MainActivity.buttText2));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dp.setMaxDate(cal.getTimeInMillis());
                    ​
                }
                else{//Är det inget datum på det andra knappen så sätter vi maxDate till 10 dagar framåt
                    dp.setMaxDate(c.getTimeInMillis() + 864000000); //10 dagar = 864000000 ms (seriously...)
                }
                ​
                break;
            ​
            case R.id.button2://Om vi tryckte på knapp 2
                dp.setMaxDate(c.getTimeInMillis() + 864000000); //Sätter maxdatum som dagens datum + 10 dagar
                ​
                if(MainActivity.buttText1 != "default1") { //Om ett datum har valts på den andra knappen, så använder vi det datumet som valbara mindatum
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = new GregorianCalendar();
                    ​
                    try {
                        cal.setTime(dateFormat.parse(MainActivity.buttText1));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    dp.setMinDate(cal.getTimeInMillis());
                    ​
                }
                else{//Är det inget datum på det andra knappen så sätter vi minDate till dagens datum
                    dp.setMinDate(c.getTimeInMillis());
                }
                ​
                break;
        }
        return d;
    }
    ​
    public void onDateSet(DatePicker view, int year, int month, int day) {// När användaren har valt datum och tryckt på OK, kommer denna metod att anropas
        Button butt = (Button) MainActivity.view; //Get the button that was pressed
        ​
        Calendar cal = Calendar.getInstance();      //Skapar ett datumobjekt för datumet som användaren har valt
        cal.set(year, month + 1, day);
        ​
        String df = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());//Formatet för datum blir yyyy-MM-dd
        ​
        switch(butt.getId()){  //Vi skriver ner datumet till en global variabel, för att kunna begränsa datum (se ovan)
            case R.id.button:
                MainActivity.buttText1 = Integer.toString(year) + "-" + Integer.toString(month + 1) + "-" + Integer.toString(day);
                break;
            case R.id.button2:
                MainActivity.buttText2 = Integer.toString(year) + "-" + Integer.toString(month + 1) + "-" + Integer.toString(day);
                break;
        }
        butt.setText(df);   //Skriver valt datum på knappen
    }
}
*/