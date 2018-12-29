package com.example.meetapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Function;

public class MenuHandler {

    private RecyclerView contactRecyclerView;
    private Dialog addMemberDialog;
    private ArrayList<String> membersToAdd;
    private int membersAmount;
    private String groupMembers;


    MenuHandler(Dialog dialog, int memberNum, String membersNames){
        this.addMemberDialog = dialog;
        membersAmount = memberNum;
        this.groupMembers = membersNames;
    }

    void handleAddParticipant(Runnable showContacts, Toolbar toolbar, CalendarSlotsHandler calendarSlotsHandler)
    {
        addMemberDialog.setContentView(R.layout.add_member_popup);
        handleExitPopup();
        showContacts.run();
        chooseMembers(toolbar, calendarSlotsHandler);
        addMemberDialog.show();
    }

    void handleGroupDetails(Context context)
    {
        Toast.makeText(context," 'Group Details' Button Pressed ", Toast.LENGTH_LONG).show();
    }


    void handleResetTimeChoice(Consumer<TimeSlot> resetFunction, Set<TimeSlot> slotSelection)
    {
        ArrayList<TimeSlot> slotsToReset = new ArrayList<>();
        for (TimeSlot selectedSlot : slotSelection) {
            slotsToReset.add(selectedSlot);
        }
        for (TimeSlot slotToReset : slotsToReset){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                resetFunction.accept(slotToReset);
            }
        }
    }

    void handleExitGroup(Context context)
    {
        Toast.makeText(context, " 'Exit Group' Button Pressed ", Toast.LENGTH_LONG).show();
    }

    private void handleExitPopup()
    {
        TextView exitPopupBtn;
        exitPopupBtn = addMemberDialog.findViewById(R.id.addMemberExitBtn);
        exitPopupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMemberDialog.dismiss();
                membersToAdd.clear();
            }
        });
    }

    void setMembersToAdd(ArrayList<String> newMembers){
        membersToAdd = newMembers;
    }

    void setButtonDisabled() {
        Button okButton = addMemberDialog.findViewById(R.id.chooseMembers);
        okButton.setBackgroundResource(R.drawable.disabled_button_background);
    }

    void setButtonEnabled() {
        Button okButton = addMemberDialog.findViewById(R.id.chooseMembers);
        okButton.setBackgroundResource(R.drawable.green_round_background);
    }

    String chooseMembers(final Toolbar toolbar, final CalendarSlotsHandler calendarSlotsHandler){
        Button okBtn = addMemberDialog.findViewById(R.id.chooseMembers);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String memberName : membersToAdd) {
                    addMemberDialog.dismiss();
                    groupMembers += ", " + memberName;
                }
                membersAmount += membersToAdd.size();
                calendarSlotsHandler.setMembersAmount(membersAmount);
                membersToAdd.clear();
                toolbar.setSubtitle(groupMembers);
                for (TimeSlot timeSlot : calendarSlotsHandler.getSlotSelections().keySet())
                calendarSlotsHandler.clickedOn(timeSlot, true);
            }
        });
        return groupMembers;
    }
}
