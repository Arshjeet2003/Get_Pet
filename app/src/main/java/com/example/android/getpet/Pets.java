package com.example.android.getpet;

public class Pets {

    private String animal;
    private String breed;
    private String age;
    private String size;
    private String gender;
    private String profilePic;

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

    public Pets(String animal, String breed, String age, String size, String gender,String profilePic) {
        this.animal = animal;
        this.breed = breed;
        this.age = age;
        this.size = size;
        this.gender = gender;
        this.profilePic = profilePic;
    }

    public Pets() {
    }
}
