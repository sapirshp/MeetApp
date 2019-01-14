package com.example.meetapp;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Group {
    private String name;
    private String admin;
    private String groupId;
    private List<String> members;
    //    private @ServerTimestamp Date timestamp;
    private boolean isScheduled;
    private boolean isFirstEntrance;
    private HashMap<TimeSlot, Integer> groupSlotSelections;
    private String chosenDate;

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
    }

    public Group(String name, String groupId, String admin, boolean isScheduled) {
        this.name = name;
//        this.timestamp = timestamp;
        this.groupId = groupId;
        this.admin = admin;
        this.members = new ArrayList<>(Arrays.asList(admin));
        this.isScheduled = isScheduled;
        this.isFirstEntrance = true;
        this.groupSlotSelections = new HashMap<>();
        this.chosenDate = "";
    }

    public Group() {

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

    public String getMembersString() {
        String allMembers = "";
        for (String member : members) {
            allMembers += member + ", ";
        }
        return allMembers.substring(0, allMembers.length() - 2);
    }

    public void setIsFirstEntrance(boolean isFirstEntrance){
        this.isFirstEntrance = isFirstEntrance;
    }

    public boolean isFirstEntrance(){
        return isFirstEntrance;
    }

    public HashMap<TimeSlot, Integer> getGroupSlotSelections(){
        return this.groupSlotSelections;
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

    public void setChosenDate(String chosenDate){
        this.chosenDate = chosenDate;
    }

    public String getChosenDate(){
        return this.chosenDate;
    }
}
