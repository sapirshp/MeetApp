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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class GroupsDisplayFeaturesHandler {
    private Dialog newGroupDialog;
    private Dialog addMembersDialog;
    private Activity activity;
    private ArrayList<String> members = new ArrayList<>();
    private RecyclerView.Adapter adapter;


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

    private void handleNewGroupRequest(String userName, List<String> members)
    {
        EditText userInput = newGroupDialog.findViewById(R.id.newGroupNameInput);
        String newGroupName = userInput.getText().toString();
        if(groupNameAlreadyExists(newGroupName))
        {
            makeToastToCenterOfScreen(activity.getString(R.string.groupNameExists));
        }
        else {
            Group newGroup = new Group(newGroupName, MockDB.findNextAvailableId(), userName, members, false);
            MockDB.addGroupToListFirst(newGroup);
            adapter.notifyDataSetChanged();
            makeToastToCenterOfScreen(activity.getString(R.string.newGroupCreated));
            newGroupDialog.dismiss();
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

    void handleAddNewMembers(final String userName){
        addMembersDialog.setContentView(R.layout.add_member_popup);
        TextView exitBtn = addMembersDialog.findViewById(R.id.addMemberExitBtn);
        handleExitPopup(addMembersDialog, exitBtn);
        ContactsGetter.showContacts(activity, addMembersDialog);
        AddMembersHandler.chooseMembers(new Runnable() {
            @Override
            public void run() {
                onChooseMembers(userName);
            }
        });
        addMembersDialog.show();
    }

    private void onChooseMembers(String userName){
        members.clear();
        members.add("You");
        members.addAll(AddMembersHandler.getMembersToAdd());
        String groupMembers = members.toString().substring(1,members.toString().length()-1);
        String membersNames = String.format("Group Members: %s",groupMembers);
        showNewGroupPopup(membersNames, userName);
    }
}
