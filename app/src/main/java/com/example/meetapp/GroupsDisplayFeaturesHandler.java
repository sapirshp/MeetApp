package com.example.meetapp;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GroupsDisplayFeaturesHandler {
    private Dialog newGroupDialog;
    private Dialog addMembersDialog;
    private Activity activity;
    private ArrayList<String> members = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private int TIME_SLOTS_AMOUNT = 21;

    GroupsDisplayFeaturesHandler(Activity activity, HashMap<String, Dialog> dialogs, RecyclerView.Adapter adapter){
        this.newGroupDialog = dialogs.get("newGroupDialog");
        this.addMembersDialog = dialogs.get("addMembersDialog");
        this.activity = activity;
        this.adapter = adapter;
    }

    private void showNewGroupPopup(String membersNames, String userName) {
        newGroupDialog.setContentView(R.layout.new_group_popup);
        TextView membersList = newGroupDialog.findViewById(R.id.membersList);
        membersList.setText(membersNames);
        TextView exitBtn = newGroupDialog.findViewById(R.id.exitNewGroupBtn);
        handleExitPopup(newGroupDialog, exitBtn);
        handleCreateNewGroup(members, userName);
        newGroupDialog.show();
    }

    private void handleCreateNewGroup(List<String> members, String userName) {
        final Button createGroup = newGroupDialog.findViewById(R.id.CreateGroupBtn);
        EditText userInput = newGroupDialog.findViewById(R.id.newGroupNameInput);
        userInput.addTextChangedListener(onInputChange(createGroup, members, userName));
    }

    private TextWatcher onInputChange(final Button createGroup, final List<String> members, final String userName) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    createGroup.setBackgroundResource(R.drawable.disabled_button_background);
                }else {
                    createGroup.setBackgroundResource(R.drawable.green_round_background);
                    createGroup.setOnClickListener(onCreateGroupBtn(members, userName));
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private View.OnClickListener onCreateGroupBtn(final List<String> members, final String userName){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNewGroupRequest(userName, members);
            }
        };
    }

    private void handleNewGroupRequest(final String userName, final List<String> members)
    {
        EditText userInput = newGroupDialog.findViewById(R.id.newGroupNameInput);
        final String newGroupName = userInput.getText().toString();
        if(groupNameAlreadyExists(newGroupName))
        {
            makeToastToCenterOfScreen(activity.getString(R.string.groupNameExists));
        }
        else {
            String newGroupId = addNewGroupToDB(userName, newGroupName, members);
            Group newGroup = new Group(newGroupName, newGroupId, userName, members, false);
            MockDB.addGroupToListFirst(newGroup);
            adapter.notifyDataSetChanged();
            makeToastToCenterOfScreen(activity.getString(R.string.newGroupCreated));
            newGroupDialog.dismiss();
        }
    }

    private String addNewGroupToDB(final String userName, final String groupName,
                                   final List<String> groupMembers)
    {
        Map<String, Object> group = new HashMap<String, Object>()
        {{
            put("name", groupName);
            put("admin", userName);
            put("members", groupMembers);
            put("isScheduled", false);
            put("chosenDate", "");
        }};
        DocumentReference groupRef = db.collection("groups").document();
        String groupId = groupRef.getId();
        group.put("groupId", groupId);
        groupRef.set(group);
        addGroupCalendar(groupMembers, groupId);
        addGroupToUsers(groupMembers, groupId);
        return groupId;
    }

    private void addGroupCalendar(final List<String> groupMembers, final String groupId) {
        Map<String, Integer> initialMap = new HashMap<String, Integer>();
        for (int i = 0; i < TIME_SLOTS_AMOUNT; i++) {
            initialMap.put(String.valueOf(i), 0);
        }
        Map<String, Object> initialCalendar = new HashMap<String, Object>();
        initialCalendar.put("all", initialMap);
        DocumentReference calendarRef = db.collection("calendars").document(groupId);
        for (String member: groupMembers) {
            initialCalendar.put(member, initialMap);
        }
        calendarRef.set(initialCalendar);
    }

    private void addGroupToUsers(final List<String> groupMembers, final String groupId) {
        CollectionReference allUsersRef = db.collection("users");
        DocumentReference userRef;
        for (String memberID: groupMembers) {
            userRef = allUsersRef.document(memberID);
            userRef.update("memberOf", FieldValue.arrayUnion(groupId));
        }
    }

    private void makeToastToCenterOfScreen(String message)
    {
        Toast toast = Toast.makeText(activity.getBaseContext(),message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private boolean groupNameAlreadyExists(String newGroupName)
    {
        for(Group group: MockDB.getGroupsList())
        {
            if(group.getName().equals(newGroupName)) { return true; }
        }
        return false;
    }

    private void handleExitPopup(final Dialog dialog, TextView exitBtn)
    {
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (dialog == addMembersDialog) {
                    AddMembersHandler.clearMembersToAdd();
                }
            }
        });
    }

    void handleAddNewMembers(final String userName, final String userID){
        addMembersDialog.setContentView(R.layout.add_member_popup);
        TextView exitBtn = addMembersDialog.findViewById(R.id.addMemberExitBtn);
        handleExitPopup(addMembersDialog, exitBtn);
        ContactsGetter.showContacts(activity, addMembersDialog);
        AddMembersHandler.chooseMembers(new Runnable() {
            @Override
            public void run() {
                onChooseMembers(userName, userID);
            }
        });
        addMembersDialog.show();
    }

    private void onChooseMembers(String userName, String userID){
        members.clear();
        members.add(userID);
        members.addAll(AddMembersHandler.getMembersToAdd());
        String groupMembers = members.toString().substring(1,members.toString().length()-1);
        String membersNames = String.format("Group Members: %s",groupMembers);
        showNewGroupPopup(membersNames, userName);
    }
}
