package com.technion.doggyguide.Adapters;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.EventElement;
import com.technion.doggyguide.notifications.AlertRecieverEvent;

import java.util.Calendar;

public class EventElementAdapter extends
        FirestoreRecyclerAdapter<EventElement, EventElementAdapter.EventHolder> {
    private final String TAG = "Event Adapter";
    String mDogOwners = "dogOwners";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public EventElementAdapter(@NonNull FirestoreRecyclerOptions<EventElement> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull EventHolder holder, int position, @NonNull EventElement model) {
        final String userId = mAuth.getCurrentUser().getUid();
        final DocumentReference eventDocRef = db.collection(  mDogOwners + "/"
                + userId + "/eventsByDate").document(model.getDate())
                .collection("events").document(model.getEventId());
        holder.textViewTitle.setText(model.getTitle());
        holder.textViewTime.setText(model.getStart_time() + "-" + model.getEnd_time());
        holder.textViewDescription.setText(model.getDescription());
        holder.textViewDate.setText(model.getDate());

        holder.imageButtonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Event canceled!", Snackbar.LENGTH_SHORT).show();
                eventDocRef.delete();
            }
        });
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,
                parent, false);
        return new EventHolder(v);
    }

    public class EventHolder extends RecyclerView.ViewHolder {
        public static final String TITLE = "Notification title";
        public static final String DESCRIPTION = "Notification description";

        private TextView textViewTitle;
        private TextView textViewDate;
        private TextView textViewTime;
        private TextView textViewDescription;
        private ImageButton imageButtonAlarm;
        private ImageButton imageButtonClear;
        private int numOfAlarmClicks = 0;

        public EventHolder(final View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.event_title);
            textViewDate = itemView.findViewById(R.id.event_date);
            textViewTime = itemView.findViewById(R.id.event_time);
            textViewDescription = itemView.findViewById(R.id.event_description);
            imageButtonAlarm = itemView.findViewById(R.id.event_alarm);
            imageButtonClear = itemView.findViewById(R.id.event_cancel);

            imageButtonAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                @TargetApi(24)
                public void onClick(View v) {
                    //set reminder 10 mins before start time on this date
                    numOfAlarmClicks++;
                    Calendar calendar = Calendar.getInstance();
                    String[] start_time = textViewTime.getText().toString().split("-")[0].split(":");
                    int hourOfDay = Integer.parseInt(start_time[0]);
                    int minute = Integer.parseInt(start_time[1]);
                    if (minute < 10 && minute != 0) {
                        hourOfDay--;
                        minute = 60 - (minute % 10);
                    } else if (minute == 0) {
                        hourOfDay--;
                        minute = 50;
                    } else {
                        minute -= 10;
                    }
                    String[] d = textViewDate.getText().toString().split("-");
                    calendar.set(Integer.parseInt(d[2]), Integer.parseInt(d[1]) - 1,
                            Integer.parseInt(d[0]), hourOfDay, minute);
                    if (numOfAlarmClicks % 2 == 1) {
                        startAlarm(calendar);
                        Toast.makeText(v.getContext(), "Alarm set 10 minutes before the event starts!",
                                Toast.LENGTH_LONG).show();
                    } else {
                        cancelAlarm(calendar);
                        Toast.makeText(v.getContext(), "Alarm is canceled!", Toast.LENGTH_LONG).show();
                    }


                }

                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                private void startAlarm(Calendar calendar) {
                    int startHour = Integer.parseInt(textViewTime.getText().toString().split("-")[0].split(":")[0]);
                    int startMinute = Integer.parseInt(textViewTime.getText().toString().split("-")[0].split(":")[1]);
                    AlarmManager alarmManager = (AlarmManager) itemView.getContext().getSystemService(Context.ALARM_SERVICE);
                    Intent intent = new Intent(itemView.getContext(), AlertRecieverEvent.class);
                    intent.putExtra(TITLE, textViewTitle.getText().toString());
                    intent.putExtra(DESCRIPTION, textViewDescription.getText().toString() + " in 10 minutes");
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
                    intent.putExtra(TITLE, textViewTitle.getText().toString());
                    intent.putExtra(DESCRIPTION, textViewDescription.getText().toString());
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(itemView.getContext(),
                            startHour * 3600 + startMinute * 60, intent, 0);
                    alarmManager.cancel(pendingIntent);
                }
            });
        }
    }
}
