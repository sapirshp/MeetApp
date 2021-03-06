package com.example.meetapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private List<String> groupMembersList;
    private Group thisGroup;
    private MenuHandler menuHandler;
    private int membersAmount;
    private String groupMembers;
    private String groupName;
    private String groupId;
    private String userId;
    private Toolbar toolbar;
    private CalendarSlotsHandler calendarSlotsHandler;
    private Intent goToGroupsDisplay;
    private boolean nameChanged;
    Query groupUsers;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference groupRef;
    private CollectionReference usersRef;

    public InsideGroupActivity(){
        DateSetter.createIntToDayMap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        groupId = getIntent().getExtras().getString("groupId");
        userId = getIntent().getExtras().getString("userId");
        readGroupData(groupId);
        setContentView(R.layout.activity_group);
    }

    protected void readGroupData(String groupId) {
        groupRef = db.collection("groups").document(groupId);
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
                    groupActivityHandler();
                }
            }
        });
    }

    protected void groupActivityHandler() {
        View layout = findViewById(R.id.calendarView);
        DateSetter.setDatesToDisplay(layout);
        createSlotHandler(layout);
        goToGroupsDisplay = new Intent();
        setDialogsMap();
    }

    protected void readMembersNames(final Group group) {
        usersRef = db.collection("users");
        groupUsers = usersRef.whereArrayContains("memberOf", groupId);
        groupUsers.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value,
                                    @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }
                    group.resetNamesList();
                    for (QueryDocumentSnapshot doc : value) {
                        if (doc.get("name") != null) {
                            group.addNameToNamesList(doc.getString("name"));
                        }
                    }
                    setToolbar();
                }
            });
    }

    private void createSlotHandler(View layout){
        calendarSlotsHandler = new CalendarSlotsHandler(thisGroup, userId, this, layout);
    }

    public void setDialogsMap(){
        createDialogs();
        AddMembersHandler.setDialog(addMemberDialog);
        dialogs.put("addMemberDialog", addMemberDialog);
        dialogs.put("groupDetailsDialog", groupDetailsDialog);
        dialogs.put("editGroupNameDialog", editGroupNameDialog);
        dialogs.put("topSuggestionsDialog", topSuggestionsDialog);
        dialogs.put("meetingChosenDialog", meetingChosenDialog);
    }

    private void createDialogs() {
        groupActionsDialog = new Dialog(this);
        addMemberDialog = new Dialog(this);
        topSuggestionsDialog = new Dialog(this);
        groupDetailsDialog = new Dialog(this);
        editGroupNameDialog = new Dialog(this);
        meetingChosenDialog = new Dialog(this);
    }

    @Override
    public void onBackPressed() {
        this.setResult(1, goToGroupsDisplay);
        this.finish();
        this.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    // ================= Toolbar and Menu ==================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.group_menu, menu);
        groupMembersList = new LinkedList<>(Arrays.asList(groupMembers.replaceAll
                (",\\s",",").split(",")));
        menuHandler = new MenuHandler(dialogs, groupMembersList, this, thisGroup);
        if (thisGroup.getIsScheduled()){
            nameChanged = menuHandler.handleGroupDetails(calendarSlotsHandler, groupName, toolbar);
        }
        return true;
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.groupToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            groupName = thisGroup.getName();
            getSupportActionBar().setTitle(groupName);
            getSupportActionBar().setLogo(R.drawable.new_logo);
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
                nameChanged = menuHandler.handleGroupDetails(calendarSlotsHandler,
                        toolbar.getTitle().toString(), toolbar);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.AddParticipantBtn:
                menuHandler.handleAddParticipant(groupId);
                break;
            case R.id.groupDetailsBtn:
                nameChanged = menuHandler.handleGroupDetails(calendarSlotsHandler,
                        toolbar.getTitle().toString(), toolbar);
                break;
            case R.id.resetTimeChoiceBtn:
                    menuHandler.handleResetTimeChoice(calendarSlotsHandler, groupId, userId);
                break;
            case R.id.exitGroupBtn:
                menuHandler.handleExitGroup(this, groupId, userId);
                break;
            case R.id.createMeetingBtn:
                menuHandler.handleCreateMeeting(calendarSlotsHandler);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
