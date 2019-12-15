package com.technion.doggyguide.homeScreen;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.technion.doggyguide.DatePickerFragment;
import com.technion.doggyguide.R;
import com.technion.doggyguide.TimePickerFragment;

import java.text.DateFormat;
import java.util.Calendar;

public class Fab extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    private TextView postdate;
    private TextView poststarttime;
    private TextView postendtime;

    private int clicked_btn_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab);
        getSupportActionBar().setTitle("Post a request");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void datePickerHandler(View view) {
        clicked_btn_id = R.id.post_datepicker;
        DialogFragment datepicker = new DatePickerFragment();
        datepicker.show(getSupportFragmentManager(), "Date Picker");
    }

    public void startTimeHandler(View view) {
        clicked_btn_id = R.id.post_starttimepicker;
        DialogFragment starttimepicker = new TimePickerFragment();
        starttimepicker.show(getSupportFragmentManager(), "start time picker");
    }

    public void endTimeHandler(View view) {
        clicked_btn_id = R.id.post_endtimepicker;
        DialogFragment endtimepicker = new TimePickerFragment();
        endtimepicker.show(getSupportFragmentManager(), "end time picker");
    }

    public void postbtnHandler(View view) {
        /* creating a post and posting it to all
           friends' feed */
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String pickeddate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        postdate =  findViewById(R.id.post_date);
        postdate.setText(pickeddate);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        switch (clicked_btn_id){
            case R.id.post_starttimepicker:
                poststarttime = findViewById(R.id.post_starttime);
                poststarttime.setText("Starts at " + hourOfDay + ":" + minute);
                break;
            case R.id.post_endtimepicker:
                postendtime = findViewById(R.id.post_endtime);
                postendtime.setText("Ends at " + hourOfDay + ":" + minute);
                break;
        }
    }
}
