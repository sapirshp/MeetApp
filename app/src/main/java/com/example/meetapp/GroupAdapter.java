package com.example.meetapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class GroupAdapter extends FirestoreRecyclerAdapter<Group, GroupAdapter.GroupHolder>
{
    private Activity activity;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String userId = "";

    public GroupAdapter(@NonNull FirestoreRecyclerOptions<Group> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    public class GroupHolder extends RecyclerView.ViewHolder
    {
        public TextView textViewGroupName;
        public TextView textViewParticipants;
        public LinearLayout linearLayout;
        public ImageView dayScheduled;
        public ImageView timeInDay;

        public GroupHolder(@NonNull View itemView) {
            super(itemView);
            textViewGroupName = itemView.findViewById(R.id.textViewGroupName);
            textViewParticipants = itemView.findViewById(R.id.textViewParticipantsName);
            linearLayout = itemView.findViewById(R.id.groupCard);
            dayScheduled = itemView.findViewById(R.id.dayRepresentation);
            timeInDay = itemView.findViewById(R.id.timeInDay);
        }
    }

    @NonNull
    @Override
    public GroupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        View newViewHolder = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.group_item, viewGroup, false);
        return new GroupHolder(newViewHolder);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupHolder groupHolder, int position, @NonNull final Group group) {
        groupHolder.textViewGroupName.setText(group.getName());
        readMembersNames(groupHolder, group);
    }

    protected void readMembersNames(@NonNull final GroupHolder groupHolder, @NonNull final Group group) {
        CollectionReference usersRef = db.collection("users");
        Query groupUsers = usersRef.whereArrayContains("memberOf", group.getGroupId());
        groupUsers.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                group.resetNamesList();
                for (QueryDocumentSnapshot doc : value) {
                    if (doc.get("name") != null) {
                        group.addNameToNamesList(doc.getString("name"));
                    }
                }
                setGroupsContent(groupHolder, group);
            }
        });
    }

    private void setGroupsContent(@NonNull GroupHolder groupHolder, @NonNull final Group group)
    {
        groupHolder.textViewParticipants.setText(group.getMembersString());
        if(group.getIsScheduled())
        {
            showScheduledTimeSymbols(groupHolder, group.getChosenDate());
        }
        groupHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToGroupScreen = new Intent(activity, InsideGroupActivity.class);
                goToGroupScreen.putExtra("groupId", group.getGroupId());
                goToGroupScreen.putExtra("userId", userId);
                activity.startActivityForResult(goToGroupScreen, 1);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    private void showScheduledTimeSymbols(@NonNull GroupHolder groupHolder, String chosenDate)
    {
        String[] chosenDayAndTime = chosenDate.split(" ");
        TextDrawable dayLetterRepr = daySymbolFactory(chosenDayAndTime[0]);    // OREN - CHANGE HERE
        groupHolder.dayScheduled.setImageDrawable(dayLetterRepr);
        int timeInDaySymbol = timeInDayFactory(chosenDayAndTime[1]);           // OREN - CHANGE HERE
        groupHolder.timeInDay.setImageResource(timeInDaySymbol);
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

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
