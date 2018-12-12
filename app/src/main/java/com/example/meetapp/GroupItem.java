package com.example.meetapp;

public class GroupItem
{
    private String GroupName;
    private String Participants;
    private String AdminPhone;
    private boolean isScheduled;

    public GroupItem(String groupName, String participants, String adminPhone)
    {
        GroupName = groupName;
        Participants = participants;
        AdminPhone = adminPhone;
    }

    public String getGroupName()
    {
        return GroupName;
    }

    public String getParticipants()
    {
        return Participants;
    }

    public String getAdminPhone() { return AdminPhone; }
}
