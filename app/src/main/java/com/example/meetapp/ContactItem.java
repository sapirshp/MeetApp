package com.example.meetapp;

public class ContactItem {
    private String contactName;
    private String contactNumber;

    public ContactItem (String name, String number){
        this.contactName = name;
        this.contactNumber = number;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }
}
