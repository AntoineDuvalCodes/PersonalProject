package com.antoine.startupproject.MapsActivityClass;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Antoine on 23/01/2016.
 */
public class CommentPlaceActivity extends ActionBarActivity implements View.OnClickListener {


    private Toolbar toolbar;
    private ImageView profile_image,ivPhoto;
    private SavedToPreferences savedToPreferences;
    private EditText etCommentPlace;
    private RelativeLayout layPhoto, laySendComment;
    private TextView tvSendComment, tvPhoto;
    private ProgressBar progressBarSendComment;
    private RatingBar ratingBarComment;
    private int Rating;
    private MarkerInfos markerInfos;
    private static final int RESULT_LOAD_IMAGE = 2;
    private static final int PHOTO_CODE = 1;
    public static final int CONNECTION_TIMEOUT = 1000*50;
    public static final String SERVER_ADDRESS = "http://antoine-duval.esy.es/";
    private Bitmap ivPhotoOnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_comment_place);

        Intent intent = getIntent();
        markerInfos = intent.getParcelableExtra("marker");

        tvPhoto = (TextView) findViewById(R.id.tvPhoto);

        savedToPreferences = new SavedToPreferences(this);

        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        profile_image = (ImageView) findViewById(R.id.profile_image);

        tvSendComment = (TextView) findViewById(R.id.tvSendComment);

        ratingBarComment = (RatingBar)findViewById(R.id.ratingBarComment);
        ratingBarComment.setNumStars(5);
        ratingBarComment.setRating(0);
        ratingBarComment.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                Rating = (int) rating;
            }
        });

        progressBarSendComment = (ProgressBar) findViewById(R.id.progressBarSendComment);
        progressBarSendComment.setVisibility(View.INVISIBLE);

        byte[] decodedimage = Base64.decode(savedToPreferences.getLoggedInUser().encodedimage, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedimage, 0, decodedimage.length);
        profile_image.setImageBitmap(image);

        etCommentPlace = (EditText) findViewById(R.id.etCommentPlace);
        layPhoto = (RelativeLayout) findViewById(R.id.layPhoto);
        laySendComment = (RelativeLayout) findViewById(R.id.laySendComment);
        laySendComment.setOnClickListener(this);
        layPhoto.setOnClickListener(this);

        ivPhotoOnCreate = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();



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

            case R.id.layPhoto:

                final CharSequence[] options = {"Prendre une photo", "Ouvrir la galerie", "Annuler"};
                final AlertDialog.Builder myAlert = new AlertDialog.Builder(CommentPlaceActivity.this);
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

            case R.id.laySendComment:

                if (etCommentPlace.getText().length() == 0 && Rating == 0){

                    Toast.makeText(getApplicationContext(), "Veuillez entrez du text ou une note", Toast.LENGTH_SHORT).show();

                }else{

                    Bitmap image = ((BitmapDrawable) ivPhoto.getDrawable()).getBitmap();

                    String encodedImage = "none";

                    if (ivPhotoOnCreate != image){

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                        encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    }

                    progressBarSendComment.setVisibility(View.VISIBLE);
                    tvSendComment.setText("");
                    new CommentsThisPlaceAsyncTask(savedToPreferences.getLoggedInUser().user_id, markerInfos.getId(),etCommentPlace.getText().toString(),encodedImage ,Rating).execute();

                }


                break;
        }


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

                    ivPhoto.setImageBitmap(image);
                    tvPhoto.setText("");

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

                    ivPhoto.setImageBitmap(image);
                    tvPhoto.setText("");

                }
        }

    }

    private class CommentsThisPlaceAsyncTask extends AsyncTask<Void, Void, String> {

        private int user_id;
        private String place_id;
        private String text;
        private String image;
        int note;

        public CommentsThisPlaceAsyncTask(int user_id, String place_id, String text, String image, int note){

            this.user_id = user_id;
            this.place_id = place_id;
            this.text = text;
            this.image = image;
            this.note = note;

        }

        @Override
        protected String doInBackground(Void... params) {


            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("user_id", user_id+""));
            dataToSend.add(new BasicNameValuePair("place_id", place_id));
            dataToSend.add(new BasicNameValuePair("text", text));
            dataToSend.add(new BasicNameValuePair("image", image));
            dataToSend.add(new BasicNameValuePair("place_note", note+""));


            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "UploadComments.php");

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

                progressBarSendComment.setVisibility(View.INVISIBLE);
                tvSendComment.setText("ENVOYER");

                Toast.makeText(getApplicationContext(), "Serveur inaccessible", Toast.LENGTH_SHORT).show();

            }else{

                Toast.makeText(getApplicationContext(), "Commentaire envoyé avec succes", Toast.LENGTH_SHORT).show();
                finish();

            }

        }
    }
}
