package com.example.meetapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

public class GroupsDisplayActivity extends AppCompatActivity {
    private Dialog newGroupDialog;
    private Dialog addMembersDialog;
    private HashMap<String, Dialog> dialogs= new HashMap<>();
    private static long back_pressed;
    private final int EXIT_DELAY = 2000;
    private RecyclerView recyclerView;
    private GroupAdapter adapter;
    private String userName;
    private String userID;
    GroupsDisplayFeaturesHandler groupsDisplayFeaturesHandler;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchUserId();
        setContentView(R.layout.activity_main);
        setToolbar();
        setDialogs();
        setRecyclerViewAndAdapter();
        readUserNameFromDB();
    }

    private void fetchUserId(){
        userID = getIntent().getExtras().getString("userId");
    }

    private void setRecyclerViewAndAdapter(){
        CollectionReference groupsRef = db.collection("groups");
        Query groups = groupsRef.whereArrayContains("members", userID).
                orderBy("created", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Group> options = new FirestoreRecyclerOptions.Builder<Group>().
                setQuery(groups, Group.class).build();
        adapter = new GroupAdapter(options, this);
        adapter.setUserId(userID);
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

    private void readUserNameFromDB() {
        usersRef = db.collection("users").document(userID);
        usersRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    userName = (String) document.get("name");
                    setCreateNewGroupListener();
                }
            }
        });
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
