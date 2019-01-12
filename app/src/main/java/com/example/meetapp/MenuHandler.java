package com.example.meetapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class MenuHandler {
    private final int LEAVE_GROUP_RESULT_CODE = 1;
    private final int NO_OPTION_CHOSEN = -1;
    private int currentMeetingChoice = NO_OPTION_CHOSEN;
    private Dialog topSuggestionsDialog;
    private Dialog addMemberDialog;
    private Dialog groupDetailsDialog;
    private Dialog editGroupNameDialog;
    private int membersAmount;
    private List<String> groupMembers;
    private ArrayList<TimeSlot> slotsToReset = new ArrayList<>();
    private String members = "";
    private final int NO_SLOTS_CHOSEN = 0;
    private Activity activity;


     MenuHandler(HashMap<String, Dialog> dialogs, List<String> membersNames, Activity activity){
        this.addMemberDialog = dialogs.get("addMemberDialog");
        this.groupDetailsDialog = dialogs.get("groupDetailsDialog");
        this.membersAmount = membersNames.size();
        this.groupMembers = membersNames;
        this.topSuggestionsDialog = dialogs.get("topSuggestionsDialog");
        this.editGroupNameDialog = dialogs.get("editGroupNameDialog");
        this.activity = activity;
    }

    void handleAddParticipant(final Toolbar toolbar, final CalendarSlotsHandler calendarSlotsHandler)
    {
        addMemberDialog.setContentView(R.layout.add_member_popup);
        TextView exitPopupBtn = addMemberDialog.findViewById(R.id.addMemberExitBtn);
        handleExitPopup(addMemberDialog, exitPopupBtn);
        ContactsGetter.showContacts(activity, addMemberDialog);
        AddMembersHandler.chooseMembers(new Runnable() {
            @Override
            public void run() {
                onChooseMembers(toolbar, calendarSlotsHandler);
            }
        });
        addMemberDialog.show();
    }

    boolean handleGroupDetails(CalendarSlotsHandler calendarSlotsHandler, String groupName, Toolbar toolbar)
    {
        groupDetailsDialog.setContentView(R.layout.group_details_popap);
        TextView exitPopupBtn = groupDetailsDialog.findViewById(R.id.groupDetailsExitBtn);
        handleExitPopup(groupDetailsDialog, exitPopupBtn);
        displayGroupName(groupName);
        displayMembersInfo(calendarSlotsHandler.getContext());
        displayTopSelection(calendarSlotsHandler);
        handleEditGroupName(toolbar);
        String newName = toolbar.getTitle().toString();
        groupDetailsDialog.show();
        return (newName.equals(groupName));
    }


    void handleResetTimeChoice(final CalendarSlotsHandler calendarSlotsHandler) {
        slotsToReset.clear();
        if (calendarSlotsHandler.getUserClicks().isEmpty()){
            Toast.makeText(activity,activity.getString(R.string.resetEmptySelection), Toast.LENGTH_LONG).show();
        }else {
            slotsToReset.addAll(calendarSlotsHandler.getUserClicks());
            for (TimeSlot slotToReset : slotsToReset) {
                calendarSlotsHandler.clickedOff(slotToReset);
            }
            setUndoBar(calendarSlotsHandler);
        }
    }

    private void setUndoBar(final CalendarSlotsHandler calendarSlotsHandler){
        SuperActivityToast.OnButtonClickListener onButtonClickListener =
                new SuperActivityToast.OnButtonClickListener() {

                    @Override
                    public void onClick(View view, Parcelable token) {
                        for (TimeSlot slotToReset : slotsToReset) {
                            if (!slotToReset.getClicked()) {
                                calendarSlotsHandler.clickedOn(slotToReset, false);
                            }
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

    void handleExitGroup(final Context context, String groupId)
    {
        AlertDialog exitGroupDialog = new AlertDialog.Builder(context).create();
        exitGroupDialog.setTitle(context.getString(R.string.leaveGroupTitle));
        exitGroupDialog.setMessage(context.getString(R.string.leaveGroupQuestion));
        handlePositiveExitAnswer(context, exitGroupDialog, groupId);
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

     private void handlePositiveExitAnswer(final Context context, AlertDialog alertDialog, final String groupId) {

         alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.leaveAnswer),
                 new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int which) {
                         dialog.dismiss();
                         Intent goToGroupsDisplay = new Intent();
                         goToGroupsDisplay.putExtra("groupId", groupId);
                         activity.setResult(LEAVE_GROUP_RESULT_CODE, goToGroupsDisplay);
                         activity.finish();
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


     private void handleExitPopup(final Dialog dialog, TextView exitBtn)
    {
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (dialog == addMemberDialog) {
                    AddMembersHandler.clearMembersToAdd();
                }
            }
        });
    }

    private void handleEditGroupName(final Toolbar toolbar){
         final Button editGroupNameBtn = groupDetailsDialog.findViewById(R.id.editGroupName);
         editGroupNameBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 editGroupNameDialog.setContentView(R.layout.edit_group_name_popup);
                 TextView exitBtn = editGroupNameDialog.findViewById(R.id.exitEditNameBtn);
                 handleExitPopup(editGroupNameDialog, exitBtn);
                 handleEditInput(toolbar);
                 editGroupNameDialog.show();
             }
         });
    }

    private void handleEditInput(final Toolbar toolbar){
         final Button changeNameBtn = editGroupNameDialog.findViewById(R.id.changeNameBtn);
         EditText userInput = editGroupNameDialog.findViewById(R.id.editGroupNameInput);
         userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    changeNameBtn.setBackgroundResource(R.drawable.disabled_button_background);
                }else {
                    changeNameBtn.setBackgroundResource(R.drawable.green_round_background);
                    changeNameBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handleChangeNameRequest(toolbar);
                        }
                    });
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleChangeNameRequest(Toolbar toolbar){
        EditText userInput = editGroupNameDialog.findViewById(R.id.editGroupNameInput);
        String newGroupName = userInput.getText().toString();
        toolbar.setTitle(newGroupName);
        editGroupNameDialog.dismiss();
        displayGroupName(newGroupName);
    }

    private void onChooseMembers(Toolbar toolbar, CalendarSlotsHandler calendarSlotsHandler){
        groupMembers.addAll(AddMembersHandler.getMembersToAdd());
        membersAmount += AddMembersHandler.getMembersToAdd().size();
        calendarSlotsHandler.setMembersAmount(membersAmount);
        members = groupMembers.toString().substring(1,groupMembers.toString().length()-1);
        toolbar.setSubtitle(members);
        for (TimeSlot timeSlot : calendarSlotsHandler.getSlotSelections().keySet())
            calendarSlotsHandler.clickedOn(timeSlot, true);
    }

    private void displayMembersInfo(Context context) {
        createTextView("Members:", 20, context);
        for (String member : groupMembers) {
            createTextView(member, 18, context);
        }
    }

    private void createTextView(String writeIn, int textSize, Context context){
        LinearLayout membersInfo = groupDetailsDialog.findViewById(R.id.membersList);
        TextView newTextView = new TextView(groupDetailsDialog.getContext());
        newTextView.setText(writeIn);
        newTextView.setTextColor(Color.BLACK);
        newTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        newTextView.setTextSize(textSize);
        Typeface fontTypeface = ResourcesCompat.getFont(context, R.font.poppins_light);
        newTextView.setTypeface(fontTypeface);
        membersInfo.addView(newTextView);
    }

    private void displayTopSelection (CalendarSlotsHandler calendarSlotsHandler){
         TextView topSelectionInfo = groupDetailsDialog.findViewById(R.id.topSelections);
         ArrayList<String> topSelections = calendarSlotsHandler.displayTopSelections();
         String topSelectionText = "Suggestions:";
         for (String suggestion: topSelections){
             topSelectionText = String.format("%s\n%s", topSelectionText, suggestion);
         }
         topSelectionInfo.setText(topSelectionText);
    }

    private void displayGroupName(String groupName) {
        TextView groupNameInfo = groupDetailsDialog.findViewById(R.id.groupName);
        groupNameInfo.setText(groupName);
    }


    void handleCreateMeeting(final CalendarSlotsHandler calendarSlotsHandler)
    {
        ArrayList<String> stringTopSuggestionsArr = calendarSlotsHandler.displayTopSelections();
        int numOfOptionsToDisplay = stringTopSuggestionsArr.size();
        if(numOfOptionsToDisplay == NO_SLOTS_CHOSEN) {
            Toast.makeText(activity, R.string.noTimeSlotsSelected, Toast.LENGTH_SHORT).show();
            return;
        }
        topSuggestionsDialog.setContentView(R.layout.top_suggestions_popup);
        TextView exitBtn = topSuggestionsDialog.findViewById(R.id.exitPopupBtn);
        handleExitPopup(topSuggestionsDialog, exitBtn);
        Button[] allOptionsLst = createAllOptionsLst();
        final ArrayList<Button> currentOptionLst = activateOnlyRelevantButtons(allOptionsLst,
                numOfOptionsToDisplay);
        setTextForOptions(currentOptionLst, calendarSlotsHandler);
        handleAllOptionPresses(currentOptionLst, allOptionsLst);
        handleCreateMeetingBtnPress(currentOptionLst.get(currentOptionLst.size()-1));
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


    private void setTextForOptions(ArrayList<Button> buttonsSuggestion, CalendarSlotsHandler calendarSlotsHandler)
    {
        ArrayList<String> topSelections = calendarSlotsHandler.displayTopSelections();
        for (int i = 0; i< buttonsSuggestion.size()-1; i++){
            buttonsSuggestion.get(i).setText(topSelections.get(i));
        }
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
         Button setMeetingBtn = btnList.get(btnList.size()-1);
         if(currentMeetingChoice != NO_OPTION_CHOSEN) {
             btnList.get(currentMeetingChoice).setBackgroundResource(R.drawable.custom_border);
         }
         btnList.get(buttonToTurnOn).setBackgroundResource(R.drawable.green_regular_background);
         setMeetingBtn.setBackgroundResource(R.drawable.green_round_background);
         currentMeetingChoice = buttonToTurnOn;
     }


     private void handleCreateMeetingBtnPress(android.widget.Button createMeeting)
     {
         createMeeting.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(currentMeetingChoice != NO_OPTION_CHOSEN) {
//                    cretaeMeetup();       //TODO: MEETUP IMPLEMENTATION HERE
                     int currentRealChoice = currentMeetingChoice + 1;
                     Toast.makeText(activity, "You Chose Option" + currentRealChoice,
                             Toast.LENGTH_SHORT).show();
                 }
             }
         });
     }
}