package com.antoine.startupproject.MapsActivityClass;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.antoine.startupproject.MyPlacesActivity.MarkerInfos;
import com.antoine.startupproject.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Antoine on 20/01/2016.
 */
public class ListOfPlaceActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private Location location;
    private ArrayList<MarkerInfos> markers;
    private RecyclerView listPlace;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_place);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listPlace = (RecyclerView) findViewById(R.id.listPlace);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        listPlace.setLayoutManager(linearLayoutManager);

        markers = new ArrayList<>();

        Intent intent = getIntent();
        markers = intent.getParcelableArrayListExtra("markers");
        location = intent.getParcelableExtra("location");

        populateRecyclerView();


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

    private void populateRecyclerView(){


        for (int i = 0; i< markers.size(); i++){


            Location locMarker = new Location(LocationManager.GPS_PROVIDER);
            locMarker.setLatitude(markers.get(i).getLat());
            locMarker.setLongitude(markers.get(i).getLng());

            float distance = location.distanceTo(locMarker);
            float distanceArrondie = (int) distance;
            distanceArrondie = (int) distanceArrondie / 100;
            distanceArrondie *= 100;

            markers.get(i).setDistance(distanceArrondie);

        }

        Collections.sort(markers);


        RecyclerViewListPlace adapter = new RecyclerViewListPlace(markers, location,getApplicationContext());
        listPlace.setAdapter(adapter);


    }
}
