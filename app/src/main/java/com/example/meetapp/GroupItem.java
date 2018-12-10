package com.example.meetapp;

public class GroupItem
{
    private String GroupName;
    private String Participants;
    private boolean isScheduled;

    public GroupItem(String groupName, String participants)
    {
        GroupName = groupName;
        Participants = participants;
    }

    public String getHead()
    {
        return GroupName;
    }

    public String getParticipants()
    {
        return Participants;
    }
}
