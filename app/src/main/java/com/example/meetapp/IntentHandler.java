package com.example.meetapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

class IntentHandler {
    private final int EDIT_NAME_RESULT_CODE = 2;
    private final int ADD_MEMBERS_RESULT_CODE = 3;
    private final int CHANGE_NAME_AND_MEMBERS = 4;

    void groupNameAndMemberChanged(Intent insideGroupIntent, Activity insideGroupActivity, Toolbar toolbar, String groupId){
        insideGroupIntent.putExtra("newGroupName", toolbar.getTitle().toString());
        insideGroupIntent.putExtra("groupId", groupId);
        insideGroupIntent.putExtra("newMembers", toolbar.getSubtitle().toString());
        insideGroupActivity.setResult(CHANGE_NAME_AND_MEMBERS, insideGroupIntent);
        insideGroupActivity.finish();
    }

    void groupNameChanged(Intent insideGroupIntent, Activity insideGroupActivity, Toolbar toolbar, String groupId){
        insideGroupIntent.putExtra("newGroupName", toolbar.getTitle().toString());
        insideGroupIntent.putExtra("groupId", groupId);
        insideGroupActivity.setResult(EDIT_NAME_RESULT_CODE, insideGroupIntent);
        insideGroupActivity.finish();
    }

    void groupMembersAdded(Intent insideGroupIntent, Activity insideGroupActivity, Toolbar toolbar, String groupId){
        insideGroupIntent.putExtra("newMembers", toolbar.getSubtitle().toString());
        insideGroupIntent.putExtra("groupId", groupId);
        insideGroupActivity.setResult(ADD_MEMBERS_RESULT_CODE, insideGroupIntent);
        insideGroupActivity.finish();
    }

    void defaultFinish(Activity insideGroupActivity){
        insideGroupActivity.finish();
    }

    void handleLeaveGroupResult(Intent data, RecyclerView.Adapter adapter){
        String groupId = data.getStringExtra("groupId");
        Group groupToRemove = MockDB.getGroupById(groupId);
        adapter.notifyItemRemoved(MockDB.getGroupsList().indexOf(groupToRemove));
        MockDB.removeGroupFromList(groupToRemove);
        adapter.notifyDataSetChanged();
    }

    void handleAddMembersResult(Intent data, RecyclerView.Adapter adapter){
        String newMembers = data.getStringExtra("newMembers");
        List<String> newMembersList = new LinkedList<>(Arrays.asList(newMembers.replaceAll(",\\s",",").split(",")));
        String groupId = data.getStringExtra("groupId");
        Group groupToAddMembers = MockDB.getGroupById(groupId);
        groupToAddMembers.setMembers(newMembersList);
        adapter.notifyDataSetChanged();
    }

    void handleEditNameResult(Intent data, RecyclerView.Adapter adapter){
        String newGroupName = data.getStringExtra("newGroupName");
        String groupId = data.getStringExtra("groupId");
        Group groupToChangeName = MockDB.getGroupById(groupId);
        groupToChangeName.setName(newGroupName);
        adapter.notifyDataSetChanged();
    }

    void handleChangeNameAndMembersResult(Intent data, RecyclerView.Adapter adapter){
        String newGroupName = data.getStringExtra("newGroupName");
        String groupId = data.getStringExtra("groupId");
        String newMembers = data.getStringExtra("newMembers");
        List<String> newMembersList = new LinkedList<>(Arrays.asList(newMembers.replaceAll(",\\s",",").split(",")));
        Group groupToChange = MockDB.getGroupById(groupId);
        groupToChange.setName(newGroupName);
        groupToChange.setMembers(newMembersList);
        adapter.notifyDataSetChanged();
    }
}
