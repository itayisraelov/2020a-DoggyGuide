package com.technion.doggyguide.notifications;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.technion.doggyguide.R;

public class AlertRecieverEvent extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        View view = View.inflate(context, R.layout.event_item, null);
        NotificationHelperEvent notificationHelperEvent = new NotificationHelperEvent(context);
        NotificationCompat.Builder nb = notificationHelperEvent.getChannelNotification();
        notificationHelperEvent.getManager().notify(1, nb.build());
    }
}
