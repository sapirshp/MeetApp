package com.example.meetapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

public class GroupsDisplayActivity extends AppCompatActivity {
    private final int LEAVE_GROUP_RESULT_CODE = 1;
    private final int EDIT_NAME_RESULT_CODE = 2;
    private final int ADD_MEMBERS_RESULT_CODE = 3;
    private final int CHANGE_NAME_AND_MEMBERS = 4;
    private Dialog newGroupDialog;
    private Dialog addMembersDialog;
    private HashMap<String, Dialog> dialogs= new HashMap<>();
    private static long back_pressed;
    private final int EXIT_DELAY = 2000;
    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private String userName = "Oren";
    private String userID = "Q6vPMTUMQZe9IS9gQjWzmXSjPB22";
    private String phoneNumber = "972528240512";
    IntentHandler groupsIntentHandler;
    GroupsDisplayFeaturesHandler groupsDisplayFeaturesHandler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        groupsIntentHandler = new IntentHandler();
        setDialogs();
        setCreateNewGroupListener();
        setRecyclerViewAndAdapter();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == LEAVE_GROUP_RESULT_CODE) {
                groupsIntentHandler.handleLeaveGroupResult(data, adapter);
                AddMembersHandler.setDialog(addMembersDialog);
            }else if (resultCode == EDIT_NAME_RESULT_CODE){
                groupsIntentHandler.handleEditNameResult(data, adapter);
            }else if (resultCode == ADD_MEMBERS_RESULT_CODE){
                groupsIntentHandler.handleAddMembersResult(data, adapter);
            }else if (resultCode == CHANGE_NAME_AND_MEMBERS){
                groupsIntentHandler.handleChangeNameAndMembersResult(data, adapter);
            }else {
                AddMembersHandler.setDialog(addMembersDialog);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void setRecyclerViewAndAdapter(){
        CollectionReference groupsRef = db.collection("groups");
        Query groups = groupsRef.whereArrayContains("members", userID).orderBy("name");
        FirestoreRecyclerOptions<Group> options = new FirestoreRecyclerOptions.Builder<Group>().
                setQuery(groups, Group.class).build();
        adapter = new GroupAdapter(options, this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setCreateNewGroupListener(){
        groupsDisplayFeaturesHandler = new GroupsDisplayFeaturesHandler(this, dialogs, adapter);
        Button createNewGroup = findViewById(R.id.AddGroupBtn);
        createNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsDisplayFeaturesHandler.handleAddNewMembers(userID, userName);
            }
        });
    }

    private void setDialogs(){
        newGroupDialog = new Dialog(this);
        addMembersDialog = new Dialog(this);
        AddMembersHandler.setDialog(addMembersDialog);
        dialogs.put("newGroupDialog", newGroupDialog);
        dialogs.put("addMembersDialog", addMembersDialog);
    }

    public void setToolbar() {
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("MeetApp");
            getSupportActionBar().setLogo(R.drawable.meetapp_logo_toolbar);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
    }

    @Override
    public void onBackPressed()
    {
        if (back_pressed + EXIT_DELAY > System.currentTimeMillis())
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
        }
        else Toast.makeText(getBaseContext(), "Press once again to exit!", Toast.LENGTH_SHORT).show();
        back_pressed = System.currentTimeMillis();
    }
}
