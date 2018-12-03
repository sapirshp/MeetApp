package com.example.meetapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{

    public MyAdapter(List<GroupItem> groupItem, Context context) {
        groupItems = groupItem;
        this.context = context;
    }

    private List<GroupItem> groupItems;
    private Context context;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View newViewHolder = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.group_item, viewGroup, false);
        return new ViewHolder(newViewHolder);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        GroupItem newGroup = groupItems.get(position);

        viewHolder.textViewGroupName.setText(newGroup.getHead());
        viewHolder.textViewParticipants.setText(newGroup.getParticipants());

    }


    @Override
    public int getItemCount() {
        return groupItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewGroupName;
        public TextView textViewParticipants;




        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewGroupName = (TextView) itemView.findViewById(R.id.textViewGroupName);
            textViewParticipants = (TextView) itemView.findViewById(R.id.textViewParticipantsName);
        }
    }


}

