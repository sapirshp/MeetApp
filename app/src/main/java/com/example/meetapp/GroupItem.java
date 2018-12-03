package com.example.meetapp;

public class GroupItem
{
    private String Head;
    private String Participants;
    private boolean isScheduled;

    public GroupItem(String head, String participants)
    {
        Head = head;
        Participants = participants;
    }

    public String getHead()
    {
        return Head;
    }

    public String getParticipants()
    {
        return Participants;
    }
}
