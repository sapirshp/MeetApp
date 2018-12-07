package com.example.meetapp;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ScrollView;
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

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
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
        TextView Xbtn;
        newGroupDialog.setContentView(R.layout.new_group_popup);
        Xbtn = (TextView) findViewById(R.id.exitNewGroupBtn);
//        Xbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                newGroupDialog.dismiss();
//            }
//        });
//        newGroupDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        newGroupDialog.show();
    }

    private void createDemoCards() {
        for (int i=0; i<=10; i++)
        {
            GroupItem newCur = new GroupItem("heading" + (i), "chen, sapir, oren");
            groupItems.add(newCur);
        }
    }


}
