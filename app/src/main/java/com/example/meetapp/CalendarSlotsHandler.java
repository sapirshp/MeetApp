package com.example.meetapp;

import android.content.Context;
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
    private ArrayList<Integer> buttonsIdForListeners = new ArrayList<>();
    private HashMap<TimeSlot,Integer> slotSelections = new HashMap<>();
    private int membersAmount;
    private int topSelectionToDisplay;


    CalendarSlotsHandler(int membersNun){
        this.membersAmount = membersNun;
        this.topSelectionToDisplay = 3;
    }


    void setButtonsIdForListeners(int daysNum, Context context) {
        for (int i = 0; i < daysNum; i++) {
            int morningBtnId = context.getResources().getIdentifier("d" + i + "m", "id", context.getPackageName());
            buttonsIdForListeners.add(morningBtnId);
            int afternoonBtnId = context.getResources().getIdentifier("d" + i + "a", "id", context.getPackageName());
            buttonsIdForListeners.add(afternoonBtnId);
            int eveningBtnId = context.getResources().getIdentifier("d" + i + "e", "id", context.getPackageName());
            buttonsIdForListeners.add(eveningBtnId);
        }
    }

    void setListeners(View v, Map<String, String> datesToDisplay){
        for (int id : buttonsIdForListeners){
            final Button timeSlotButton = v.findViewById(id);
            int indexOfDate = Integer.valueOf(timeSlotButton.getTag().toString());
            final TimeSlot timeSlot = new TimeSlot(timeSlotButton, datesToDisplay.get(indexOfDate));
            timeSlotButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    buttonSelection(timeSlot);
                }
            });
        }
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
            slotSelections.put(timeSlot, slotSelections.containsKey(timeSlot) ? slotSelections.get(timeSlot) + 1 : 1);
        }
        if (membersAmount > 1) {
            String textWithSelectionNumber = slotSelections.get(timeSlot) +
                    "/" + membersAmount;
            timeSlot.getButton().setText(textWithSelectionNumber);
        }
        timeSlot.setClicked(true);
        float percentage = ((float)slotSelections.get(timeSlot)/(float)membersAmount)*100;
        int bgColor = SlotBackgroundSetter.getColorPercentage(0xe0ffd2, 0x67a34c,(int)percentage);
        timeSlot.getButton().setBackground(SlotBackgroundSetter.setBackGroundColorAndBorder(bgColor));
    }

    void clickedOff(TimeSlot timeSlot){
        timeSlot.getButton().setBackgroundResource(R.drawable.custom_border);

        timeSlot.setClicked(false);
        String textWithSelectionNumber = timeSlot.getHour();
        timeSlot.getButton().setText(textWithSelectionNumber);
        if (slotSelections.get(timeSlot)>1){
            slotSelections.put(timeSlot, slotSelections.containsKey(timeSlot) ? slotSelections.get(timeSlot) -1 : 1);
        }else{
            slotSelections.remove(timeSlot);
        }
    }

    private HashMap<TimeSlot, Integer> sortByValue(HashMap<TimeSlot, Integer> hm)
    {
        List<Map.Entry<TimeSlot, Integer> > list = new LinkedList<>(hm.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<TimeSlot, Integer> >() {
            public int compare(Map.Entry<TimeSlot, Integer> o1,
                               Map.Entry<TimeSlot, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        HashMap<TimeSlot, Integer> sortedList = new LinkedHashMap<>();
        for (Map.Entry<TimeSlot, Integer> item : list) {
            sortedList.put(item.getKey(), item.getValue());
        }
        return sortedList;
    }

    private void displayTopSelections(){
        String topSelections = "Top Suggesions:\n";
        int iterationNumber = topSelectionToDisplay;
        if (iterationNumber > slotSelections.size()){
            iterationNumber = slotSelections.size();
        }
        for (int i = 0; i < iterationNumber; i++){
            TimeSlot topSlot = (TimeSlot) slotSelections.keySet().toArray()[i];
            topSelections = topSelections + topSlot.getDate() + " " + topSlot.getHour() + " - " +
                    + slotSelections.get(topSlot) + "/" + membersAmount + "\n";
        }
    }

    HashMap<TimeSlot,Integer> getSlotSelections(){
        return slotSelections;
    }

    void setMembersAmount(int newAmount){
        membersAmount = newAmount;
    }
}
