package com.antoine.startupproject.MapsActivityClass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antoine.startupproject.MyPlacesActivity.MarkerInfos;
import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Antoine on 23/01/2016.
 */
public class TakePictureOfPlaceActivity extends ActionBarActivity implements View.OnClickListener{

    private Toolbar toolbar;
    private ProgressBar progressBarSendPhoto;
    private RelativeLayout layTakePhoto, laySendPhoto;
    private static final int RESULT_LOAD_IMAGE = 2;
    private static final int PHOTO_CODE = 1;
    public static final int CONNECTION_TIMEOUT = 1000*50;
    public static final String SERVER_ADDRESS = "http://antoine-duval.esy.es/";
    private ImageView ivTakePictureOfThisPlace;
    private Bitmap bitmap;
    private SavedToPreferences savedToPreferences;
    private MarkerInfos markerInfos;
    private TextView tvSendPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_take_picture_of_place);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        markerInfos = intent.getParcelableExtra("marker");

        tvSendPhoto = (TextView) findViewById(R.id.tvSendPhoto);

        progressBarSendPhoto = (ProgressBar) findViewById(R.id.progressBarSendPhoto);
        progressBarSendPhoto.setVisibility(View.INVISIBLE);

        savedToPreferences = new SavedToPreferences(this);

        ivTakePictureOfThisPlace = (ImageView) findViewById(R.id.ivTakePictureOfThisPlace);
        bitmap = ((BitmapDrawable) ivTakePictureOfThisPlace.getDrawable()).getBitmap();

        layTakePhoto = (RelativeLayout) findViewById(R.id.layTakePhoto);
        laySendPhoto = (RelativeLayout) findViewById(R.id.laySendPhoto);

        layTakePhoto.setOnClickListener(this);
        laySendPhoto.setOnClickListener(this);

        setDialogueBox();
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

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.layTakePhoto:

                setDialogueBox();

                break;

            case R.id.laySendPhoto:

                Bitmap image = ((BitmapDrawable) ivTakePictureOfThisPlace.getDrawable()).getBitmap();


                if (bitmap != image){

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                     String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    progressBarSendPhoto.setVisibility(View.VISIBLE);
                    tvSendPhoto.setText("");
                    new TakePhotoOfPlaceAsyncTask(savedToPreferences.getLoggedInUser().user_id,markerInfos.getId(), encodedImage).execute();

                }else{

                    Toast.makeText(getApplicationContext(), "Choisissez une photo", Toast.LENGTH_SHORT).show();

                }



                break;

        }

    }

    private void setDialogueBox (){

        final CharSequence[] options = {"Prendre une photo", "Ouvrir la galerie", "Annuler"};
        final AlertDialog.Builder myAlert = new AlertDialog.Builder(TakePictureOfPlaceActivity.this);
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

                    Bitmap image = null;

                    if (BMP.getHeight()>512 || BMP.getWidth()>384){

                        image = Bitmap.createScaledBitmap(BMP, 512, 384, true);
                    }

                    ivTakePictureOfThisPlace.setImageBitmap(image);

                }

            case RESULT_LOAD_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();

                    Bitmap bitmap = null;
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Bitmap image = null;

                    if (bitmap.getHeight()>512 || bitmap.getWidth()>384){

                        image = Bitmap.createScaledBitmap(bitmap, 512, 384, true);
                    }

                    ivTakePictureOfThisPlace.setImageBitmap(image);

                }
        }

    }

    public class TakePhotoOfPlaceAsyncTask extends AsyncTask<Void, Void, String> {


        private int user_id;
        private String place_id,encodedImage;


        public TakePhotoOfPlaceAsyncTask(int user_id, String place_id, String encodedImage){

            this.user_id = user_id;
            this.place_id = place_id;
            this.encodedImage = encodedImage;

        }



        @Override
        protected String doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("user_id", user_id+""));
            dataToSend.add(new BasicNameValuePair("place_id", place_id));
            dataToSend.add(new BasicNameValuePair("photo", encodedImage));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "UploadPhoto.php");

            String response = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);

                JSONObject jObject = new JSONObject(result);

                response = jObject.getString("response");


            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if ( s == null){

                progressBarSendPhoto.setVisibility(View.INVISIBLE);
                tvSendPhoto.setText("ENVOYER");

                Toast.makeText(getApplicationContext(), "Serveur inaccessible", Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(getApplicationContext(), "Commentaire envoyé avec succes", Toast.LENGTH_SHORT).show();
                finish();

            }
        }
    }


}
