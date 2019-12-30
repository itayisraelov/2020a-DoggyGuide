package com.technion.doggyguide.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class AlertReceiverShower extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelperShower notificationHelperShower = new NotificationHelperShower(context);
        NotificationCompat.Builder nb = notificationHelperShower.getChannelNotification();
        notificationHelperShower.getManager().notify(1, nb.build());
    }
}