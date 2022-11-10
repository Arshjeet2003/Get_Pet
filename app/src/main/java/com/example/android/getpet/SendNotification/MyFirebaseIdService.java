package com.example.android.getpet.SendNotification;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;


// updating token in background
public class MyFirebaseIdService extends FirebaseMessagingService {
    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
        // generate FCM token from getToken()
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {

                        // if token generation is unsuccessful generate Toast message and return;
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "new token failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        /* if new token is generated successfully
                        then get new FCM registration token as string */
                        String token = task.getResult();
                        updateToken(token);
                    }
                });
    }

    // setting value of token at appropriate position in Firebase
    private void updateToken(String refreshToken){

        Token token = new Token(refreshToken);
        FirebaseDatabase.getInstance().getReference("Tokens").child(FirebaseAuth.getInstance().getUid()).setValue(token);
    }

}
