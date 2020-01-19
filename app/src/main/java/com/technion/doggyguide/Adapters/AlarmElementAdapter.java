package com.technion.doggyguide.Adapters;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.technion.doggyguide.R;
import com.technion.doggyguide.dataElements.AlarmElement;
import com.technion.doggyguide.notifications.AlertReceiverFeed;
import com.technion.doggyguide.notifications.AlertReceiverShower;
import com.technion.doggyguide.notifications.AlertReceiverWalk;

import java.util.Calendar;

public class AlarmElementAdapter extends
        FirestoreRecyclerAdapter<AlarmElement, AlarmElementAdapter.AlarmElementViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public AlarmElementAdapter(@NonNull FirestoreRecyclerOptions<AlarmElement> options) {
        super(options);
    }

    @NonNull
    @Override
    public AlarmElementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item,
                parent, false);
        return new AlarmElementViewHolder(v);
    }


    @Override
    protected void onBindViewHolder(@NonNull AlarmElementViewHolder holder,
                                    int position, @NonNull final AlarmElement model) {
        final Context context = holder.itemView.getContext();
        holder.textView.setText(model.getTime());
        holder.aSwitch.setChecked(model.isSet());

        final AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @SuppressLint("SetTextI18n")
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int hourOfDay = model.getHourOfDay();
                int minute = model.getMinute();
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);

                Intent intent;
                PendingIntent pendingIntent;
                switch (model.getType()) {
                    case "walkAlarm":
                        intent = new Intent(context, AlertReceiverWalk.class);
                        pendingIntent = PendingIntent.getBroadcast(context, 0,
                                intent, 0);
                        break;
                    case "feedAlarm":
                        intent = new Intent(context, AlertReceiverFeed.class);
                        pendingIntent = PendingIntent.getBroadcast(context, 0,
                                intent, 0);
                        break;
                    default:
                        intent = new Intent(context, AlertReceiverShower.class);
                        pendingIntent = PendingIntent.getBroadcast(context, 0,
                                intent, 0);
                        break;
                }
                if (isChecked) {
                    startAlarm(c, pendingIntent);
                    db.document("dogOwners/"
                            + mAuth.getCurrentUser().getUid()
                            + "/" + model.getType() + "/"
                            + model.getAlarmId())
                            .update("set", true);
                } else {
                    alarmManager.cancel(pendingIntent);
                    db.document("dogOwners/"
                            + mAuth.getCurrentUser().getUid()
                            + "/" + model.getType() + "/"
                            + model.getAlarmId())
                            .update("set", false);
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            private void startAlarm(Calendar c, PendingIntent pendingIntent) {
                if (c.before(Calendar.getInstance())) {
                    c.add(Calendar.DATE, 1);
                }
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                        pendingIntent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                if (model.isSet()) {
                                    Toast.makeText(context, "Unset the alarm and try again!", Toast.LENGTH_LONG).show();
                                    break;
                                }
                                db.document("dogOwners/"
                                        + mAuth.getCurrentUser().getUid()
                                        + "/" + model.getType() + "/"
                                        + model.getAlarmId())
                                        .delete();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete the alarm?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                return false;
            }
        });

    }

    public static class AlarmElementViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private Switch aSwitch;

        public AlarmElementViewHolder(final View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.alarm_time);
            aSwitch = itemView.findViewById(R.id.switch1);

        }

    }
}
