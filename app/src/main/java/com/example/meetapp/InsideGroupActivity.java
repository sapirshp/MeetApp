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
        dialogs.put("topSuggestionsDialog", topSuggestionsDialog);
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
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
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
