package com.technion.doggyguide.homeScreen;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.technion.doggyguide.ExampleAdapter;
import com.technion.doggyguide.ExampleItem;
import com.technion.doggyguide.R;
import com.technion.doggyguide.homeScreen.alarm.walkAlarmActivity;
import com.technion.doggyguide.homeScreen.alarm.showerAlarmActivity;
import com.technion.doggyguide.homeScreen.alarm.eatAlarmActivity;
import com.technion.doggyguide.notifications.AlertReceiver;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class NotificationsFragment extends Fragment implements TimePickerDialog.OnTimeSetListener {
    private TextView mTextView;
    private ArrayList<ExampleItem> mExampleList;
    private RecyclerView mRecyclerView;
    private ExampleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private View mView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_notifications, container, false);
        mView = view;
        mTextView = view.findViewById(R.id.textView);
//        Button buttonTimePicker = view.findViewById(R.id.button_time_picker);
//        Button buttonCancelAlarm = view.findViewById(R.id.button_cancel);

        createExampleList();
        buildRecyclerView();
//        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DialogFragment timePicker = new TimePickerFragment();
//                timePicker.show(getActivity().getSupportFragmentManager(), "time picker");
//            }
//        });
//        buttonCancelAlarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cancelAlarm();
//            }
//        });
        return view;
    }

    public void changeItem(int position, String text) {
        mExampleList.get(position).changeText1(text);
        mAdapter.notifyItemChanged(position);
    }

    public void createExampleList() {
        mExampleList = new ArrayList<>();
        mExampleList.add(new ExampleItem(R.mipmap.dog_walk, "Take your dog for a walk"));
        mExampleList.add(new ExampleItem(R.mipmap.dog_shower, "Give your dog shower"));
        mExampleList.add(new ExampleItem(R.mipmap.dog_eating, "Feed your dog"));
    }

    public void buildRecyclerView() {
        mRecyclerView = mView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new ExampleAdapter(mExampleList);

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                changeItem(position, "Clicked");
            }

            @Override
            public void onWalkAlarmClick(int position) {

                beginWalkAlarmActivity();
            }

            @Override
            public void onShowerAlarmClick(int position) {
                beginShowerAlarmActivity();
            }

            @Override
            public void onFeedAlarmClick(int position) {
                beginFeedAlarmActivity();
            }
        });
    }
    public void beginWalkAlarmActivity(){
        Intent intent = new Intent(getActivity(), walkAlarmActivity.class);
        startActivity(intent);
    }
    public void beginShowerAlarmActivity(){
        Intent intent = new Intent(getActivity(), showerAlarmActivity.class);
        startActivity(intent);
    }
    public void beginFeedAlarmActivity(){
        Intent intent = new Intent(getActivity(), eatAlarmActivity.class);
        startActivity(intent);
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

        mTextView.setText(timeText);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    @SuppressLint("SetTextI18n")
    private void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 1, intent, 0);

        alarmManager.cancel(pendingIntent);
        mTextView.setText("Alarm canceled");
    }
}
