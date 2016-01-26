package com.antoine.startupproject.LogRegisterAddFriendsUser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;
import com.antoine.startupproject.Settings;

import java.io.ByteArrayOutputStream;

/**
 * Created by Antoine on 15/12/2015.
 */
public class ChangePictureActivity extends ActionBarActivity implements View.OnClickListener {


    private Toolbar toolbar;
    private SavedToPreferences savedToPreferences;
    private ImageView profile_image;
    private static final int RESULT_LOAD_IMAGE = 2;
    private static final int PHOTO_CODE = 1;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_picture);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        savedToPreferences = new SavedToPreferences(this);
        String userPicture = savedToPreferences.getLoggedInUser().encodedimage;


        profile_image = (ImageView) findViewById(R.id.profile_image);

        profile_image.setOnClickListener(this);


        byte[] decodedimage = Base64.decode(userPicture, Base64.DEFAULT);
        image = BitmapFactory.decodeByteArray(decodedimage, 0, decodedimage.length);
        profile_image.setImageBitmap(image);

    }


    public void changeImage( View view){


        image = ((BitmapDrawable) profile_image.getDrawable()).getBitmap();

        if (image.getHeight()>512 || image.getWidth()>384){

            image = Bitmap.createScaledBitmap(image, 512, 384, true);
        }



        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


        User user = savedToPreferences.getLoggedInUser();

        User userNewPicture = new User(user.user_id, user.username, user.age, user.mail, user.password, user.userphone, "", encodedImage, "changePicture");

        changePictureInDB(userNewPicture);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.profile_image:

                chooseImage();

                break;

        }

    }


    private void chooseImage(){

        final CharSequence[] options = {"Prendre une photo", "Ouvrir la galerie", "Annuler"};
        final AlertDialog.Builder myAlert = new AlertDialog.Builder(ChangePictureActivity.this);
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
                    profile_image.setImageBitmap(BMP);
                }

            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    profile_image.setImageURI(selectedImage);
                }
        }
    }


    private void changePictureInDB(User user){

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.changeUserDataInBackground(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {

                if (returnedUser == null){

                    Toast.makeText(getApplicationContext(),"Serveur inaccessible !",Toast.LENGTH_SHORT).show();


                }else{

                    Toast.makeText(getApplicationContext(),"Picture successfully changed !", Toast.LENGTH_SHORT).show();

                    savedToPreferences.storeUserData(returnedUser);

                    startActivity(new Intent(ChangePictureActivity.this, Settings.class));

                }

            }
        });

    }
}
