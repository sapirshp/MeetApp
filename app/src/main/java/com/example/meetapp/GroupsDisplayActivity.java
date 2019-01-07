package com.example.meetapp;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class GroupsDisplayActivity extends AppCompatActivity {
    Dialog newGroupDialog;
    Dialog addMembersDialog;
    private static long back_pressed;
    private final int EXIT_DELAY = 2000;
    private static RecyclerView recyclerView;
    private static RecyclerView.Adapter adapter;
    public static ArrayList<Group> groups = new ArrayList<>();
    private String userName = "Oren";
    private String phoneNumber = "972528240512";
    private ArrayList<String> membersToAdd = new ArrayList<>();
    private ArrayList<String> members = new ArrayList<>();
    private ArrayList<ContactItem> contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadGroups();
        setToolbar();
        adapter = new GroupAdapter(groups, this);
        recyclerView.setAdapter(adapter);
        newGroupDialog = new Dialog(this);
        addMembersDialog = new Dialog(this);
    }

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("MeetApp");
            getSupportActionBar().setLogo(R.drawable.meetapp_logo_toolbar);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
    }

    private void showNewGroupPopup(String members)
    {
        newGroupDialog.setContentView(R.layout.new_group_popup);
        TextView membersList = newGroupDialog.findViewById(R.id.membersList);
        membersList.setText(members);
        TextView exitBtn = newGroupDialog.findViewById(R.id.exitNewGroupBtn);
        handleExitPopup(newGroupDialog, exitBtn);
        handleCreateNewGroup();
        newGroupDialog.show();
    }

    private void handleCreateNewGroup()
    {
        final Button createGroup;
        createGroup = newGroupDialog.findViewById(R.id.CreateGroupBtn);
        EditText userInput = newGroupDialog.findViewById(R.id.newGroupNameInput);
        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    createGroup.setBackgroundResource(R.drawable.disabled_button_background);
                }else {
                    createGroup.setBackgroundResource(R.drawable.green_round_background);
                    createGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handleNewGroupRequest();
                        }
                    });
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleNewGroupRequest()
    {
        EditText userInput = newGroupDialog.findViewById(R.id.newGroupNameInput);
        String newGroupName = userInput.getText().toString();
        if(groupNameAlreadyExists(newGroupName))
        {
            makeToastToCenterOfScreen(getString(R.string.groupNameExists));
        }
        else {
            Group newGroup = new Group(newGroupName, "1", userName, members, false);
            groups.add(newGroup);
            makeToastToCenterOfScreen(getString(R.string.newGroupCreated));
            newGroupDialog.dismiss();
        }
    }

    private void makeToastToCenterOfScreen(String message)
    {
        Toast toast = Toast.makeText(getBaseContext(),message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private boolean groupNameAlreadyExists(String newGroupName)
    {
        for(Group group:groups)
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
                membersToAdd.clear();
            }
        });
    }

    private void loadGroups() {
        groups = MockDB.buildMockGroups(userName, groups);
    }

    @Override
    public void onBackPressed()
    {
        if (back_pressed + EXIT_DELAY > System.currentTimeMillis())
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

    public void handleAddNewMembers(View v){
        addMembersDialog.setContentView(R.layout.add_member_popup);
        TextView exitBtn = addMembersDialog.findViewById(R.id.addMemberExitBtn);
        handleExitPopup(addMembersDialog, exitBtn);
        showContacts();
        ChooseMembers();
        addMembersDialog.show();
    }

    public void onContactClick(View v){
        RelativeLayout contactLayout = v.findViewById(R.id.contactLayout);
        TextView contactName = v.findViewById(R.id.contactName);
        ImageButton okButton = addMembersDialog.findViewById(R.id.chooseMembers);
        if (contactLayout.getTag() != "chosen") {
            okButton.setBackgroundResource(R.drawable.green_round_background);
            contactLayout.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            contactLayout.setTag("chosen");
            membersToAdd.add((String) contactName.getText());
        }else{
            membersToAdd.remove((String) contactName.getText());
            if (membersToAdd.isEmpty()){
                okButton.setBackgroundResource(R.drawable.disabled_button_background);
            }
            contactLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            contactLayout.setTag("notChosen");
        }
    }

    private void showContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 100);
        } else {
            RecyclerView contactRecyclerView = addMembersDialog.findViewById(R.id.contactsRecyclerView);
            contactRecyclerView.setHasFixedSize(true);
            contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            contacts = ContactsGetter.getContacts(this);
            RecyclerView.Adapter contactAdapter = new ContactsAdapter(contacts);
            contactRecyclerView.setAdapter(contactAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(this, getString(R.string.ContactsPermissionError), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void ChooseMembers(){
        members.clear();
        members.add("You");
        ImageButton okBtn = addMembersDialog.findViewById(R.id.chooseMembers);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                members.addAll(membersToAdd);
                addMembersDialog.dismiss();
                String membersNames = String.format("Group Members: %s",members.toString().substring(1,members.toString().length()-1));
                showNewGroupPopup(membersNames);
                membersToAdd.clear();
            }
        });
    }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
             if(resultCode == RESULT_OK) {
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
        }
    }
}
