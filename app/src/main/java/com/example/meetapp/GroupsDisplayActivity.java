package com.example.meetapp;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;
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
    private RecyclerView.Adapter adapter;
    private String userName = "Oren";
    private String userID = "dzg4pwC1vyKtVZnk0EeA";
    private String phoneNumber = "972528240512";
    IntentHandler groupsIntentHandler;
    GroupsDisplayFeaturesHandler groupsDisplayFeaturesHandler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRecyclerViewAndAdapter();
        setToolbar();
        groupsIntentHandler = new IntentHandler();
        setDialogs();
        setCreateNewGroupListener();
    }

    private void setRecyclerViewAndAdapter(){
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                loadGroups();
        adapter = new GroupAdapter(MockDB.getGroupsList(), this);
        recyclerView.setAdapter(adapter);
    }

    private void setCreateNewGroupListener(){
        groupsDisplayFeaturesHandler = new GroupsDisplayFeaturesHandler(this, dialogs, adapter);
        Button createNewGroup = findViewById(R.id.AddGroupBtn);
        createNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupsDisplayFeaturesHandler.handleAddNewMembers(userName, userID);
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

    private void loadGroups() {
//        MockDB.buildMockGroups(userName);
        MockDB.buildDBGroups(userName);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ContactsGetter.showContacts(this, addMembersDialog);
            } else {
                Toast.makeText(this, getString(R.string.ContactsPermissionError), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
}
