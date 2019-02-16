package com.example.meetapp;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class GroupsDisplayFeaturesHandler {
    private Dialog newGroupDialog;
    private Dialog addMembersDialog;
    private Activity activity;
    private ArrayList<String> membersNamesList = new ArrayList<>();
    private ArrayList<String> membersIdsList = new ArrayList<>();
    private RecyclerView.Adapter adapter;
    private Map<String, Object> group;
    private List<QuerySnapshot> queriesList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference userRef;
    private DocumentReference groupRef;
    private DocumentReference calendarRef;
    private CollectionReference allUsersRef;
    private CollectionReference usersRef;
    public static int TIME_SLOTS_AMOUNT = 21;
    private Map<String, Integer> initialMap;
    private Map<String, Object> initialCalendar;
    private Query allUsers;
    private Query admin;
    private String groupMembers;
    private String membersNames;

    GroupsDisplayFeaturesHandler(Activity activity, HashMap<String, Dialog> dialogs, RecyclerView.Adapter adapter){
        this.newGroupDialog = dialogs.get("newGroupDialog");
        this.addMembersDialog = dialogs.get("addMembersDialog");
        this.activity = activity;
        this.adapter = adapter;
    }

    private void showNewGroupPopup(String adminID, String membersNames, ArrayList<String> membersIds) {
        newGroupDialog.setContentView(R.layout.new_group_popup);
        TextView membersList = newGroupDialog.findViewById(R.id.membersList);
        membersList.setText(membersNames);
        TextView exitBtn = newGroupDialog.findViewById(R.id.exitNewGroupBtn);
        handleExitPopup(newGroupDialog, exitBtn);
        handleCreateNewGroup(adminID, membersIds);
        newGroupDialog.show();
    }

    private void handleCreateNewGroup(String adminID, List<String> members) {
        final Button createGroup = newGroupDialog.findViewById(R.id.CreateGroupBtn);
        EditText userInput = newGroupDialog.findViewById(R.id.newGroupNameInput);
        newGroupDialog.getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        userInput.addTextChangedListener(onInputChange(createGroup, adminID, members));
    }

    private TextWatcher onInputChange(final Button createGroup, final String adminID,
                                      final List<String> members) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    createGroup.setBackgroundResource(R.drawable.disabled_button_background);
                }else {
                    createGroup.setBackgroundResource(R.drawable.green_round_background);
                    createGroup.setOnClickListener(onCreateGroupBtn(adminID, members));
                }
            }
            
            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    private View.OnClickListener onCreateGroupBtn(final String adminID, final List<String> members){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleNewGroupRequest(adminID, members);
            }
        };
    }

    private void handleNewGroupRequest(final String adminID, final List<String> members)
    {
        EditText userInput = newGroupDialog.findViewById(R.id.newGroupNameInput);
        final String newGroupName = userInput.getText().toString();
        //TODO decide if check for double names is required, the DB supports two groups with the same name
        // makeToastToCenterOfScreen(activity.getString(R.string.groupNameExists));
        addNewGroupToDB(adminID, newGroupName, members);
        makeToastToCenterOfScreen(activity.getString(R.string.newGroupCreated));
        newGroupDialog.dismiss();
    }

    private void addNewGroupToDB(final String adminID, final String groupName,
                                   final List<String> groupMembers)
    {
        group = new HashMap<String, Object>()
        {{
            put("name", groupName);
            put("admin", adminID);
            put("members", groupMembers);
            put("isScheduled", false);
            put("chosenDate", "");
            put("created", new Timestamp(new Date()));
        }};
        groupRef = db.collection("groups").document();
        String groupId = groupRef.getId();
        group.put("groupId", groupId);
        groupRef.set(group);
        addGroupCalendar(groupMembers, groupId);
        addGroupToUsers(groupMembers, groupId);
    }

    private void addGroupCalendar(final List<String> groupMembers, final String groupId) {
        initialMap = new HashMap<>();
        for (int i = 0; i < TIME_SLOTS_AMOUNT; i++) {
            initialMap.put(Integer.toString(i), 0);
        }
        initialCalendar = new HashMap<String, Object>();
        initialCalendar.put("all", initialMap);
        calendarRef = db.collection("calendars").document(groupId);
        for (String member: groupMembers) {
            initialCalendar.put(member, initialMap);
        }
        calendarRef.set(initialCalendar);
    }

    private void addGroupToUsers(final List<String> groupMembers, final String groupId) {
        allUsersRef = db.collection("users");
        for (String memberID: groupMembers) {
            userRef = allUsersRef.document(memberID);
            userRef.update("memberOf", FieldValue.arrayUnion(groupId));
        }
    }

    private void makeToastToCenterOfScreen(String message)
    {
        Toast toast = Toast.makeText(activity.getBaseContext(),message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void handleExitPopup(final Dialog dialog, TextView exitBtn)
    {
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (dialog == addMembersDialog) {
                    AddMembersHandler.clearMembersToAdd();
                }
            }
        });
    }

    void handleAddNewMembers(final String adminID, final String adminName){
        addMembersDialog.setContentView(R.layout.add_member_popup);
        TextView exitBtn = addMembersDialog.findViewById(R.id.addMemberExitBtn);
        handleExitPopup(addMembersDialog, exitBtn);
        usersRef = db.collection("users");
        allUsers = usersRef.orderBy("name");
        admin = usersRef.whereEqualTo("userId", adminID);
        Task firstTask = allUsers.get();
        Task secondTask = admin.get();
        Task combinedTask = Tasks.whenAllSuccess(firstTask, secondTask).addOnSuccessListener(new OnSuccessListener<List<Object>>() {
            @Override
            public void onSuccess(List<Object> list) {
                queriesList = (List<QuerySnapshot>)(Object)list;
                UsersGetter.showUsers(activity, addMembersDialog, queriesList);
                finishAddParticipant(adminID, adminName);
            }
        });
    }

    private void finishAddParticipant(final String adminID, final String adminName) {
        AddMembersHandler.setDialog(addMembersDialog);
        AddMembersHandler.chooseMembers(new Runnable() {
            @Override
            public void run() {
                onChooseMembers(adminID, adminName);
            }
        });
        addMembersDialog.show();
    }

    private void onChooseMembers(final String adminID, final String adminName){
        membersNamesList.clear();
        membersNamesList.add(adminName);
        membersNamesList.addAll(AddMembersHandler.getMembersNamesToAdd());
        membersIdsList.clear();
        membersIdsList.add(adminID);
        membersIdsList.addAll(AddMembersHandler.getMembersIdsToAdd());
        groupMembers = membersNamesList.toString().substring(1, membersNamesList.toString().length()-1);
        membersNames = String.format("Group Members: %s", groupMembers);
        showNewGroupPopup(adminID, membersNames, membersIdsList);
    }
}
