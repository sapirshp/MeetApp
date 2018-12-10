package com.example.meetapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>
{
    public MyAdapter(List<GroupItem> groupItem, Context context) {
        this.groupItem = groupItem;
        this.context = context;
    }

    private List<GroupItem> groupItem;
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
        final GroupItem newGroup = groupItem.get(position);

        viewHolder.textViewGroupName.setText(newGroup.getGroupName());
        viewHolder.textViewParticipants.setText(newGroup.getParticipants());

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "pressed "+ newGroup.getGroupName(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return groupItem.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewGroupName;
        public TextView textViewParticipants;
        public LinearLayout linearLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewGroupName = (TextView) itemView.findViewById(R.id.textViewGroupName);
            textViewParticipants = (TextView) itemView.findViewById(R.id.textViewParticipantsName);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
        }
    }
}
