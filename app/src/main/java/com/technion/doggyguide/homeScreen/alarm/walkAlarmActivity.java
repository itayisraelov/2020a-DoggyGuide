package com.technion.doggyguide.homeScreen.alarm;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.technion.doggyguide.Adapters.AlarmElementAdapter;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.AlarmElement;
import com.technion.doggyguide.notifications.AlertReceiverWalk;
import com.technion.doggyguide.notifications.TimePickerFragment;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class walkAlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mWalkAlarms = db.collection("dogOwners/"
            + mAuth.getCurrentUser().getUid() + "/walkAlarms");

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<AlarmElement> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_alarm);
        getSupportActionBar().setTitle("Walk alarm");
        getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_up_button);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alarm_menu, menu);
        return true;
    }

    private void setUpRecyclerView() {
        Query query = mWalkAlarms.orderBy("time", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<AlarmElement> options = new FirestoreRecyclerOptions.Builder<AlarmElement>()
                .setQuery(query, AlarmElement.class)
                .build();
        AlarmElementAdapter mAdapter = new AlarmElementAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        if (mAdapter != null)
            mAdapter.startListening();
    }

    public boolean alarmbtnHandler(MenuItem item) {
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getSupportFragmentManager(), "time picker");
        return true;
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        String timeText = DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        AlarmElement alarm = new AlarmElement(timeText, false);
        mWalkAlarms.document(mAuth.getCurrentUser().getUid()
                + " " + Timestamp.now().toString())
                .set(alarm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Walk Alarm", "Reminder added");
                    }
                });

        // startAlarm(c);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiverWalk.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                pendingIntent); //AlarmManager.INTERVAL_DAY ---> daily repeated

    }

    @SuppressLint("SetTextI18n")
    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiverWalk.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, 0);
        alarmManager.cancel(pendingIntent);
    }

}
