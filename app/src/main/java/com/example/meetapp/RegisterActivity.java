package com.example.meetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private CharSequence userName = "";
    private CharSequence userEmail = "";
    private CharSequence userPassword1 = "";
    private CharSequence userPassword2 = "";
    private String emailAlreadyExistsError = "The email address is already in use by another account.";
    private TextView feedbackToUser;
    private Button registerBtn;
    private String EMPTY = "";
    private final String emailRegex = "^(.+)@(.+)\\.(.+)$";
    private FirebaseAuth mAuth;
    String userId;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_register);
        handleUserInput();
    }

    private void handleUserInput() {
        registerBtn = findViewById(R.id.registerBtn);
        feedbackToUser = findViewById(R.id.userFeedbackPasswordsDontMatch);
        feedbackToUser.setText("");
        handleUserNameInput();
        handleUserEmailInput();
        handleUserPassword1();
        handleUserPassword2();
    }

    // NOTE:
    // It may seem that there is code duplication in the next 4 functions. These functions use the global variables
    // userName, userEmail, userPassword1 and userPassword2. However, the use of nested function demands either global
    // or final variables. These are not final variables, as they hold current user's input, and therefore were written one by one.


    private void handleUserNameInput() {
        EditText userNameInput = findViewById(R.id.RegisterNameInput);
        userNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    userName = "";
                } else {
                    userName = s.toString();
                }
                handleRegisterBtnAndPasswordFeedback();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleUserEmailInput() {
        final EditText userEmailInput = findViewById(R.id.RegisterEmailInput);
        userEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    userEmail = "";
                } else {
                    userEmail = s.toString();
                }
                handleRegisterBtnAndPasswordFeedback();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        userEmailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !userEmail.toString().matches(emailRegex)){
                    userEmailInput.setError("Invalid Email Address");
                }
            }
        });
    }

    private void handleUserPassword1() {
        final EditText password1EditText = findViewById(R.id.RegisterPassword1Input);
        password1EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    userPassword1 = "";
                } else {
                    userPassword1 = s.toString();
                }
                handleRegisterBtnAndPasswordFeedback();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        password1EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && userPassword1.toString().length() < 6){
                    password1EditText.setError("The Password must be at least 6 characters long");
                }
            }
        });
    }

    private void handleUserPassword2() {
        final EditText password2EditText = findViewById(R.id.RegisterPassword2Input);
        password2EditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    userPassword2 = "";
                } else {
                    userPassword2 = s.toString();
                }
                handleRegisterBtnAndPasswordFeedback();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        password2EditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && userPassword1.toString().length() < 6){
                    password2EditText.setError("The Password must be at least 6 characters long");
                }
            }
        });
    }

    private void handleRegisterBtnAndPasswordFeedback(){
        if( (!userName.toString().equals(EMPTY))      &&
            (!userEmail.toString().equals(EMPTY))     &&
            (userEmail.toString().matches(emailRegex)) &&
            (!userPassword1.toString().equals(EMPTY)) &&
            (!userPassword2.toString().equals(EMPTY))){
                registerBtn.setBackgroundResource(R.drawable.green_round_background);
                feedbackToUser.setText(EMPTY);
                onClickGoToGroupsScreen();
        }
        else{
            registerBtn.setBackgroundResource(R.drawable.disabled_button_background);
        }
    }

    private void onClickGoToGroupsScreen() {
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!userPassword1.toString().equals(userPassword2.toString())) {
                    EditText password2EditText = findViewById(R.id.RegisterPassword2Input);
                    password2EditText.setText("");
                    userPassword2 = "";
                    feedbackToUser.setText(v.getContext().getString(R.string.PasswordsDoNotMatch));
                } else {
                    writeToDB(v);
                }
            }
        });
    }

    private void writeToDB(final View v){
        final String name = userName.toString();
        final String email = userEmail.toString();
        final String password = userPassword1.toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            userId = user.getUid();
                            addUserToDB(userId, name);
                            goToGroupDisplayScreen(userId);
                        } else {
                            if (task.getException().getMessage().equals(emailAlreadyExistsError)) {
                                feedbackToUser.setText(v.getContext().getString(R.string.EmailAlreadyExists));
                            }
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addUserToDB(final String userId, final String userName) {
        HashMap<String, Object> user = new HashMap<String, Object>()
        {{
            put("name", userName);
            put("userId", userId);
            put("phoneNumber", "");
            put("memberOf", new ArrayList<String>());
        }};
        usersRef = db.collection("users").document(userId);
        usersRef.set(user);
    }

    public void goToGroupDisplayScreen(String currentUserId) {
        final Intent goToGroupsScreen = new Intent(getApplicationContext(), GroupsDisplayActivity.class);
        goToGroupsScreen.putExtra("userId", currentUserId);
        startActivityForResult(goToGroupsScreen, 1);
    }
}
