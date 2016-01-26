package com.antoine.startupproject.MapsActivityClass;


import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.antoine.startupproject.LogRegisterAddFriendsUser.Friend;
import com.antoine.startupproject.MyPlacesActivity.MarkerInfos;
import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class PlacesDetailsActivity extends ActionBarActivity implements View.OnClickListener {


    private Location previousLocation;
    private RecyclerView recyclerView;
    private static final String GOOGLE_PLACE_DETAIL = "https://maps.googleapis.com/maps/api/place/details/json?placeid=";
    private static final String PLACES_API_KEY = "AIzaSyBDa-0x-M16wB4J_1-hNOH66JvU1QJeCX4";
    private static final String GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=800&photoreference=";
    public static final int CONNECTION_TIMEOUT = 1000*50;
    public static final String SERVER_ADDRESS = "http://antoine-duval.esy.es/";
    private MarkerInfos markerInfos;
    private float distanceArrondie;
    private ProgressBar progressBarMarkerInfo,progressBarLike;
    private Toolbar toolbar;
    private ImageView ivLikeThisPlace,ivCommentThisPlace, ivTakePictureOfThisPlace;
    private SavedToPreferences savedToPreferences;
    private ArrayList<Commentaire> commentaires;
    private ArrayList<String> photo_path;
    private int nbLike;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);


        progressBarMarkerInfo = (ProgressBar) findViewById(R.id.progressBarMarkerInfo);

        progressBarLike = (ProgressBar) findViewById(R.id.progressBarLike);
        progressBarLike.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        markerInfos = intent.getParcelableExtra("marker");
        previousLocation = intent.getParcelableExtra("location");

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(markerInfos.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        savedToPreferences = new SavedToPreferences(getApplicationContext());

        ivLikeThisPlace = (ImageView) findViewById(R.id.ivLikeThisPlace);
        ivCommentThisPlace = (ImageView) findViewById(R.id.ivCommentThisPlace);
        ivTakePictureOfThisPlace = (ImageView) findViewById(R.id.ivTakePictureOfThisPlace);

        ivLikeThisPlace.setOnClickListener(this);
        ivCommentThisPlace.setOnClickListener(this);
        ivTakePictureOfThisPlace.setOnClickListener(this);



        setViewInfos(markerInfos);

    }

    private void setViewInfos(MarkerInfos markerInfos) {

        String placeTest = savedToPreferences.getPlacesILiked();

        try {
            JSONArray jsonArray = new JSONArray(placeTest);

            for( int i = 0; i<jsonArray.length();i++){

                JSONObject place = jsonArray.getJSONObject(i);

                if (place.getString("name").equals(markerInfos.getName())){

                    ivLikeThisPlace.setImageResource(R.drawable.ic_thumb_up_white_24dp);

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


        Location locMarker = new Location(LocationManager.GPS_PROVIDER);
        locMarker.setLatitude(markerInfos.getLat());
        locMarker.setLongitude(markerInfos.getLng());

        float distance = previousLocation.distanceTo(locMarker);
        distanceArrondie = (int) distance;
        distanceArrondie = (int) distanceArrondie / 100;
        distanceArrondie *= 100;

        getDataFromPersonalDB(markerInfos.getId());


    }


    private void getDataFromPersonalDB(String place_id){

        progressBarMarkerInfo.setVisibility(View.VISIBLE);


        SetPointOnMap setPointOnMap = new SetPointOnMap(this);
        setPointOnMap.getPlaceDataFromPersonalDB(place_id, new GetLocationCallback() {
            @Override
            public void done(String result) {

                castDataFromPersonalDB(result);

            }
        });


    }

    private void castDataFromPersonalDB(String result){

        commentaires = new ArrayList<>();
        photo_path = new ArrayList<>();


        try {
            JSONObject jObject = new JSONObject(result);

            nbLike = jObject.getInt("likes");

            JSONArray comments = jObject.getJSONArray("commentaire");

            for (int i = 0 ; i < comments.length(); i++){

                JSONObject comment = comments.getJSONObject(i);

                String author = comment.getString("from_user");
                String text = comment.getString("text");
                int note = comment.getInt("note");
                String mail = comment.getString("mail");
                String profile_picture_url = SERVER_ADDRESS + "pictures/" + mail + ".JPG";
                String path = comment.getString("photo_path");
                String photoPath = SERVER_ADDRESS + path;
                Commentaire c = new Commentaire(profile_picture_url ,author, text, note, photoPath);

                commentaires.add(c);
            }


            JSONArray photos = jObject.getJSONArray("photos");

            for (int i = 0 ; i < photos.length(); i++){

                String path = photos.getString(i);

                String full_path = SERVER_ADDRESS + path;

                photo_path.add(full_path);

                Log.e("FULL_PATH", full_path);
            }





        } catch (JSONException e) {
            e.printStackTrace();
        }


        String url = GOOGLE_PLACE_DETAIL + markerInfos.getId() + "&key=" + PLACES_API_KEY;
        placeDetails(url);

    }




    private void placeDetails(String url) {


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        SetPointOnMap getPlaceDetails = new SetPointOnMap(this);
        getPlaceDetails.getPlaceDetailFromGooglePlace(url, new GetLocationCallback() {
            @Override
            public void done(String placesFound) {

                castNewDataAboutMarker(placesFound);

            }
        });

    }


    private void castNewDataAboutMarker(String newData) {


        JSONArray jsonArrayPhotos;
        String phoneNumber = "non renseigné";

        if (newData != null) {

            try {

                JSONObject jObject = new JSONObject(newData);
                JSONObject jsonObjectResult = jObject.getJSONObject("result");

                try {

                    jsonArrayPhotos = jsonObjectResult.getJSONArray("photos");

                    for (int i = 0; i < jsonArrayPhotos.length(); i++) {

                        try {

                            String path = jsonArrayPhotos.getJSONObject(i).getString("photo_reference");
                            String pathUrl = GOOGLE_PHOTO_URL + path+ "&key="+ PLACES_API_KEY;
                            photo_path.add(pathUrl);

                        } catch (JSONException e) {


                        }
                    }

                } catch (JSONException e) {


                }

                try {
                    phoneNumber = jsonObjectResult.getString("international_phone_number");

                } catch (JSONException e) {
                    phoneNumber= "none";
                }

                try {

                    JSONArray reviews = jsonObjectResult.getJSONArray("reviews");

                    for (int z = 0; z < reviews.length(); z++) {

                        JSONObject avis = reviews.getJSONObject(z);

                        String author_name = avis.getString("author_name");
                        int note = avis.getInt("rating");
                        String text = avis.getString("text");

                        Commentaire commentaire = new Commentaire(null, author_name, text, note);
                        commentaires.add(commentaire);

                    }


                } catch (JSONException e) {


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (photo_path.size() == 0){

            photo_path.add(null);
        }


        if( commentaires.size()==0){

            commentaires.add(new Commentaire("","","",0));
        }
        progressBarMarkerInfo.setVisibility(View.GONE);

        DataToSendToRecyclerView dataToSendToRecyclerView = new DataToSendToRecyclerView(markerInfos.getIsOpen(), markerInfos.getVicinity(), distanceArrondie, markerInfos.getCategorie(),photo_path, phoneNumber, nbLike);

        recyclerView = (RecyclerView) findViewById(R.id.listOfCommentaires);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        RecyclerViewCommentsAdapter adapter = new RecyclerViewCommentsAdapter(commentaires,getApplicationContext(), dataToSendToRecyclerView);
        recyclerView.setAdapter(adapter);



    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.ivLikeThisPlace:

                progressBarLike.setVisibility(View.VISIBLE);
                ivLikeThisPlace.setVisibility(View.INVISIBLE);
                ivLikeThisPlace.setClickable(false);

                new LikeThisPlaceAsyncTask(savedToPreferences.getLoggedInUser().user_id, markerInfos.getId()).execute();

                break;

            case R.id.ivCommentThisPlace:

                Intent intent = new Intent(PlacesDetailsActivity.this, CommentPlaceActivity.class);
                intent.putExtra("marker", markerInfos);
                startActivity(intent);


                break;

            case R.id.ivTakePictureOfThisPlace:

                Intent i = new Intent(PlacesDetailsActivity.this, TakePictureOfPlaceActivity.class);
                i.putExtra("marker", markerInfos);
                startActivity(i);


                break;

        }

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


    public class DataToSendToRecyclerView{

        public String isOpen;
        public String vicinity;
        public float distanceToPlace;
        public String[] categories;
        public ArrayList<String> photoReferences;
        public String phoneNumber;
        public int nbLike;

        public DataToSendToRecyclerView(String isOpen, String vicinity, float distanceToPlace, String[] categories, ArrayList<String> photoReferences, String phoneNumber, int nbLike){

            this.isOpen = isOpen;
            this.vicinity = vicinity;
            this.distanceToPlace = distanceToPlace;
            this.categories = categories;
            this.photoReferences = photoReferences;
            this.phoneNumber = phoneNumber;
            this.nbLike = nbLike;

        }

    }

    public class LikeThisPlaceAsyncTask extends AsyncTask<Void, Void, int[]>{

        private int user_id;
        private String place_id;

        public LikeThisPlaceAsyncTask(int user_id, String place_id){

            this.user_id = user_id;
            this.place_id = place_id;

        }

        @Override
        protected int[] doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("user_id", user_id+""));
            dataToSend.add(new BasicNameValuePair("place_id", place_id));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "UploadLike.php");


            int[]response = new int [1];
            response[0] = 0;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);

                JSONObject jObject = new JSONObject(result);

                response[0] = jObject.getInt("response");


            } catch (Exception e) {
                e.printStackTrace();
            }



            return response;
        }

        @Override
        protected void onPostExecute(int[] response) {

            if (response[0] == 0) {

                progressBarLike.setVisibility(View.INVISIBLE);
                ivLikeThisPlace.setVisibility(View.VISIBLE);
                ivLikeThisPlace.setClickable(true);

                Toast.makeText(getApplicationContext(), "Serveur inaccessible", Toast.LENGTH_SHORT).show();

            }else if (response[0] == 1){

                progressBarLike.setVisibility(View.INVISIBLE);
                ivLikeThisPlace.setVisibility(View.VISIBLE);
                ivLikeThisPlace.setImageResource(R.drawable.ic_thumb_up_black_24dp);
                ivLikeThisPlace.setClickable(true);

                String placesILiked = savedToPreferences.getPlacesILiked();
                JSONArray jsonArray;
                JSONArray newJsonArray = new JSONArray();

                try {
                    if( placesILiked.length() == 2 || placesILiked.length()==0){

                        jsonArray = new JSONArray();

                    }else{

                        jsonArray = new JSONArray(placesILiked);

                    }

                    for (int i = 0; i < jsonArray.length(); i++){

                        JSONObject place = jsonArray.getJSONObject(i);

                        String place_id = place.getString("place_id");

                        if (!place_id.equals(markerInfos.getId())) {

                            JSONObject filtrePlace = new JSONObject();

                            filtrePlace.put("place_id", place.getString("place_id"));
                            filtrePlace.put("name", place.getString("name"));
                            filtrePlace.put("vicinity", place.getString("vicinity"));
                            filtrePlace.put("latitude", place.getString("latitude"));
                            filtrePlace.put("longitude", place.getString("longitude"));

                            JSONArray categories = place.getJSONArray("categories");
                            JSONArray newCategories = new JSONArray();

                            for (int j = 0 ; j < categories.length() ; j++){

                                String categorie = categories.getString(j);
                                newCategories.put(categorie);

                            }

                            filtrePlace.put("categories", newCategories);
                            newJsonArray.put(filtrePlace);

                        }
                    }

                    savedToPreferences.setPlacesILiked(newJsonArray.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else{

                progressBarLike.setVisibility(View.INVISIBLE);
                ivLikeThisPlace.setVisibility(View.VISIBLE);
                ivLikeThisPlace.setImageResource(R.drawable.ic_thumb_up_white_24dp);
                ivLikeThisPlace.setClickable(true);


                JSONObject jsonObject = new JSONObject();

                try {
                    String placesILiked = savedToPreferences.getPlacesILiked();
                    JSONArray jsonArray;

                    if (placesILiked.length() == 2 || placesILiked.length() == 0){

                        jsonArray = new JSONArray();

                    }else{

                        jsonArray = new JSONArray(placesILiked);

                    }

                    jsonObject.put("place_id", markerInfos.getId());
                    jsonObject.put("name", markerInfos.getName());
                    jsonObject.put("vicinity", markerInfos.getVicinity());
                    jsonObject.put("latitude", markerInfos.getLat());
                    jsonObject.put("longitude", markerInfos.getLng());

                    JSONArray newCategories = new JSONArray();

                    for (int j = 0 ; j <  markerInfos.getCategorie().length ; j++){

                        String categorie = markerInfos.getCategorie()[j];
                        newCategories.put(categorie);

                    }
                    jsonObject.put("categories", newCategories);

                    jsonArray.put(jsonObject);

                    savedToPreferences.setPlacesILiked(jsonArray.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


            super.onPostExecute(response);
        }
    }

}
