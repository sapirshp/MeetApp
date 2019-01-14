package com.example.meetapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>
{
    private ArrayList<Group> groups;
    private Activity activity;

    public GroupAdapter(ArrayList<Group> groups, Activity activity) {
        this.groups = groups;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View newViewHolder = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.group_item, viewGroup, false);
        return new ViewHolder(newViewHolder);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Group newGroup = groups.get(position);
        viewHolder.textViewGroupName.setText(newGroup.getName());
        viewHolder.textViewParticipants.setText(newGroup.getMembersString());

//        if(newGroup.getIsScheduled())
//        {
//            showScheduledTimeSymbols(viewHolder, position);
//        }

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToGroupScreen = new Intent(activity, InsideGroupActivity.class);
                goToGroupScreen.putExtra("groupId", newGroup.getGroupId());
                activity.startActivityForResult(goToGroupScreen, 1);
            }
        });
    }

    private void showScheduledTimeSymbols(@NonNull ViewHolder viewHolder, int position)
    {
        TextDrawable dayLetterRepr = daySymbolFactory(position % 7);    // OREN - CHANGE HERE
        viewHolder.dayScheduled.setImageDrawable(dayLetterRepr);
        int timeInDaySymbol = timeInDayFactory(position % 3);           // OREN - CHANGE HERE
        viewHolder.timeInDay.setImageResource(timeInDaySymbol);
    }

    public TextDrawable daySymbolFactory(int dayRepr)
    {
        switch (dayRepr){
            case 1:
                return TextDrawable.builder().buildRound("Sun", Color.BLUE);
            case 2:
                return TextDrawable.builder().buildRound("Mon", Color.RED);
            case 3:
                return TextDrawable.builder().buildRound("Tue", Color.DKGRAY);
            case 4:
                return TextDrawable.builder().buildRound("Wed", Color.BLUE);
            case 5:
                return TextDrawable.builder().buildRound("Thu", Color.LTGRAY);
            case 6:
                return TextDrawable.builder().buildRound("Fri", Color.GREEN);
            case 7:
                return TextDrawable.builder().buildRound("Sat", Color.MAGENTA);
            default:
                return TextDrawable.builder().buildRound("Sat", Color.MAGENTA);
        }
    }

    public int timeInDayFactory(int timeRepr) {
        switch (timeRepr) {
            case 1:
                return R.drawable.morning_icon;
            case 2:
                return R.drawable.afternoon_icon;
            case 3:
                return R.drawable.evening_icon;
            default:
                return R.drawable.evening_icon;
        }
    }


    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewGroupName;
        public TextView textViewParticipants;
        public LinearLayout linearLayout;
        public ImageView dayScheduled;
        public ImageView timeInDay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewGroupName = itemView.findViewById(R.id.textViewGroupName);
            textViewParticipants = itemView.findViewById(R.id.textViewParticipantsName);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            dayScheduled = itemView.findViewById(R.id.dayRepresentation);
            timeInDay = itemView.findViewById(R.id.timeInDay);
        }
    }
}
