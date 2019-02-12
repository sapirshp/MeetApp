package com.example.meetapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MenuHandler {
    private final int LEAVE_GROUP_RESULT_CODE = 1;
    private final int NO_OPTION_CHOSEN = -1;
    private int currentMeetingChoice = NO_OPTION_CHOSEN;
    private Dialog topSuggestionsDialog;
    private Dialog addMemberDialog;
    private Dialog groupDetailsDialog;
    private Dialog editGroupNameDialog;
    private Dialog meetingChosenDialog;
    private int membersAmount;
    private List<String> groupMembers;
    private List<String> membersIdsToAdd;
    private ArrayList<TimeSlot> slotsToReset = new ArrayList<>();
    private String members = "";
    private final int NO_SLOTS_CHOSEN = 0;
    private Activity activity;
    private boolean isDateChosen;
    private String dateChosenByAdmin;
    private Group currentGroup;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference groupRef;
    private DocumentReference userRef;
    private DocumentReference calendarRef;
    private CollectionReference usersRef;
    private HashMap<String,Long> userCalendar;
    private HashMap<String,Long> allUsersCalendar;
    private DocumentReference calendarRefForUpdate;

    MenuHandler(HashMap<String, Dialog> dialogs, List<String> membersNames, Activity activity, Group currentGroup){
        this.addMemberDialog = dialogs.get("addMemberDialog");
        this.groupDetailsDialog = dialogs.get("groupDetailsDialog");
        this.membersAmount = membersNames.size();
        this.groupMembers = membersNames;
        this.topSuggestionsDialog = dialogs.get("topSuggestionsDialog");
        this.editGroupNameDialog = dialogs.get("editGroupNameDialog");
        this.meetingChosenDialog = dialogs.get("meetingChosenDialog");
        this.activity = activity;
        this.isDateChosen = false;
        this.dateChosenByAdmin = "";
        this.currentGroup = currentGroup;
    }

    void handleAddParticipant(final String groupId)
    {
        addMemberDialog.setContentView(R.layout.add_member_popup);
        TextView exitPopupBtn = addMemberDialog.findViewById(R.id.addMemberExitBtn);
        handleExitPopup(addMemberDialog, exitPopupBtn);
        usersRef = db.collection("users");
        Query allUsers = usersRef.orderBy("name");
        Query groupUsers = usersRef.whereArrayContains("memberOf", groupId).orderBy("name");
        Task firstTask = allUsers.get();
        Task secondTask = groupUsers.get();
        Task combinedTask = Tasks.whenAllSuccess(firstTask, secondTask).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> list) {
                List<QuerySnapshot> queriesList = (List<QuerySnapshot>)(Object)list;
                UsersGetter.showUsers(activity, addMemberDialog, queriesList);
                finishAddParticipant(groupId);
            }
        });
    }

    private void finishAddParticipant(final String groupId) {
        AddMembersHandler.chooseMembers(new Runnable() {
            @Override
            public void run() {
                onChooseMembers(groupId);
            }
        });
        addMemberDialog.show();
    }

    boolean handleGroupDetails(CalendarSlotsHandler calendarSlotsHandler, String groupName, Toolbar toolbar, String groupId)
    {
        groupDetailsDialog.setContentView(R.layout.group_details_popap);
        TextView exitPopupBtn = groupDetailsDialog.findViewById(R.id.groupDetailsExitBtn);
        handleExitPopup(groupDetailsDialog, exitPopupBtn);
        displayGroupName(groupName);
        displayMembersInfo(calendarSlotsHandler.getContext());
        displayTopSelection(calendarSlotsHandler);
        handleEditGroupName(groupId);
        String newName = toolbar.getTitle().toString();
        groupDetailsDialog.show();
        return (newName.equals(groupName));
    }

    private void RemoveArrivals(final String groupId, HashMap<String,Long> userCalendar,
                                HashMap<String,Long>allUsersCalendar) {
        calendarRefForUpdate = db.collection("calendars").document(groupId);
        for (String slotIndex : userCalendar.keySet()) {
            if (userCalendar.get(slotIndex) == 1) {
                calendarRefForUpdate.update("all." + slotIndex,
                        allUsersCalendar.get(slotIndex) - 1);
            }
        }
    }

    private void findAndRemoveArrivals(final String groupId, final String userId) {
        calendarRef = db.collection("calendars").document(groupId);
        calendarRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    userCalendar = (HashMap<String,Long>) document.get(userId);
                    allUsersCalendar = (HashMap<String,Long>) document.get("all");
                    RemoveArrivals(groupId, userCalendar, allUsersCalendar);
                }
            }
        });
    }

    void handleResetTimeChoice(final CalendarSlotsHandler calendarSlotsHandler, final String groupId, final String userId) {
        findAndRemoveArrivals(groupId, userId);
//        slotsToReset.clear();
//        if (calendarSlotsHandler.getUserClicks().isEmpty()){
//            Toast.makeText(activity,activity.getString(R.string.resetEmptySelection), Toast.LENGTH_LONG).show();
//        }else {
//            slotsToReset.addAll(calendarSlotsHandler.getUserClicks());
//            for (TimeSlot slotToReset : slotsToReset) {
//                calendarSlotsHandler.clickedOff(slotToReset);
//            }
//            setUndoBar(calendarSlotsHandler);
//        }
        //TODO change it to update firebase
    }

    private void setUndoBar(final CalendarSlotsHandler calendarSlotsHandler){
        SuperActivityToast.OnButtonClickListener onButtonClickListener = undoOnClickListener(calendarSlotsHandler);
        SuperActivityToast.create(activity, new Style(), Style.TYPE_BUTTON)
                .setButtonText("UNDO")
                .setOnButtonClickListener("undo_bar", null, onButtonClickListener)
                .setText("Your Selection Has Been Reset")
                .setDuration(Style.DURATION_LONG)
                .setColor(activity.getResources().getColor(R.color.undoDark))
                .setAnimations(Style.ANIMATIONS_POP).show();
    }

    private SuperActivityToast.OnButtonClickListener undoOnClickListener(final CalendarSlotsHandler calendarSlotsHandler){
         return new SuperActivityToast.OnButtonClickListener() {
             @Override
             public void onClick(View view, Parcelable token) {
                 for (TimeSlot slotToReset : slotsToReset) {
                     if (!slotToReset.getClicked()) {
                         calendarSlotsHandler.clickedOn(slotToReset);
                         }
                 }
             }
         };
    }

    void handleExitGroup(final Context context, String groupId, String userId)
    {
        AlertDialog exitGroupDialog = new AlertDialog.Builder(context).create();
        exitGroupDialog.setTitle(context.getString(R.string.leaveGroupTitle));
        exitGroupDialog.setMessage(context.getString(R.string.leaveGroupQuestion));
        handlePositiveExitAnswer(context, exitGroupDialog, groupId, userId);
        handleNegativeExitAnswer(context, exitGroupDialog);
        exitGroupDialog.show();
        handleButtonsLayoutAndColor(context, exitGroupDialog);
    }

     private void handleButtonsLayoutAndColor(Context context, AlertDialog alertDialog) {
         Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
         Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
         btnNegative.setTextColor(context.getResources().getColor(R.color.colorGreen));
         btnPositive.setTextColor(context.getResources().getColor(R.color.colorRed));
         LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
         layoutParams.weight = 10;
         btnPositive.setLayoutParams(layoutParams);
         btnNegative.setLayoutParams(layoutParams);

     }

     private void activeGroupUpdate(final String groupId, final String userId, final String adminId,
                                    final ArrayList<String> membersList) {
         groupRef = db.collection("groups").document(groupId);
         groupRef.update("members", FieldValue.arrayRemove(userId));
         if (userId.equals(adminId)) {
             if (membersList.get(0).equals(adminId)) {
                 groupRef.update("admin", membersList.get(1));
             } else {
                 groupRef.update("admin", membersList.get(0));
             }
         }
         findAndRemoveArrivals(groupId, userId);
         Map<String,Object> userToRemove = new HashMap<>();
         userToRemove.put(userId, FieldValue.delete());
         calendarRef = db.collection("calendars").document(groupId);
         calendarRef.update(userToRemove);
     }

     private void checkGroupActivityAndUpdate(final String groupId, final String userId,
                                              DocumentSnapshot document) {
         ArrayList<String> membersList = (ArrayList<String>) document.get("members");
         if (membersList.size() == 1) {
             db.collection("calendars").document(groupId).delete();
             db.collection("groups").document(groupId).delete();
         } else {
             String adminId = (String) document.get("admin");
             activeGroupUpdate(groupId, userId, adminId, membersList);
         }
     }

     private void updateDBOfLeaving(final String groupId, final String userId) {
         userRef = db.collection("users").document(userId);
         userRef.update("memberOf", FieldValue.arrayRemove(groupId));
         groupRef = db.collection("groups").document(groupId);
         groupRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if (task.isSuccessful()) {
                     DocumentSnapshot document = task.getResult();
                     if (document.exists()) {
                         checkGroupActivityAndUpdate(groupId, userId, document);
                     }
                 }
             }
         });
     }

     private void handlePositiveExitAnswer(final Context context, AlertDialog alertDialog,
                                           final String groupId, final String userId) {
         alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(R.string.leaveAnswer),
             new DialogInterface.OnClickListener() {
                 public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                     updateDBOfLeaving(groupId, userId);
                     Intent goToGroupsDisplay = new Intent();
                     activity.setResult(LEAVE_GROUP_RESULT_CODE, goToGroupsDisplay);
                     activity.finish();
                     activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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


     private void handleExitPopup(final Dialog dialog, TextView exitBtn) {
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

    private void handleEditGroupName(final String groupId){
         final Button editGroupNameBtn = groupDetailsDialog.findViewById(R.id.editGroupName);
         editGroupNameBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 editGroupNameDialog.setContentView(R.layout.edit_group_name_popup);
                 TextView exitBtn = editGroupNameDialog.findViewById(R.id.exitEditNameBtn);
                 handleExitPopup(editGroupNameDialog, exitBtn);
                 handleEditInput(groupId);
                 editGroupNameDialog.show();
             }
         });
    }

    private void handleEditInput(final String groupId){
         final Button changeNameBtn = editGroupNameDialog.findViewById(R.id.changeNameBtn);
         EditText userInput = editGroupNameDialog.findViewById(R.id.editGroupNameInput);
        setTxtInChangeGroupPopup(userInput);
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
                            handleChangeNameRequest(groupId);
                        }
                    });
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setTxtInChangeGroupPopup(EditText userInput) {
        TextView groupNameInfo = groupDetailsDialog.findViewById(R.id.groupName);
        userInput.setText(groupNameInfo.getText(), TextView.BufferType.EDITABLE);
        userInput.setSelectAllOnFocus(true);
        editGroupNameDialog.getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void handleChangeNameRequest(String groupId){
        EditText userInput = editGroupNameDialog.findViewById(R.id.editGroupNameInput);
        String newGroupName = userInput.getText().toString();
        groupRef = db.collection("groups").document(groupId);
        groupRef.update("name", newGroupName);
        editGroupNameDialog.dismiss();
        displayGroupName(newGroupName);
    }

    private void onChooseMembers(final String groupId){
        groupRef = db.collection("groups").document(groupId);
        membersIdsToAdd = AddMembersHandler.getMembersIdsToAdd();
        for (String memberId : membersIdsToAdd) {
            groupRef.update("members", FieldValue.arrayUnion(memberId));
            addGroupToUser(memberId, groupId);
            addUserCalendar(memberId, groupId);
        }
    }

    private void addGroupToUser(final String memberId, final String groupId) {
        userRef = db.collection("users").document(memberId);
        userRef.update("memberOf", FieldValue.arrayUnion(groupId));
    }

    private void addUserCalendar(final String memberId, final String groupId) {
        calendarRef = db.collection("calendars").document(groupId);
        Map<String, Integer> initialMap = new HashMap<>();
        for (int i = 0; i < GroupsDisplayFeaturesHandler.TIME_SLOTS_AMOUNT; i++) {
            initialMap.put(Integer.toString(i), 0);
        }
        calendarRef.update(memberId, initialMap);
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
         String topSelectionText = "";
         ArrayList<String> topSelections = calendarSlotsHandler.displayTopSelections();
         if (!currentGroup.getIsScheduled()) {
             topSelectionText = displaySuggestions(topSelections);
         }else {
             topSelectionText = String.format("Next MeetUp:\n%s%s", topSelectionText, currentGroup.getChosenDate());
             topSelectionInfo.setTextSize(30);
         }
        topSelectionInfo.setText(topSelectionText);
    }

    private String displaySuggestions(ArrayList<String> topSelections){
        String topSelectionText = "Suggestions:";
        for (String suggestion : topSelections) {
            topSelectionText = String.format("%s\n%s", topSelectionText, suggestion);
        }
        return topSelectionText;
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
        if (!isDateChosen) {
            displayTopSuggestionsDialog(numOfOptionsToDisplay, calendarSlotsHandler);
            topSuggestionsDialog.show();
        }else {
            createMeetUp();
        }
    }

    private void displayTopSuggestionsDialog(int numOfOptionsToDisplay, CalendarSlotsHandler calendarSlotsHandler){
        topSuggestionsDialog.setContentView(R.layout.top_suggestions_popup);
        TextView exitBtn = topSuggestionsDialog.findViewById(R.id.exitPopupBtn);
        handleExitPopup(topSuggestionsDialog, exitBtn);
        Button[] allOptionsLst = createAllOptionsLst();
        final ArrayList<Button> currentOptionLst = activateOnlyRelevantButtons(allOptionsLst, numOfOptionsToDisplay);
        setTextForOptions(currentOptionLst, calendarSlotsHandler);
        handleAllOptionPresses(currentOptionLst, allOptionsLst);
        handleCreateMeetingBtnPress(currentOptionLst);
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
        buildSuggestionsButtonsList(numOfOptionsToDisplay, currentOptionLst, allOptionsLst);
        showRelevantSuggestionsButtons(numOfOptionsToDisplay, allOptionsLst);
        return currentOptionLst;
    }

    private void buildSuggestionsButtonsList(int numOfOptionsToDisplay, ArrayList<Button> currentOptionLst, Button[] allOptionsLst){
        Button createMeeting = allOptionsLst[3];
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
    }

    private void showRelevantSuggestionsButtons(int numOfOptionsToDisplay, Button[] allOptionsLst){
        switch(numOfOptionsToDisplay) {
            case 1:
                allOptionsLst[1].setVisibility(View.GONE);
            case 2:
                allOptionsLst[2].setVisibility(View.GONE);
        }
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
         if(newButtonPressed == currentMeetingChoice) {
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


     private void handleCreateMeetingBtnPress(final ArrayList<Button> buttons) {
         Button createMeeting = buttons.get(buttons.size()-1);
         createMeeting.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(currentMeetingChoice != NO_OPTION_CHOSEN) {
                     Button optionSelected = buttons.get(currentMeetingChoice);
                     String chosenDate = optionSelected.getText().toString();
                     chosenDate = chosenDate.substring(0, chosenDate.indexOf("-"));
                     topSuggestionsDialog.dismiss();
                     dateChosenByAdmin = chosenDate;
                     createMeetUp();
                 }
             }
         });
     }

     public void createMeetUp(){
         isDateChosen = true;
         if (!currentGroup.getIsScheduled()) {
             currentGroup.setIsScheduled(true);
             setChosenDate(dateChosenByAdmin);
             groupRef = db.collection("groups").document(currentGroup.getGroupId());
             groupRef.update("isScheduled", true);
             groupRef.update("chosenDate", dateChosenByAdmin);
         }
         setCalendarInvisible();
         setGifBackground();
         meetingChosenDialog.setContentView(R.layout.date_setup_popup);
         TextView dateText = meetingChosenDialog.findViewById(R.id.dateChoise);
         dateText.setText(currentGroup.getChosenDate());
         meetingChosenDialog.show();
     }

     private void setChosenDate(String dateToSet){
         String[] dayAndTime = dateToSet.split(" ");
         String day = dayAndTime[0];
         if (day.equals("Today")){
             day = DateSetter.getToday();
         }
         dateChosenByAdmin = String.format("%s %s", day, dayAndTime[1]);
         currentGroup.setChosenDate(dateChosenByAdmin);
    }

     private void setCalendarInvisible(){
         LinearLayout calendar = activity.findViewById(R.id.calendarView);
         calendar.setVisibility(View.INVISIBLE);
         TextView calendarText = activity.findViewById(R.id.textView);
         calendarText.setVisibility(View.INVISIBLE);
     }

     private void setGifBackground(){
         LinearLayout calendarBackground = activity.findViewById(R.id.groupBackGround);
         calendarBackground.setBackgroundResource(R.drawable.calendars_background);
     }
}