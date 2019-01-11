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

    void groupNameAndMemberChanged(Intent insideGroupIntent, Activity insideGroupActivity, Toolbar toolbar, String oldName){
        insideGroupIntent.putExtra("EditGroupName", toolbar.getTitle().toString());
        insideGroupIntent.putExtra("OldName", oldName);
        insideGroupIntent.putExtra("AddMembers", toolbar.getSubtitle().toString());
        insideGroupIntent.putExtra("GroupName", toolbar.getTitle().toString());
        insideGroupActivity.setResult(CHANGE_NAME_AND_MEMBERS, insideGroupIntent);
        insideGroupActivity.finish();
    }

    void groupNameChanged(Intent insideGroupIntent, Activity insideGroupActivity, Toolbar toolbar, String oldName){
        insideGroupIntent.putExtra("EditGroupName", toolbar.getTitle().toString());
        insideGroupIntent.putExtra("OldName", oldName);
        insideGroupActivity.setResult(EDIT_NAME_RESULT_CODE, insideGroupIntent);
        insideGroupActivity.finish();
    }

    void groupMembersAdded(Intent insideGroupIntent, Activity insideGroupActivity, Toolbar toolbar){
        insideGroupIntent.putExtra("AddMembers", toolbar.getSubtitle().toString());
        insideGroupIntent.putExtra("GroupName", toolbar.getTitle().toString());
        insideGroupActivity.setResult(ADD_MEMBERS_RESULT_CODE, insideGroupIntent);
        insideGroupActivity.finish();
    }

    void defaultFinish(Activity insideGroupActivity){
        insideGroupActivity.finish();
    }

    void handleLeaveGroupResult(Intent data, ArrayList<Group> groups, RecyclerView.Adapter adapter){
        String groupName = data.getStringExtra("groupName");
        Group groupToRemove = null;
        for(Group group: groups){
            if (group.getName().equals(groupName)){
                groupToRemove = group;
                break;
            }
        }
        adapter.notifyItemRemoved(groups.indexOf(groupToRemove));
        groups.remove(groupToRemove);
        adapter.notifyDataSetChanged();
    }

    void handleAddMembersResult(Intent data, ArrayList<Group> groups, RecyclerView.Adapter adapter){
        Group groupToAddMembers = null;
        String newMembers = data.getStringExtra("AddMembers");
        List<String> newMembersList = new LinkedList<>(Arrays.asList(newMembers.replaceAll(",\\s",",").split(",")));
        String groupName = data.getStringExtra("GroupName");
        for(Group group: groups){
            if (group.getName().equals(groupName)){
                groupToAddMembers = group;
                break;
            }
        }
        groupToAddMembers.setMembers(newMembersList);
        adapter.notifyDataSetChanged();
    }

    void handleEditNameResult(Intent data, ArrayList<Group> groups, RecyclerView.Adapter adapter){
        Group groupToChangeName = null;
        String newGroupName = data.getStringExtra("EditGroupName");
        String oldName = data.getStringExtra("OldName");
        for(Group group: groups){
            if (group.getName().equals(oldName)){
                groupToChangeName = group;
                break;
            }
        }
        groupToChangeName.setName(newGroupName);
        adapter.notifyDataSetChanged();
    }

    void handleChangeNameAndMembersResult(Intent data, ArrayList<Group> groups, RecyclerView.Adapter adapter){
        Group groupToChange = null;
        String newGroupName = data.getStringExtra("EditGroupName");
        String oldName = data.getStringExtra("OldName");
        String newMembers = data.getStringExtra("AddMembers");
        List<String> newMembersList = new LinkedList<>(Arrays.asList(newMembers.replaceAll(",\\s",",").split(",")));
        for(Group group: groups){
            if (group.getName().equals(oldName)){
                groupToChange = group;
                break;
            }
        }
        groupToChange.setName(newGroupName);
        groupToChange.setMembers(newMembersList);
        adapter.notifyDataSetChanged();
    }
}
