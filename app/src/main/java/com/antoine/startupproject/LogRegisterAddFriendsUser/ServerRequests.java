package com.antoine.startupproject.LogRegisterAddFriendsUser;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antoine on 28/10/2015.
 */
public class ServerRequests {

    ProgressDialog progressDialog;

    public static final int CONNECTION_TIMEOUT = 1000*50;
    public static final String SERVER_ADDRESS = "http://antoine-duval.esy.es/";


    public ServerRequests(Context context){
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Processing");
        progressDialog.setMessage("Please wait...");

    }

    public void storeUserDataInBackground(User user, GetUserCallback userCallback){

        progressDialog.show();

        new StoreUserDataAsyncTask(user,userCallback).execute();

    }

    public void fetchUserDataInBackground(User user, GetUserCallback callback){
        progressDialog.show();
        new FetchUserDataAsyncTask(user, callback).execute();

    }

    public void changeUserDataInBackground(User user, GetUserCallback callback){
        progressDialog.show();
        new changeUserDataAsyncTask(user, callback).execute();

    }

    public void resetPasswordInBackground(User user, GetUserCallback callback){
        progressDialog.show();
        new resetPasswordAsyncTask(user, callback).execute();
    }

    public void SearchFriendInBackground(Friend sendFriend, GetFriendCallback callback){
        progressDialog.show();
        new SearchFriendAsyncTask(sendFriend, callback).execute();

    }

    public void AddFriendInBackground(Friend sendFriendRequest, GetFriendCallbackObject callback){

        progressDialog.show();
        new AddFriendAsyncTask(sendFriendRequest, callback).execute();
    }

    public void SearchRequestInBackground (Friend sendFriend, GetFriendCallbackObject callback){

       // progressDialog.show();
        new SearchRequestAsyncTask (sendFriend, callback).execute();
    }


    public void DisconnectInBackground(User user, GetUserCallback callback){

        progressDialog.show();
        new DisconnectAsyncTask(user, callback).execute();
    }



    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, User> {

        User user;
        GetUserCallback userCallback;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;

        }

