package com.technion.doggyguide.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.technion.doggyguide.Adapters.EventElementAdapter;

public class AlertRecieverEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelperEvent notificationHelperEvent = new NotificationHelperEvent(context);
        String title = intent.getStringExtra(EventElementAdapter.EventHolder.TITLE);
        String description = intent.getStringExtra(EventElementAdapter.EventHolder.DESCRIPTION);
        NotificationCompat.Builder nb = notificationHelperEvent.getChannelNotification(title, description);
        notificationHelperEvent.getManager().notify(1, nb.build());
    }
}
