package com.technion.doggyguide.homeScreen.alarm;


import android.app.TimePickerDialog;
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
import com.technion.doggyguide.notifications.TimePickerFragment;
import java.text.DateFormat;
import java.util.Calendar;



public class eatAlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mFeedAlarms = db.collection("dogOwners/"
            + mAuth.getCurrentUser().getUid() + "/feedAlarm");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_alarm);
        getSupportActionBar().setTitle("Feed Alarm");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpRecyclerView() {
        Query query = mFeedAlarms.orderBy("time", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<AlarmElement> options = new FirestoreRecyclerOptions.Builder<AlarmElement>()
                .setQuery(query, AlarmElement.class)
                .build();
        AlarmElementAdapter mAdapter = new AlarmElementAdapter(options);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
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
        String alarmId = mAuth.getCurrentUser().getUid() + Timestamp.now().toString();
        AlarmElement alarm = new AlarmElement(timeText, false, alarmId, hourOfDay, minute, "feedAlarm");
        mFeedAlarms.document(alarmId)
                .set(alarm)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Feed Alarm", "Reminder added");
                    }
                });
    }
}