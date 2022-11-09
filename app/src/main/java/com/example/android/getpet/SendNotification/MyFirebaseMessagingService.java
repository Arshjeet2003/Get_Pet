package com.example.android.getpet.SendNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import com.example.android.getpet.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static Uri alarmsound;
    String title="Heading of notification ", ourmessage = " request for pet adoption";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        createNotificationChannel();

        Intent intent = new Intent(getApplicationContext(),com.example.android.getpet.allListsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            intent = new Intent(getApplicationContext(), com.example.android.getpet.loginPage.class);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1
                ,intent,PendingIntent.FLAG_ONE_SHOT);

        title = message.getData().get("Title");
        ourmessage = message.getData().get("Message");
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.app_icon1);
        alarmsound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"GetPetNotification")
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.notification_)
                .setContentTitle(title)
                .setContentText(ourmessage)
                .setAutoCancel(true)
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        builder.setContentIntent(pendingIntent);


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
