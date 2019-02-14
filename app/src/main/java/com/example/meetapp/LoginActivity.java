package com.example.meetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private final String DOES_NOT_EXIST = "";
    private static long back_pressed;
    private CharSequence userPassword = "";
    private CharSequence userEmail = "";
    private TextView feedbackToUser;
    private EditText userPasswordInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIfUserIsAlreadyRegistered();
        setContentView(R.layout.activity_login);
        handleUserInputOfEmailAndPassword();
        handleNewSignIn();
    }

    private void checkIfUserIsAlreadyRegistered(){
        // TODO:    OREN, IMPLEMENT HERE.
        // TODO:    CHECK IF THE USER IS ALREADY REGISTETRD, AND PUT NAME AND ID IN RELEVANT VARIABLES
        if("user is already registered".equals(DOES_NOT_EXIST)){
            String id = "stammmmm";
            String name = "stammm2";
            goToGroupDisplayScreen(id, name);
        }
    }


    void handleUserInputOfEmailAndPassword(){
        final Button loginBtn = findViewById(R.id.loginBtn);
        feedbackToUser = findViewById(R.id.InvalidEmailOrPassword);
        feedbackToUser.setText("");

        userPasswordInput = findViewById(R.id.enterPasswordInput);
        handleEmailInput(loginBtn);
        handlePasswordInput(loginBtn);
    }


    private void handleEmailInput(final Button loginBtn) {
        EditText userEmailInput = findViewById(R.id.enterEmailInput);
        userEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    userEmail = "";
                    loginBtn.setBackgroundResource(R.drawable.disabled_button_background);
                }
                else {
                    handleChangeInEmailInput(s, loginBtn);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleChangeInEmailInput(CharSequence s, Button loginBtn) {
        userEmail = s.toString();
        if(!userPassword.equals("")){
            loginBtn.setBackgroundResource(R.drawable.green_round_background);
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginClick();
            }
        });
    }


    private void handlePasswordInput(final Button loginBtn) {
        userPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    loginBtn.setBackgroundResource(R.drawable.disabled_button_background);
                    userPassword = "";
                }else {
                    handleChangeInPasswordInput(s, loginBtn);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleChangeInPasswordInput(CharSequence s, Button loginBtn) {
        feedbackToUser.setText("");
        userPassword = s.toString();
        if(!userEmail.equals("")){
            loginBtn.setBackgroundResource(R.drawable.green_round_background);
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoginClick();
            }
        });
    }


    void handleLoginClick(){
        if(!userEmail.equals("") && !userPassword.equals("")){
            checkIfUserExists();
        }
    }


    void checkIfUserExists(){
        String[] idAndName = doEmailAndPasswordExistInDB(userEmail.toString(), userPassword.toString());
        String id = idAndName[0];
        String name = idAndName[1];
        if(id.equals(DOES_NOT_EXIST)){
            feedbackToUser.setText(this.getString(R.string.InvalidEmailOrPassword));
            userPasswordInput.setText("");
            userPasswordInput.setSelectAllOnFocus(true);
            popKeyboardUp();
        }
        else{
            goToGroupDisplayScreen(id, name);
        }
    }

    public void goToGroupDisplayScreen(String id, String name) {
        Intent goToGroupsScreen = new Intent(getApplicationContext(), GroupsDisplayActivity.class);
        goToGroupsScreen.putExtra("USER_ID", id);
        goToGroupsScreen.putExtra("USER_NAME", name);
        startActivityForResult(goToGroupsScreen, 1);
    }


    private String[] doEmailAndPasswordExistInDB( String userEmail, String userPassword){
        // TODO:     OREN, CHECK HERE IF THE EMAIL AND PASSWORD EXIST.
        // TODO:     IF THEY DO - RETURN THE ID (as an int).
        // TODO:     if not - return constant DOES_NOT_EXIST.

//        if(  EXISTS){
//            return id;
//        } else{
//            return DOES_NOT_EXIST;
//        }
        return new String[]{"Q6vPMTUMQZe9IS9gQjWzmXSjPB22", "oren"};
//        return new String[]{DOES_NOT_EXIST, "noName"};
    }


    private void popKeyboardUp() {
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(userPasswordInput.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
        userPasswordInput.requestFocus();
    }


    private void handleNewSignIn(){
        final Button signUpBtn = findViewById(R.id.registerNow);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSignUpScreen = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(goToSignUpScreen, 1);
            }
        });
    }


    @Override
    public void onBackPressed()
    {
        final int EXIT_DELAY = 2000;
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
