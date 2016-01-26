package com.antoine.startupproject.LogRegisterAddFriendsUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.antoine.startupproject.MapsActivityClass.MapsActivity;
import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;
//import com.example.antoine.myapplication.MapsActivity;
//import com.example.antoine.myapplication.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antoine on 28/10/2015.
 */
public class Login extends Activity implements View.OnClickListener {

    Button bLogin;
    EditText etMail, etPassword;
    TextView tvregisterLink, tvForgotPass;
    GoogleCloudMessaging gcm;
    SavedToPreferences userLocalStore;
    String phoneID;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etMail = (EditText) findViewById(R.id.etMail);
        etPassword = (EditText) findViewById(R.id.etPassword);

        bLogin = (Button) findViewById(R.id.blogin);

        tvregisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        //tvForgotPass = (TextView)findViewById(R.id.tvForgotPass);

        tvregisterLink.setOnClickListener(this);
        bLogin.setOnClickListener(this);
        //tvForgotPass.setOnClickListener(this);

        userLocalStore = new SavedToPreferences(this);

    }


    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.blogin:

                gcm = GoogleCloudMessaging.getInstance(this);
                registerInBackground();


                break;

            case R.id.tvRegisterLink:

                startActivity(new Intent(this, Register.class));

                break;

            // case R.id.tvForgotPass:

            // startActivity(new Intent(this, ForgotPassword.class));
            // break;
        }
    }

    private void authenticate(User user){
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.fetchUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                } else {
                    logUserIn(returnedUser);
                }
            }
        });
    }




    private void showErrorMessage(){

        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(Login.this);
        dialogbuilder.setMessage("Incorrect user details");
        dialogbuilder.setPositiveButton("Ok", null);
        dialogbuilder.show();

        String passwordReset = "";
        etPassword.setText(passwordReset);

    }

    private void logUserIn(User returneduser){

        User setUserInDB = new User (returneduser.user_id,returneduser.username,returneduser.age,returneduser.mail,returneduser.password,returneduser.userphone,returneduser.encodedimage);
        userLocalStore.storeUserData(setUserInDB);
        userLocalStore.storeUserFriend(returneduser.dataToChange);
        userLocalStore.setNewFriendRequest(returneduser.tag);
        userLocalStore.setUserLoggedIn(true);
        startActivity(new Intent(Login.this, MapsActivity.class));



    }


    @Override
    public void onBackPressed() {

    }

    public String registerGCM(){

        gcm = GoogleCloudMessaging.getInstance(this);

        if (TextUtils.isEmpty(phoneID)){

            registerInBackground();

            Log.d("RegisterActivity", "Successfully register with GCM server " + phoneID);

        }else

            Toast.makeText(getApplicationContext(), "RegId already available: " + phoneID, Toast.LENGTH_SHORT).show();

        return phoneID;
    }



    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    phoneID = gcm.register("880296172737");
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + phoneID);
                    msg = "Device registered, registration ID=" + phoneID;


                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

                String mailBeforeTrim = etMail.getText().toString();
                String mail = mailBeforeTrim.trim();
                String passwordBeforeTrim = etPassword.getText().toString();
                String password = passwordBeforeTrim.trim();

                User user = new User(mail, password, phoneID);

                authenticate(user);



            }
        }.execute(null, null, null);

    }



}
