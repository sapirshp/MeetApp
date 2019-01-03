package com.example.meetapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class MenuHandler {

    private Dialog addMemberDialog;
    private Dialog groupDetailsDialog;
    private Dialog editGroupNameDialog;
    private ArrayList<String> membersToAdd = new ArrayList<>();
    private int membersAmount;
    private List<String> groupMembers;
    private ArrayList<TimeSlot> slotsToReset = new ArrayList<>();
    String members = "";


     MenuHandler(HashMap<String, Dialog> dialogs, List<String> membersNames){
        this.addMemberDialog = dialogs.get("addMemberDialog");
        this.groupDetailsDialog = dialogs.get("groupDetailsDialog");
        this.membersAmount = membersNames.size();
        this.groupMembers = membersNames;
        this.editGroupNameDialog = dialogs.get("editGroupNameDialog");
    }

    void handleAddParticipant(Runnable showContacts, Toolbar toolbar, CalendarSlotsHandler calendarSlotsHandler)
    {
        addMemberDialog.setContentView(R.layout.add_member_popup);
        TextView exitPopupBtn = addMemberDialog.findViewById(R.id.addMemberExitBtn);
        handleExitPopup(addMemberDialog, exitPopupBtn);
        showContacts.run();
        chooseMembers(toolbar, calendarSlotsHandler);
        addMemberDialog.show();
    }

    void handleGroupDetails(CalendarSlotsHandler calendarSlotsHandler, String groupName, Toolbar toolbar)
    {
        groupDetailsDialog.setContentView(R.layout.group_details_popap);
        TextView exitPopupBtn = groupDetailsDialog.findViewById(R.id.groupDetailsExitBtn);
        handleExitPopup(groupDetailsDialog, exitPopupBtn);
        displayGroupName(groupName);
        displayMembersInfo(calendarSlotsHandler.getContext());
        displayTopSelection(calendarSlotsHandler);
        handleEditGroupName(toolbar);
        groupDetailsDialog.show();
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


     private void handleExitPopup(final Dialog dialog, TextView exitBtn)
    {
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                membersToAdd.clear();
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

    void setMembersToAdd(ArrayList<String> newMembers){
        membersToAdd = newMembers;
    }

    void setButtonDisabled() {
        ImageButton okButton = addMemberDialog.findViewById(R.id.chooseMembers);
        okButton.setBackgroundResource(R.drawable.disabled_button_background);
    }

    void setButtonEnabled() {
        ImageButton okButton = addMemberDialog.findViewById(R.id.chooseMembers);
        okButton.setBackgroundResource(R.drawable.green_round_background);
    }

    String chooseMembers(final Toolbar toolbar, final CalendarSlotsHandler calendarSlotsHandler){
        ImageButton okBtn = addMemberDialog.findViewById(R.id.chooseMembers);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (String memberName : membersToAdd) {
                    addMemberDialog.dismiss();
                    groupMembers.add(memberName);
                }
                membersAmount += membersToAdd.size();
                calendarSlotsHandler.setMembersAmount(membersAmount);
                membersToAdd.clear();
                members = groupMembers.toString().substring(1,groupMembers.toString().length()-1);
                toolbar.setSubtitle(members);
                for (TimeSlot timeSlot : calendarSlotsHandler.getSlotSelections().keySet())
                calendarSlotsHandler.clickedOn(timeSlot, true);
            }
        });
        return members;
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
         String topSelectionText = calendarSlotsHandler.displayTopSelections();
         topSelectionInfo.setText(topSelectionText);
    }

    private void displayGroupName(String groupName) {
        TextView groupNameInfo = groupDetailsDialog.findViewById(R.id.groupName);
        groupNameInfo.setText(groupName);
    }
}
