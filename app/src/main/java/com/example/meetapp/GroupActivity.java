package com.example.meetapp;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    ArrayList<CalendarTime> button = new ArrayList<>();
    static boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        for (int i = 1; i < 8; i++) {
            int idMorning = getResources().getIdentifier("d"+i+"m", "id", getPackageName());
            int idAfternoon = getResources().getIdentifier("d"+i+"a", "id", getPackageName());
            int idEvening = getResources().getIdentifier("d"+i+"e", "id", getPackageName());
            final Button btnMorning = findViewById(idMorning);
            final CalendarTime ctMorning = new CalendarTime(btnMorning,btnMorning.getParent().toString(), "Morning");
            final Button btnAfternoon = findViewById(idAfternoon);
            final CalendarTime ctAfternoon = new CalendarTime(btnAfternoon,btnMorning.getParent().toString(), "Afernoon");
            final Button btnEvening = findViewById(idEvening);
            final CalendarTime ctEvening = new CalendarTime(btnEvening,btnMorning.getParent().toString(), "Evening");
            btnMorning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(ctMorning);
                }
            });
            btnAfternoon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(ctAfternoon);
                }
            });btnEvening.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(ctEvening);
                }
            });

        }
    }
    public void buttonSelection(CalendarTime ct) {
        System.out.println(ct.getDate());
        if (!ct.getClicked()) {
            ct.getButton().setBackgroundColor(Color.GREEN);
            ct.setClicked(true);
        } else {
            if (ct.getHour() != "Evening") {
                ct.getButton().setBackgroundResource(R.drawable.border_bottom);
            }else {
                ct.getButton().setBackgroundResource(R.drawable.evening_border);
            }
            ct.setClicked(false);
        }



    }



}
