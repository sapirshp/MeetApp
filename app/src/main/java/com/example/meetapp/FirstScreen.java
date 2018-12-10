package com.example.meetapp;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FirstScreen extends AppCompatActivity {
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
        handleExitPopup();

        newGroupDialog.show();
    }

    private void handleExitPopup() {
        TextView exitPopupBtn;
        newGroupDialog.setContentView(R.layout.new_group_popup);
        exitPopupBtn = newGroupDialog.findViewById(R.id.exitNewGroupBtn2);
        exitPopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGroupDialog.dismiss();
            }
        });
    }

    private void createDemoCards() {
        for (int i=0; i<=10; i++)
        {
            GroupItem newCur = new GroupItem("heading" + (i), "chen, sapir, oren");
            groupItems.add(newCur);
        }
    }


}
