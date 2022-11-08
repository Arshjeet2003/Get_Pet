package com.example.android.getpet.SendNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.android.getpet.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String title="Heading of notification ", ourmessage = " request for pet adoption";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        createNotificationChannel();

        title = message.getData().get("Title");
        ourmessage = message.getData().get("Message");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"GetPetNotification").setSmallIcon(R.drawable.notification_)
                .setContentTitle(title)
                .setContentText(ourmessage)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "GetPet_Notification_Channel";
            String description = "This notification channel is for get pet app notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("GetPetNotification", name, importance);
            channel.setDescription(description);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
