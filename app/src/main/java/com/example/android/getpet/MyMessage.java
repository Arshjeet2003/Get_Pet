package com.example.android.getpet;

public class MyMessage {

    private String senderName;
    private String senderEmail;
    private String senderPic;
    private String content;

    public MyMessage(String senderName, String senderEmail, String senderPic, String content) {
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.senderPic = senderPic;
        this.content = content;
    }

    public MyMessage() {
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderEmail(){
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderPic() {
        return senderPic;
    }

    public void setSenderPic(String senderPic) {
        this.senderPic = senderPic;
    }
}
