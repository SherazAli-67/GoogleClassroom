package com.sheraz.app.googleclassroom.classroom.model;

public class Messages {
    String message;
    String message_id;
    String sender_id;
    String phoneNumber;

    String imgURL;
    String timeStamp;

    public Messages() {
    }

    public Messages(String message, String sender_id,  String timeStamp,String phoneNumber) {
        this.message = message;
        this.sender_id = sender_id;
        this.phoneNumber = phoneNumber;
        this.timeStamp = timeStamp;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getMessage_id() {
        return message_id;
    }

    public String getSender_id() {
        return sender_id;
    }

    public String getTimeStamp() {
        return timeStamp;
    }
}
