package com.example.meetapp;

public class User {
    private String name;
    private String userId;
    private String phoneNumber;

    public User (String name, String userId, String phoneNumber){
        this.name = name;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    public User (){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getphoneNumber() {
        return phoneNumber;
    }

    public void setphoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof User))return false;
        User otherUser = (User)other;
        return (this.userId.equals(otherUser.userId));
    }
}
