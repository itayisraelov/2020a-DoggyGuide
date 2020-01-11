package com.technion.doggyguide.services;


import android.app.PendingIntent;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.technion.doggyguide.MainActivity;
import com.technion.doggyguide.R;

public class PostService extends FirebaseMessagingService {
    private final String CHANNEL_ID = "Post Notifications";
    @Override
    public void onMessageReceived(RemoteMessage payload) {
        super.onMessageReceived(payload);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(payload.getData().get("title"))
                .setContentText(payload.getData().get("body"))
                .setContentIntent(pendingIntent);
    }
}
