package com.example.meetapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static long back_pressed;
    private CharSequence userPassword = "";
    private CharSequence userEmail = "";
    private TextView feedbackToUser;
    private EditText userPasswordInput;
    private EditText userEmailInput;
    private FirebaseAuth mAuth;
    private final String emailRegex = "^(.+)@(.+)\\.(.+)$";
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goToGroupDisplayScreen(currentUser.getUid());
        } else {
            setContentView(R.layout.activity_login);
            handleUserInputOfEmailAndPassword();
            handleNewSignUp();
        }
    }

    void handleUserInputOfEmailAndPassword(){
        final Button loginBtn = findViewById(R.id.loginBtn);
        feedbackToUser = findViewById(R.id.InvalidEmailOrPassword);
        feedbackToUser.setText("");
        userPasswordInput = findViewById(R.id.enterPasswordInput);
        userEmailInput = findViewById(R.id.enterEmailInput);
        handleEmailInput(loginBtn);
        handlePasswordInput(loginBtn);
    }

    private void handleEmailInput(final Button loginBtn) {
        userEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    userEmail = "";
                    loginBtn.setBackgroundResource(R.drawable.disabled_button_background);
                } else {
                    handleChangeInEmailInput(s, loginBtn);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        verifyValidEmailInput();
    }

    private void verifyValidEmailInput() {
        userEmailInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !checkEmailValidation()){
                    userEmailInput.setError(getString(R.string.InvalidEmailAddress));
                }
            }
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
                } else {
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
        if(checkEmailValidation() && !userPassword.toString().isEmpty()){
            logIn(userEmail.toString(), userPassword.toString());
        }
    }

    boolean checkEmailValidation(){
        return  (!userEmail.toString().isEmpty() && userEmail.toString().matches(emailRegex));
    }

    public void goToGroupDisplayScreen(String currentUserId) {
        final Intent goToGroupsScreen = new Intent(getApplicationContext(), GroupsDisplayActivity.class);
        goToGroupsScreen.putExtra("userId", currentUserId);
        startActivityForResult(goToGroupsScreen, 1);
    }

    private void logIn(String userEmail, String userPassword){
        final AppCompatActivity activityRef = this;
        hideKeyboard();
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            goToGroupDisplayScreen(user.getUid());
                        } else {
                            handleWrongEmailOrPassword(activityRef);
                        }
                    }
                });
    }

    private void handleWrongEmailOrPassword(AppCompatActivity activityRef) {
        feedbackToUser.setText(activityRef.getString(R.string.InvalidEmailOrPassword));
        userPasswordInput.setText("");
        userPasswordInput.setSelectAllOnFocus(true);
        popKeyboardUp();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void popKeyboardUp() {
        inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(userPasswordInput.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
        userPasswordInput.requestFocus();
    }

    private void handleNewSignUp(){
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
