package com.antoine.startupproject.LogRegisterAddFriendsUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;

/**
 * Created by Antoine on 09/12/2015.
 */
public class ChangePasswordActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private EditText etPassword;
    private SavedToPreferences savedToPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etPassword = (EditText) findViewById(R.id.etPassword);

        savedToPreferences = new SavedToPreferences(this);

    }


    public void changePassword(View view){

        String password = savedToPreferences.getLoggedInUser().password;
        String checkPassword = etPassword.getText().toString().trim();

        if (password.equals(checkPassword)){

            startActivity(new Intent(ChangePasswordActivity.this, ConfirmChangePassword.class));


        }else{

            Toast.makeText(getApplicationContext(),"Incorrect Password", Toast.LENGTH_SHORT).show();

        }


    }

}
