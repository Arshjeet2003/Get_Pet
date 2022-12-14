package com.example.android.getpet.utils;

import com.example.android.getpet.models.NotificationSender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",

                    // server key for using firebase messaging services
                    "Authorization:key=AAAAMIzI_cU:APA91bHVvU61_sLjzaLWsvu1RJpKhRzGHGizHKzFPCTILKZDSekDCA3zMLCUGRs40Zm-WyndVwklblrtBXjIyT-s-AcyqHJE49Mg-tIzg_y3R3cR_OhUrTXYCwn5JW5nJUnaTbusu3W-" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")

    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
