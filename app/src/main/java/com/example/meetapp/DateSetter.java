package com.example.meetapp;

import android.view.View;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class DateSetter {

    private static int DAYS_IN_CALENDAR = 7;
    private static HashMap<Integer, String> intsToDays = new HashMap<>();
    private static Map<String, String> datesToDisplay = new LinkedHashMap<>();

    static void createIntToDayMap() {
        intsToDays.put(1, "Sun");
        intsToDays.put(2, "Mon");
        intsToDays.put(3, "Tue");
        intsToDays.put(4, "Wed");
        intsToDays.put(5, "Thu");
        intsToDays.put(6, "Fri");
        intsToDays.put(7, "Sat");
    }

    static Map<String, String> createDatesMap() {
        Calendar groupCalender = Calendar.getInstance();
        int date = groupCalender.get(Calendar.DAY_OF_MONTH);
        String day;
        datesToDisplay.put(Integer.toString(date), "Today");
        for (int i = 1; i < DAYS_IN_CALENDAR; i++) {
            groupCalender.roll(Calendar.DATE, 1);
            date = groupCalender.get(Calendar.DAY_OF_MONTH);
            if (date == 1){
                groupCalender.roll(Calendar.MONTH, 1);
                int month = groupCalender.get(Calendar.MONTH);
                if (month == 0){
                    groupCalender.roll(Calendar.YEAR, 1);
                }
            }
            day = intsToDays.get(groupCalender.get(Calendar.DAY_OF_WEEK));
            datesToDisplay.put(Integer.toString(date), day);
        }
        return datesToDisplay;
    }

    static void setDatesToDisplay(View view){
        datesToDisplay = DateSetter.createDatesMap();
        for (int i = 0; i < DAYS_IN_CALENDAR; i++){
            int dayNumTextViewId = view.getResources().getIdentifier("d" + i + "n" , "id", view.getContext().getPackageName());
            TextView dayNumTextView = view.findViewById(dayNumTextViewId);
            dayNumTextView.setText((String)datesToDisplay.keySet().toArray()[i]);
            int dayTextViewId = view.getResources().getIdentifier("d" + i + "d" , "id", view.getContext().getPackageName());
            TextView dayTextView = view.findViewById(dayTextViewId);
            int dayString = view.getResources().getIdentifier(datesToDisplay.get((datesToDisplay.keySet().toArray()[i])), "string", view.getContext().getPackageName());
            dayTextView.setText(view.getContext().getString(dayString));
        }
    }

    static Map<String, String> getDatesToDisplay() {
        return datesToDisplay;
    }

    static int getDaysInCalendar(){
        return DAYS_IN_CALENDAR;
    }

    static String getToday(){
        Calendar groupCalender = Calendar.getInstance();
        return intsToDays.get(groupCalender.get(Calendar.DAY_OF_WEEK));
    }
}
