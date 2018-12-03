package com.example.meetapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class FirstScreen extends AppCompatActivity {

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

        for (int i=0; i<=10; i++)
        {
            GroupItem newCur = new GroupItem("heading" + (i), "chen, sapir, oren");
            groupItems.add(newCur);
        }

        adapter = new MyAdapter(groupItems, this);

        recyclerView.setAdapter(adapter);

    }



}
