package com.example.meetapp;

import android.widget.Button;

public class TimeSlot {
    private Button timeSlotBtn;
    private boolean isClicked;
    private String date;
    private String hour;
    private int slotIndex;

    TimeSlot(Button button, String date, String hour, int slotIndex){
        this.timeSlotBtn = button;
        this.date = date;
        this.hour = hour;
        this.slotIndex = slotIndex;
        isClicked = false;
    }

    public Button getButton(){
        return timeSlotBtn;
    }

    public void setClicked(boolean clicked) {
        isClicked = clicked;
    }

    public boolean getClicked(){
        return isClicked;
    }

    public String getHour(){
        return hour;
    }

    public String getDate(){
        return date;
    }

    public int getSlotIndex(){
        return slotIndex;
    }

}
