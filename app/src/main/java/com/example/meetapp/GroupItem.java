package com.example.meetapp;

public class GroupItem
{
    private String Admin;
    private String Participants;
    private boolean isScheduled;

    public GroupItem(String admin, String participants)
    {
        Admin = admin;
        Participants = participants;
    }

    public String getHead()
    {
        return Admin;
    }

    public String getParticipants()
    {
        return Participants;
    }
}
