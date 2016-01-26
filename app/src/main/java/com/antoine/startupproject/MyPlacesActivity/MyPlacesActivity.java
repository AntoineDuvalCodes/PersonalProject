package com.antoine.startupproject.MyPlacesActivity;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.antoine.startupproject.MapsActivityClass.RecyclerViewListPlace;
import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Antoine on 03/12/2015.
 */
public class MyPlacesActivity extends ActionBarActivity implements View.OnClickListener {


    private Toolbar toolbar;
    private FloatingActionButton FAB;
    private RecyclerView myPlaces;
    private ArrayList<MarkerInfos> markers;
    private LocationManager locationManager;
    private Location location;
    private SavedToPreferences savedToPreferences;
    private TextView tvNoPlaceLiked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_places);

        savedToPreferences = new SavedToPreferences(this);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNoPlaceLiked = (TextView) findViewById(R.id.tvNoPlaceLiked);
        tvNoPlaceLiked.setVisibility(View.INVISIBLE);

        FAB = (FloatingActionButton) findViewById(R.id.fabToAddPlaces);

        myPlaces = (RecyclerView) findViewById(R.id.myPlaces);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        myPlaces.setLayoutManager(linearLayoutManager);
        FAB.setOnClickListener(this);

        castDataFromSharedPreferences();

    }

    private void castDataFromSharedPreferences(){

        markers = new ArrayList<>();
        boolean vide = false;
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location firstLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        String myPlaces = savedToPreferences.getPlacesILiked();

        try {
            JSONArray jsonArray;
            if(myPlaces.length() == 2 || myPlaces.length() == 0){

                vide = true;

            }else{

                jsonArray = new JSONArray(myPlaces);


                for(int i = 0 ; i < jsonArray.length(); i++){

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String place_id = jsonObject.getString("place_id");
                    String name = jsonObject.getString("name");
                    String vicinity = jsonObject.getString("vicinity");

                    JSONArray jsonCategorie = jsonObject.getJSONArray("categories");
                    String[] categories = new String[jsonCategorie.length()];
                    for(int j = 0 ; j < jsonCategorie.length(); j++){

                        categories[j] = jsonCategorie.getString(j);

                    }

                    double lat = jsonObject.getDouble("latitude");
                    double lng = jsonObject.getDouble("longitude");

                    Location locMarker = new Location(LocationManager.GPS_PROVIDER);
                    locMarker.setLatitude(lat);
                    locMarker.setLongitude(lng);

                    float distance = firstLocation.distanceTo(locMarker);
                    float distanceArrondie = (int) distance;
                    distanceArrondie = (int) distanceArrondie / 100;
                    distanceArrondie *= 100;

                    MarkerInfos markerInfos = new MarkerInfos(place_id,lat,lng,name,categories, "",vicinity, distanceArrondie);

                    markers.add(markerInfos);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(vide){

            tvNoPlaceLiked.setVisibility(View.VISIBLE);


        }else{

            populateRecyclerView();

        }


    }

    private void populateRecyclerView(){

        Collections.sort(markers);


        RecyclerViewListPlace adapter = new RecyclerViewListPlace(markers, location, getApplicationContext());
        myPlaces.setAdapter(adapter);


    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){


            case R.id.fabToAddPlaces:

                startActivity(new Intent(MyPlacesActivity.this, AddPlacesActivity.class));

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
}
