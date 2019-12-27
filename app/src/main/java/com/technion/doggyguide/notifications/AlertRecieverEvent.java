package com.technion.doggyguide.notifications;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.core.app.NotificationCompat;

import com.technion.doggyguide.R;

public class AlertRecieverEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Activity v = (Activity) context;
        String title = v.findViewById(R.id.event_title).toString();
        String description = v.findViewById(R.id.event_description).toString();
        NotificationHelperEvent notificationHelperEvent = new NotificationHelperEvent(context);
        NotificationCompat.Builder nb = notificationHelperEvent.getChannelNotification(title, description);
        notificationHelperEvent.getManager().notify(1, nb.build());
    }
}
