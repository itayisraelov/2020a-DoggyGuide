package com.technion.doggyguide.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class AlertReceiverWalk extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelperWalk notificationHelperWalk = new NotificationHelperWalk(context);
        NotificationCompat.Builder nb = notificationHelperWalk.getChannelNotification();
        notificationHelperWalk.getManager().notify(1, nb.build());
    }
}