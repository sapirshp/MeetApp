package com.example.meetapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

class MockDB {

    static void createMockSelections(HashMap<TimeSlot, Integer> mockSlotSelections, int membersNum) {
        for (TimeSlot ts : mockSlotSelections.keySet()){
            Random rand = new Random();
            int randomNum = rand.nextInt((membersNum-1 - 0) + 1) + 0;
            mockSlotSelections.put(ts, randomNum);
        }
    }

    static ArrayList<Group> buildMockGroups(String userName, ArrayList<Group> groups){
        for (int i=0; i<=1; i++)
        {
            List<String> members = Arrays.asList("Oren", "Chen", "Sapir");
            Group newGroup = new Group("Group" + i, "2", userName, members, false);
            groups.add(newGroup);
        }
        for (int i=2; i<=4; i++)
        {
            List<String> members = Arrays.asList("Oren", "Chen", "Sapir");
            Group newGroup = new Group("Group" + i, "2", userName, members, true);
            groups.add(newGroup);
        }
        return groups;
    }
}
