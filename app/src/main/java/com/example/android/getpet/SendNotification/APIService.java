package com.example.android.getpet.SendNotification;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//Client class for registering the client
public class Client {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String url) {

        // if user not registered register user
        if (retrofit == null) {
            retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }
}
