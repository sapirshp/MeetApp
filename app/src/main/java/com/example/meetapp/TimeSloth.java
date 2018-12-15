package com.example.meetapp;

import android.widget.Button;

public class TimeSloth {
    private Button btn;
    private boolean isClicked;
    private String date;
    private String hour;
    private int chosenCounter;

    TimeSloth(Button button){
        this.btn = button;
        this.date = btn.getParent().toString();
        this.hour = btn.getText().toString();
        isClicked = false;
        chosenCounter = 0;

    }

    public Button getButton(){
        return btn;
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
