package com.example.android.getpet.models;

// Data class to store the notification content : title and message
public class Data {
    private String Title;
    private String Message;

    // parameterised constructor
    public Data(String title, String message) {
        Title = title;
        Message = message;
    }

    public Data(){
    }

    // getters and setters
    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
