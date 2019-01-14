package com.example.meetapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

class MockDB {
    private static int availableId = 0;
    private static ArrayList<Group> groups = new ArrayList<>();

    static HashMap<TimeSlot, Integer> createMockSelections(HashMap<TimeSlot, Integer> mockSlotSelections, int membersNum, Group group) {
        if (group.isFirstEntrance()) {
            setRandomSelections(mockSlotSelections, membersNum, group);
        }else {
            setGroupSelections(mockSlotSelections, group);
        }
        return mockSlotSelections;
    }

    private static void setRandomSelections(HashMap<TimeSlot, Integer> mockSlotSelections, int membersNum, Group group){
        for (TimeSlot ts : mockSlotSelections.keySet()) {
            Random rand = new Random();
            int randomNum = rand.nextInt((membersNum - 1) + 1);
            group.getGroupSlotSelections().put(ts,randomNum);
            group.setIsFirstEntrance(false);
            mockSlotSelections.put(ts, randomNum);
        }
    }

    private static void setGroupSelections(HashMap<TimeSlot, Integer> mockSlotSelections, Group group){
        for (TimeSlot ts : mockSlotSelections.keySet()) {
            TimeSlot groupTimeSlot = group.getTimeSlot(ts);
            if (groupTimeSlot != null) {
                if (groupTimeSlot.getClicked()){
                    ts.setClicked(true);
                }
                mockSlotSelections.put(ts, group.getGroupSlotSelections().get(groupTimeSlot));
            }
        }
    }

    static void buildMockGroups(String userName){

        for (int i=0; i<=1; i++)
        {
            List<String> members = Arrays.asList("Oren", "Chen", "Sapir");
            Group newGroup = new Group("Group" + i, findNextAvailableId(), userName, members, false);
            addGroupToList(newGroup);
        }
        for (int i=2; i<=4; i++)
        {
            List<String> members = Arrays.asList("Oren", "Chen", "Sapir");
            Group newGroup = new Group("Group" + i, findNextAvailableId(), userName, members, true);
            newGroup.setChosenDate(getChosenDate(i));
            addGroupToList(newGroup);
        }
    }

    private static String getChosenDate(int iterationNum){
        switch (iterationNum){
            case 2:
                return "Sat Morning";
            case 3:
                return "Sun Afternoon";
            default:
                return "Fri Evening";
        }
    }

    static String  findNextAvailableId(){
        int nextId = availableId;
        availableId++;
        return Integer.toString(nextId);
    }

    static void addGroupToList(Group newGroup){
        groups.add(newGroup);
    }

    static void addGroupToListFirst(Group newGroup){
        groups.add(0, newGroup);
    }

    static void removeGroupFromList(Group groupToRemove){
        groups.remove(groupToRemove);
        System.out.println(groups.toString());
    }

    static ArrayList<Group> getGroupsList(){
        return groups;
    }

    static Group getGroupById(String groupId){
        Group groupToReturn = null;
        for (Group group : groups){
            if (group.getGroupId().equals(groupId)){
                groupToReturn = group;
            }
        } return groupToReturn;
    }
}
