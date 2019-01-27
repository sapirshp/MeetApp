package com.example.meetapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
    Dialog meetingChosenDialog;
    HashMap<String, Dialog> dialogs = new HashMap<>();
    private Group thisGroup;
    private MenuHandler menuHandler;
    private int membersAmount;
    private String groupMembers;
    private String groupName;
    private String groupId;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private Toolbar toolbar;
    private CalendarSlotsHandler calendarSlotsHandler;
    private Intent goToGroupsDisplay;
    private boolean nameChanged;
    private boolean membersAdded;
    IntentHandler insideGroupIntentHandler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public InsideGroupActivity(){
        DateSetter.createIntToDayMap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getIntent().getExtras().getString("groupId");
        readGroupData(groupId);
    }

    protected void readGroupData(String groupId) {
        DocumentReference groupRef = db.collection("groups").document(groupId);
        groupRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    thisGroup = snapshot.toObject(Group.class);
                    readMembersNames(thisGroup);
                }
            }
        });
    }

    protected void readMembersNames(final Group group) {
        CollectionReference usersRef = db.collection("users");
        Query groupUsers = usersRef.whereArrayContains("memberOf", groupId);
        groupUsers.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("name") != null) {
                            group.namesList.add(doc.getString("name"));
                        }
                    }
                    groupDetailsHandler();
                }
            });
    }

    protected void groupDetailsHandler() {
        setContentView(R.layout.activity_group);
        View layout = findViewById(R.id.calendarView);
        DateSetter.setDatesToDisplay(layout);
//        thisGroup = MockDB.getGroupById(groupId);
        setToolbar();
        createSlotHandler(layout);
        setIntentHandler();
        setDialogsMap();
    }

    private void createSlotHandler(View layout){
        calendarSlotsHandler = new CalendarSlotsHandler(membersAmount, this, layout, thisGroup);
        calendarSlotsHandler.setButtonsIdForListeners(DateSetter.getDaysInCalendar(), this);
        calendarSlotsHandler.setListeners(DateSetter.getDatesToDisplay());
    }

    private void setIntentHandler(){
        goToGroupsDisplay = new Intent();
        insideGroupIntentHandler = new IntentHandler();
        membersAdded = false;
        nameChanged = false;
    }

    public void setDialogsMap(){
        groupActionsDialog = new Dialog(this);
        addMemberDialog = new Dialog(this);
        topSuggestionsDialog = new Dialog(this);
        groupDetailsDialog = new Dialog(this);
        editGroupNameDialog = new Dialog(this);
        meetingChosenDialog = new Dialog(this);
        AddMembersHandler.setDialog(addMemberDialog);
        dialogs.put("addMemberDialog", addMemberDialog);
        dialogs.put("groupDetailsDialog", groupDetailsDialog);
        dialogs.put("editGroupNameDialog", editGroupNameDialog);
        dialogs.put("topSuggestionsDialog", topSuggestionsDialog);
        dialogs.put("meetingChosenDialog", meetingChosenDialog);
    }

    @Override
    public void onBackPressed() {
        if (nameChanged && membersAdded){
            insideGroupIntentHandler.groupNameAndMemberChanged(goToGroupsDisplay, this, toolbar, groupId);
        }
        else if (nameChanged) {
            insideGroupIntentHandler.groupNameChanged(goToGroupsDisplay, this, toolbar, groupId);
        }
        else if (membersAdded) {
            insideGroupIntentHandler.groupMembersAdded(goToGroupsDisplay, this, toolbar, groupId);
        } else {
            insideGroupIntentHandler.defaultFinish(this, goToGroupsDisplay);
        }
    }

    // ================= Toolbar and Menu ==================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        List<String> groupMembersList = new LinkedList<>(Arrays.asList(groupMembers.replaceAll(",\\s",",").split(",")));
        menuHandler = new MenuHandler(dialogs, groupMembersList, this, thisGroup);
        if (thisGroup.getIsScheduled()){
            nameChanged = menuHandler.handleGroupDetails(calendarSlotsHandler, groupName, toolbar, groupId);
        }
        return true;
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            groupName = thisGroup.getName();
            getSupportActionBar().setTitle(groupName);
            getSupportActionBar().setLogo(R.drawable.meetapp_logo_toolbar);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        groupMembers = thisGroup.getMembersString();
        toolbar.setSubtitle(groupMembers);
        membersAmount = thisGroup.getMembersAmount();
        setOnClickToolbarListener();
    }

    private void setOnClickToolbarListener(){
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameChanged = menuHandler.handleGroupDetails(calendarSlotsHandler, toolbar.getTitle().toString(), toolbar, groupId);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.AddParticipantBtn:
                menuHandler.handleAddParticipant(toolbar, calendarSlotsHandler);
                membersAdded = true;
                break;
            case R.id.groupDetailsBtn:
                nameChanged = menuHandler.handleGroupDetails(calendarSlotsHandler, toolbar.getTitle().toString(), toolbar, groupId);
                break;
            case R.id.resetTimeChoiceBtn:
                    menuHandler.handleResetTimeChoice(calendarSlotsHandler);
                break;
            case R.id.exitGroupBtn:
                menuHandler.handleExitGroup(this, groupId);
                break;
            case R.id.createMeetingBtn:
                menuHandler.handleCreateMeeting(calendarSlotsHandler);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    // ============== Permission for Contacts Handlers ======================

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ContactsGetter.showContacts(this, addMemberDialog);
            } else {
                Toast.makeText(this, getString(R.string.ContactsPermissionError), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
