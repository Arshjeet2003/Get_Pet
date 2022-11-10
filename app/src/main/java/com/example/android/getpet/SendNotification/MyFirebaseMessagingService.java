package com.example.android.getpet.SendNotification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
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

    // title and message of notification
    String title="Heading of notification ", ourmessage = " request for pet adoption";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        // method to create notification channel called
        createNotificationChannel();

        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(this, com.example.android.getpet.allListsActivity.class);

        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);

        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // setting intent to open new activity on clicking notification
        Intent intent = new Intent(getApplicationContext(),com.example.android.getpet.allListsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            intent = new Intent(getApplicationContext(), com.example.android.getpet.loginPage.class);
        }

        // Passing a future intent to our foreign application so as to execute intent with our application's permissions
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),1
                ,intent,PendingIntent.FLAG_ONE_SHOT);

        title = message.getData().get("Title");
        ourmessage = message.getData().get("Message");
        Bitmap icon = BitmapFactory.decodeResource(getApplicationContext().getResources(),   // changing jpg to bitmap
                R.drawable.app_icon1);


        // creating custom notification using NotificationCompat Builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"GetPetNotification")
                .setLargeIcon(icon)
                .setSmallIcon(R.drawable.notification_)
                .setContentTitle(title)
                .setContentText(ourmessage)
                .setContentTitle(title)   // setting title
                .setContentText(ourmessage)   // setting message of notification
                .setAutoCancel(true)
                .setColor(Color.BLUE)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent);

        // initialising notificationManager
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,builder.build());
    }

    private void createNotificationChannel() {
        /* Create the NotificationChannel, but only on API 26+ because
         the NotificationChannel class is new and not in the support library */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "GetPet_Notification_Channel";
            String description = "This notification channel is for get pet app notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("GetPetNotification", name, importance);
            channel.setDescription(description);

            // Registering the channel with the system
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
