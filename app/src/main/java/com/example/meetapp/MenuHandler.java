package com.example.meetapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.undobar.UndoBarController;
import java.util.ArrayList;

 class MenuHandler {

    private Dialog addMemberDialog;
    private ArrayList<String> membersToAdd = new ArrayList<>();
    private int membersAmount;
    private String groupMembers;
    ArrayList<TimeSlot> slotsToReset = new ArrayList<>();


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


    void handleResetTimeChoice(Activity activity, final CalendarSlotsHandler calendarSlotsHandler) {
        slotsToReset.clear();
        if (calendarSlotsHandler.getSlotSelections().isEmpty()){
            Toast.makeText(activity,activity.getString(R.string.resetEmptySelection), Toast.LENGTH_LONG).show();
        }else {
            for (TimeSlot selectedSlot : calendarSlotsHandler.getSlotSelections().keySet()) {
                slotsToReset.add(selectedSlot);
                System.out.println(selectedSlot.getDate());
            }
            for (TimeSlot slotToReset : slotsToReset) {
                calendarSlotsHandler.clickedOff(slotToReset);
            }
            new UndoBarController.UndoBar(activity).message("Reset Selection").style(UndoBarController.UNDOSTYLE).listener(new UndoBarController.AdvancedUndoListener() {
                @Override
                public void onHide(@Nullable Parcelable token) {}

                @Override
                public void onClear(@NonNull Parcelable[] token) {}

                @Override
                public void onUndo(@Nullable Parcelable token) {
                    for (TimeSlot slot : slotsToReset) {
                        calendarSlotsHandler.clickedOn(slot, false);
                    }
                }
            }).show();
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
