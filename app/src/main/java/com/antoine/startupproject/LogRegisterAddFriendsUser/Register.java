package com.antoine.startupproject.LogRegisterAddFriendsUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Antoine on 28/10/2015.
 */
public class Register extends Activity implements View.OnClickListener {

    Button bRegister;
    EditText etMail, etUsername, etPassword, etPassword2;
    //EditText etAge, etPhone;
    CircleImageView choosePicture;
    Bitmap image;
    SavedToPreferences userLocalStore;

    private static final int RESULT_LOAD_IMAGE = 2;
    private static final int PHOTO_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etMail = (EditText) findViewById(R.id.etMail);
        //etAge = (EditText) findViewById(R.id.etAge);
        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        //etPhone = (EditText) findViewById(R.id.etPhone);
        etPassword2 = (EditText) findViewById(R.id.etPassword2);

        choosePicture = (CircleImageView) findViewById(R.id.choosePicture);

        bRegister = (Button) findViewById(R.id.bRegister);

        userLocalStore = new SavedToPreferences(this);


        image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_person_black_48dp);
        choosePicture.setImageBitmap(image);

        bRegister.setOnClickListener(this);
        choosePicture.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bRegister:

                String mailBeforeTrim = etMail.getText().toString();
                String mail = mailBeforeTrim.trim();
                String usernameBeforeTrim = etUsername.getText().toString();
                String username = usernameBeforeTrim.trim();
                String passwordBeforeTrim = etPassword.getText().toString();
                String password = passwordBeforeTrim.trim();
                String password2BeforeTrim = etPassword2.getText().toString();
                String password2 = password2BeforeTrim.trim();

                int age = 1;
                int userphone = 1;

                // int age = Integer.parseInt(etAge.getText().toString());
                //int userphone = Integer.parseInt(etPhone.getText().toString());

                image = ((BitmapDrawable) choosePicture.getDrawable()).getBitmap();

                 if (image.getHeight()>512 || image.getWidth()>384){

                     image = Bitmap.createScaledBitmap(image, 512, 384, true);
                 }


                if (mail.equals("") || username.equals("") || password.equals("") || password2.equals("")) {

                    Toast.makeText(getApplicationContext(), "One or more fields are empty !", Toast.LENGTH_SHORT).show();

                } else {

                    if (password.length() > 5) {

                        if (password.equals(password2)) {
                            if (image != null) {


                                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                                image.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                                String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


                                User user = new User(0,username, age, mail, password, userphone, encodedImage);

                                registerUser(user);
                            }else{
                                Toast.makeText(getApplicationContext(), "Choose a picture !", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Password does not match !", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Password should be minimum 6 characters", Toast.LENGTH_SHORT).show();
                    }

                }


                break;

            case R.id.choosePicture:

                final CharSequence[] options = {"Prendre une photo", "Ouvrir la galerie", "Annuler"};
                final AlertDialog.Builder myAlert = new AlertDialog.Builder(Register.this);
                myAlert.setTitle("Choisissez une option");
                myAlert.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {

                        if (options[choice] == "Prendre une photo") {
                            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (i.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(i, PHOTO_CODE);
                            }

                        } else if (options[choice] == "Ouvrir la galerie") {

                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);

                        } else if (options[choice] == "Annuler") {
                            dialog.dismiss();
                        }
                    }
                });
                myAlert.create();
                myAlert.show();

                break;
        }
    }


    public void registerUser(User user) {
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.storeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {



                if(returnedUser == null){

                    Toast.makeText(getApplicationContext(), "Can't access server !!", Toast.LENGTH_SHORT).show();

                }else {
                    String InUse = returnedUser.dataToChange;
                    if (InUse.equals("UserMailInUse")) {

                        Toast.makeText(getApplicationContext(), "E-mail address already in use !", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(getApplicationContext(), "Profil created successfully !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Register.this, Login.class));

                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PHOTO_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle;
                    bundle = data.getExtras();
                    Bitmap BMP;
                    BMP = (Bitmap) bundle.get("data");
                    choosePicture.setImageBitmap(BMP);
                }

            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    choosePicture.setImageURI(selectedImage);



                }
        }

    }
}
