package team21.flashbackmusic;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ryanle on 3/11/18.
 */

public class TimeSetterFragment extends android.support.v4.app.DialogFragment {

    View view;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private boolean dateChosen = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.time_body, null) ;

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view);

        // Formatting date and time to make a time
        datePicker = view.findViewById(R.id.date_picker);
        timePicker = view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);

        builder.setMessage(R.string.time_prompt)
                .setPositiveButton(R.string.set_time, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    // Set the time for mainactivity
                        long millis = getMillis();
                        final Timestamp time = new Timestamp(millis);
                        ((MainActivity) getActivity()).setCustomTime(time);
                    }
                })
                .setNegativeButton(R.string.reset_time, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // CANCEL
                        // Reset boolean in main
                        ((MainActivity) getActivity()).resetTime();
                    }
                });


        // Create the AlertDialog object and return it
        return builder.create();
    }

    private long getMillis() {
        int month = datePicker.getMonth() + 1, day = datePicker.getDayOfMonth(),
                year = datePicker.getYear();

        String monthStr = month >= 10 ? Integer.toString(month) : "0" + month;
        String dayStr = day >= 10 ? Integer.toString(day) : "0" + day;

        int hour = timePicker.getHour(), minute = timePicker.getMinute();
        String seconds = "00";
        String hourStr = hour >= 10 ? Integer.toString(hour) : "0" + hour;
        String minuteStr = minute >= 10 ? Integer.toString(minute) : "0" + minute;

        String timeStr = year + "/" + monthStr + "/" + dayStr + " " + hourStr + ":" + minuteStr +
                            ":" + seconds;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();

        Log.i("Time set: ", timeStr);
        return millis;
    }


}
