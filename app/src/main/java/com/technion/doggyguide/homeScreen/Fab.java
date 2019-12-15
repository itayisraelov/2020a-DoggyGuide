package com.technion.doggyguide.homeScreen;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.technion.doggyguide.DatePickerFragment;
import com.technion.doggyguide.R;

import java.text.DateFormat;
import java.util.Calendar;

public class Fab extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private TextView postdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fab);
        getSupportActionBar().setTitle("Post a request");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void datePickerHandler(View view) {
        DialogFragment datepicker = new DatePickerFragment();
        datepicker.show(getSupportFragmentManager(), "Date Picker");
    }

    public void startTimeHandler(View view) {
    }

    public void endTimeHandler(View view) {
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
        String currdate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
        postdate =  findViewById(R.id.post_date);
        postdate.setText(currdate);
    }
}
