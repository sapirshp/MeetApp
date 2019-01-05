package com.example.meetapp;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MockDB {

    private static HashMap<TimeSlot, Integer> mockSlotSelections = new HashMap<>();
    private static HashMap<Integer, String> buttonsIdForMockDB = new HashMap<>();
    private static ArrayList<TimeSlot> calendarSlots = new ArrayList<>();

    private static void createMockSelections(View v, Context context, Map<String, String> datesToDisplay, int membersNum) {
        for (int i = 0; i < 7; i++) {
            int morningBtnId = context.getResources().getIdentifier("d" + i + "m", "id", context.getPackageName());
            buttonsIdForMockDB.put(morningBtnId, "Morning");
            int afternoonBtnId = context.getResources().getIdentifier("d" + i + "a", "id", context.getPackageName());
            buttonsIdForMockDB.put(afternoonBtnId, "Afternoon");
            int eveningBtnId = context.getResources().getIdentifier("d" + i + "e", "id", context.getPackageName());
            buttonsIdForMockDB.put(eveningBtnId, "Evening");
        }
        for (int id : buttonsIdForMockDB.keySet()) {
            final Button timeSlotButton = v.findViewById(id);
            int indexOfDate = Integer.valueOf(timeSlotButton.getTag().toString());
            String date = datesToDisplay.get(datesToDisplay.keySet().toArray()[indexOfDate]);
            String hour = buttonsIdForMockDB.get(id);
            final TimeSlot timeSlot = new TimeSlot(timeSlotButton, date, hour);
            calendarSlots.add(timeSlot);
        }
        for (TimeSlot ts : calendarSlots){
            Random rand = new Random();
            int randomNum = rand.nextInt((membersNum - 0) + 1) + 0;
            mockSlotSelections.put(ts, randomNum);
        }
    }

    static HashMap<TimeSlot, Integer> getMockSlotSelections(View v, Context context, Map<String, String> datesToDisplay, int membersNum){
        createMockSelections(v,context,datesToDisplay, membersNum);
        return mockSlotSelections;
    }

    static ArrayList<Group> buildMockGroups(String userName, ArrayList<Group> groups){
        for (int i=0; i<=1; i++)
        {
            List<String> members = Arrays.asList("Oren", "Chen", "Sapir");
            Group newGroup = new Group("Group" + i, "2", userName, members, false);
            groups.add(newGroup);
        }
        for (int i=2; i<=4; i++)
        {
            List<String> members = Arrays.asList("Oren", "Chen", "Sapir");
            Group newGroup = new Group("Group" + i, "2", userName, members, true);
            groups.add(newGroup);
        }
        return groups;
    }
}
