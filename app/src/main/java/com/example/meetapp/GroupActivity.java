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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GroupActivity extends AppCompatActivity {
    Dialog groupActionsDialog;
    Dialog topSuggestionsDialog;
    Dialog addMemberDialog;
    private MenuHandler menuHandler;
    private ArrayList<String> membersToAdd = new ArrayList<>();
    private int membersAmount;
    private String groupMembers;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Toolbar toolbar;
    private CalendarSlotsHandler calendarSlotsHandler;
    boolean isTopChoicePressed = false;


    public GroupActivity(){
        DateSetter.createIntToDayMap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        View layout = findViewById(R.id.calendarView);
        DateSetter.setDatesToDisplay(layout);
        setToolbar();
        calendarSlotsHandler = new CalendarSlotsHandler(membersAmount);
        calendarSlotsHandler.setButtonsIdForListeners(DateSetter.getDaysInCalendar(), this);
        calendarSlotsHandler.setListeners(layout, DateSetter.getDatesToDisplay());
        groupActionsDialog = new Dialog(this);
        addMemberDialog = new Dialog(this);
        topSuggestionsDialog = new Dialog(this);
    }

    // ================= Toolbar and Menu ==================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        menuHandler = new MenuHandler(addMemberDialog, membersAmount, groupMembers, topSuggestionsDialog);
        return true;
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            String groupName = getIntent().getExtras().getString("groupName");
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
                menuHandler.handleGroupDetails(this);
                break;
            case R.id.resetTimeChoiceBtn:
                    menuHandler.handleResetTimeChoice(this, calendarSlotsHandler);
                break;
            case R.id.exitGroupBtn:
                menuHandler.handleExitGroup(this);
                break;
            case R.id.createMeetingBtn:
                // TODO: CHENS CODE HERE
//                 menuHandler.handleTopSuggestions(this, calendarSlotsHandler, topSuggestionsDialog);


//                ArrayList<String> stringTopSuggestionsArr = calendarSlotsHandler.displayTopSelections();
                topSuggestionsDialog.setContentView(R.layout.top_suggestions_popup);

                // exit btn
                TextView exitPopupBtn;
                exitPopupBtn = topSuggestionsDialog.findViewById(R.id.exitPopupBtn);
                exitPopupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        topSuggestionsDialog.dismiss();
                    }
                });

                final Button option1, option2, option3, createMeeting;
                option1 = topSuggestionsDialog.findViewById(R.id.option1);
                option2 = topSuggestionsDialog.findViewById(R.id.option2);
                option3 = topSuggestionsDialog.findViewById(R.id.option3);
                createMeeting = topSuggestionsDialog.findViewById(R.id.CreateMeetupBtn);

                option1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSingleChoiceAndDisableOthers(option1, option2, option3, createMeeting);
                    }
                });

                option2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSingleChoiceAndDisableOthers(option2, option1, option3, createMeeting);
                    }
                });


                option3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSingleChoiceAndDisableOthers(option3, option2, option1, createMeeting);
                    }
                });

                createMeeting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isTopChoicePressed)
                        {
                            Toast.makeText(v.getContext(), "create group implementation - to come...", Toast.LENGTH_SHORT).show();
                            // implement here
                        }
                    }
                });



                // after sapir's fix
//                option1.setText(stringTopSuggestionsArr.get(0));
//                option2.setText(stringTopSuggestionsArr.get(1));
//                option3.setText(stringTopSuggestionsArr.get(2));
                option1.setText("#1:" + "Monday Morning");
                option2.setText("#2:" + "Sunday Afternoon");
                option3.setText("#3:" + "Saturday Evening");


                topSuggestionsDialog.show();



                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setSingleChoiceAndDisableOthers(Button makeGreen, Button disable1, Button disable2,
                                                 Button createMeeting)
    {
        makeGreen.setBackgroundResource(R.drawable.green_round_background);
        disable1.setBackgroundResource(R.drawable.custom_border);
        disable2.setBackgroundResource(R.drawable.custom_border);
        createMeeting.setBackgroundResource(R.drawable.green_round_background);
        isTopChoicePressed = true;

    }

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
