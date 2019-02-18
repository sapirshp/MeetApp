package com.example.meetapp;

import java.util.ArrayList;
import java.util.List;

// According to Firebase documentation, Each custom class must have a public constructor that takes
// no arguments. In addition, the class must include a public getter for each property, in order to
// use the toObject method, that turn the data back into object instance.
public class Group {
    private String name;
    private String admin;
    private String groupId;
    private List<String> members;
    public List<String> namesList;
    private boolean isScheduled;
    private String chosenDate;

    public Group(String name, String groupId, String admin, List<String> members, boolean isScheduled) {
        this.name = name;
        this.groupId = groupId;
        this.admin = admin;
        this.members = members;
        this.isScheduled = isScheduled;
        this.chosenDate = "";
        this.namesList = new ArrayList<>();
    }

    public Group() {
        this.namesList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public String getChosenDate() {
        return chosenDate;
    }

    public void setChosenDate(String chosenDate) {
        this.chosenDate = chosenDate;
    }

    public void addNameToNamesList(String name) {
        this.namesList.add(name);
    }

    public void resetNamesList() {
        this.namesList.clear();
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
}
