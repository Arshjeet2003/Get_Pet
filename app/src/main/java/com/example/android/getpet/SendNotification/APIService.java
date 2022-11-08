package com.example.android.getpet.SendNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAXUNEFu0:APA91bFG-ucIF9DbRmII3LVHt-i2H9isdso2gggPpjXZjp9JJw7xq8JR-yD9QiHKRefXkyyJeGZXX6CBlrkFW-1px96NsVXvkFJArE5z8ZBzCAb3LgYzPyQ6hjzO3nFC_NrgEsealtee" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}
