package com.example.android.getpet.models;

//This class contains information about the pets.
public class Pets implements Owner, Animal{

    private String petKey;
    private String key;
    private String animalName;
    private String animal;
    private String breed;
    private String age;
    private String size;
    private String gender;
    private String description;
    private String profilePic;
    private String OwnerKey;
    private String OwnerName;
    private String OwnerEmail;
    private String OwnerProfilePic;
    private String petLat;
    private String petLong;
    private String location;

    public Pets(String petKey, String key, String animalName, String animal, String breed, String age, String size, String gender, String description, String profilePic, String ownerKey, String ownerName, String ownerEmail, String ownerProfilePic, String petLat, String petLong, String location) {
        this.petKey = petKey;
        this.key = key;
        this.animalName = animalName;
        this.animal = animal;
        this.breed = breed;
        this.age = age;
        this.size = size;
        this.gender = gender;
        this.description = description;
        this.profilePic = profilePic;
        OwnerKey = ownerKey;
        OwnerName = ownerName;
        OwnerEmail = ownerEmail;
        OwnerProfilePic = ownerProfilePic;
        this.petLat = petLat;
        this.petLong = petLong;
        this.location = location;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getAnimalName() {
        return animalName;
    }

    @Override
    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    @Override
    public String getPetKey() {
        return petKey;
    }

    @Override
    public void setPetKey(String petKey) {
        this.petKey = petKey;
    }

    @Override
    public String getOwnerKey() {
        return OwnerKey;
    }

    @Override
    public void setOwnerKey(String ownerkey) {
        OwnerKey = ownerkey;
    }

    @Override
    public String getOwnerName() {
        return OwnerName;
    }

    @Override
    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    @Override
    public String getOwnerEmail() {
        return OwnerEmail;
    }

    @Override
    public void setOwnerEmail(String ownerEmail) {
        OwnerEmail = ownerEmail;
    }

    @Override
    public String getOwnerProfilePic() {
        return OwnerProfilePic;
    }

    @Override
    public void setOwnerProfilePic(String ownerProfilePic) {
        OwnerProfilePic = ownerProfilePic;
    }

    @Override
    public String getProfilePic() {
        return profilePic;
    }

    @Override
    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    @Override
    public String getAnimal() {
        return animal;
    }

    @Override
    public void setAnimal(String animal) {
        this.animal = animal;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public String getBreed() {
        return breed;
    }

    @Override
    public void setBreed(String breed) {
        this.breed = breed;
    }

    @Override
    public String getAge() {
        return age;
    }

    @Override
    public void setAge(String age) {
        this.age = age;
    }

    @Override
    public String getSize() {
        return size;
    }

    @Override
    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String getPetLat() {
        return petLat;
    }

    @Override
    public void setPetLat(String petLat) {
        this.petLat = petLat;
    }

    @Override
    public String getPetLong() {
        return petLong;
    }

    @Override
    public void setPetLong(String petLong) {
        this.petLong = petLong;
    }

    public Pets(){}

}
