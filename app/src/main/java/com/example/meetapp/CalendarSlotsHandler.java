package com.example.meetapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class CalendarSlotsHandler {
    private static HashMap<Integer, String> buttonsIdForListeners = new HashMap<>();
    private HashMap<TimeSlot, Integer> slotSelections = new HashMap<>();
    private ArrayList<TimeSlot> userClicks = new ArrayList<>();
    private int membersAmount;
    private int topSelectionToDisplay;
    private Context context;
    private View view;
    private int bgColor;
    private String textWithSelectionNumber;
    private Drawable userChooseMark;


    CalendarSlotsHandler(int membersNun, Context context, View view) {
        this.membersAmount = membersNun;
        this.topSelectionToDisplay = 3;
        this.context = context;
        this.view = view;
    }


    void setButtonsIdForListeners(int daysNum, Context context) {
        for (int i = 0; i < daysNum; i++) {
            int morningBtnId = context.getResources().getIdentifier("d" + i + "m", "id", context.getPackageName());
            buttonsIdForListeners.put(morningBtnId, "Morning");
            int afternoonBtnId = context.getResources().getIdentifier("d" + i + "a", "id", context.getPackageName());
            buttonsIdForListeners.put(afternoonBtnId, "Afternoon");
            int eveningBtnId = context.getResources().getIdentifier("d" + i + "e", "id", context.getPackageName());
            buttonsIdForListeners.put(eveningBtnId, "Evening");
        }
    }

    private int getHourIndex(String dayPart)
    {
        switch(dayPart) {
            case "Morning":
                return 0;
            case "Afternoon":
                return 1;
            default:
                return 2;
        }
    }

    void setListeners(Map<String, String> datesToDisplay) {
        for (int id : buttonsIdForListeners.keySet()) {
            final Button timeSlotButton = view.findViewById(id);
            int dateIndex = Integer.valueOf(timeSlotButton.getTag().toString());
            String date = datesToDisplay.get(datesToDisplay.keySet().toArray()[dateIndex]);
            String hour = buttonsIdForListeners.get(id);
            int hourIndex = getHourIndex(hour);
            int slotIndex = (3 * dateIndex) + hourIndex;
            final TimeSlot timeSlot = new TimeSlot(timeSlotButton, date, hour, slotIndex);
            slotSelections.put(timeSlot, 0);
            timeSlotButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(timeSlot);
                }
            });
        }
        displayInitSelections();
    }

    private void buttonSelection(TimeSlot timeSlot) {
        if (!timeSlot.getClicked()) {
            clickedOn(timeSlot, false);
        } else {
            clickedOff(timeSlot);
        }
        slotSelections = sortByValue(slotSelections);
        displayTopSelections();
    }

    void clickedOn(TimeSlot timeSlot, boolean isMemberAmountRefresh) {
        if (!isMemberAmountRefresh) {
            setSelectionNumber(timeSlot, true);
            timeSlot.setClicked(true);
            userChooseMark = context.getDrawable(R.drawable.v_green);
            userClicks.add(timeSlot);
        }
        if (getSelectionNumber(timeSlot) > 0) {
            if (membersAmount > 1) {
                textWithSelectionNumber = getSelectionNumber(timeSlot) +
                        "/" + membersAmount;
                timeSlot.getButton().setText(textWithSelectionNumber);
            }
            if (isMemberAmountRefresh) {
                userChooseMark = context.getDrawable(R.drawable.empty);
                if (containsInUserClicked(timeSlot)){
                    userChooseMark = context.getDrawable(R.drawable.v_green);
                }
            }
            float percentage = ((float) getSelectionNumber(timeSlot) / (float) membersAmount) * 100;
            bgColor = SlotBackgroundSetter.getColorPercentage(0xe0ffd2, 0x67a34c, (int) percentage);
            timeSlot.getButton().setBackground(SlotBackgroundSetter.setBackGroundColorAndBorder(bgColor, userChooseMark, context));
        }
    }

    void clickedOff(TimeSlot timeSlot) {
        timeSlot.setClicked(false);
        userClicks.remove(timeSlot);
        setSelectionNumber(timeSlot, false);
        userChooseMark = context.getDrawable(R.drawable.empty);
        if (getSelectionNumber(timeSlot)>0){
            float percentage = ((float) getSelectionNumber(timeSlot) / (float) membersAmount) * 100;
            bgColor = SlotBackgroundSetter.getColorPercentage(0xe0ffd2, 0x67a34c, (int) percentage);
                textWithSelectionNumber = getSelectionNumber(timeSlot) + "/" + membersAmount;
        }
        else {
            bgColor = Color.WHITE;
            textWithSelectionNumber = "";
        }
        timeSlot.getButton().setBackground(SlotBackgroundSetter.setBackGroundColorAndBorder(bgColor, userChooseMark, context));
        timeSlot.getButton().setText(textWithSelectionNumber);
    }

    private HashMap<TimeSlot, Integer> sortByValue(HashMap<TimeSlot, Integer> hm) {
        List<Map.Entry<TimeSlot, Integer>> list = new LinkedList<>(hm.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<TimeSlot, Integer>>() {
            public int compare(Map.Entry<TimeSlot, Integer> o1,
                               Map.Entry<TimeSlot, Integer> o2) {
                int selectionCompare = o2.getValue().compareTo(o1.getValue());
                if (selectionCompare != 0) {
                    return selectionCompare;
                }
                int firstSlot = o1.getKey().getSlotIndex();
                int secondSlot = o2.getKey().getSlotIndex();
                return Integer.compare(firstSlot, secondSlot);
            }
        });

        HashMap<TimeSlot, Integer> sortedList = new LinkedHashMap<>();
        for (Map.Entry<TimeSlot, Integer> item : list) {
            sortedList.put(item.getKey(), item.getValue());
        }
        return sortedList;
    }

    public ArrayList<String> displayTopSelections(){
        slotSelections = sortByValue(slotSelections);
        ArrayList<String> topSuggestions = new ArrayList<>();
        int iterationNumber = topSelectionToDisplay;
        if (iterationNumber > slotSelections.size()) {
            iterationNumber = slotSelections.size();
        }
        for (int i = 0; i < iterationNumber; i++) {
            String currentSlot = "";
            TimeSlot topSlot = (TimeSlot) slotSelections.keySet().toArray()[i];
            currentSlot = String.format("%s%s %s - %d/%d", currentSlot, topSlot.getDate(), topSlot.getHour(), getSelectionNumber(topSlot), membersAmount);
            if (getSelectionNumber(topSlot) > 0) {
                topSuggestions.add(currentSlot);
            }
        }
        return topSuggestions;
    }

    HashMap<TimeSlot, Integer> getSlotSelections() {
        return slotSelections;
    }

    void setMembersAmount(int newAmount) {
        membersAmount = newAmount;
    }

    private int getSelectionNumber(TimeSlot timeSlot) {
        for (TimeSlot slot : slotSelections.keySet()) {
            if (slot.getDate().equals(timeSlot.getDate()) && slot.getHour().equals(timeSlot.getHour())) {
                return slotSelections.get(slot);
            }
        }
        return 0;
    }

    private boolean containsInUserClicked(TimeSlot timeSlot){
        for (TimeSlot ts : userClicks){
            if (ts.getDate() == timeSlot.getDate() && ts.getHour() == timeSlot.getHour()){
                return true;
            }
        }
        return false;
    }

    private void setSelectionNumber(TimeSlot timeSlot, boolean add) {
        boolean doneSetting = false;
        for (TimeSlot slot : slotSelections.keySet()) {
            if (slot.getDate() == timeSlot.getDate() && slot.getHour() == (timeSlot.getHour())) {
                if (add) {
                    if (getSelectionNumber(timeSlot) < membersAmount) {
                        slotSelections.put(slot, getSelectionNumber(timeSlot) + 1);
                    }
                    doneSetting = true;
                    break;
                } else {
                    if (getSelectionNumber(slot) > 0) {
                        slotSelections.put(slot, getSelectionNumber(slot) - 1);
                        break;
                    }
                }
            }
        }
        if (add && !doneSetting){
            slotSelections.put(timeSlot, 1);
        }
    }

    private void displayInitSelections() {
        MockDB.createMockSelections(slotSelections, membersAmount);
        for (TimeSlot ts : slotSelections.keySet()) {
            if (getSelectionNumber(ts) > 0) {
                textWithSelectionNumber = getSelectionNumber(ts) + "/" + membersAmount;
                ts.getButton().setText(textWithSelectionNumber);
                userChooseMark = context.getDrawable(R.drawable.empty);
                float percentage = ((float) getSelectionNumber(ts) / (float) membersAmount) * 100;
                bgColor = SlotBackgroundSetter.getColorPercentage(0xe0ffd2, 0x67a34c, (int) percentage);
            } else {
                userChooseMark = context.getDrawable(R.drawable.empty);
                bgColor = Color.WHITE;
            }
            ts.getButton().setBackground(SlotBackgroundSetter.setBackGroundColorAndBorder(bgColor, userChooseMark, context));
        }
    }

    Context getContext(){
        return context;
    }

    ArrayList<TimeSlot> getUserClicks() {
        return userClicks;
    }
}
