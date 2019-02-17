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
    private HashMap<String,Long> userCalendar;
    private HashMap<String,Long> allUsersCalendar;
    private final String CALENDER_COLLECTION_PATH = "calendars";
    private final String MORNING = "Morning";
    private final String AFTERNOON = "Afternoon";
    private final String EVENING = "Evening";
    private final String MORNING_PREFIX = "m";
    private final String AFTERNOON_PREFIX= "a";
    private final String EVENING_PREFIX = "e";
    private final String EMPTY = "";
    private String arrivalsText;
    private String userId;
    private int slotIndex;
    private int membersAmount;
    private int topSelectionToDisplay;
    private final int slotsAmount = 21;
    private final int slotsPerDay = 3;
    private int bgColor;
    private int today;
    private DocumentReference calendarRef;
    private DocumentReference calendarRefForUpdate;
    private Context context;
    private View view;
    private Drawable userChooseMark;
    private Group group;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    CalendarSlotsHandler(Group group, String userId, Context context, View view) {
        this.group = group;
        this.userId = userId;
        this.membersAmount = group.getMembersAmount();
        this.topSelectionToDisplay = 3;
        this.context = context;
        this.view = view;
        calendarRefForUpdate = db.collection(CALENDER_COLLECTION_PATH).document(group.getGroupId());
        setButtonsIdForListeners(DateSetter.getDaysInCalendar(), context);
        setListeners(DateSetter.getDatesToDisplay());
    }

    private void setButtonsIdForListeners(int daysNum, Context context) {
        final String idDefType = "id";
        final String dayPrefix = "d";
        for (int i = 0; i < daysNum; i++) {
            int morningBtnId = context.getResources().getIdentifier
                    (dayPrefix + i + MORNING_PREFIX, idDefType, context.getPackageName());
            buttonsIdForListeners.put(morningBtnId, MORNING);
            int afternoonBtnId = context.getResources().getIdentifier
                    (dayPrefix + i + AFTERNOON_PREFIX, idDefType, context.getPackageName());
            buttonsIdForListeners.put(afternoonBtnId, AFTERNOON);
            int eveningBtnId = context.getResources().getIdentifier
                    (dayPrefix + i + EVENING_PREFIX, idDefType, context.getPackageName());
            buttonsIdForListeners.put(eveningBtnId, EVENING);
        }
    }

    private int getHourIndex(String dayPart)
    {
        switch(dayPart) {
            case MORNING:
                return 0;
            case AFTERNOON:
                return 1;
            default:
                return 2;
        }
    }

    private void setListeners(Map<String, String> datesToDisplay) {
        for (int id : buttonsIdForListeners.keySet()) {
            final Button timeSlotButton = view.findViewById(id);
            int dateIndex = Integer.valueOf(timeSlotButton.getTag().toString());
            String date = datesToDisplay.get(datesToDisplay.keySet().toArray()[dateIndex]);
            String hour = buttonsIdForListeners.get(id);
            int hourIndex = getHourIndex(hour);
            int slotIndex = (slotsPerDay * dateIndex) + hourIndex;
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

    int convertToSlotsIndex(int index) {
        return (slotsAmount + index - (today * slotsPerDay)) % slotsAmount;
    }

    private int convertToDBIndex(int index) {
        return ((today * slotsPerDay) + index) % slotsAmount;
    }

    void clickedOn(TimeSlot timeSlot) {
        timeSlot.setClicked(true);
        slotIndex = convertToDBIndex(timeSlot.getSlotIndex());
        int arrivalsAmount = slotSelections.get(timeSlot) + 1;
        slotSelections.put(timeSlot, arrivalsAmount);
        calendarRefForUpdate.update("all." + slotIndex, arrivalsAmount,
                userId + "." + slotIndex, 1);
    }

    private HashMap<TimeSlot, Integer> sortByValue(HashMap<TimeSlot, Integer> hm) {
        List<Map.Entry<TimeSlot, Integer>> list = new LinkedList<>(hm.entrySet());
        timeSlotsComparator(list);
        HashMap<TimeSlot, Integer> sortedList = new LinkedHashMap<>();
        for (Map.Entry<TimeSlot, Integer> item : list) {
            sortedList.put(item.getKey(), item.getValue());
        }
        return sortedList;
    }

    private void timeSlotsComparator(List<Map.Entry<TimeSlot, Integer>> list) {
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
    }

    void clickedOff(TimeSlot timeSlot) {
        timeSlot.setClicked(false);
        slotIndex = convertToDBIndex(timeSlot.getSlotIndex());
        int arrivalsAmount = slotSelections.get(timeSlot) - 1;
        slotSelections.put(timeSlot, arrivalsAmount);
        calendarRefForUpdate.update("all." + slotIndex, arrivalsAmount,
                userId + "." + slotIndex, 0);
    }

    ArrayList<String> displayTopSelections(){
        slotSelections = sortByValue(slotSelections);
        ArrayList<String> topSuggestions = new ArrayList<>();
        int iterationNumber = topSelectionToDisplay;
        if (iterationNumber > slotSelections.size()) {
            iterationNumber = slotSelections.size();
        }
        chooseTopSelections(topSuggestions, iterationNumber);
        return topSuggestions;
    }

    private void chooseTopSelections(ArrayList<String> topSuggestions, int iterationNumber) {
        for (int i = 0; i < iterationNumber; i++) {
            String currentSlot = EMPTY;
            TimeSlot topSlot = (TimeSlot) slotSelections.keySet().toArray()[i];
            currentSlot = String.format(context.getString(R.string.timeSlotStringFormat),
                                        currentSlot, topSlot.getDate(), topSlot.getHour(),
                                        getSelectionNumber(topSlot), membersAmount);
            if (getSelectionNumber(topSlot) > 0) {
                topSuggestions.add(currentSlot);
            }
        }
    }

    private int getSelectionNumber(TimeSlot slotToFind) {
        for (TimeSlot wantedSlot : slotSelections.keySet()) {
            if (wantedSlot.getSlotIndex() == slotToFind.getSlotIndex()){
                return slotSelections.get(wantedSlot);
            }
        }
        return 0;
    }

    TimeSlot getSlotById(int slotId) {
        for (TimeSlot ts : slotSelections.keySet()) {
            if (ts.getSlotIndex() == slotId) {
                return ts;
            }
        }
        return null;
    }

    private void readCalendarFromDB() {
        calendarRef = db.collection(CALENDER_COLLECTION_PATH).document(group.getGroupId());
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
            String convertedIndex = String.valueOf(convertToDBIndex(index));
            long arrivalsAmount = allUsersCalendar.get(convertedIndex);
            decideButtonFormatAccordingToArrivals(ts, arrivalsAmount);
            if (userCalendar != null && userCalendar.get(convertedIndex) == 1) {
                userChooseMark = context.getDrawable(R.drawable.v_green);
                ts.setClicked(true);
            }
            ts.getButton().setBackground(SlotBackgroundSetter.setBackGroundColorAndBorder(bgColor, userChooseMark, context));
        }
    }

    private void decideButtonFormatAccordingToArrivals(TimeSlot ts, long arrivalsAmount) {
        if (arrivalsAmount > 0) {
            showButtonFormatChosenByOthers(ts, arrivalsAmount);
        } else {
            showButtonFormatUnchosenByOthers(ts);
        }
    }

    private void showButtonFormatUnchosenByOthers(TimeSlot ts) {
        ts.getButton().setText(EMPTY);
        slotSelections.put(ts, 0);
        userChooseMark = context.getDrawable(R.drawable.empty);
        bgColor = Color.WHITE;
    }

    private void showButtonFormatChosenByOthers(TimeSlot ts, long arrivalsAmount) {
        arrivalsText = arrivalsAmount + "/" + membersAmount;
        ts.getButton().setText(arrivalsText);
        slotSelections.put(ts, (int)arrivalsAmount);
        userChooseMark = context.getDrawable(R.drawable.empty);
        float percentage = ((float) arrivalsAmount / (float) membersAmount) * 100;
        bgColor = SlotBackgroundSetter.getColorPercentage(0xe0ffd2, 0x67a34c, (int) percentage);
    }

    Context getContext(){
        return context;
    }
}
