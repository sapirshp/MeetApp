package com.example.meetapp;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.lang.reflect.Array;
import java.util.Date;

@IgnoreExtraProperties
public class Group
{
    private String groupName;
    private String admin;
    private String groupId;
    private String participants;
    //    private String[] participants;
//    private @ServerTimestamp Date timestamp;
    private boolean isScheduled;

    public Group(String groupName, String admin, String groupId, String participants)
    {
        this.groupName = groupName;
//        this.timestamp = timestamp;
        this.groupId = groupId;
        this.admin = admin;
        this.participants = participants;
//        this.participants = new String[]{};
        this.isScheduled = false;
    }

    public String getAdmin()
    {
        return admin;
    }

    public String getParticipants()
    {
        return participants;
    }
}
