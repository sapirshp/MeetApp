package com.example.meetapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {

    private ArrayList<Integer> buttonsIdForListeners = new ArrayList<>();

    private int DAYS_IN_CALENDAR = 7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        for (int i = 0; i < DAYS_IN_CALENDAR; i++) {
            int morningBtnId = getResources().getIdentifier("d" + i + "m", "id", getPackageName());
            buttonsIdForListeners.add(morningBtnId);
            int afternoonBtnId = getResources().getIdentifier("d" + i + "a", "id", getPackageName());
            buttonsIdForListeners.add(afternoonBtnId);
            int eveningBtnId = getResources().getIdentifier("d" + i + "e", "id", getPackageName());
            buttonsIdForListeners.add(eveningBtnId);
        }
        for (int id : buttonsIdForListeners){
            final Button timeSlotButton = findViewById(id);
            final TimeSlot timeSlot = new TimeSlot(timeSlotButton);
            timeSlotButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(timeSlot);
                }
            });
        }
    }
    public void buttonSelection(TimeSlot timeSlot) {
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
