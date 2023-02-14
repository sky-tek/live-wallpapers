package com.skytek.live.wallpapers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.skytek.live.wallpapers.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);

        getFirebaseMessagingServiceOnLowDevices(message.getNotification().getTitle() , message.getNotification().getBody());
        getFirebaseMessaging(message.getNotification().getTitle() , message.getNotification().getBody());
    }
    private void getFirebaseMessaging(String getTitle , String getBody)
    {

        NotificationCompat.Builder firebaseNotification = new NotificationCompat.Builder(this , "myFirebaseChannel")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(getTitle)
                .setContentText(getBody)
                .setAutoCancel(true);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(101 , firebaseNotification.build());

    }
    private void getFirebaseMessagingServiceOnLowDevices(String getTitle , String getBody)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)

        {
            Log.d("asdasjkdasjdhas" , "getFirebaseMessagingServiceOnLowDevices");
            int importance  = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel("myFirebaseChannel" , getTitle ,  importance);
            notificationChannel.setDescription(getBody);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}
