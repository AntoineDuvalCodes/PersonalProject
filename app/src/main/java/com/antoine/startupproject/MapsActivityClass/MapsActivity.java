package com.antoine.startupproject.MapsActivityClass;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.antoine.startupproject.FriendsActivities.FriendActivity;
import com.antoine.startupproject.LogRegisterAddFriendsUser.Login;
import com.antoine.startupproject.LogRegisterAddFriendsUser.User;
import com.antoine.startupproject.MyPlacesActivity.FriendsPLaceActivity;
import com.antoine.startupproject.MyPlacesActivity.MarkerInfos;
import com.antoine.startupproject.MyPlacesActivity.MyPlacesActivity;
import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;
import com.antoine.startupproject.Settings;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;


public class MapsActivity extends ActionBarActivity implements View.OnClickListener, LocationListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Toolbar toolbar;
    private FloatingActionsMenu FAB;
    private FloatingActionButton fabButton1;
    private FloatingActionButton fabButton2;
    private FloatingActionButton fabButton3;
    private FloatingActionButton fabButton4;
    private Switch sBeVisible, sBar, sBus, sNightClub, sCafe, sFastFood, sRestaurant;
    private SavedToPreferences savedToPreferences;
    LocationManager locationManager;
    private CircleImageView profile_image;
    private TextView tvNameDrawer;
    public ArrayList<MarkerInfos> listOfMarkers = null;
    private Location previousLocation = null;
    private static final String GOOGLE_PLACE_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_API_KEY = "AIzaSyBDa-0x-M16wB4J_1-hNOH66JvU1QJeCX4";
    private int NbOfOnLocationChange;
    private Switch[] switches;
    private RelativeLayout layProgressBar;
    private ProgressBar progressBarMarkerDownload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        NbOfOnLocationChange = 0;

        layProgressBar = (RelativeLayout) findViewById(R.id.layProgressBar);
        layProgressBar.setVisibility(View.INVISIBLE);
        progressBarMarkerDownload = (ProgressBar) findViewById(R.id.progressBarMarkerDownload);
        progressBarMarkerDownload.setVisibility(View.INVISIBLE);

        savedToPreferences = new SavedToPreferences(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvNameDrawer = (TextView) findViewById(R.id.tvNameDrawer);
        tvNameDrawer.setText(savedToPreferences.getLoggedInUser().username);

        profile_image = (CircleImageView) findViewById(R.id.profile_image);

        byte[] decodedimage = Base64.decode(savedToPreferences.getLoggedInUser().encodedimage, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedimage, 0, decodedimage.length);
        profile_image.setImageBitmap(image);


        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        FAB = (FloatingActionsMenu) findViewById(R.id.multiple_actions);
        fabButton1 = (FloatingActionButton) findViewById(R.id.action_a);
        fabButton2 = (FloatingActionButton) findViewById(R.id.action_b);
        fabButton3 = (FloatingActionButton) findViewById(R.id.action_c);
        fabButton4 = (FloatingActionButton) findViewById(R.id.action_d);


        fabButton1.setOnClickListener(this);
        fabButton2.setOnClickListener(this);
        fabButton3.setOnClickListener(this);
        fabButton4.setOnClickListener(this);


        sBeVisible = (Switch) findViewById(R.id.sBeVisible);
        sBar = (Switch) findViewById(R.id.sBar);
        sBus = (Switch) findViewById(R.id.sBus);
        sNightClub = (Switch) findViewById(R.id.sNightClub);
        sCafe = (Switch) findViewById(R.id.sCafe);
        sFastFood = (Switch) findViewById(R.id.sFastFood);
        sRestaurant = (Switch) findViewById(R.id.sRestaurant);

        switches = new Switch[]{sBar, sBus, sNightClub, sCafe, sFastFood, sRestaurant};

        authenticate();
        isTheFirstUtilisation();
        setSwiftView();


    }

    public void authenticate() {

        if (savedToPreferences.getUserLoggedIn() == false) {

            startActivity(new Intent(this, Login.class));

        } else {

            setUpMapIfNeeded();
            abonnementGPS();

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.action_a:

                startActivity(new Intent(MapsActivity.this, FriendActivity.class));

                break;

            case R.id.action_b:

                Intent intent = new Intent(MapsActivity.this, MyPlacesActivity.class);
                intent.putExtra("location", previousLocation);
                startActivity(intent);

                break;

            case R.id.action_c:

                startActivity(new Intent(MapsActivity.this, FriendsPLaceActivity.class));


                break;

            case R.id.action_d:

                String categories = savedToPreferences.getVisibleCategoriesOnMap();

                Toast.makeText(getApplicationContext(), listOfMarkers.size()+"", Toast.LENGTH_SHORT).show();

                break;

        }


    }


    public void drawerItemCliked(View view) {


        switch (view.getId()) {

            case R.id.drawerHeader:

                startActivity(new Intent(MapsActivity.this, Settings.class));

                break;


            case R.id.ldBeVisible:

                if (!sBeVisible.isChecked()) {

                    sBeVisible.setChecked(true);

                } else {

                    sBeVisible.setChecked(false);
                }

                savedToPreferences.setUserVisiblility(sBeVisible.isChecked());

                break;

            case R.id.ldDemandePartage:

                Toast.makeText(getApplicationContext(), "Item Demande Partage cliked", Toast.LENGTH_SHORT).show();

                break;

            case R.id.ldBar:

                if (!sBar.isChecked()) {

                    sBar.setChecked(true);

                    String url = GOOGLE_PLACE_SEARCH_URL + "location=" + previousLocation.getLatitude() + "," + previousLocation.getLongitude() + "&radius=2000&sensor=true&types=bar&key=" + PLACES_API_KEY;
                    getPlacesInBackground(url);
                    listOfMarkers = new ArrayList<MarkerInfos>();
                    mMap.clear();

                } else {

                    sBar.setChecked(false);
                    mMap.clear();

                }

                setArrayListOfPoint("bar", sBar.isChecked());
                uncheckSwitchView(sBar);

                break;

            case R.id.ldBus:

                if (!sBus.isChecked()) {

                    sBus.setChecked(true);
                    String url = GOOGLE_PLACE_SEARCH_URL + "location=" + previousLocation.getLatitude() + "," + previousLocation.getLongitude() + "&radius=2000&sensor=true&types=bus_station&key=" + PLACES_API_KEY;
                    getPlacesInBackground(url);
                    listOfMarkers = new ArrayList<MarkerInfos>();
                    mMap.clear();

                } else {

                    sBus.setChecked(false);
                    mMap.clear();

                }

                setArrayListOfPoint("bus", sBus.isChecked());
                uncheckSwitchView(sBus);


                break;

            case R.id.ldNightClub:

                if (!sNightClub.isChecked()) {

                    sNightClub.setChecked(true);

                    String url = GOOGLE_PLACE_SEARCH_URL + "location=" + previousLocation.getLatitude() + "," + previousLocation.getLongitude() + "&radius=2000&sensor=true&types=night_club&key=" + PLACES_API_KEY;
                    getPlacesInBackground(url);
                    listOfMarkers = new ArrayList<MarkerInfos>();
                    mMap.clear();

                } else {

                    sNightClub.setChecked(false);
                    mMap.clear();

                }

                setArrayListOfPoint("nightClub", sNightClub.isChecked());
                uncheckSwitchView(sNightClub);


                break;

            case R.id.ldCafe:

                if (!sCafe.isChecked()) {

                    sCafe.setChecked(true);

                    String url = GOOGLE_PLACE_SEARCH_URL + "location=" + previousLocation.getLatitude() + "," + previousLocation.getLongitude() + "&radius=2000&sensor=true&types=cafe&key=" + PLACES_API_KEY;
                    getPlacesInBackground(url);
                    listOfMarkers = new ArrayList<MarkerInfos>();
                    mMap.clear();

                } else {

                    sCafe.setChecked(false);
                    mMap.clear();

                }

                setArrayListOfPoint("cafe", sCafe.isChecked());
                uncheckSwitchView(sCafe);


                break;

            case R.id.ldFastFood:

                if (!sFastFood.isChecked()) {

                    sFastFood.setChecked(true);

                    String url = GOOGLE_PLACE_SEARCH_URL + "location=" + previousLocation.getLatitude() + "," + previousLocation.getLongitude() + "&radius=2000&sensor=true&types=meal_takeaway&key=" + PLACES_API_KEY;
                    getPlacesInBackground(url);
                    listOfMarkers = new ArrayList<MarkerInfos>();
                    mMap.clear();


                } else {

                    sFastFood.setChecked(false);
                    mMap.clear();

                }

                setArrayListOfPoint("meal_takeaway", sFastFood.isChecked());
                uncheckSwitchView(sFastFood);


                break;

            case R.id.ldRestaurant:

                if (!sRestaurant.isChecked()) {

                    sRestaurant.setChecked(true);

                    String url = GOOGLE_PLACE_SEARCH_URL + "location=" + previousLocation.getLatitude() + "," + previousLocation.getLongitude() + "&radius=2000&sensor=true&types=restaurant&key=" + PLACES_API_KEY;
                    getPlacesInBackground(url);
                    listOfMarkers = new ArrayList<MarkerInfos>();
                    mMap.clear();


                } else {

                    sRestaurant.setChecked(false);
                    mMap.clear();

                }

                setArrayListOfPoint("restaurant", sRestaurant.isChecked());
                uncheckSwitchView(sRestaurant);


                break;


            case R.id.ldSettings:

                startActivity(new Intent(MapsActivity.this, Settings.class));

                break;

        }

    }

    private void uncheckSwitchView (Switch s){

        for (int i = 0; i < switches.length; i++){

            if (s != switches[i]){

                switches[i].setChecked(false);

            }
        }


    }

    private void setArrayListOfPoint(String categorie, boolean visibility) {

        String categories = savedToPreferences.getVisibleCategoriesOnMap();

        JSONArray newArrayCategories = new JSONArray();


        try {
            JSONArray jsonArray = new JSONArray(categories);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jObjFriend = jsonArray.getJSONObject(i);
                JSONObject newJObject = new JSONObject();
                String filtreCategorie = jObjFriend.getString("categorie");
                //boolean filtreVisibility = jObjFriend.getBoolean("visibility");


                if (categorie.equals(filtreCategorie)) {

                    newJObject.put("categorie", categorie);
                    newJObject.put("visibility", visibility);


                } else {

                    newJObject.put("categorie", filtreCategorie);
                    newJObject.put("visibility", false);


                }

                newArrayCategories.put(newJObject);

            }


            savedToPreferences.setVisibleCategoriesOnMap(newArrayCategories.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        byte[] decodedimage = Base64.decode(savedToPreferences.getLoggedInUser().encodedimage, Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedimage, 0, decodedimage.length);
        profile_image.setImageBitmap(image);

        tvNameDrawer.setText(savedToPreferences.getLoggedInUser().username);


        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);


        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            abonnementGPS();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        if (savedToPreferences.getUserVisibility()) {

            //creer notif

        } else {

            desabonnementGPS();


        }

    }

    private void desabonnementGPS() {

        locationManager.removeUpdates(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.SearchPlace) {

            Intent intent = new Intent(MapsActivity.this, TextSearchActivity.class);
            intent.putExtra("location", previousLocation);
            startActivity(intent);

        }

        if( id == R.id.ListOfPlace){

            Toast.makeText(getApplicationContext(), "list", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(MapsActivity.this, ListOfPlaceActivity.class);
            intent.putExtra("location", previousLocation);
            intent.putParcelableArrayListExtra("markers", listOfMarkers);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }

    public void onDrawerSlide(float slideOffset) {

        FAB.setTranslationX(slideOffset * 200);

    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

                mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {

                        View v = getLayoutInflater().inflate(R.layout.marker_info_window, null);

                        TextView tvInfoWindow = (TextView) v.findViewById(R.id.tvInfoWindow);
                        //TextView tvMarkerVicinity = (TextView) v.findViewById(R.id.tvMarkerVicinity);
                        //ImageView ivOpenDetailsActivity = (ImageView) v.findViewById(R.id.ivOpenDetailsActivity);
                        ImageView ivCategorieInfoWindow = (ImageView) v.findViewById(R.id.ivCategorieInfoWindow);
                        ImageView ivCategorieInfoWindow2 = (ImageView) v.findViewById(R.id.ivCategorieInfoWindow2);
                        ImageView ivCategorieInfoWindow3 = (ImageView) v.findViewById(R.id.ivCategorieInfoWindow3);
                        ImageView ivCategorieInfoWindow4 = (ImageView) v.findViewById(R.id.ivCategorieInfoWindow4);

                        ivCategorieInfoWindow.setVisibility(View.INVISIBLE);
                        ivCategorieInfoWindow2.setVisibility(View.INVISIBLE);
                        ivCategorieInfoWindow3.setVisibility(View.INVISIBLE);
                        ivCategorieInfoWindow4.setVisibility(View.INVISIBLE);


                        ImageView[] ivTab = new ImageView[4];
                        ivTab[0] = ivCategorieInfoWindow;
                        ivTab[1] = ivCategorieInfoWindow2;
                        ivTab[2] = ivCategorieInfoWindow3;
                        ivTab[3] = ivCategorieInfoWindow4;

                        tvInfoWindow.setText(marker.getTitle());

                        for (int i = 0; i<listOfMarkers.size(); i++){

                            MarkerInfos markerFilter = listOfMarkers.get(i);
                            int j = 0;

                            if (marker.getTitle().equals(markerFilter.getName())){

                                //tvMarkerVicinity.setText(markerFilter.getVicinity());

                                for (int k = 0; k < markerFilter.getCategorie().length; k++) {

                                    switch (markerFilter.getCategorie()[k]) {


                                        case "restaurant":

                                            ivTab[j].setImageResource(R.drawable.ic_restaurant_menu_black_24dp);
                                            ivTab[j].setVisibility(View.VISIBLE);
                                            j++;
                                            break;

                                        case "bar":

                                            ivTab[j].setImageResource(R.drawable.ic_local_bar_black_24dp);
                                            ivTab[j].setVisibility(View.VISIBLE);
                                            j++;

                                            break;

                                        case "night_club":

                                            ivTab[j].setImageResource(R.drawable.ic_music_note_black_24dp);
                                            ivTab[j].setVisibility(View.VISIBLE);
                                            j++;

                                            break;

                                        case "cafe":

                                            ivTab[j].setImageResource(R.drawable.ic_free_breakfast_black_24dp);
                                            ivTab[j].setVisibility(View.VISIBLE);
                                            j++;

                                            break;

                                    }
                                }

                                break;
                            }



                        }



                        return v;
                    }
                });

                setUpMap();
            }
        }

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                String markerName = marker.getTitle();


                for (int i = 0; i < listOfMarkers.size(); i++) {


                    MarkerInfos findMarker = listOfMarkers.get(i);

                    if (markerName.equals(findMarker.getName())) {

                        Intent intent = new Intent(MapsActivity.this, PlacesDetailsActivity.class);
                        intent.putExtra("marker", findMarker);
                        intent.putExtra("location", previousLocation);
                        startActivity(intent);

                        break;
                    }
                }
            }
        });

        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);
        mMap.getUiSettings().setCompassEnabled(false);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        Toast.makeText(getApplicationContext(), "Vous allez apparaitre sur la carte dans moins d'une minute ", Toast.LENGTH_SHORT).show();
        mMap.setMyLocationEnabled(true);

    }


    private void abonnementGPS() {

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setSpeedRequired(true);
        criteria.setBearingRequired(true);
        criteria.setCostAllowed(false);

        locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, true), 1000, 10, this);

        Location firstLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (firstLocation != null) {

            float firstOrientation = firstLocation.getBearing();

            LatLng latLng = new LatLng(firstLocation.getLatitude(), firstLocation.getLongitude());

            previousLocation = firstLocation;

            CameraPosition cameraPosition = new CameraPosition.Builder().bearing(firstOrientation).target(latLng).zoom(15).tilt(90).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

           // String url = GOOGLE_PLACE_SEARCH_URL + "location=" + firstLocation.getLatitude() + "," + firstLocation.getLongitude() + "&radius=500&sensor=true&types=restaurant&key=" + PLACES_API_KEY;

            //getPlacesInBackground(url);
            //NbOfOnLocationChange++;

        }


    }


    private void setSwiftView() {

        String categoriesVisibles = savedToPreferences.getVisibleCategoriesOnMap();

        try {

            JSONArray jsonArray = new JSONArray(categoriesVisibles);

            sBar.setChecked(jsonArray.getJSONObject(0).getBoolean("visibility"));
            sBus.setChecked(jsonArray.getJSONObject(1).getBoolean("visibility"));
            sNightClub.setChecked(jsonArray.getJSONObject(2).getBoolean("visibility"));
            sCafe.setChecked(jsonArray.getJSONObject(3).getBoolean("visibility"));
            sFastFood.setChecked(jsonArray.getJSONObject(4).getBoolean("visibility"));
            sRestaurant.setChecked(jsonArray.getJSONObject(5).getBoolean("visibility"));


            sBeVisible.setChecked(savedToPreferences.getUserVisibility());

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void isTheFirstUtilisation() {


        if (savedToPreferences.isFistUtilisation()) {

            JSONArray jsonArray = new JSONArray();
            String[] listCategorie = {"bar", "bus", "nightClub", "cafe", "meal_takeaway", "restaurant"};

            try {
                for (int i = 0; i < listCategorie.length; i++) {

                    JSONObject jObject = new JSONObject();

                    jObject.put("categorie", listCategorie[i]);
                    jObject.put("visibility", false);

                    jsonArray.put(jObject);

                }

                savedToPreferences.setVisibleCategoriesOnMap(jsonArray.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        savedToPreferences.setTheFirstUtilisation(false);


    }

    private void getPlacesInBackground(String url) {

        Log.e("URL", url);
        layProgressBar.setVisibility(View.VISIBLE);
        progressBarMarkerDownload.setVisibility(View.VISIBLE);

        SetPointOnMap setPointOnMap = new SetPointOnMap(this);
        setPointOnMap.getPointFromGooglePlace(url, new GetLocationCallback() {
            @Override
            public void done(String placesFound) {

                if (placesFound == null) {

                    Toast.makeText(getApplicationContext(), "Erreur dans le chargement des lieux proches", Toast.LENGTH_SHORT).show();

                    progressBarMarkerDownload.setVisibility(View.INVISIBLE);

                } else {

                    setMarkersOnMap(placesFound);
                    layProgressBar.setVisibility(View.INVISIBLE);
                }

            }
        });

    }


    private void setMarkersOnMap(String placesFound) {


        JSONObject jsonObject = null;
        double latitude;
        double longitude;


        try {
            jsonObject = new JSONObject(placesFound);


            JSONArray jsonArray = jsonObject.getJSONArray("results");


            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                JSONObject geometry = jsonObject1.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");

                latitude = location.getDouble("lat");
                longitude = location.getDouble("lng");
                final LatLng latLng = new LatLng(latitude, longitude);

                String place_id = jsonObject1.getString("place_id");

                JSONArray JSONARRAYcategories = jsonObject1.getJSONArray("types");

                final String[] categories = new String[JSONARRAYcategories.length()];

                for (int j = 0; j < JSONARRAYcategories.length(); j++) {

                    categories[j] = JSONARRAYcategories.getString(j);

                }
                final String name = jsonObject1.getString("name");

                String isOpen = "";
                try {

                    JSONObject opening_hours = jsonObject1.getJSONObject("opening_hours");
                    Boolean isOpenBoolean = opening_hours.getBoolean("open_now") ;
                    isOpen = isOpenBoolean.toString();

                }catch (JSONException f){

                    isOpen = "unknown";
                }


                String vicinity = jsonObject1.getString("vicinity");


                mMap.addMarker(new MarkerOptions().position(latLng).title(name));//.icon(BitmapDescriptorFactory.fromBitmap(iconFound)));

                MarkerInfos marker = new MarkerInfos(place_id, latitude, longitude, name, categories, isOpen, vicinity);

                listOfMarkers.add(marker);

            }


            String next_page_token = "";
            try{

                next_page_token = jsonObject.getString("next_page_token");

            }catch (JSONException e){


            }

            if (next_page_token.equals("")){

                Log.e("LIEUX TELECHARGES", "DONE");
                //Log.e("ListOfMarker_SIZE", listOfMarkers.size()+"");
                progressBarMarkerDownload.setVisibility(View.INVISIBLE);


            }else{


                try {

                    Thread.sleep(1500);

                } catch (InterruptedException e) {

                    e.printStackTrace();

                }

                String url = GOOGLE_PLACE_SEARCH_URL + "location=" + previousLocation.getLatitude() + "," + previousLocation.getLongitude() + "&radius=2000&sensor=true&key=" + PLACES_API_KEY + "&pagetoken=" + next_page_token;
                //Log.e("next_page_toker_url", url);
                getPlacesInBackground(url);

            }




        } catch (JSONException e) {
            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Error parsing google place data", Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public void onLocationChanged(Location location) {


        String categories = savedToPreferences.getVisibleCategoriesOnMap();

        String url = "null";

        try {
            JSONArray jsonArray = new JSONArray(categories);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jObjFriend = jsonArray.getJSONObject(i);
                String filtreCategorie = jObjFriend.getString("categorie");


                if (jObjFriend.getBoolean("visibility")) {

                    url = GOOGLE_PLACE_SEARCH_URL + "location=" + location.getLatitude() + "," + location.getLongitude() + "&radius=2000&sensor=true&types=" + filtreCategorie + "&key=" + PLACES_API_KEY;

                    Toast.makeText(getApplicationContext(), filtreCategorie, Toast.LENGTH_SHORT).show();
                }


            }

        }catch (JSONException e){


        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        NbOfOnLocationChange++;

        if (previousLocation == null || NbOfOnLocationChange == 1) {

            CameraPosition cameraPosition = new CameraPosition.Builder().bearing(location.getBearing()).target(latLng).zoom(15).tilt(90).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            previousLocation = location;

            if (!url.equals("null")) {

                listOfMarkers = new ArrayList<>();

                getPlacesInBackground(url);
            }

        } else {

            if (previousLocation.distanceTo(location) >= 50) {

                CameraPosition cameraPosition = new CameraPosition.Builder().bearing(location.getBearing()).target(latLng).zoom(15).tilt(90).build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                if (!url.equals("null")) {

                    listOfMarkers = new ArrayList<>();

                    getPlacesInBackground(url);
                }

                previousLocation = location;

            }
        }

        previousLocation = location;





    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

        Toast.makeText(this, "onStatusChanged", Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onProviderEnabled(String provider) {

        Toast.makeText(this, "onProviderEnabled", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {

        Toast.makeText(this, "onProviderDisabled", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        FAB.setVisibility(View.INVISIBLE);

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        FAB.setVisibility(View.VISIBLE);

    }
}
