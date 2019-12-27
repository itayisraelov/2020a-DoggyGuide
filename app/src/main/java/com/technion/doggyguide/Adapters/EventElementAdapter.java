package com.technion.doggyguide.Adapters;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.EventElement;
import com.technion.doggyguide.notifications.AlertRecieverEvent;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class EventElementAdapter extends
        FirestoreRecyclerAdapter<EventElement, EventElementAdapter.EventHolder> {
    private final String TAG = "Event Adapter";

    public EventElementAdapter(@NonNull FirestoreRecyclerOptions<EventElement> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull EventElement model) {
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewTime.setText(model.getStart_time() + "-" + model.getEnd_time());
        holder.textViewDescription.setText(model.getDescription());
        holder.textViewDate.setText(model.getDate());
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,
                parent, false);
        return new EventHolder(v);
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDate;
        private TextView textViewTime;
        private TextView textViewDescription;
        private ImageButton imageButtonAlarm;
        private int numOfAlarmClicks = 0;
        public EventHolder(final View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.event_title);
            textViewDate = itemView.findViewById(R.id.event_date);
            textViewTime = itemView.findViewById(R.id.event_time);
            textViewDescription = itemView.findViewById(R.id.event_description);
            imageButtonAlarm = itemView.findViewById(R.id.event_alarm);


            imageButtonAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                @TargetApi(24)
                public void onClick(View v) {
                    //set reminder 10 mins before start time on this date
                    numOfAlarmClicks++;
                    Calendar calendar = Calendar.getInstance();
                    String start_time = textViewTime.getText().toString().split("-")[0];
                    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm");
                    try {
                        Date date = dateFormat.parse(start_time);
                        int hourOfDay = date.getHours();
                        int minute = date.getMinutes();
                        if (minute < 10 && minute != 0) {
                            hourOfDay--;
                            minute = 60 - (minute % 10);
                        } else if (minute == 0) {
                            hourOfDay--;
                            minute = 50;
                        } else {
                            minute -= 10;
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        if (numOfAlarmClicks % 2 == 1) {
                            startAlarm(calendar);
                            Toast.makeText(v.getContext(), "Alarm set 10 minutes before the events starts!",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            cancelAlarm(calendar);
                            Toast.makeText(v.getContext(), "Alarm is canceled!", Toast.LENGTH_LONG).show();
                        }

                    } catch (ParseException e) {
                        Log.d(TAG, e.getMessage());
                    }


                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                private void startAlarm(Calendar calendar) {
                    int startHour = Integer.parseInt(textViewTime.getText().toString().split("-")[0].split(":")[0]);
                    int startMinute = Integer.parseInt(textViewTime.getText().toString().split("-")[0].split(":")[1]);
                    AlarmManager alarmManager = (AlarmManager) itemView.getContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(itemView.getContext(), AlertRecieverEvent.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(itemView.getContext(),
                            startHour * 3600 + startMinute * 60, intent, 0);
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                private void cancelAlarm(Calendar calendar) {
                    int startHour = Integer.parseInt(textViewTime.getText().toString().split("-")[0].split(":")[0]);
                    int startMinute = Integer.parseInt(textViewTime.getText().toString().split("-")[0].split(":")[1]);
                    AlarmManager alarmManager = (AlarmManager) itemView.getContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(itemView.getContext(), AlertRecieverEvent.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(itemView.getContext(),
                            startHour * 3600 + startMinute * 60, intent, 0);
                    alarmManager.cancel(pendingIntent);
                }
            });
        }


    }
}
