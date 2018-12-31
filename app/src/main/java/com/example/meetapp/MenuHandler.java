package com.example.meetapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.cocosw.undobar.UndoBarController;
import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;

import java.util.ArrayList;

 class MenuHandler {

    private Dialog addMemberDialog;
    private ArrayList<String> membersToAdd = new ArrayList<>();
    private int membersAmount;
    private String groupMembers;
    private ArrayList<TimeSlot> slotsToReset = new ArrayList<>();


     MenuHandler(Dialog dialog, int memberNum, String membersNames){
        this.addMemberDialog = dialog;
        this.membersAmount = memberNum;
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

            SuperActivityToast.OnButtonClickListener onButtonClickListener =
                    new SuperActivityToast.OnButtonClickListener() {

                        @Override
                        public void onClick(View view, Parcelable token) {
                            for (TimeSlot slotToReset : slotsToReset) {
                                calendarSlotsHandler.clickedOn(slotToReset, false);
                            }
                        }
                    };
            SuperActivityToast.create(activity, new Style(), Style.TYPE_BUTTON)
                    .setButtonText("UNDO")
                    .setOnButtonClickListener("undo_bar", null, onButtonClickListener)
                    .setText("Your Selection Has Been Reset")
                    .setDuration(Style.DURATION_LONG)
                    .setColor(activity.getResources().getColor(R.color.undoDark))
                    .setAnimations(Style.ANIMATIONS_POP).show();
        }
    }

    void handleExitGroup(final Context context)
    {
        AlertDialog exitGroupDialog = new AlertDialog.Builder(context).create();
        exitGroupDialog.setTitle(context.getString(R.string.leaveGroupTitle));
        exitGroupDialog.setMessage(context.getString(R.string.leaveGroupQuestion));
        handlePositiveExitAnswer(context, exitGroupDialog);
        handleNegativeExitAnswer(context, exitGroupDialog);
        exitGroupDialog.show();
        handleButtonsLayoutAndColor(context, exitGroupDialog);
    }

     private void handleButtonsLayoutAndColor(Context context, AlertDialog alertDialog) {
         Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
         Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
         btnNegative.setTextColor(context.getResources().getColor(R.color.colorGreen));
         btnPositive.setTextColor(context.getResources().getColor(R.color.colorRed));

         LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                 btnPositive.getLayoutParams();
         layoutParams.weight = 10;
         btnPositive.setLayoutParams(layoutParams);
         btnNegative.setLayoutParams(layoutParams);

     }

     private void handlePositiveExitAnswer(final Context context, AlertDialog alertDialog) {

         alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.leaveAnswer),
                 new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                         // TODO: erase group from user's groups - implement here.
                         // for now:
                         Toast.makeText(context, " 'Exit Group' Button Pressed ",
                                 Toast.LENGTH_LONG).show();
                     }
                 });
     }

     private void handleNegativeExitAnswer(final Context context,AlertDialog alertDialog) {
         alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, context.getString(R.string.dontLeaveAnswer),
                 new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                     }
                 });
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
