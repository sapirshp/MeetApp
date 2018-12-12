package com.example.meetapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FirstScreen extends AppCompatActivity {
    private static long back_pressed;
    private final int EXIT_DELAY = 2000;
    Dialog newGroupDialog;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<GroupItem> groupItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        groupItems = new ArrayList<>();

        createDemoCards();

        adapter = new MyAdapter(groupItems, this);
        recyclerView.setAdapter(adapter);

        newGroupDialog = new Dialog(this);

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
        TextView createGroup;
        createGroup = newGroupDialog.findViewById(R.id.CreateGroupBtn);
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewGroup();
                newGroupDialog.dismiss();
            }
        });
    }

    private void addNewGroup()
    {
        EditText userInput = newGroupDialog.findViewById(R.id.newGroupNameInput);
        String newGroupName = userInput.getText().toString();
        if(newGroupName.equals(""))
        {
            makeToastAndDismissDialog(newGroupDialog,"Empty Group Name Not Allowed!");
        }
        else if(groupNameAlreadyExists(newGroupName))
        {
            makeToastAndDismissDialog(newGroupDialog,"The Name You Chose Already exists!");
        }
        else{
            GroupItem newGroup = new GroupItem(newGroupName, "only me :) ", "+000");
            groupItems.add(newGroup);
            Toast.makeText(getBaseContext(), "New Group Created Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeToastAndDismissDialog(Dialog currentDialog, String message)
    {
        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
        currentDialog.dismiss();
    }

    private boolean groupNameAlreadyExists(String newGroupName)
    {
        for(GroupItem group:groupItems)
        {
            if(group.getGroupName().equals(newGroupName)) { return true; }
        }
        return false;
    }

    private void handleExitPopup() {
        TextView exitPopupBtn;
        exitPopupBtn = newGroupDialog.findViewById(R.id.exitNewGroupBtn);
        exitPopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGroupDialog.dismiss();
            }
        });
    }

    private void createDemoCards() {
        for (int i=0; i<=3; i++)
        {
            GroupItem newCur = new GroupItem("heading" + (i), "chen, sapir, oren", "+111");
            groupItems.add(newCur);
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
