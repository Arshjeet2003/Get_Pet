package com.example.android.getpet;

public class Pets {


    private String petKey;
    private String key;
    private String animal;
    private String breed;
    private String age;
    private String size;
    private String gender;
    private String profilePic;
    private String OwnerKey;
    private String OwnerName;
    private String OwnerEmail;
    private String OwnerProfilePic;
    private String petLat;
    private String petLong;
    private Boolean isFav;

    public Pets(String petKey, String key, String animal, String breed, String age, String size, String gender, String profilePic, String ownerKey, String ownerName, String ownerEmail, String ownerProfilePic, String petLat, String petLong, Boolean isFav) {
        this.petKey = petKey;
        this.key = key;
        this.animal = animal;
        this.breed = breed;
        this.age = age;
        this.size = size;
        this.gender = gender;
        this.profilePic = profilePic;
        OwnerKey = ownerKey;
        OwnerName = ownerName;
        OwnerEmail = ownerEmail;
        OwnerProfilePic = ownerProfilePic;
        this.petLat = petLat;
        this.petLong = petLong;
        this.isFav = isFav;
    }

    public String getPetKey() {
        return petKey;
    }

    public void setPetKey(String petKey) {
        this.petKey = petKey;
    }

    public String getOwnerKey() {
        return OwnerKey;
    }

    public void setOwnerKey(String ownerkey) {
        OwnerKey = ownerkey;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getOwnerEmail() {
        return OwnerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        OwnerEmail = ownerEmail;
    }

    public String getOwnerProfilePic() {
        return OwnerProfilePic;
    }

    public void setOwnerProfilePic(String ownerProfilePic) {
        OwnerProfilePic = ownerProfilePic;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getAnimal() {
        return animal;
    }

    public void setAnimal(String animal) {
        this.animal = animal;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPetLat() {
        return petLat;
    }

    public void setPetLat(String petLat) {
        this.petLat = petLat;
    }

    public String getPetLong() {
        return petLong;
    }

    public void setPetLong(String petLong) {
        this.petLong = petLong;
    }

    public Boolean getFav() {
        return isFav;
    }

    public void setFav(Boolean fav) {
        isFav = fav;
    }

    public Pets(){}
}
