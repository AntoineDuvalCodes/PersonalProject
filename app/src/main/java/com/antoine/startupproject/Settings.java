package com.antoine.startupproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.antoine.startupproject.LogRegisterAddFriendsUser.ChangePasswordActivity;
import com.antoine.startupproject.LogRegisterAddFriendsUser.ChangePictureActivity;
import com.antoine.startupproject.LogRegisterAddFriendsUser.ChangeUsernameActivity;
import com.antoine.startupproject.LogRegisterAddFriendsUser.GetUserCallback;
import com.antoine.startupproject.LogRegisterAddFriendsUser.Login;
import com.antoine.startupproject.LogRegisterAddFriendsUser.ServerRequests;
import com.antoine.startupproject.LogRegisterAddFriendsUser.User;


/**
 * Created by Antoine on 02/12/2015.
 */
public class Settings extends ActionBarActivity{

    private Toolbar toolbar;
    private SavedToPreferences savedToPreferences;
    private TextView tvMail, tvUsername, tvPassword, tvUserphone, tvAge;
    private ImageView profilPicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        savedToPreferences = new SavedToPreferences(this);

        tvMail = (TextView) findViewById(R.id.tvMail);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        tvPassword = (TextView) findViewById(R.id.tvPassword);
        tvUserphone = (TextView) findViewById(R.id.tvUserphone);
        tvAge = (TextView) findViewById(R.id.tvAge);

        profilPicture = (ImageView) findViewById(R.id.profilPicture);

        displayUserDetails();

    }

    private void displayUserDetails(){
        User user = savedToPreferences.getLoggedInUser();


        tvUsername.setText(user.username);
        tvMail.setText(user.mail);
        tvAge.setText(user.user_id + "");
        tvUserphone.setText("0" + user.userphone +"");

        tvPassword.setText(user.password);

        byte[] decodedimage = Base64.decode(user.encodedimage, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedimage, 0, decodedimage.length);

        profilPicture.setImageBitmap(image);


    }


    public void itemClikedInSettings(View v){

        switch(v.getId()){


            case R.id.layNomUtilisateur:


                startActivity(new Intent(Settings.this, ChangeUsernameActivity.class));


                break;

            case R.id.layMail:



                break;

            case R.id.layPassword:


                startActivity(new Intent(Settings.this, ChangePasswordActivity.class));

                break;

            case R.id.layAge:



                break;

            case R.id.layPhone:



                break;

            case R.id. layPhoto:

                startActivity(new Intent(Settings.this, ChangePictureActivity.class));

                break;

            case R.id.layDeconnexion:

                int user_id = savedToPreferences.getLoggedInUser().user_id;
                User userLogout = new User(user_id);

                logoutUser(userLogout);



                break;

        }
    }


    private void logoutUser(User userToLogout){

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.DisconnectInBackground(userToLogout, new GetUserCallback() {
            @Override
            public void done(User user_logout) {

                if (user_logout == null) {

                    Toast.makeText(getApplicationContext(), "Can't access server", Toast.LENGTH_SHORT).show();


                } else if (user_logout.response == 1) {


                    logoutUserConfirmation();

                } else {

                    Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();


                }

            }
        });
    }


    private void logoutUserConfirmation(){


        savedToPreferences.clearUserData();
        savedToPreferences.setUserLoggedIn(false);
        savedToPreferences.storeDrawerAsSaw(true);
        finish();
        startActivity(new Intent(this, Login.class));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                this.finish();

                return true;

            default:

                return super.onOptionsItemSelected(item);

        }

    }
}








