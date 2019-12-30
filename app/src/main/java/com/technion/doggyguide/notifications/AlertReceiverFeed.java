package com.technion.doggyguide.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class AlertReceiverFeed extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelperFeed notificationHelperFeed = new NotificationHelperFeed(context);
        NotificationCompat.Builder nb = notificationHelperFeed.getChannelNotification();
        notificationHelperFeed.getManager().notify(1, nb.build());
    }
}