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
import com.antoine.startupproject.Settings;

/**
 * Created by Antoine on 09/12/2015.
 */
public class ConfirmChangePassword extends ActionBarActivity {

    private Toolbar toolbar;
    private EditText etNewPassword, etConfirmNewPassword;
    private SavedToPreferences savedToPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_change_password);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        savedToPreferences = new SavedToPreferences(this);


        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        etConfirmNewPassword = (EditText) findViewById(R.id.etConfirmNewPassword);


    }

    public void confirmChangePassword(View view){

        String password1 = etNewPassword.getText().toString().trim();
        String password2 = etConfirmNewPassword.getText().toString().trim();

        User user = savedToPreferences.getLoggedInUser();

        if(password1.length() > 5) {

            if (password1.equals(password2)) {

                User userToChange = new User(user.user_id, user.username, user.age, user.mail, user.password, user.userphone, user.encodedimage, password2, "changePassword");

                changePassword(userToChange);
            } else {

                Toast.makeText(getApplicationContext(), "Password does not match", Toast.LENGTH_SHORT).show();

            }
        }else{

            Toast.makeText(getApplicationContext(), "Password should be minimum 6 characters", Toast.LENGTH_SHORT).show();


        }
    }

    public void changePassword(User user){

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.changeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {

                if (returnedUser == null){

                    Toast.makeText(getApplicationContext(),"Serveur inaccessible !",Toast.LENGTH_SHORT).show();


                }else{

                    Toast.makeText(getApplicationContext(),"Password successfully changed !", Toast.LENGTH_SHORT).show();

                    savedToPreferences.storeUserData(returnedUser);

                    startActivity(new Intent(ConfirmChangePassword.this, Settings.class));

                }


            }
        });

    }



}
