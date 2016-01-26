package com.antoine.startupproject.GCM;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.antoine.startupproject.FriendsActivities.FriendActivity;
import com.antoine.startupproject.R;
import com.antoine.startupproject.SavedToPreferences;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Antoine on 03/11/2015.
 */
public class GCMNotificationIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCMNotificationIntent";
    public static final String SERVER_ADDRESS = "http://antoine-duval.esy.es/";
    private SavedToPreferences savedToPreferences;



    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()){

            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)){

                sendNotification("Send error: " + extras.toString());

            }else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)){

                sendNotification("Deleted message on server: " + extras.toString());

            }else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {


                String notificationMessage = (String) extras.get("message");
                int notificationFrom;
                String message = "";

                if (notificationMessage.equals("demandeAjout")) {


                    String notificationFromMail = (String) extras.get("fromUserMail");
                    int request_id = Integer.parseInt(extras.getString("request_id"));
                    String fromFriend = extras.getString("fromUsername");
                    message = fromFriend+ " vous a envoyé une demande d'amitié";
                    notificationFrom = Integer.parseInt(extras.getString("fromUserID"));
                    String url = SERVER_ADDRESS + "pictures/" + notificationFromMail + ".JPG";

                    URLConnection connection = null;
                    try {
                        connection = new URL(url).openConnection();
                        connection.setConnectTimeout(1000 * 30);
                        connection.setReadTimeout(1000 * 30);

                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                        String encodedimage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


                        savedToPreferences = new SavedToPreferences(this);

                        String jsonFriendRequest = savedToPreferences.getNewFriendRequest();

                        if (jsonFriendRequest.equals("") || jsonFriendRequest.length() == 2){

                            jsonFriendRequest = null;

                        }

                        if ( jsonFriendRequest == null){

                            JSONArray jsonArray = new JSONArray();

                            JSONObject jObjectData = new JSONObject();
                            jObjectData.put("user_id", notificationFrom);
                            jObjectData.put("mail", encodedimage);
                            jObjectData.put("username", fromFriend);
                            jObjectData.put("request_id", request_id);

                            jsonArray.put(jObjectData);

                            String jArrayString = jsonArray.toString();
                            savedToPreferences.setNewFriendRequest(jArrayString);


                        }else {

                            JSONArray jsonArray = new JSONArray(jsonFriendRequest);

                            JSONObject jObjectData = new JSONObject();
                            jObjectData.put("user_id", notificationFrom);
                            jObjectData.put("mail", encodedimage);
                            jObjectData.put("username", fromFriend);
                            jObjectData.put("request_id", request_id);


                            jsonArray.put(jObjectData);

                            String jArrayString = jsonArray.toString();
                            savedToPreferences.setNewFriendRequest(jArrayString);
                        }



                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }


                    sendNotification(message);

                } else if (notificationMessage.equals("acceptRequest")) {


                    String fromFriend = extras.getString("fromUsername");
                    String fromMail = extras.getString("fromUserMail");
                    message = fromFriend + " a accepté votre demande d'amitié";
                    notificationFrom = Integer.parseInt(extras.getString("fromUserID"));

                    String url = SERVER_ADDRESS + "pictures/" + fromMail + ".JPG";

                    URLConnection connection;
                    try {
                        connection = new URL(url).openConnection();

                        connection.setConnectTimeout(1000 * 30);
                        connection.setReadTimeout(1000 * 30);

                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                        String encodedimage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


                        savedToPreferences = new SavedToPreferences(this);
                        String userFriends = savedToPreferences.getFriendInLoggedInUser();


                        JSONArray jsonArray = new JSONArray(userFriends);

                        JSONObject jObjectData = new JSONObject();
                        jObjectData.put("user_id", notificationFrom);
                        jObjectData.put("mail", encodedimage);
                        jObjectData.put("username", fromFriend);

                        jsonArray.put(jObjectData);

                        String jObjectString = jsonArray.toString();

                        Log.e("Friend Array modified", jObjectString);

                        savedToPreferences.storeUserFriend(jObjectString);

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }


                    sendNotification(message);

                }
                 else if (notificationMessage.equals("deleteFriend")){

                   notificationFrom = Integer.parseInt(extras.getString("fromUserID"));
                   savedToPreferences = new SavedToPreferences(this);
                   String userFriends = savedToPreferences.getFriendInLoggedInUser();
                   JSONArray jArrayFriendPicture = new JSONArray();

                    Log.e("DeletedFriend", notificationFrom + "" + " Friend" + userFriends);

                    try {
                        JSONArray jsonArray = new JSONArray(userFriends);

                        for (int i =0;i<jsonArray.length();i++){

                            JSONObject jObjFriend = jsonArray.getJSONObject(i);

                             int filtreID = jObjFriend.getInt("user_id");
                             String filtreUsername = jObjFriend.getString("username");
                             String filtrePicture = jObjFriend.getString("mail");

                             if (filtreID == notificationFrom){

                                 Log.i(TAG,"Amis suprimé");

                             }else{

                                jObjFriend.put("mail",filtrePicture);
                                jObjFriend.put("username",filtreUsername);
                                jObjFriend.put("user_id",filtreID);

                                jArrayFriendPicture.put(jObjFriend);

                             }

                        }

                        savedToPreferences.storeUserFriend(jArrayFriendPicture.toString());

                     } catch (JSONException e) {
                      e.printStackTrace();
                     }


                 }else if (notificationMessage.equals("friendChangeUsername")){

                    notificationFrom = Integer.parseInt(extras.getString("fromUserID"));
                    String newUsername = extras.getString("newUsername");
                    savedToPreferences = new SavedToPreferences(this);

                    String userFriendList = savedToPreferences.getFriendInLoggedInUser();

                    JSONArray jArrayFriendPicture = new JSONArray();


                    try {
                        JSONArray jsonArray = new JSONArray(userFriendList);

                        for (int i =0;i<jsonArray.length();i++){

                            JSONObject jObjFriend = jsonArray.getJSONObject(i);

                            int filtreID = jObjFriend.getInt("user_id");
                            String filtreUsername = jObjFriend.getString("username");
                            String filtrePicture = jObjFriend.getString("mail");

                            if (filtreID == notificationFrom){

                                jObjFriend.put("mail",filtrePicture);
                                jObjFriend.put("username",newUsername);
                                jObjFriend.put("user_id",filtreID);

                                jArrayFriendPicture.put(jObjFriend);

                            }else{

                                jObjFriend.put("mail",filtrePicture);
                                jObjFriend.put("username",filtreUsername);
                                jObjFriend.put("user_id",filtreID);

                                jArrayFriendPicture.put(jObjFriend);

                            }

                        }

                        savedToPreferences.storeUserFriend(jArrayFriendPicture.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                 }else if (notificationMessage.equals("friendChangePicture")){


                    notificationFrom = Integer.parseInt(extras.getString("fromUserID"));
                    String notificationFromMail = extras.getString("fromMail");

                    String url = SERVER_ADDRESS + "pictures/" + notificationFromMail + ".JPG";

                    URLConnection connection = null;
                    String encodedimage="";
                    try {
                        connection = new URL(url).openConnection();

                        connection.setConnectTimeout(1000 * 30);
                        connection.setReadTimeout(1000 * 30);

                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);


                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, byteArrayOutputStream);
                        encodedimage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    savedToPreferences = new SavedToPreferences(this);


                    String userFriendList = savedToPreferences.getFriendInLoggedInUser();

                    JSONArray jArrayFriendPicture = new JSONArray();


                    try {
                        JSONArray jsonArray = new JSONArray(userFriendList);

                        for (int i =0;i<jsonArray.length();i++){

                            JSONObject jObjFriend = jsonArray.getJSONObject(i);

                            int filtreID = jObjFriend.getInt("user_id");
                            String filtreUsername = jObjFriend.getString("username");
                            String filtrePicture = jObjFriend.getString("mail");

                            if (filtreID == notificationFrom){

                                jObjFriend.put("mail", encodedimage);
                                jObjFriend.put("username",filtreUsername);
                                jObjFriend.put("user_id",filtreID);

                                jArrayFriendPicture.put(jObjFriend);

                            }else{

                                jObjFriend.put("mail",filtrePicture);
                                jObjFriend.put("username",filtreUsername);
                                jObjFriend.put("user_id",filtreID);

                                jArrayFriendPicture.put(jObjFriend);

                            }

                        }

                        savedToPreferences.storeUserFriend(jArrayFriendPicture.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else if (notificationMessage.equals("friendLikePlace")){

                    notificationFrom = Integer.parseInt(extras.getString("fromUserID"));
                    String place_id = extras.getString("place_id");
                    savedToPreferences = new SavedToPreferences(this);

                    String friendLikesList = savedToPreferences.getFriendInLoggedInUser();

                    try {

                        JSONArray jsonArrayLikes;

                        if (friendLikesList.length() == 2){

                            jsonArrayLikes = new JSONArray();

                        }else{

                            jsonArrayLikes = new JSONArray(friendLikesList);

                        }


                        JSONObject newData = new JSONObject();
                        newData.put("user_id", notificationFrom);
                        newData.put("place_id", place_id);

                        jsonArrayLikes.put(newData);

                        savedToPreferences.setFriendLike(jsonArrayLikes.toString());


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {

                    sendNotification(notificationMessage);
                }



                Log.i(TAG, "Received: " + extras.toString());
            }

        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }

    private void sendNotification (String msg){

        Log.d(TAG, "Preparing to send notification..." + msg);

        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, FriendActivity.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_person_black_36dp).setContentTitle("Start Up project Test")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully");


    }
}
