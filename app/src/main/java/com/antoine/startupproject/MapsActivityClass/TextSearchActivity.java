package com.antoine.startupproject.MapsActivityClass;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.antoine.startupproject.MyPlacesActivity.MarkerInfos;
import com.antoine.startupproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Antoine on 14/01/2016.
 */
public class TextSearchActivity extends ActionBarActivity implements View.OnClickListener{

    private static final String PLACES_API_KEY = "API_KEY"; // your API_KEY
    private static final String TEXT_SEARCH_PLACE_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=";
    private Toolbar toolbar;
    private EditText etTextSearch;
    private ImageView ivSearchWithText;
    private Location previousLocation;
    private RecyclerView recyclerView;
    private ArrayList<MarkerInfos> markerList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.text_search_activity);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        markerList = new ArrayList<>();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) findViewById(R.id.listOfPlaceTextSeach);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        etTextSearch = (EditText) findViewById(R.id.etTextSearch);
        ivSearchWithText = (ImageView) findViewById(R.id.ivSearchWithText);
        ivSearchWithText.setOnClickListener(this);

        Intent intent = getIntent();
        previousLocation = intent.getParcelableExtra("location");
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

            case R.id.ivSearchWithText:

                Toast.makeText(getApplicationContext(), etTextSearch.getText().toString(), Toast.LENGTH_SHORT).show();

                String url = TEXT_SEARCH_PLACE_URL + etTextSearch.getText().toString().trim().replaceAll(" ", "+") + "&location=" + previousLocation.getLatitude() + "," + previousLocation.getLongitude() + "&radius=800" + "&key=" + PLACES_API_KEY;

                getPlacesInBackground(url);

                break;

        }

    }

    private void getPlacesInBackground(String url) {

        Log.e("URL", url);

        progressBar.setVisibility(View.VISIBLE);

        SetPointOnMap setPointOnMap = new SetPointOnMap(this);
        setPointOnMap.getPointFromGooglePlace(url, new GetLocationCallback() {
            @Override
            public void done(String placesFound) {

                if (placesFound == null) {

                    Toast.makeText(getApplicationContext(), "Erreur dans la recherche pas text", Toast.LENGTH_SHORT).show();

                } else {

                    castJsonData(placesFound);

                }

            }
        });

    }

    private void castJsonData(String placesFound){

        markerList = new ArrayList<>();
        Location locMarker;

        try {
            JSONObject jsonObject = new JSONObject(placesFound);


            JSONArray jsonArray = jsonObject.getJSONArray("results");


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                JSONObject geometry = jsonObject1.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");

                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");
                locMarker = new Location(LocationManager.GPS_PROVIDER);
                locMarker.setLatitude(latitude);
                locMarker.setLongitude(longitude);

                float distance = previousLocation.distanceTo(locMarker);
                float distanceArrondie = (int) distance;
                distanceArrondie = (int) distanceArrondie / 100;
                distanceArrondie *= 100;

                String place_id = jsonObject1.getString("place_id");

                final String name = jsonObject1.getString("name");

                JSONArray JSONARRAYcategories = jsonObject1.getJSONArray("types");

                final String[] categories = new String[JSONARRAYcategories.length()];

                for (int j = 0; j < JSONARRAYcategories.length(); j++) {

                    categories[j] = JSONARRAYcategories.getString(j);

                }


                String isOpen = "";
                try {

                    JSONObject opening_hours = jsonObject1.getJSONObject("opening_hours");
                    Boolean isOpenBoolean = opening_hours.getBoolean("open_now") ;
                    isOpen = isOpenBoolean.toString();

                }catch (JSONException f){

                    isOpen = "unknown";
                }


                String vicinity = jsonObject1.getString("formatted_address");



                MarkerInfos place = new MarkerInfos(place_id, latitude, longitude, name, categories, isOpen, vicinity, distanceArrondie);

                markerList.add(place);

            }


            String next_page_token = "";
            try{

                next_page_token = jsonObject.getString("next_page_token");

            }catch (JSONException e){


            }

            if (next_page_token.equals("")){

                Log.e("LIEUX TELECHARGES", "DONE");
                //Log.e("ListOfMarker_SIZE", listOfMarkers.size()+"");


            }else{


                try {

                    Thread.sleep(1500);

                } catch (InterruptedException e) {

                    e.printStackTrace();

                }

                //String url = GOOGLE_PLACE_SEARCH_URL + "location=" + previousLocation.getLatitude() + "," + previousLocation.getLongitude() + "&radius=800&sensor=true&key=" + PLACES_API_KEY + "&pagetoken=" + next_page_token;
                //Log.e("next_page_toker_url", url);
                //getPlacesInBackground(url);

            }




        } catch (JSONException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Error parsing google place data", Toast.LENGTH_SHORT).show();

        }




        populateRecyclerView();
    }

    private void populateRecyclerView(){


        Collections.sort(markerList);


        RecyclerViewTextSearch adapter = new RecyclerViewTextSearch(getApplicationContext(), markerList,previousLocation);
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.INVISIBLE);


    }

}
