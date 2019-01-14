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

        if(newGroup.getIsScheduled())
        {
            showScheduledTimeSymbols(viewHolder, newGroup.getChosenDate());
        }

        viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToGroupScreen = new Intent(activity, InsideGroupActivity.class);
                goToGroupScreen.putExtra("groupId", newGroup.getGroupId());
                activity.startActivityForResult(goToGroupScreen, 1);
            }
        });
    }

    private void showScheduledTimeSymbols(@NonNull ViewHolder viewHolder, String chosenDate)
    {
        String[] chosenDayAndTime = chosenDate.split(" ");
        TextDrawable dayLetterRepr = daySymbolFactory(chosenDayAndTime[0]);    // OREN - CHANGE HERE
        viewHolder.dayScheduled.setImageDrawable(dayLetterRepr);
        int timeInDaySymbol = timeInDayFactory(chosenDayAndTime[1]);           // OREN - CHANGE HERE
        viewHolder.timeInDay.setImageResource(timeInDaySymbol);
    }

    public TextDrawable daySymbolFactory(String dayRepr)
    {
        switch (dayRepr){
            case "Sun":
                return TextDrawable.builder().buildRound("Sun", Color.BLUE);
            case "Mon":
                return TextDrawable.builder().buildRound("Mon", Color.RED);
            case "Tue":
                return TextDrawable.builder().buildRound("Tue", Color.DKGRAY);
            case "Wed":
                return TextDrawable.builder().buildRound("Wed", Color.BLUE);
            case "Thu":
                return TextDrawable.builder().buildRound("Thu", Color.LTGRAY);
            case "Fri":
                return TextDrawable.builder().buildRound("Fri", Color.GREEN);
            default:
                return TextDrawable.builder().buildRound("Sat", Color.MAGENTA);
        }
    }

    public int timeInDayFactory(String timeRepr) {
        switch (timeRepr) {
            case "Morning":
                return R.drawable.morning_icon;
            case "Afternoon":
                return R.drawable.afternoon_icon;
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
