package com.example.meetapp;

import android.content.Context;
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
        setContentView(R.layout.activity_login);
        handleUserInputOfEmailAndPassword();
    }


    void handleUserInputOfEmailAndPassword(){
        final Button loginBtn = findViewById(R.id.loginBtn);
        feedbackToUser = findViewById(R.id.InvalidEmailOrPassword);
        feedbackToUser.setText("");

        EditText userEmailInput = findViewById(R.id.enterEmailInput);
        userPasswordInput = findViewById(R.id.enterPasswordInput);

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
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });


        userPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    loginBtn.setBackgroundResource(R.drawable.disabled_button_background);
                    userPassword = "";
                }else {
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
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    void handleLoginClick(){
        if(!userEmail.equals("") && !userPassword.equals("")){
            checkIfUserExists();
        }
    }


    void checkIfUserExists(){

        String id = EmailAndPasswordExistInDB(userEmail.toString(), userPassword.toString());
        if(id.equals(DOES_NOT_EXIST)){
            feedbackToUser.setText(this.getString(R.string.InvalidEmailOrPassword));
            userPasswordInput.setText("");
            userPasswordInput.setSelectAllOnFocus(true);
            popKeyboardUp();

        }
        else{
            Toast.makeText(getBaseContext(), "user exists!", Toast.LENGTH_SHORT).show();
            // get id
            // put extra - id
            // go to group activity screen



            Intent goToGroupsScreen = new Intent(getApplicationContext(), GroupsDisplayActivity.class);
            goToGroupsScreen.putExtra("USER_ID", id);
            startActivityForResult(goToGroupsScreen, 1);
        }


    }

    private void popKeyboardUp() {
        InputMethodManager inputMethodManager =
                (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null){
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        }
    }


    private String EmailAndPasswordExistInDB( String userEmail, String userPassword){
        // TODO: OREN, CHECK HERE IF THE EMAIL AND PASSWORD EXIST.
        // IF THEY DO - RETURN THE ID (as an int).
        // if not - return constant DOES_NOT_EXIST.
//        if(  EXISTS){
//            return id;
//        } else{
//            return DOES_NOT_EXIST;
//        }
//        return "Q6vPMTUMQZe9IS9gQjWzmXSjPB22";
        return DOES_NOT_EXIST;

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
