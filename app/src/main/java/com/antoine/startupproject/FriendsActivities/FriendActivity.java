package com.antoine.startupproject.FriendsActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antoine.startupproject.LogRegisterAddFriendsUser.Friend;
import com.antoine.startupproject.LogRegisterAddFriendsUser.GetFriendCallback;
import com.antoine.startupproject.LogRegisterAddFriendsUser.GetFriendCallbackObject;
import com.antoine.startupproject.LogRegisterAddFriendsUser.ServerRequests;
import com.antoine.startupproject.LogRegisterAddFriendsUser.User;
import com.antoine.startupproject.MapsActivityClass.MapsActivity;
import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by Antoine on 02/12/2015.
 */
public class FriendActivity extends ActionBarActivity {

    private FloatingActionButton FAB;
    //private Toolbar toolbar;
    private SavedToPreferences savedToPreferences;
    private ListView lvMyFriends, lvFriendshipRequest;
    private TextView tvFriendshipRequest, tvMyFriends, tvUsernameFriend;
    private ImageView backToMap;
    private RelativeLayout layMenuAmis;
    private LinearLayout layFriendPrincipal;
    private View root;
    private int FriendID;
    private String FriendUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        //toolbar = (Toolbar) findViewById(R.id.app_bar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        layMenuAmis = (RelativeLayout) findViewById(R.id.layMenuAmis);
        layMenuAmis.setVisibility(View.INVISIBLE);

        layFriendPrincipal = (LinearLayout) findViewById(R.id.layFriendPrincipal);
        layMenuAmis.setClickable(false);


        backToMap = (ImageView) findViewById(R.id.backToMap);
        backToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        FAB = (FloatingActionButton) findViewById(R.id.fabToAddFriend);
        lvMyFriends = (ListView) findViewById(R.id.lvMyFriends);
        lvFriendshipRequest = (ListView) findViewById(R.id.lvFriendshipRequest);

        lvFriendshipRequest.setScrollingCacheEnabled(false);
        lvMyFriends.setScrollingCacheEnabled(false);


        tvFriendshipRequest = (TextView) findViewById(R.id.tvFriendshipRequest);
        tvMyFriends = (TextView) findViewById(R.id.tvMyFriends);
        tvUsernameFriend = (TextView) findViewById(R.id.tvUsernameFriend);

