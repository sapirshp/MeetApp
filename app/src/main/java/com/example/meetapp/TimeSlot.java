package com.example.meetapp;

import android.widget.Button;

public class TimeSlot {
    private Button timeSlotBtn;
    private boolean isClicked;
    private String date;
    private String hour;
    private int chosenCounter;

    TimeSlot(Button button, String date){
        this.timeSlotBtn = button;
        this.date = date;
        this.hour = timeSlotBtn.getText().toString();
        isClicked = false;
        chosenCounter = 0;
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

    public void upperCounter(){
        chosenCounter++;
    }
    public void lowerCounter(){
        chosenCounter--;
    }
}
