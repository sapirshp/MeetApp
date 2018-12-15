package com.example.meetapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    private int DAYS_IN_CALENDAR = 7;
;
    static boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        for (int i = 0; i < DAYS_IN_CALENDAR; i++) {
            int morningBtnId = getResources().getIdentifier("d"+i+"m", "id", getPackageName());
            int afternoonBtnId = getResources().getIdentifier("d"+i+"a", "id", getPackageName());
            int eveningBtnId = getResources().getIdentifier("d"+i+"e", "id", getPackageName());
            final Button morningBtn = findViewById(morningBtnId);
            final TimeSlot morningTimeSlot = new TimeSlot(morningBtn);
            final Button afternoonBtn = findViewById(afternoonBtnId);
            final TimeSlot afternoonTimeSlot = new TimeSlot(afternoonBtn);
            final Button eveningBtn = findViewById(eveningBtnId);
            final TimeSlot eveningTimeSlot = new TimeSlot(eveningBtn);
            morningBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(morningTimeSlot);
                }
            });
            afternoonBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(afternoonTimeSlot);
                }
            });eveningBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(eveningTimeSlot);
                }
            });

        }
    }
    public void buttonSelection(TimeSlot timeSlot) {
        System.out.println(timeSlot.getDate());
        if (!timeSlot.getClicked()) {
            timeSlot.getButton().setBackgroundColor(Color.GREEN);
            timeSlot.setClicked(true);
        } else {
            if (timeSlot.getHour() != "Evening") {
                timeSlot.getButton().setBackgroundResource(R.drawable.border_bottom);
            }else {
                timeSlot.getButton().setBackgroundResource(R.drawable.evening_border);
            }
            timeSlot.setClicked(false);
        }



    }



}
