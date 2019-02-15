package com.example.meetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private CharSequence userName = "";
    private CharSequence userEmail = "";
    private CharSequence userPassword1 = "";
    private CharSequence userPassword2 = "";
    private TextView feedbackToUser;
    private Button registerBtn;
    private String EMPTY = "";
    private final String emailRegex = "^(.+)@(.+).(.+)$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    // It may seem that there is code duplication in the next 4 functions.
    // These functions use the global variables userName, userEmail, userPassword1 and userPassword2.
    // These variables are accessed from within an inner scope - for the the "onTextChanged"
    // function - and therefore must need to be final if declared locally.
    // We cannot make these variables final, because they hold the current input of
    // the user (which is not final...).
    // Therefore we had to re-write this short code segment 4 times, for each field separately. In
    // each function we addressed a different global variable.

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
        EditText password1EditText = findViewById(R.id.RegisterPassword1Input);
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
    }

    private void handleUserPassword2() {
        EditText password2EditText = findViewById(R.id.RegisterPassword2Input);
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
                }else {
                    String userId = writeToDB();
                    handleRegisterClick(userId, userName.toString());
                }
            }
        });
    }

    String writeToDB(){
        String name = userName.toString();
        String email = userEmail.toString();
        String password = userPassword1.toString();

        // TODO: OREN, WRITE HERE TO DB AND RETURN THE ID

        String id = "Q6vPMTUMQZe9IS9gQjWzmXSjPB22";
        userName = "oren"; //TODO: REMOVE THIS
        return id;
    }

    private void handleRegisterClick(String id, String name){
        Intent goToGroupsScreen = new Intent(getApplicationContext(), GroupsDisplayActivity.class);
        goToGroupsScreen.putExtra("USER_ID", id);
        goToGroupsScreen.putExtra("USER_NAME", name);
        startActivityForResult(goToGroupsScreen, 1);
    }

}
