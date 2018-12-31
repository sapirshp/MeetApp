package com.example.meetapp;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupsDisplayActivity extends AppCompatActivity {
    Dialog newGroupDialog;
    private static long back_pressed;
    private final int EXIT_DELAY = 2000;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Group> groups = new ArrayList<>();
    private String userName = "Oren";
    private String phoneNumber = "972528240512";

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

    public void showNewGroupPopup(View v)
    {
        newGroupDialog.setContentView(R.layout.new_group_popup);
        handleExitPopup();
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
            List<String> members = Arrays.asList("You");
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

    private void handleExitPopup()
    {
        TextView exitPopupBtn;
        exitPopupBtn = newGroupDialog.findViewById(R.id.exitNewGroupBtn);
        exitPopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGroupDialog.dismiss();
            }
        });
    }

    private void loadGroups() {
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

}
