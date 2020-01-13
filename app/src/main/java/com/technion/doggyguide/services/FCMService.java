package com.technion.doggyguide.services;

import android.app.PendingIntent;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.technion.doggyguide.MainActivity;
import com.technion.doggyguide.R;
import com.technion.doggyguide.users.UserProfile;

public class FCMService extends FirebaseMessagingService {
    private String CHANNEL_ID = "";
    private  Intent intent;
    private PendingIntent pendingIntent;

    @Override
    public void onMessageReceived(RemoteMessage payload) {
        super.onMessageReceived(payload);
        switch(payload.getData().get("notification_type")) {
            case "POST":
                CHANNEL_ID = "Post Notification";
                intent = new Intent(this, MainActivity.class);
                break;
            case "Friend_Req":
                CHANNEL_ID = "FriendReq Notifications";
                String from_user_id = payload.getData().get("sender_id");
                intent = new Intent(this, UserProfile.class);
                intent.putExtra("user_id", from_user_id);
                break;

        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(payload.getData().get("title"))
                .setContentText(payload.getData().get("body"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        int mNotificationId = (int) System.currentTimeMillis();
        NotificationManagerCompat mNotifyMgr = NotificationManagerCompat.from(this);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}