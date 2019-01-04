package com.example.meetapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;

import java.util.ArrayList;


 class MenuHandler
 {
     private final int NO_OPTION_CHOSEN = -1;
     private int currentMeetingChoice = NO_OPTION_CHOSEN;
     private Dialog topSuggestionsDialog;
    private Dialog addMemberDialog;
    private ArrayList<String> membersToAdd = new ArrayList<>();
    private int membersAmount;
    private String groupMembers;
    private ArrayList<TimeSlot> slotsToReset = new ArrayList<>();
     private final int NO_SLOTS_CHOSEN = 0;



     MenuHandler(Dialog addMemberDialog, int memberNum, String membersNames, Dialog topSuggestionsDialog){
        this.addMemberDialog = addMemberDialog;
        this.membersAmount = memberNum;
        this.groupMembers = membersNames;
        this.topSuggestionsDialog = topSuggestionsDialog;
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


    void handleCreateMeeting(final Activity activity, final CalendarSlotsHandler calendarSlotsHandler)
    {
        ArrayList<String> stringTopSuggestionsArr = calendarSlotsHandler.displayTopSelections();
        int numOfOptionsToDisplay = stringTopSuggestionsArr.size();
        if(numOfOptionsToDisplay == NO_SLOTS_CHOSEN) {
            Toast.makeText(activity, R.string.noTimeSlotsSelected, Toast.LENGTH_SHORT).show();
            return;
        }
        topSuggestionsDialog.setContentView(R.layout.top_suggestions_popup);
        handleExitBtnOfCreateMeetingPopup();
        Button[] allOptionsLst = createAllOptionsLst();
        final ArrayList<Button> currentOptionLst = activateOnlyRelevantButtons(allOptionsLst,
                numOfOptionsToDisplay);
        setTextForOptions(allOptionsLst);   // TODO: CHANGE TO currentOptionLst AFTER USING SAPIR'S FUNCTION
        handleAllOptionPresses(currentOptionLst, allOptionsLst);
        handleCreateMeetingBtnPress(activity, currentOptionLst.get(currentOptionLst.size()-1));
        topSuggestionsDialog.show();
    }


    private Button[] createAllOptionsLst()
    {
        final Button option1, option2, option3, createMeeting;
        option1 = topSuggestionsDialog.findViewById(R.id.option1);
        option2 = topSuggestionsDialog.findViewById(R.id.option2);
        option3 = topSuggestionsDialog.findViewById(R.id.option3);
        createMeeting = topSuggestionsDialog.findViewById(R.id.CreateMeetupBtn);
        return new Button[]{option1, option2, option3, createMeeting};
    }


    private ArrayList<Button> activateOnlyRelevantButtons(Button[] allOptionsLst, int numOfOptionsToDisplay)
    {
        final ArrayList<Button> currentOptionLst = new ArrayList<>();
        Button createMeeting = allOptionsLst[3];
        // build button list according to numOfOptionsToDisplay
        switch (numOfOptionsToDisplay){
            case 3:
                currentOptionLst.add(allOptionsLst[2]);
            case 2:
                currentOptionLst.add(0, allOptionsLst[1]);
            case 1:
                currentOptionLst.add(0, allOptionsLst[0]);
            default:
                currentOptionLst.add(createMeeting);
        }
        // show only the relevant buttons
        switch(numOfOptionsToDisplay)
        {
            case 1:
                allOptionsLst[1].setVisibility(View.GONE);
            case 2:
                allOptionsLst[2].setVisibility(View.GONE);
        }
        return currentOptionLst;
    }


    private void setTextForOptions(Button[] AllOptionsLst)
    {
        // TODO: GET REAL CHOICES FROM SAPIR'S FUNCTION
        AllOptionsLst[0].setText("#1:" + "Monday Morning");
        AllOptionsLst[1].setText("#2:" + "Sunday Afternoon");
        AllOptionsLst[2].setText("#3:" + "Saturday Evening");
    }


    private void handleAllOptionPresses(final ArrayList<Button> currentOptionLst, Button[] allOptionsLst)
    {
        for(int i = 0; i < 3; i++)
        {
            final int curOptionNum = i;
            allOptionsLst[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleOptionPress(curOptionNum, currentOptionLst);
                }
            });
        }
    }


     private void handleExitBtnOfCreateMeetingPopup() {
         TextView exitPopupBtn;
         exitPopupBtn = topSuggestionsDialog.findViewById(R.id.exitPopupBtn);
         exitPopupBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 topSuggestionsDialog.dismiss();
             }
         });
     }


     private void handleOptionPress(int newButtonPressed, ArrayList<Button> btnList)
     {
         if(newButtonPressed == currentMeetingChoice)
         {
             turnOffCurrentBtn(newButtonPressed, btnList);
         }
         else{
             setSingleChoiceAndDisableOthers(newButtonPressed, btnList);
         }
     }


     private void turnOffCurrentBtn(int newButtonPressed, ArrayList<Button> btnList)
     {
         btnList.get(newButtonPressed).setBackgroundResource(R.drawable.custom_border);
         btnList.get(btnList.size()-1).setBackgroundResource(R.drawable.disabled_button_background);
         currentMeetingChoice = NO_OPTION_CHOSEN;
     }


     private void setSingleChoiceAndDisableOthers(int buttonToTurnOn, ArrayList<Button> btnList)
     {
         if(currentMeetingChoice != NO_OPTION_CHOSEN) {
             btnList.get(currentMeetingChoice).setBackgroundResource(R.drawable.custom_border);
         }
         btnList.get(buttonToTurnOn).setBackgroundResource(R.drawable.green_regular_background);

         btnList.get(btnList.size()-1).setBackgroundResource(R.drawable.green_round_background);
         currentMeetingChoice = buttonToTurnOn;

     }


     private void handleCreateMeetingBtnPress(final Activity activity, android.widget.Button createMeeting)
     {
         createMeeting.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(currentMeetingChoice != NO_OPTION_CHOSEN)
                 {
//                    cretaeMeetup();       //TODO: MEETUP IMPLEMENTATION HERE
                     int currentRealChoice = currentMeetingChoice + 1;
                     Toast.makeText(activity, "You Chose Option" + currentRealChoice, Toast.LENGTH_SHORT).show();

                 }
             }
         });
     }


}
