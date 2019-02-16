package com.example.meetapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;

public class GroupsDisplayActivity extends AppCompatActivity {
    private Dialog newGroupDialog;
    private Dialog addMembersDialog;
    private Dialog userProfileDialog;
    private Dialog editNameDialog;
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
    private FirebaseAuth DBAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fetchUserId();
        setContentView(R.layout.activity_main);
        setToolbar();
        setDialogs();
        setRecyclerViewAndAdapter();
        readUserNameFromDB();
        DBAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.all_groups_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                handleUserProfile();
                break;
            case R.id.Logout:
                handleLogout();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void handleUserProfile(){
        userProfileDialog.setContentView(R.layout.user_profile_popup);
        setProfileDetails();
        Button exitBtn = userProfileDialog.findViewById(R.id.userProfileExitBtn);
        handleEditUserName();
        handleExitPopup(exitBtn);
        userProfileDialog.show();
    }

    private void handleEditUserName(){
        final Button editNameBtn = userProfileDialog.findViewById(R.id.editUserName);
        editNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameDialog.setContentView(R.layout.edit_user_name_popup);
                TextView exitBtn = editNameDialog.findViewById(R.id.exitEditNameBtn);
                handleExitPopup(exitBtn);
                handleEditInput();
                editNameDialog.show();
            }
        });
    }

    private void handleEditInput(){
        final Button changeNameBtn = editNameDialog.findViewById(R.id.changeNameBtn);
        EditText userInput = editNameDialog.findViewById(R.id.editUserNameInput);
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
                            handleChangeNameRequest();
                        }
                    });
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setTxtInChangeGroupPopup(EditText userInput) {
        TextView userNameInfo = userProfileDialog.findViewById(R.id.userName);
        userInput.setText(userNameInfo.getText(), TextView.BufferType.EDITABLE);
        userInput.setSelectAllOnFocus(true);
        editNameDialog.getWindow().
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void handleChangeNameRequest(){
        EditText userInput = editNameDialog.findViewById(R.id.editUserNameInput);
        String newUserName = userInput.getText().toString();
        usersRef = db.collection("users").document(userID);
        usersRef.update("name", newUserName);
        editNameDialog.dismiss();
        readUserNameFromDB();
        displayUserName(newUserName);
    }

    private void displayUserName(String username) {
        TextView userNameInfo = userProfileDialog.findViewById(R.id.userName);
        userNameInfo.setText(username);
    }

    private void setProfileDetails(){
        readUserNameFromDB();
        TextView userNameTextView = userProfileDialog.findViewById(R.id.userName);
        userNameTextView.setText(userName);
        TextView userEmailTextView = userProfileDialog.findViewById(R.id.userEmailAddress);
        userEmailTextView.setText(DBAuth.getCurrentUser().getEmail());
    }

    private void handleExitPopup(Button exitBtn){
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userProfileDialog.dismiss();
            }
        });
    }

    private void handleExitPopup(TextView exitBtn){
        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNameDialog.dismiss();
            }
        });
    }

    void handleLogout() {
        AlertDialog logoutDialog = new AlertDialog.Builder(this).create();
        logoutDialog.setTitle(getString(R.string.logoutDialogTitle));
        logoutDialog.setMessage(getString(R.string.logoutQuestion));
        handlePositiveLogoutAnswer(logoutDialog);
        handleNegativeLogoutAnswer(logoutDialog);
        logoutDialog.show();
        handleButtonsLayoutAndColor(logoutDialog);
    }

    private void handleButtonsLayoutAndColor(AlertDialog alertDialog) {
        Button btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnNegative.setTextColor(getResources().getColor(R.color.colorGreen));
        btnPositive.setTextColor(getResources().getColor(R.color.colorRed));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);

    }

    private void handlePositiveLogoutAnswer(AlertDialog alertDialog) {
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.logoutAnswer),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        dialog.dismiss();
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }
                });
    }

    private void handleNegativeLogoutAnswer(AlertDialog alertDialog) {
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.dontLeaveAnswer),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
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
        userProfileDialog = new Dialog(this);
        editNameDialog = new Dialog(this);
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
