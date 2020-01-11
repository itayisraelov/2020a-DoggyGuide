package com.technion.doggyguide.homeScreen.alarm;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.doggyguide.R;
import com.technion.doggyguide.notifications.AlertReceiverFeed;
import com.technion.doggyguide.notifications.AlertReceiverWalk;
import com.technion.doggyguide.notifications.TimePickerFragment;
import java.text.DateFormat;
import java.util.Calendar;

public class eatAlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private TextView mTextView, mTextView2, mTextView3;
    private Integer mWhichButtonIsClicked;
    Button mButtonTimePicker, mButtonTimePicker2, mButtonTimePicker3;
    Button mButtonCancelAlarm, mButtonCancelAlarm2, mButtonCancelAlarm3;
    Button mButtonSet;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth users = FirebaseAuth.getInstance();
    private CollectionReference usersRef = db.collection("dogOwners");
    String userUid = users.getCurrentUser().getUid();
    private CollectionReference alarmsByUserUidRef = usersRef.document(userUid).collection("Alarms");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eat_alarm);
        readFromDataBase();
        initFields();
        initClickListenerForButtons();
    }

    private void initFields() {
        mTextView = findViewById(R.id.textView);
        mTextView2 = findViewById(R.id.textView_2);
        mTextView3 = findViewById(R.id.textView_3);
        mButtonTimePicker = findViewById(R.id.button_time_picker);
        mButtonCancelAlarm = findViewById(R.id.button_cancel);
        mButtonTimePicker2 = findViewById(R.id.button_time_picker_2);
        mButtonCancelAlarm2 = findViewById(R.id.button_cancel_2);
        mButtonTimePicker3 = findViewById(R.id.button_time_picker_3);
        mButtonCancelAlarm3 = findViewById(R.id.button_cancel_3);
        mButtonSet = findViewById(R.id.setButton);
    }

    private void initClickListenerForButtons() {
        mButtonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 4;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        mButtonCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 4;
                cancelAlarm();
            }
        });

        mButtonTimePicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 5;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        mButtonCancelAlarm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 5;
                cancelAlarm();
            }
        });
        mButtonTimePicker3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 6;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        mButtonCancelAlarm3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 6;
                cancelAlarm();
            }
        });
        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

        updateTimeText(c);
        startAlarm(c);
    }

    private void updateTimeText(Calendar c) {
        String timeText = "Alarm set for: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());

        if(mWhichButtonIsClicked == 4){
            mTextView.setText(timeText);
        }else if(mWhichButtonIsClicked == 5){
            mTextView2.setText(timeText);
        }
        else if(mWhichButtonIsClicked == 6){
            mTextView3.setText(timeText);
        }
        updateFireBase(mWhichButtonIsClicked, timeText);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiverFeed.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, mWhichButtonIsClicked,
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
        Intent intent = new Intent(this, AlertReceiverFeed.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, mWhichButtonIsClicked,
                intent, 0);
        alarmManager.cancel(pendingIntent);

        if(mWhichButtonIsClicked == 4){
            mTextView.setText("Alarm canceled");
        }else if(mWhichButtonIsClicked == 5){
            mTextView2.setText("Alarm canceled");
        }
        else if(mWhichButtonIsClicked == 6){
            mTextView3.setText("Alarm canceled");
        }
        updateFireBase(mWhichButtonIsClicked, "Alarm canceled");
        mWhichButtonIsClicked =0;
    }

    private void updateFireBase(int n, String str) {
        readFromDataBase();
        FeedAlarm feedAlarm;
        if(n==4){
            feedAlarm = new FeedAlarm(str, mTextView2.getText().toString(), mTextView3.getText().toString()) ;
        }else if(n==5){
            feedAlarm = new FeedAlarm(mTextView.getText().toString(), str, mTextView3.getText().toString()) ;
        }else{
            feedAlarm = new FeedAlarm(mTextView.getText().toString(), mTextView2.getText().toString(), str) ;
        }
        writeToDataBase(feedAlarm);
    }


    private void writeToDataBase(FeedAlarm feedAlarm) {
        alarmsByUserUidRef.document("feed alarm").set(feedAlarm);
    }

    private void readFromDataBase() {
        DocumentReference docRef = alarmsByUserUidRef.document("feed alarm");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                FeedAlarm feedAlarm = documentSnapshot.toObject(FeedAlarm.class);
                if (feedAlarm != null){
                    mTextView.setText(feedAlarm.getFeed_time_1());
                    mTextView2.setText(feedAlarm.getFeed_time_2());
                    mTextView3.setText(feedAlarm.getFeed_time_3());
                }
            }
        });
    }
}
