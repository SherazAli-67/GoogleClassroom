package com.sheraz.app.googleclassroom.classroom.model;

public class User{
    String name, user_id, phoneNumber, img;

    public User() {
    }

    public User(String name, String user_id, String phoneNumber, String img) {
        this.name = name;
        this.user_id = user_id;
        this.phoneNumber = phoneNumber;
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getImg() {
        return img;
    }
}