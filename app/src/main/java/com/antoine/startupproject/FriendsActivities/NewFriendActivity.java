package com.antoine.startupproject.FriendsActivities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antoine.startupproject.LogRegisterAddFriendsUser.Friend;
import com.antoine.startupproject.LogRegisterAddFriendsUser.GetFriendCallback;
import com.antoine.startupproject.LogRegisterAddFriendsUser.GetFriendCallbackObject;
import com.antoine.startupproject.LogRegisterAddFriendsUser.ServerRequests;
import com.antoine.startupproject.LogRegisterAddFriendsUser.User;
import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Antoine on 02/12/2015.
 */
public class NewFriendActivity extends ActionBarActivity implements View.OnClickListener{


    private Toolbar toolbar;
    private EditText etSearchNewFriend;
    private ImageButton searchImage;
    private SavedToPreferences savedToPreferences;
    private ListView lvSearchNewFriend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);

        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        savedToPreferences = new SavedToPreferences(this);

        etSearchNewFriend = (EditText) findViewById(R.id.etSearchNewFriend);
        searchImage = (ImageButton) findViewById(R.id.searchImage);
        lvSearchNewFriend = (ListView) findViewById(R.id.lvSearchNewFriend);

        searchImage.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.searchImage:


                String friendSearchedUsername = etSearchNewFriend.getText().toString().trim();
                int myId = savedToPreferences.getLoggedInUser().user_id;

                if (friendSearchedUsername.length()>0) {


                    Friend friend = new Friend(myId,friendSearchedUsername, "searchFriend");

                    findFriend(friend);

                }else{
                    Toast.makeText(getApplicationContext(), "Veuillez entrer un nom d'utilisateur valide", Toast.LENGTH_SHORT).show();
                }


                break;

        }
    }

    private void findFriend( Friend sendFriend){
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.SearchFriendInBackground(sendFriend, new GetFriendCallback() {
            @Override
            public void done(ArrayList<Friend> friendsfound) {

                if (friendsfound == null){

                    Toast.makeText(getApplicationContext(), "Can't access server !", Toast.LENGTH_SHORT).show();

                }

                else if (friendsfound.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Auncun utilisateur trouvé", Toast.LENGTH_SHORT).show();
                } else {
                    putIntoListView(friendsfound);
                }
            }


        });
    }


    private void putIntoListView(ArrayList<Friend> friendsfound) {

        lvSearchNewFriend.setAdapter(new MyListAdapter(NewFriendActivity.this, R.layout.custom_row_search_friend, friendsfound));


    }


    private class MyListAdapter extends ArrayAdapter<Friend> {

        private int layout;
        public MyListAdapter(Context context, int resource, ArrayList<Friend> object) {
            super(context, resource, object);

            layout = resource;
        }

        @Override
        public View getView( final int position, View convertView, ViewGroup parent) {

            ViewHolder mainViewHolder = null;

            if(convertView == null){

                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout,parent,false);

                mainViewHolder  = new ViewHolder();
                mainViewHolder.profile_image = (ImageView) convertView.findViewById(R.id.profile_image);
                mainViewHolder.addButton = (ImageView) convertView.findViewById(R.id.addButton);
                mainViewHolder.tvLvFriends = (TextView)convertView.findViewById(R.id.tvLvFriends);


                convertView.setTag(mainViewHolder);
            }
            else{
                mainViewHolder = (ViewHolder) convertView.getTag();

            }

            mainViewHolder.addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(getContext(),"Button was cliked for list item " + position,Toast.LENGTH_SHORT).show();

                    int addFriend = getItem(position).friendID;
                    String tag = "add";

                    User usertest = savedToPreferences.getLoggedInUser();

                    Friend sendFriendRequest = new Friend (usertest.user_id,addFriend,tag);


                    AddFriend(sendFriendRequest);
                }
            });
            mainViewHolder.tvLvFriends.setText(getItem(position).friendUsername);
            byte[] decodedimage = Base64.decode(getItem(position).friendPicture, Base64.DEFAULT);
            Bitmap image = BitmapFactory.decodeByteArray(decodedimage, 0, decodedimage.length);
            mainViewHolder.profile_image.setImageBitmap(image);

            return convertView;
        }

        public class ViewHolder{

            ImageView profile_image,addButton;
            TextView  tvLvFriends;

        }
    }

    private void AddFriend(Friend sendFriendRequest){
        ServerRequests serverRequests = new ServerRequests(this);
        serverRequests.AddFriendInBackground(sendFriendRequest, new GetFriendCallbackObject() {
            @Override
            public void done(Friend response) {


                if (response == null) {

                    Toast.makeText(getApplicationContext(), "Can't access server !", Toast.LENGTH_SHORT).show();

                } else {

                    if (response.request_id == 0) {

                        Toast.makeText(getApplicationContext(), "Erreur connection base de donnée !", Toast.LENGTH_SHORT).show();


                    } else if (response.request_id == 1) {

                        Toast.makeText(getApplicationContext(), "Demande envoyée ! !", Toast.LENGTH_SHORT).show();

                    } else if (response.request_id == 2) {

                        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(NewFriendActivity.this);
                        dialogbuilder.setCancelable(false);
                        dialogbuilder.setMessage("Vous etes déjà amis avec cette personne, ou vous lui avez déjà envoyé une demande");
                        dialogbuilder.setPositiveButton("ok", null);
                        //dialogbuilder.setNegativeButton("Oui", new DialogInterface.OnClickListener() {
                        //   @Override
                        // public void onClick(DialogInterface dialog, int which) {


                        //    int my_id = sendFriendRequest.My_ID;
                        //   int friend_id = sendFriendRequest.friendID;
                        //int request_id = sendFriendRequest.request_id;


                        // Friend sendFriendRequest = new Friend(my_id, friend_id, "cancel2");


                        //   CancelFriendRequest(sendFriendRequest);
                        // }
                        // });
                        dialogbuilder.show();
                    }
                    else if (response.request_id == 3){

                        AlertDialog.Builder dialogbuilder = new AlertDialog.Builder(NewFriendActivity.this);
                        dialogbuilder.setCancelable(false);
                        dialogbuilder.setMessage("Vous etes déjà amis avec cette personne !");
                        dialogbuilder.setPositiveButton("fermer", null);
                    }


                    else {


                        Toast.makeText(getApplicationContext(), "Probléme serveur", Toast.LENGTH_SHORT).show();

                    }


                }
            }
        });
    }


}
