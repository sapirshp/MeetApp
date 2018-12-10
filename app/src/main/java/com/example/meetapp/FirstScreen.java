package com.example.meetapp;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FirstScreen extends AppCompatActivity {
    private static long back_pressed;
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
            Toast.makeText(getBaseContext(), "Empty Name Not Allowed!", Toast.LENGTH_SHORT).show();
            newGroupDialog.dismiss();
        }
        else{
            GroupItem newGroup = new GroupItem(newGroupName, "only me :) ");
            groupItems.add(newGroup);
        }
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
            GroupItem newCur = new GroupItem("heading" + (i), "chen, sapir, oren");
            groupItems.add(newCur);
        }
    }


    @Override
    public void onBackPressed()
    {
        if (back_pressed + 2000 > System.currentTimeMillis())
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }

}