        @Override
        protected User doInBackground(Void... params) {



            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("mail", user.mail));
            dataToSend.add(new BasicNameValuePair("age", user.age + ""));
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));
            dataToSend.add(new BasicNameValuePair("userphone", user.userphone + ""));
            dataToSend.add(new BasicNameValuePair("image", user.encodedimage));
            dataToSend.add(new BasicNameValuePair("dataToChange", ""));
            dataToSend.add(new BasicNameValuePair("tag", "Register"));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register4.php");

            User returnedUser = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);

                String response = jObject.getString("response");

                if (jObject.length() == 0){

                    returnedUser = null;

                }else {

                    if (response.equals("UserMailInUse")) {

                        returnedUser = new User(0,user.username, user.age, user.mail, user.password, user.userphone, user.encodedimage, response, "");

                    } else {

                        returnedUser = new User(0,user.username, user.age, user.mail, user.password, user.userphone, user.encodedimage);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return  returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);

        }
    }

    public class FetchUserDataAsyncTask extends AsyncTask<Void, Void, User>{
        User user;
        GetUserCallback userCallback;
        int age = 0;
        int user_id = 0;
        int userphone = 0;
        String username = "";
        String jsonNewFriend = "";
        String jsonFriend = "";

        public FetchUserDataAsyncTask(User user, GetUserCallback userCallback){
            this.user = user;
            this.userCallback = userCallback;

        }


        @Override
        protected User doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            dataToSend.add(new BasicNameValuePair("mail", user.mail));
            dataToSend.add(new BasicNameValuePair("password", user.password));
            dataToSend.add(new BasicNameValuePair("phoneID", user.phoneID));

            Log.e("phoneID", "PhoneID = "+user.phoneID);

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "FetchUserData2.php");

            String url = SERVER_ADDRESS + "pictures/" + user.mail + ".JPG";

            User returnedUser = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);

                URLConnection connection = new URL(url).openConnection();
                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);



                int test = jObject.length();


                if(jObject.length() == 0){

                    returnedUser = null;


                }else{

                    username = jObject.getString("username");
                    age = jObject.getInt("age");
                    userphone = jObject.getInt("userphone");
                    user_id = jObject.getInt("user_id");

                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
                    String encodedimage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


                    if (jObject.getJSONArray("friend").length() == 1){

                        jsonFriend = "";

                    }else{

                        JSONArray jArrayFriend = jObject.getJSONArray("friend");
                        JSONArray jArrayFriendPicture = new JSONArray();

                        for (int i = 1; i < jArrayFriend.length(); i++) {

                            JSONObject jObjFriend = jArrayFriend.getJSONObject(i);

                            String FriendMail = jObjFriend.getString("mail");
                            String FriendUsername = jObjFriend.getString("username");
                            int FriendID = jObjFriend.getInt("user_id");

                            String url2 = SERVER_ADDRESS + "pictures/" + FriendMail + ".JPG";

                            URLConnection connection2 = new URL(url2).openConnection();
                            connection2.setConnectTimeout(1000 * 30);
                            connection2.setReadTimeout(1000 * 30);


                            bitmap = BitmapFactory.decodeStream((InputStream) connection2.getContent(), null, null);

                            ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream2);
                            String encodedimage2 = Base64.encodeToString(byteArrayOutputStream2.toByteArray(), Base64.DEFAULT);

                            jObjFriend.put("mail", encodedimage2);
                            jObjFriend.put("username", FriendUsername);
                            jObjFriend.put("user_id", FriendID);

                            jArrayFriendPicture.put(jObjFriend);

                            Log.e("jOnjFriendString", jObjFriend.getString("mail"));

                        }
                        jsonFriend = jArrayFriendPicture.toString();

                    }

                    if (jObject.getJSONArray("newFriend").length() == 1){

                        jsonNewFriend = "";

                    }else{


                        JSONArray jArrayNewFriendReceive = jObject.getJSONArray("newFriend");
                        JSONArray jArrayNewFriendToLocalDB = new JSONArray();

                        for (int i = 1; i < jArrayNewFriendReceive.length(); i++) {

                            JSONObject jObjFriend = jArrayNewFriendReceive.getJSONObject(i);

                            String NewFriendMail = jObjFriend.getString("mail");
                            String NewFriendUsername = jObjFriend.getString("username");
                            int newFriendID = jObjFriend.getInt("from_user");
                            int request_id = jObjFriend.getInt("request_id");

                            String url2 = SERVER_ADDRESS + "pictures/" + NewFriendMail + ".JPG";

                            URLConnection connection2 = new URL(url2).openConnection();
                            connection2.setConnectTimeout(1000 * 30);
                            connection2.setReadTimeout(1000 * 30);


                            bitmap = BitmapFactory.decodeStream((InputStream) connection2.getContent(), null, null);

                            ByteArrayOutputStream byteArrayOutputStream3 = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream3);
                            String encodedimage3 = Base64.encodeToString(byteArrayOutputStream3.toByteArray(), Base64.DEFAULT);

                            jObjFriend.put("mail", encodedimage3);
                            jObjFriend.put("username", NewFriendUsername);
                            jObjFriend.put("user_id", newFriendID);
                            jObjFriend.put("request_id", request_id);

                            jArrayNewFriendToLocalDB.put(jObjFriend);

                            Log.e("jOnjFriendString", jObjFriend.getString("mail"));

                        }
                        jsonNewFriend = jArrayNewFriendToLocalDB.toString();

                    }


                    returnedUser = new User(user_id,username, age, user.mail, user.password, userphone, encodedimage,jsonFriend ,jsonNewFriend );

                }

            } catch (Exception e) {
                e.printStackTrace();
            }



            return returnedUser;


        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
        }
    }

    public class resetPasswordAsyncTask extends AsyncTask<Void,Void,User>{
        User user;
        GetUserCallback userCallback;

        public resetPasswordAsyncTask(User user, GetUserCallback userCallback) {
            this.user = user;
            this.userCallback = userCallback;
        }

        @Override
        protected User doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();


            dataToSend.add(new BasicNameValuePair("mail", user.mail));
            dataToSend.add(new BasicNameValuePair("tag", "forgotPassword"));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register4.php");


            User returnedUser = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);




                if(jObject.length() == 0){

                    returnedUser = null;

                }else{

                    String dataChanged = jObject.getString("dataChanged");

                    returnedUser = new User(dataChanged);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }



            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {

            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(user);
        }
    }

    public class changeUserDataAsyncTask extends AsyncTask<Void, Void, User> {

        User user;
        GetUserCallback userCallback;

        public changeUserDataAsyncTask(User user, GetUserCallback userCallback) {

            this.user = user;
            this.userCallback = userCallback;


        }

        @Override
        protected User doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();


            dataToSend.add(new BasicNameValuePair("user_id",user.user_id + ""));
            dataToSend.add(new BasicNameValuePair("mail", user.mail));
            dataToSend.add(new BasicNameValuePair("age", user.age + ""));
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));
            dataToSend.add(new BasicNameValuePair("userphone", user.userphone + ""));
            dataToSend.add(new BasicNameValuePair("image", user.encodedimage));
            dataToSend.add(new BasicNameValuePair("dataToChange", user.dataToChange));
            dataToSend.add(new BasicNameValuePair("tag", user.tag));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Register4.php");

            // String url = SERVER_ADDRESS + "pictures/" + user.mail + ".JPG";

            User returnedUser = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);


                //URLConnection connection = new URL(url).openConnection();
                //connection.setConnectTimeout(1000 * 30);
                //connection.setReadTimeout(1000 * 30);


                if(jObject.length() == 0){

                    returnedUser = null;

                }else if(jObject.getInt("response") == 0) {

                    returnedUser = null;


                }else{

                        String dataChanged = jObject.getString("dataChanged");

                        if (dataChanged.equals("password")) {

                            String password = jObject.getString("dataToChange");

                            returnedUser = new User(user.user_id,user.username, user.age, user.mail, password, user.userphone, user.encodedimage);
                        }
                        if (dataChanged.equals("username")){

                            String username = jObject.getString("dataToChange");

                            returnedUser = new User(user.user_id,username, user.age, user.mail, user.password, user.userphone, user.encodedimage);
                        }
                        if (dataChanged.equals("image")){


                            returnedUser = new User(user.user_id,user.username, user.age, user.mail, user.password, user.userphone, user.dataToChange);
                        }


                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            return returnedUser;

        }

        @Override
        protected void onPostExecute(User returnedUser) {
            progressDialog.dismiss();
            userCallback.done(returnedUser);
            super.onPostExecute(returnedUser);
        }


    }

    public class SearchFriendAsyncTask extends AsyncTask<Void, Void, ArrayList<Friend>> {

        GetFriendCallback friendCallback;
        Friend sendFriend;

        public SearchFriendAsyncTask(Friend sendFriend, GetFriendCallback friendCallback) {

            this.sendFriend = sendFriend;
            this.friendCallback = friendCallback;
        }
        @Override
        protected ArrayList<Friend> doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();


            String friendUsername = sendFriend.friendUsername;
            String tag = sendFriend.friendPicture;
            int myUserId = sendFriend.friendID;


            dataToSend.add(new BasicNameValuePair("friendUsername", friendUsername));
            dataToSend.add(new BasicNameValuePair("tag", tag));
            dataToSend.add(new BasicNameValuePair("user_id", myUserId + ""));


            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Friends3.php");


            //Friend showJson = null;

            ArrayList<Friend> friendsfound = null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);

                friendsfound = new ArrayList<Friend>();

                JSONObject jsonObject = new JSONObject(result);
                JSONArray friendsArray = jsonObject.getJSONArray("friends");

                //showJson = new Friend(result, "tag");

                for (int i = 0; i < friendsArray.length(); i++) {

                    JSONObject jsonfriend = friendsArray.getJSONObject(i);
                    int user_id = jsonfriend.getInt("user_id");
                    String mail = jsonfriend.getString("mail");

                    String url = SERVER_ADDRESS + "pictures/" + mail + ".JPG";

                    URLConnection connection = new URL(url).openConnection();
                    connection.setConnectTimeout(1000 * 30);
                    connection.setReadTimeout(1000 * 30);

                    Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
                    String encodedimage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

                    String username = jsonfriend.getString("username");
                    Friend FriendFoundInDB = new Friend(user_id, username, encodedimage);

                    friendsfound.add(FriendFoundInDB);

                }



            } catch (Exception e) {
                e.printStackTrace();
            }

            return friendsfound;
        }

        @Override
        protected void onPostExecute(ArrayList<Friend> friendsfound) {
            progressDialog.dismiss();
            friendCallback.done(friendsfound);
            super.onPostExecute(friendsfound);
        }

    }
    public class AddFriendAsyncTask extends AsyncTask<Void,Void,Friend>{
        Friend friend;
        GetFriendCallbackObject friendCallback;

        public AddFriendAsyncTask(Friend sendFriendRequest, GetFriendCallbackObject friendCallback) {
            this.friend = sendFriendRequest;
            this.friendCallback = friendCallback;
        }

        @Override
        protected Friend doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();


            dataToSend.add(new BasicNameValuePair("friendToSendRequest", friend.friendID+""));
            dataToSend.add(new BasicNameValuePair("tag", friend.tag));
            dataToSend.add(new BasicNameValuePair("my_ID", friend.My_ID+""));
            dataToSend.add(new BasicNameValuePair("request_id", friend.request_id+""));
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "sendFriendRequest.php");


            Friend response = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);



                Log.e("JsonResponse", result );

                if(jObject.length() == 0){

                    response = null;

                }else {

                    int checkJson = jObject.getInt("response");


                    if (checkJson == 0){

                        response = new Friend(1,"","",0);
                    }
                    else if (checkJson == 1){

                        response = new Friend(friend.friendID,"","",1);
                    }
                    else if (checkJson == 2){

                        response = new Friend(friend.friendID,"","",2);
                    }
                    else if (checkJson == 4){

                        int user_id = jObject.getInt("user_id");
                        String user_mail = jObject.getString("mail");
                        String username = jObject.getString("username");



                        String url = SERVER_ADDRESS + "pictures/" + user_mail + ".JPG";

                        URLConnection connection = new URL(url).openConnection();
                        connection.setConnectTimeout(1000 * 30);
                        connection.setReadTimeout(1000 * 30);

                        Bitmap bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);

                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
                        String encodedimage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);


                        response = new Friend(user_id,username,encodedimage,checkJson);

                        Log.e("New Friend Picture", encodedimage );
                    }



                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(Friend response) {

            progressDialog.dismiss();
            try {
                friendCallback.done(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(response);
        }
    }

    public class SearchRequestAsyncTask extends AsyncTask<Void, Void, Friend> {


        GetFriendCallbackObject friendCallback;
        Friend sendFriend;

        public SearchRequestAsyncTask(Friend sendFriend, GetFriendCallbackObject friendCallback) {

            this.sendFriend = sendFriend;
            this.friendCallback = friendCallback;
        }
        @Override
        protected Friend doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();

            int my_id = sendFriend.response;


            dataToSend.add(new BasicNameValuePair("my_id", my_id+""));


            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "SearchRequest.php");


            ArrayList<Friend> friendsfound = null;
            Friend FriendFoundInDB =null;
            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);

                FriendFoundInDB = new Friend(0, "", result, 0);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return FriendFoundInDB;
        }

        @Override
        protected void onPostExecute(Friend friendsfound) {
            progressDialog.dismiss();
            try {
                friendCallback.done(friendsfound);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(friendsfound);
        }

    }


    private class DisconnectAsyncTask extends AsyncTask<Void, Void, User>{


        private User user;
        private GetUserCallback callback;

        public DisconnectAsyncTask(User user, GetUserCallback callback){

            this.user = user;
            this.callback = callback;

        }

        @Override
        protected User doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();


            dataToSend.add(new BasicNameValuePair("user_id", user.response+""));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "Logout.php");


            User user_logout = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);
                HttpEntity entity = httpResponse.getEntity();
                String result = EntityUtils.toString(entity);
                JSONObject jObject = new JSONObject(result);


                Log.e("response", jObject.getInt("response")+"");

                user_logout = new User(jObject.getInt("response"));


            } catch (Exception e) {
                e.printStackTrace();
            }



            return user_logout;
        }


        @Override
        protected void onPostExecute(User user_logout) {

            progressDialog.dismiss();
            callback.done(user_logout);
            super.onPostExecute(user_logout);
        }
    }



}
