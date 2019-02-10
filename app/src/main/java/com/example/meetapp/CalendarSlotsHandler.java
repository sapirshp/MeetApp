package com.example.meetapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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
    private int slotIndex;
    private int membersAmount;
    private int topSelectionToDisplay;
    private Context context;
    private View view;
    private int bgColor;
    private int today;
    private String arrivalsText;
    private Drawable userChooseMark;
    private Group group;
    private String userId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference calendarRef;
    private HashMap<String,Long> userCalendar;
    private HashMap<String,Long> allUsersCalendar;
    private DocumentReference calendarRefForUpdate;

    CalendarSlotsHandler(Group group, String userId, Context context, View view) {
        this.group = group;
        this.userId = userId;
        this.membersAmount = group.getMembersAmount();
        this.topSelectionToDisplay = 3;
        this.context = context;
        this.view = view;
        calendarRefForUpdate = db.collection("calendars").document(group.getGroupId());
        setButtonsIdForListeners(DateSetter.getDaysInCalendar(), context);
        setListeners(DateSetter.getDatesToDisplay());
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
        readCalendarFromDB();
    }

    private void buttonSelection(TimeSlot timeSlot) {
        if (!timeSlot.getClicked()) {
            clickedOn(timeSlot);
        } else {
            clickedOff(timeSlot);
        }
        slotSelections = sortByValue(slotSelections);
        displayTopSelections();
    }

    private int convertToDBIndex(int index) {
        today = 8 - DateSetter.getTodayInt();
        int result = index - (today * 3);
        if (result < 0) {
            result = 21 + result;
        }
        return result;
    }

    void clickedOn(TimeSlot timeSlot) {
        timeSlot.setClicked(true);
        slotIndex = convertToDBIndex(timeSlot.getSlotIndex());
        int arrivalsAmount = slotSelections.get(timeSlot) + 1;
        slotSelections.put(timeSlot, arrivalsAmount);
        calendarRefForUpdate.update("all." + slotIndex, arrivalsAmount,
                userId + "." + slotIndex, 1);
    }

    void clickedOff(TimeSlot timeSlot) {
        timeSlot.setClicked(false);
        slotIndex = convertToDBIndex(timeSlot.getSlotIndex());
        int arrivalsAmount = slotSelections.get(timeSlot) - 1;
        slotSelections.put(timeSlot, arrivalsAmount);
        calendarRefForUpdate.update("all." + slotIndex, arrivalsAmount,
                userId + "." + slotIndex, 0);
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
            currentSlot = String.format("%s%s %s - %d/%d", currentSlot, topSlot.getDate(),
                    topSlot.getHour(), getSelectionNumber(topSlot), membersAmount);
            if (getSelectionNumber(topSlot) > 0) {
                topSuggestions.add(currentSlot);
            }
        }
        return topSuggestions;
    }

    private int getSelectionNumber(TimeSlot slotToFind) {
        for (TimeSlot wantedSlot : slotSelections.keySet()) {
            if (wantedSlot.getSlotIndex() == slotToFind.getSlotIndex()){
                return slotSelections.get(wantedSlot);
            }
        }
        return 0;
    }

    private void readCalendarFromDB() {
        calendarRef = db.collection("calendars").document(group.getGroupId());
        calendarRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    userCalendar = (HashMap<String,Long>) snapshot.get(userId);
                    allUsersCalendar = (HashMap<String,Long>) snapshot.get("all");
                    displayInitSelections(userCalendar, allUsersCalendar);
                }
            }
        });
    }

    private void displayInitSelections(HashMap<String,Long> userCalendar, HashMap<String,Long> allUsersCalendar) {
        today = DateSetter.getTodayInt() - 1;
        for (TimeSlot ts : slotSelections.keySet()) {
            int index = ts.getSlotIndex();
            String convertedIndex = String.valueOf(((today * 3) + index) % 21);
            long arrivalsAmount = allUsersCalendar.get(convertedIndex);
            if (arrivalsAmount > 0) {
                arrivalsText = arrivalsAmount + "/" + membersAmount;
                ts.getButton().setText(arrivalsText);
                slotSelections.put(ts, (int)arrivalsAmount);
                userChooseMark = context.getDrawable(R.drawable.empty);
                float percentage = ((float) arrivalsAmount / (float) membersAmount) * 100;
                bgColor = SlotBackgroundSetter.getColorPercentage(0xe0ffd2, 0x67a34c, (int) percentage);
            } else {
                ts.getButton().setText("");
                slotSelections.put(ts, 0);
                userChooseMark = context.getDrawable(R.drawable.empty);
                bgColor = Color.WHITE;
            }
            if (userCalendar.get(convertedIndex) == 1) {
                userChooseMark = context.getDrawable(R.drawable.v_green);
                ts.setClicked(true);
            }

            ts.getButton().setBackground(SlotBackgroundSetter.setBackGroundColorAndBorder(bgColor, userChooseMark, context));
        }
    }

    Context getContext(){
        return context;
    }
}
