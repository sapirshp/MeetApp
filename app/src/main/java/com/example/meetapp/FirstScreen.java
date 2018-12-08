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
    private List<Group> groups;
    private String userName = "Chen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groups = new ArrayList<>();

        for (int i=0; i<=5; i++)
        {
            Group newCur = new Group("a" + i, userName, String.valueOf(i), "Chen, Sapir, Oren");
            groups.add(newCur);
        }

        adapter = new MyAdapter(groups, this);

        recyclerView.setAdapter(adapter);

    }



}