        tvFriendshipRequest.setVisibility(View.INVISIBLE);
        tvMyFriends.setVisibility(View.INVISIBLE);

        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(FriendActivity.this, NewFriendActivity.class));

            }
        });

        savedToPreferences = new SavedToPreferences(this);


        String test = savedToPreferences.getFriendInLoggedInUser();

        if (savedToPreferences.getFriendInLoggedInUser().equals("") || savedToPreferences.getFriendInLoggedInUser().length() == 2){

            tvMyFriends.setVisibility(View.VISIBLE);

        }else {

            populateFriendListView(savedToPreferences.getFriendInLoggedInUser());

        }



        if (savedToPreferences.getNewFriendRequest().equals("") || savedToPreferences.getNewFriendRequest().length() == 2){


            tvFriendshipRequest.setVisibility(View.VISIBLE);

        }else{

            populateFriendListViewFriendRequest(savedToPreferences.getNewFriendRequest());



        }


    }


    private void populateFriendListViewFriendRequest(String friendRequets){


        ArrayList<Friend> friendRequestArrayList = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(friendRequets);

            for (int i =0; i<jArray.length();i++){


                JSONObject jsonfriend = jArray.getJSONObject(i);


                String FriendImage = jsonfriend.getString("mail");
                String FriendUsername = jsonfriend.getString("username");
                int FriendID = jsonfriend.getInt("user_id");
                int request_id = jsonfriend.getInt("request_id");

                Friend FriendFoundInDB = new Friend(FriendID,request_id, FriendUsername,FriendImage);

                friendRequestArrayList.add(FriendFoundInDB);
            }

        } catch (JSONException e) {
            e.printStackTrace();

            tvFriendshipRequest.setVisibility(View.VISIBLE);

        }


        lvFriendshipRequest.setAdapter(new MyListAdapter2(this, R.layout.custom_row_listview_friendship_request , friendRequestArrayList));

    }


    private void populateFriendListView(String friends){

        ArrayList<Friend> userFriendsObjects = new ArrayList<>();

        try {
            JSONArray jArray = new JSONArray(friends);

            for (int i =0; i<jArray.length();i++){


                JSONObject jsonfriend = jArray.getJSONObject(i);


                String FriendImage = jsonfriend.getString("mail");
                String FriendUsername = jsonfriend.getString("username");
                int FriendID = jsonfriend.getInt("user_id");

                Friend FriendFoundInDB = new Friend(FriendID,FriendUsername,FriendImage);

                userFriendsObjects.add(FriendFoundInDB);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        lvMyFriends.setAdapter(new MyListAdapter(this, R.layout.custom_tab_row, userFriendsObjects));

    }

    private class MyListAdapter extends ArrayAdapter<Friend> {

        private int layout;


        public MyListAdapter(Context context, int resource, ArrayList<Friend> object) {
            super(context, resource, object);

            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder mainViewHolder = null;

            if (convertView == null) {

                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);

                mainViewHolder = new ViewHolder();
                mainViewHolder.profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
                mainViewHolder.tvLvFriends = (TextView) convertView.findViewById(R.id.tvLvFriends);
                mainViewHolder.bLvShowMoreOfFriend = (ImageView) convertView.findViewById(R.id.bLvShowMoreOfFriend);

                convertView.setTag(mainViewHolder);

            } else {

                mainViewHolder = (ViewHolder) convertView.getTag();

            }


            final ViewHolder finalMainViewHolder = mainViewHolder;

            mainViewHolder.bLvShowMoreOfFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int FriendID = getItem(position).friendID;
                    String FriendUsername = getItem(position).friendUsername;

                    openFriendDetailsLayout(FriendID, FriendUsername);
                    //Open alert dialogue


                }
            });


            mainViewHolder.tvLvFriends.setText(getItem(position).friendUsername);
            byte[] decodedimage = Base64.decode(getItem(position).friendPicture, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedimage, 0, decodedimage.length);
            mainViewHolder.profile_image.setImageBitmap(image);


            return convertView;
        }

        public class ViewHolder {

            ImageView profile_image;
            TextView tvLvFriends;
            ImageView bLvShowMoreOfFriend;
        }
    }


    private class MyListAdapter2 extends ArrayAdapter<Friend> {

        private int layout;

        public MyListAdapter2(Context context, int resource, ArrayList<Friend> object) {
            super(context, resource, object);

            layout = resource;


        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder2 mainViewHolder = null;

            if (convertView == null) {

                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);

                mainViewHolder = new ViewHolder2();
                mainViewHolder.profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
                mainViewHolder.tvLvFriends = (TextView) convertView.findViewById(R.id.tvLvFriends);
                mainViewHolder.bAccept = (ImageButton) convertView.findViewById(R.id.bAccept);
                mainViewHolder.bRefuse = (ImageButton) convertView.findViewById(R.id.bRefuse);


                convertView.setTag(mainViewHolder);

            } else {

                mainViewHolder = (ViewHolder2) convertView.getTag();

            }



            final ViewHolder2 finalMainViewHolder = mainViewHolder;

            mainViewHolder.bAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    User searchRequest = savedToPreferences.getLoggedInUser();

                    int friend_ID = getItem(position).friendID;
                    int request_ID = getItem(position).request_id;

                    Log.e("request_id", request_ID+"");

                    Friend confirmRequest = new Friend(searchRequest.user_id,friend_ID,request_ID, "acceptRequest");


                    ConfirmRequest(confirmRequest);


                }
            });

            mainViewHolder.bRefuse.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    User me = savedToPreferences.getLoggedInUser();

                    int friend_ID = getItem(position).friendID;
                    int request_ID = getItem(position).request_id;

                    Log.e("request_id", request_ID+"");

                    Friend cancelRequest = new Friend(me.user_id,friend_ID,request_ID, "cancel");

                    CancelRequest(cancelRequest);

                }
            });


            mainViewHolder.tvLvFriends.setText(getItem(position).friendUsername);
            byte[] decodedimage = Base64.decode(getItem(position).friendPicture, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedimage, 0, decodedimage.length);
            mainViewHolder.profile_image.setImageBitmap(image);



            return convertView;
        }

        public class ViewHolder2 {

            ImageView profile_image;
            TextView tvLvFriends;
            ImageButton bRefuse, bAccept;

        }
    }

    private void ConfirmRequest(Friend confirmRequest) {

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.AddFriendInBackground(confirmRequest, new GetFriendCallbackObject() {
            @Override
            public void done(Friend response) {


                if (response == null) {

                    Toast.makeText(getApplication(), "Can't access server !", Toast.LENGTH_SHORT).show();

                } else if (response.request_id == 0) {

                    Toast.makeText(getApplication(), "Server problem", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(getApplication(), "Amis ajouté !", Toast.LENGTH_SHORT).show();


                    changeFriendsList(response);


                    changeFriendRequestList(response);


                    populateFriendListViewFriendRequest(savedToPreferences.getNewFriendRequest());


                    //test.setText(response.friendID+"" +"  "+ response.friendUsername+"  "+response.friendPicture);
                }


            }
        });

    }


    private void CancelRequest(Friend cancelRequest) {


        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.AddFriendInBackground(cancelRequest, new GetFriendCallbackObject() {

            @Override
            public void done(Friend response) {

                if (response == null){

                    Toast.makeText(getApplication(),"Can't access server !",Toast.LENGTH_SHORT).show();

                }
                else {



                    Toast.makeText(getApplication(),"Demande refusée",Toast.LENGTH_SHORT).show();

                    changeFriendRequestList(response);

                    populateFriendListViewFriendRequest(savedToPreferences.getNewFriendRequest());

                }

            }


        });


    }

    private void changeFriendRequestList(Friend friendSelected){

        String friendsRequestList = savedToPreferences.getNewFriendRequest();
        JSONArray newJArrayFriendRequest = new JSONArray();


        try {
            JSONArray jsonArray = new JSONArray(friendsRequestList);

            for (int i =0;i<jsonArray.length();i++){

                JSONObject jObjFriend = jsonArray.getJSONObject(i);

                int filtreID = jObjFriend.getInt("user_id");
                String filtreUsername = jObjFriend.getString("username");
                String filtrePicture = jObjFriend.getString("mail");
                int request_id = jObjFriend.getInt("request_id");


                if (filtreID == friendSelected.friendID){



                }else{

                    jObjFriend.put("mail",filtrePicture);
                    jObjFriend.put("username",filtreUsername);
                    jObjFriend.put("user_id",filtreID);
                    jObjFriend.put("request_id",request_id);

                    newJArrayFriendRequest.put(jObjFriend);

                }

            }



            savedToPreferences.setNewFriendRequest(newJArrayFriendRequest.toString());

            if (newJArrayFriendRequest.toString().length() == 2){

                tvFriendshipRequest.setVisibility(View.VISIBLE);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }



    }



    private void changeFriendsList(Friend response) {

        String userFriends = savedToPreferences.getFriendInLoggedInUser();
        // Log.e("ArrayFromDB", userFriends);
       // Log.e("Reponse Serveur", response.friendPicture);

        if (userFriends.equals("") || userFriends.length() == 2) {

            JSONArray jObject = new JSONArray();
            JSONObject jObjectData = new JSONObject();
            try {
                jObjectData.put("user_id", response.friendID);
                jObjectData.put("mail", response.friendPicture);
                jObjectData.put("username", response.friendUsername);

                jObject.put(jObjectData);

                String jObjectString = jObject.toString();

                Log.e("New Array of friends", jObjectString);

                savedToPreferences.storeUserFriend(jObjectString);

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {

            try {
                JSONArray jObject = new JSONArray(userFriends);

                JSONObject jObjectData = new JSONObject();
                jObjectData.put("user_id", response.friendID);
                jObjectData.put("mail", response.friendPicture);
                jObjectData.put("username", response.friendUsername);

                jObject.put(jObjectData);

                String jObjectString = jObject.toString();

                savedToPreferences.storeUserFriend(jObjectString);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        tvMyFriends.setVisibility(View.INVISIBLE);

        populateFriendListView(savedToPreferences.getFriendInLoggedInUser());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void openFriendDetailsLayout(int Friend_ID, String Friend_Username){

        FriendID = Friend_ID;
        FriendUsername = Friend_Username;

        layFriendPrincipal.setAlpha((float) 0.2);

        tvUsernameFriend.setText(FriendUsername);
        layMenuAmis.setClickable(true);
        layMenuAmis.setVisibility(View.VISIBLE);




    }

    public void deleteFriend(View view){

        int myID = savedToPreferences.getLoggedInUser().user_id;

        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.AddFriendInBackground(new Friend(myID, FriendID, "deleteFriend"), new GetFriendCallbackObject() {
            @Override
            public void done(Friend response) throws JSONException {

                if (response == null){

                    Toast.makeText(getApplicationContext(), "Serveur innacessible !", Toast.LENGTH_SHORT).show();
                }
                else if (response.request_id == 0){

                    Toast.makeText(getApplicationContext(),"Erreur serveur !",Toast.LENGTH_SHORT).show();

                }
                else if (response.request_id == 1) {


                    String userFriends = savedToPreferences.getFriendInLoggedInUser();
                    JSONArray jArrayFriendPicture = new JSONArray();

                    try {
                        JSONArray jsonArray = new JSONArray(userFriends);

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jObjFriend = jsonArray.getJSONObject(i);

                            int filtreID = jObjFriend.getInt("user_id");
                            String filtreUsername = jObjFriend.getString("username");
                            String filtrePicture = jObjFriend.getString("mail");

                            if (filtreID == response.friendID) {

                                Toast.makeText(getApplicationContext(), "Amis suprimé avec succes !", Toast.LENGTH_SHORT).show();
                            } else {

                                jObjFriend.put("mail", filtrePicture);
                                jObjFriend.put("username", filtreUsername);
                                jObjFriend.put("user_id", filtreID);

                                jArrayFriendPicture.put(jObjFriend);

                            }

                        }

                        savedToPreferences.storeUserFriend(jArrayFriendPicture.toString());
                        populateFriendListView(jArrayFriendPicture.toString());
                        layMenuAmis.setVisibility(View.INVISIBLE);
                        layFriendPrincipal.setAlpha(1);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }

            }
        });


    }

    public void showFriendPLaces(View view){




    }

    public void closeFriendMenu(View view){

        layFriendPrincipal.setAlpha(1);
        layMenuAmis.setClickable(false);
        layMenuAmis.setVisibility(View.INVISIBLE);


    }

}
