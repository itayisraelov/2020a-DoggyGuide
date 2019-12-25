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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.doggyguide.R;
import com.technion.doggyguide.notifications.AlertReceiverWalk;
import com.technion.doggyguide.notifications.TimePickerFragment;
import java.text.DateFormat;
import java.util.Calendar;
import com.google.firebase.auth.FirebaseAuth;




public class walkAlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private TextView mTextView, mTextView2, mTextView3;
    private Integer mWhichButtonIsClicked = 0;
    Button mButtonTimePicker, mButtonTimePicker2, mButtonTimePicker3;
    Button mButtonCancelAlarm, mButtonCancelAlarm2, mButtonCancelAlarm3;
    Button mButtonSet;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth users = FirebaseAuth.getInstance();
    private CollectionReference usersRef = db.collection("dog owners");
    String userUid = users.getCurrentUser().getUid();
    private CollectionReference alarmsByUserUidRef = usersRef.document(userUid).collection("Alarms");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_alarm);

        initFields();
        initClickListenerForButtons();
    }

    private void initFields() {

        mTextView = findViewById(R.id.textView);
        mTextView2 = findViewById(R.id.textView_2);
        mTextView3 = findViewById(R.id.textView_3);
        readFromDataBase();

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
                mWhichButtonIsClicked = 1;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        mButtonCancelAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 1;
                cancelAlarm();
            }
        });

        mButtonTimePicker2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 2;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        mButtonCancelAlarm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 2;
                cancelAlarm();
            }
        });
        mButtonTimePicker3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 3;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        mButtonCancelAlarm3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWhichButtonIsClicked = 3;
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

        if(mWhichButtonIsClicked == 1){
            mTextView.setText(timeText);
        }else if(mWhichButtonIsClicked == 2){
            mTextView2.setText(timeText);
        }
        else if(mWhichButtonIsClicked == 3){
            mTextView3.setText(timeText);
        }
        updateFireBase(mWhichButtonIsClicked, timeText);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiverWalk.class);
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
        Intent intent = new Intent(this, AlertReceiverWalk.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, mWhichButtonIsClicked,
                                                                                intent, 0);
        alarmManager.cancel(pendingIntent);

        if(mWhichButtonIsClicked == 1){
            mTextView.setText("Alarm canceled");
        }else if(mWhichButtonIsClicked == 2){
            mTextView2.setText("Alarm canceled");
        }
        else if(mWhichButtonIsClicked == 3){
            mTextView3.setText("Alarm canceled");
        }
        updateFireBase(mWhichButtonIsClicked, "Alarm canceled");
        mWhichButtonIsClicked =0;
    }

    private void updateFireBase(int n, String str) {
        readFromDataBase();
        WalkAlarm walkAlarmNew;
        if(n==1){
            walkAlarmNew = new WalkAlarm(str, mTextView2.getText().toString(), mTextView3.getText().toString()) ;
        }else if(n==2){
            walkAlarmNew = new WalkAlarm(mTextView.getText().toString(), str, mTextView3.getText().toString()) ;
        }else{
            walkAlarmNew = new WalkAlarm(mTextView.getText().toString(), mTextView2.getText().toString(), str) ;
        }
        writeToDataBase(walkAlarmNew);
    }


    private void writeToDataBase(WalkAlarm walkAlarm) {
        alarmsByUserUidRef.document("walk alarm").set(walkAlarm);
    }

    private void readFromDataBase() {
        DocumentReference docRef = alarmsByUserUidRef.document("walk alarm");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                WalkAlarm walkAlarm = documentSnapshot.toObject(WalkAlarm.class);
                if (walkAlarm != null){
                    mTextView.setText(walkAlarm.getWalk_time_1());
                    mTextView2.setText(walkAlarm.getWalk_time_2());
                    mTextView3.setText(walkAlarm.getWalk_time_3());
                }
            }
        });
    }
}
