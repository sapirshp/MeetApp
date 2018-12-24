package com.example.meetapp;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Group {
    private String name;
    private String admin;
    private String groupId;
    private List<String> members;
    //    private @ServerTimestamp Date timestamp;
    private boolean isScheduled;

    public Group(String name, String groupId, String admin, List<String> members, boolean isScheduled) {
        this.name = name;
//        this.timestamp = timestamp;
        this.groupId = groupId;
        this.admin = admin;
        this.members = members;
        this.isScheduled = isScheduled;
    }

    public Group(String name, String groupId, String admin, boolean isScheduled) {
        this.name = name;
//        this.timestamp = timestamp;
        this.groupId = groupId;
        this.admin = admin;
        this.members = new ArrayList<>(Arrays.asList(admin));
        this.isScheduled = isScheduled;
    }

    public Group() {

    }

    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getAdmin() {
        return admin;
    }

    public boolean getIsScheduled() {
        return isScheduled;
    }

    public List<String> getMembers() {
        return members;
    }

    public String getMembersString() {
        String allMembers = "";
        for (String member : members) {
            allMembers += member + ", ";
        }
        return allMembers.substring(0, allMembers.length() - 2);
    }
}
