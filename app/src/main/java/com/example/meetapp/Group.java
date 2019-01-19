package com.example.meetapp;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class Group {
    private String name;
    private String admin;
    private String groupId;
    private List<String> members;
    public List<String> namesList;
    //    private @ServerTimestamp Date timestamp;
    private boolean isScheduled;
    private boolean isFirstEntrance;
    private HashMap<TimeSlot, Integer> groupSlotSelections;
    private String chosenDate;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Group(String name, String groupId, String admin, List<String> members, boolean isScheduled) {
        this.name = name;
//        this.timestamp = timestamp;
        this.groupId = groupId;
        this.admin = admin;
        this.members = members;
        this.isScheduled = isScheduled;
        this.isFirstEntrance = true;
        this.groupSlotSelections = new HashMap<>();
        this.chosenDate = "";
        this.namesList = new ArrayList<>();
    }

    public Group() {
        this.groupSlotSelections = new HashMap<>();
        this.namesList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name =  name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId =  groupId;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin =  admin;
    }

    public boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(boolean isScheduled) {
        this.isScheduled =  isScheduled;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members =  members;
    }

    public boolean getIsFirstEntrance() {
        return isFirstEntrance;
    }

    public void setIsFirstEntrance(boolean isFirstEntrance) {
        this.isFirstEntrance =  isFirstEntrance;
    }

    public HashMap<TimeSlot, Integer> getGroupSlotSelections(){
        return groupSlotSelections;
    }

    public void setGroupSlotSelections(HashMap<TimeSlot, Integer> groupSlotSelections) {
        this.groupSlotSelections =  groupSlotSelections;
    }

    public String getChosenDate() {
        return chosenDate;
    }

    public void setChosenDate(String chosenDate){
        this.chosenDate = chosenDate;
    }

    public String getMembersString() {
        String allMembers = "";
        for (String member : namesList) {
            allMembers += member + ", ";
        }
        if (!allMembers.isEmpty()) {
            allMembers = allMembers.substring(0, allMembers.length() - 2);
        }
        return allMembers;
    }

    public int getMembersAmount() {
        return members.size();
    }

    public boolean isFirstEntrance(){
        return isFirstEntrance;
    }


    public TimeSlot getTimeSlot(TimeSlot slotToGet){
        TimeSlot slotToReturn = slotToGet;
        for (TimeSlot groupTimeSlot : groupSlotSelections.keySet()){
            if (groupTimeSlot.getSlotIndex() == slotToGet.getSlotIndex()){
                slotToReturn = groupTimeSlot;
                break;
            }
        }
        return slotToReturn;
    }


}
