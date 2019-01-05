package com.example.meetapp;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class InsideGroupActivity extends AppCompatActivity {
    Dialog groupActionsDialog;
    Dialog topSuggestionsDialog;
    Dialog addMemberDialog;
    Dialog groupDetailsDialog;
    Dialog editGroupNameDialog;
    HashMap<String, Dialog> dialogs = new HashMap<>();
    private MenuHandler menuHandler;
    private ArrayList<String> membersToAdd = new ArrayList<>();
    private int membersAmount;
    private String groupMembers;
    private String groupName;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Toolbar toolbar;
    private CalendarSlotsHandler calendarSlotsHandler;
    boolean isTopChoicePressed = false;


    public InsideGroupActivity(){
        DateSetter.createIntToDayMap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        View layout = findViewById(R.id.calendarView);
        DateSetter.setDatesToDisplay(layout);
        setToolbar();
        calendarSlotsHandler = new CalendarSlotsHandler(membersAmount, this, layout);
        calendarSlotsHandler.setButtonsIdForListeners(DateSetter.getDaysInCalendar(), this);
        calendarSlotsHandler.setListeners(DateSetter.getDatesToDisplay());
        groupActionsDialog = new Dialog(this);
        addMemberDialog = new Dialog(this);
        topSuggestionsDialog = new Dialog(this);
        groupDetailsDialog = new Dialog(this);
        editGroupNameDialog = new Dialog(this);
        setDialogsMap();
    }

    public void setDialogsMap(){
        dialogs.put("addMemberDialog", addMemberDialog);
        dialogs.put("groupDetailsDialog", groupDetailsDialog);
        dialogs.put("editGroupNameDialog", editGroupNameDialog);
        dialogs.put("topSuggestionsDialog"", topSuggestionsDialog)
    }

    // ================= Toolbar and Menu ==================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        List<String> groupMembersList = new LinkedList<>(Arrays.asList(groupMembers.split(",")));
        menuHandler = new MenuHandler(dialogs, groupMembersList);
        return true;
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            groupName = getIntent().getExtras().getString("groupName");
            getSupportActionBar().setTitle(groupName);
            getSupportActionBar().setLogo(R.drawable.meetapp_logo_toolbar);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        groupMembers = getIntent().getExtras().getString("groupMembers");
        toolbar.setSubtitle(groupMembers);
        membersAmount = groupMembers.split(",").length;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.AddParticipantBtn:
                menuHandler.handleAddParticipant(new Runnable() {
                    @Override
                    public void run() {
                        showContacts();
                    }
                }, toolbar, calendarSlotsHandler);
                break;
            case R.id.groupDetailsBtn:
                menuHandler.handleGroupDetails(calendarSlotsHandler, toolbar.getTitle().toString(), toolbar);
                break;
            case R.id.resetTimeChoiceBtn:
                    menuHandler.handleResetTimeChoice(this, calendarSlotsHandler);
                break;
            case R.id.exitGroupBtn:
                menuHandler.handleExitGroup(this);
                break;
            case R.id.createMeetingBtn:
                menuHandler.handleCreateMeeting(this, calendarSlotsHandler);
                // TODO: CHENS CODE HERE
//                 menuHandler.handleTopSuggestions(this, calendarSlotsHandler, topSuggestionsDialog);

//
//                ArrayList<String> stringTopSuggestionsArr = calendarSlotsHandler.displayTopSelections();
//                int numOfButtonsToDisplay = stringTopSuggestionsArr.size();
//                if(numOfButtonsToDisplay == 0) {
//                    Toast.makeText(this, "No Time Slots Selected...", Toast.LENGTH_SHORT).show();
//                    break;      // TODO: CHANGE TO "RETURN" AFTER FUNCTION IS BUILT IN MENU_HANDLER CLASS
//                }
//                topSuggestionsDialog.setContentView(R.layout.top_suggestions_popup);
//
//                // exit btn
//                TextView exitPopupBtn;
//                exitPopupBtn = topSuggestionsDialog.findViewById(R.id.exitPopupBtn);
//                exitPopupBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        topSuggestionsDialog.dismiss();
//                    }
//                });
//
//                // assign buttons
//                final Button option1, option2, option3, createMeeting;
//                option1 = topSuggestionsDialog.findViewById(R.id.option1);
//                option2 = topSuggestionsDialog.findViewById(R.id.option2);
//                option3 = topSuggestionsDialog.findViewById(R.id.option3);
//                createMeeting = topSuggestionsDialog.findViewById(R.id.CreateMeetupBtn);
//                final ArrayList<Button> btnLst = new ArrayList<>();
//
//
//                // build button list according to numOfButtonsToDisplay
//                switch (numOfButtonsToDisplay){
//                    case 3:
//                        btnLst.add(option3);
//                    case 2:
//                        btnLst.add(0, option2);
//                    case 1:
//                        btnLst.add(0, option1);
//                    default:
//                        btnLst.add(0, createMeeting);
//                }
//
//                // set text
//                // TODO: GET REAL CHOICES FROM SAPIR'S FUNCTION
//                option1.setText("#1:" + "Monday Morning");
//                option2.setText("#2:" + "Sunday Afternoon");
//                option3.setText("#3:" + "Saturday Evening");
//                // show only the relevant buttons
//                switch(numOfButtonsToDisplay)
//                {
//                    case 1:
//                        option2.setVisibility(View.GONE);
//                    case 2:
//                        option3.setVisibility(View.GONE);
//                }
//
//                int currentButtonPressed = -1;
////                int[] buttonInfo = new int[]{0, 0, 0};
//
//
//                option1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        currentButtonPressed =
//                                handleOptionPress(new int[]{1, currentButtonPressed}, btnLst);
//                    }
//                });
//
//                topSuggestionsDialog.show();








//                ArrayList<Button> btnLst = new ArrayList<>
//                        (Arrays.asList(option1));
//                if(numOfButtonsToDisplay == 2 ) {
//                    option2 = topSuggestionsDialog.findViewById(R.id.option2);
//                    btnLst.add(option2);
////                    btnLst = Arrays.asList(option1, option2, createMeeting);
//                }
//                else if(numOfButtonsToDisplay == 3) {
//                    option3 = topSuggestionsDialog.findViewById(R.id.option3);
//                }
//                createMeeting = topSuggestionsDialog.findViewById(R.id.CreateMeetupBtn);


                // ##### ON CLICK LISTENERS  #####
//                option1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        ArrayList<Button> btnLst1 = List.of(option1, option2);
//                        ArrayList<Button> btnLst = new ArrayList<>
//                                (Arrays.asList(option1, option2, option3, createMeeting));
//                        setSingleChoiceAndDisableOthers(btnLst);
//
////                        setSingleChoiceAndDisableOthers(option1, option2, option3, createMeeting);
//                    }
//                });
//
//                option2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        setSingleChoiceAndDisableOthers(option2, option1, option3, createMeeting);
//                        ArrayList<Button> btnLst = new ArrayList<>
//                                (Arrays.asList(option2, option1, option3, createMeeting));
//                        setSingleChoiceAndDisableOthers(btnLst);
//                    }
//                });
//
//
//                option3.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                        setSingleChoiceAndDisableOthers(option3, option2, option1, createMeeting);
//                        ArrayList<Button> btnLst = new ArrayList<>
//                                (Arrays.asList(option3, option2, option1, createMeeting));
//                        setSingleChoiceAndDisableOthers(btnLst);
//                    }
//                });
//
//                createMeeting.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if(isTopChoicePressed)
//                        {
//                            Toast.makeText(v.getContext(), "create group implementation - to come...", Toast.LENGTH_SHORT).show();
//                            // implement here
//                        }
//                    }
//                });



                // after sapir's fix
//                option1.setText(stringTopSuggestionsArr.get(0));
//                option2.setText(stringTopSuggestionsArr.get(1));
//                option3.setText(stringTopSuggestionsArr.get(2));



//                topSuggestionsDialog.show();



                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }



//    private int handleOptionPress(int[] buttonsInfo, ArrayList<Button> btnList)
//    {
//        int newButtonPressed = buttonsInfo[0];
//        int currentButtonOn = buttonsInfo[1];
//        if(newButtonPressed == currentButtonOn)
//        {
//            turnOffCurrentBtn(newButtonPressed, btnList);
//            return -1;  // i.e. now no button is pressed
//        }
//        else{
//            setSingleChoiceAndDisableOthers(newButtonPressed, btnList);
//            return newButtonPressed;
//        }
//    }
//
//    private void setSingleChoiceAndDisableOthers(int buttonToTurnOn, ArrayList<Button> btnList)
//
////            Button makeGreen, Button disable1, Button disable2,
////                                                 Button createMeeting)
//    {
//        int i = 0;
//        for(Button currentBtn : btnList)
//        {
//            if (i == buttonToTurnOn) {
//                currentBtn.setBackgroundResource(R.drawable.green_round_background);
//            }
//            else{
//                currentBtn.setBackgroundResource(R.drawable.custom_border);
//            }
//            i++;
//        }
//        btnList.get(-1).setBackgroundResource(R.drawable.green_round_background);
//
//
//
//
//
////        Button makeGreen = btnList.get(0);
////        Button disable1 = btnList.get(1);
////        Button disable2 = btnList.get(2);
////        Button createMeeting = btnList.get(3);
////
////
////        makeGreen.setBackgroundResource(R.drawable.green_round_background);
////        disable1.setBackgroundResource(R.drawable.custom_border);
////        disable2.setBackgroundResource(R.drawable.custom_border);
////        createMeeting.setBackgroundResource(R.drawable.green_round_background);
////        isTopChoicePressed = true;
//
//    }

    // ============== Contacts Handlers ======================

    public void onContactClick(View v){
        RelativeLayout contactLayout = v.findViewById(R.id.contactLayout);
        TextView contactName = v.findViewById(R.id.contactName);
        if (contactLayout.getTag() != "chosen") {
            menuHandler.setButtonEnabled();
            contactLayout.setBackgroundColor(getResources().getColor(R.color.colorGreen));
            contactLayout.setTag("chosen");
            membersToAdd.add((String) contactName.getText());
        }else{
            membersToAdd.remove((String) contactName.getText());
            if (membersToAdd.isEmpty()){
                menuHandler.setButtonDisabled();
            }
            contactLayout.setBackgroundColor(getResources().getColor(R.color.colorWhite));
            contactLayout.setTag("notChosen");
        }
        menuHandler.setMembersToAdd(membersToAdd);
    }

    private void showContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            final ArrayList<ContactItem> contacts = ContactsGetter.getContacts(this);
            RecyclerView contactRecyclerView = addMemberDialog.findViewById(R.id.contactsRecyclerView);
            contactRecyclerView.setHasFixedSize(true);
            contactRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            RecyclerView.Adapter contactAdapter = new ContactsAdapter(contacts);
            contactRecyclerView.setAdapter(contactAdapter);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(this, getString(R.string.ContactsPermissionError), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
