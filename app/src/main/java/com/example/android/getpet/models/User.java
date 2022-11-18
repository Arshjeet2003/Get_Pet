package com.example.android.getpet.models;

//Contains information about user.
public class User {

    private String name;
    private String number;
    private String email;
    private String profilePic;

    public User(String name, String number, String email, String profilePic) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.profilePic = profilePic;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
