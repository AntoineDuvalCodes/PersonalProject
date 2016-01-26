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
 * Created by Antoine on 10/12/2015.
 */
public class ChangeUsernameActivity extends ActionBarActivity {


    private Toolbar toolbar;
    private SavedToPreferences savedToPreferences;
    private EditText etChangeUsername;
    private User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);


        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        savedToPreferences = new SavedToPreferences(this);

        etChangeUsername = (EditText) findViewById(R.id.etChangeUsername);
        user = savedToPreferences.getLoggedInUser();
        etChangeUsername.setText(user.username);

    }

    public void changeUsername(View view){

        String newUsername = etChangeUsername.getText().toString().trim();

        if (newUsername.length() > 2) {

            User userToChange = new User(user.user_id, user.username, user.age, user.mail, user.password, user.userphone, user.encodedimage, newUsername, "changeUsername");

            ChangeUsername(userToChange);
        }else{

            Toast.makeText(getApplicationContext(), "Please, enter a valid username",Toast.LENGTH_SHORT).show();

        }
    }


    private void ChangeUsername(User user){
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.changeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {

                    Toast.makeText(getApplicationContext(), "Serveur inaccessible !", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplicationContext(),"Username successfully changed !", Toast.LENGTH_SHORT).show();
                    savedToPreferences.storeUserData(returnedUser);

                    startActivity(new Intent(ChangeUsernameActivity.this, Settings.class));

                }
            }
        });
    }
}
