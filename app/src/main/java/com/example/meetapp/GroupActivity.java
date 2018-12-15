package com.example.meetapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GroupActivity extends AppCompatActivity {

    private ArrayList<String> datesToDisplay = new ArrayList<>(
            Arrays.asList("17/12/18", "18/12/18", "19/12/18", "20/12/18", "21/12/18", "22/12/18", "23/12/18"));

    private ArrayList<Integer> buttonsIdForListeners = new ArrayList<>();

    private int DAYS_IN_CALENDAR = 7;
    private int TOP_SELECTIONS_TO_DISPLAY = 3;

    private HashMap<TimeSlot,Integer> slotSelections = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        setDatesToDisplay();
        setButtonsIdForListeners();
        setListeners();
    }

    public void buttonSelection(TimeSlot timeSlot) {
        if (!timeSlot.getClicked()) {
            clickedOn(timeSlot);
        } else {
            clickedOff(timeSlot);
        }
        slotSelections = sortByValue(slotSelections);
        displayTopSelections();
    }

    public void clickedOn(TimeSlot timeSlot) {
        slotSelections.put(timeSlot, slotSelections.containsKey(timeSlot) ? slotSelections.get(timeSlot) + 1 : 1);
        timeSlot.getButton().setBackgroundColor(Color.GREEN);
        String textWithSelectionNumber = timeSlot.getHour() + "\n(" + slotSelections.get(timeSlot) + ")";
        timeSlot.getButton().setText(textWithSelectionNumber);
        timeSlot.setClicked(true);
    }

    public void clickedOff(TimeSlot timeSlot){
        if (!timeSlot.getHour().equals("Evening")) {
            timeSlot.getButton().setBackgroundResource(R.drawable.border_bottom);
        }else {
            timeSlot.getButton().setBackgroundResource(R.drawable.evening_border);
        }
        timeSlot.setClicked(false);
        String textWithSelectionNumber = timeSlot.getHour() + "\n";
        timeSlot.getButton().setText(textWithSelectionNumber);
        if (slotSelections.get(timeSlot)>1){
            slotSelections.put(timeSlot, slotSelections.containsKey(timeSlot) ? slotSelections.get(timeSlot) -1 : 1);
        }else{
            slotSelections.remove(timeSlot);
        }
    }

    public void setButtonsIdForListeners() {
        for (int i = 0; i < DAYS_IN_CALENDAR; i++) {
            int morningBtnId = getResources().getIdentifier("d" + i + "m", "id", getPackageName());
            buttonsIdForListeners.add(morningBtnId);
            int afternoonBtnId = getResources().getIdentifier("d" + i + "a", "id", getPackageName());
            buttonsIdForListeners.add(afternoonBtnId);
            int eveningBtnId = getResources().getIdentifier("d" + i + "e", "id", getPackageName());
            buttonsIdForListeners.add(eveningBtnId);
        }
    }

    public void setListeners(){
        for (int id : buttonsIdForListeners){
            final Button timeSlotButton = findViewById(id);
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

    public static HashMap<TimeSlot, Integer> sortByValue(HashMap<TimeSlot, Integer> hm)
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

    public void displayTopSelections(){
        TextView selectionsDisplayTextView = findViewById(R.id.selectionDisplay);
        String topSelections = "Top Suggesions:\n";
        int iterationNumber = TOP_SELECTIONS_TO_DISPLAY;
        if (iterationNumber > slotSelections.size()){
            iterationNumber = slotSelections.size();
        }
        for (int i = 0; i < iterationNumber; i++){
            TimeSlot topSlot = (TimeSlot) slotSelections.keySet().toArray()[i];
            topSelections = topSelections + topSlot.getDate() + " " + topSlot.getHour() + " - " +
                    + slotSelections.get(topSlot) + "\n";
        }
        selectionsDisplayTextView.setText(topSelections);
    }

    public void setDatesToDisplay(){
        for (int i = 0; i < DAYS_IN_CALENDAR; i++){
            int dayTextViewId = getResources().getIdentifier("d" + i , "id", getPackageName());
            TextView dayTextView = findViewById(dayTextViewId);
            dayTextView.setText(datesToDisplay.get(i));
        }
    }
}
