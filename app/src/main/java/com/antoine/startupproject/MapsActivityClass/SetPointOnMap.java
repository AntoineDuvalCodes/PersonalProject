package com.antoine.startupproject.MapsActivityClass;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by Antoine on 17/12/2015.
 */
public class SetPointOnMap {


    public static final int CONNECTION_TIMEOUT = 1000*50;
    public static final String SERVER_ADDRESS = "http://antoine-duval.esy.es/";


    public SetPointOnMap(Context context){

    }


    public void getPlaceDataFromPersonalDB(String place_id, GetLocationCallback callback){

        new getPlaceDataFromPersonalDBAsyncTask(place_id, callback).execute();

    }

    public void getPointFromGooglePlace(String url, GetLocationCallback locationCallback){

        new GetPointFromGooglePlaceAsyncTask(url, locationCallback).execute();

    }

    public void getPlaceDetailFromGooglePlace(String url, GetLocationCallback callback){

        new getPlaceDetailFromGooglePlaceAsyncTask(url, callback).execute();
    }


    public class GetPointFromGooglePlaceAsyncTask extends AsyncTask<Void, Void, String>{

        private String url;
        private GetLocationCallback locationCallback;

        public GetPointFromGooglePlaceAsyncTask(String url, GetLocationCallback locationCallback){

            this.url = url;
            this.locationCallback = locationCallback;


        }

        @Override
        protected String doInBackground(Void... params) {


            String placefound = null;

            StringBuffer buffer_string = new StringBuffer(url);

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(buffer_string.toString());


            try {
                HttpResponse response = httpClient.execute(httpGet);

                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);

                placefound = result;
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e("DO IN BACKGROUND", "YES");

            return placefound;
        }

        @Override
        protected void onPostExecute(String placesFound) {
            locationCallback.done(placesFound);

            super.onPostExecute(placesFound);
        }
    }



    public class GetIconFromGooglePlaceAPIAsyncTask extends AsyncTask<Void,Void,Bitmap>{

        private String url;
        private GetBitmapDataCallback callback;

        public GetIconFromGooglePlaceAPIAsyncTask(String url, GetBitmapDataCallback callback){

            this.url = url;
            this.callback = callback;

        }


        @Override
        protected Bitmap doInBackground(Void... params) {

            URLConnection connection = null;
            Bitmap bitmap = null;

            try {
                connection = new URL(url).openConnection();

                connection.setConnectTimeout(1000 * 30);
                connection.setReadTimeout(1000 * 30);

                bitmap = BitmapFactory.decodeStream((InputStream) connection.getContent(), null, null);


            } catch (IOException e) {
                e.printStackTrace();
            }


            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            callback.done(bitmap);
            super.onPostExecute(bitmap);
        }
    }


    public class getPlaceDetailFromGooglePlaceAsyncTask extends AsyncTask<Void, Void, String>{

        private String url;
        private GetLocationCallback callback;

        public getPlaceDetailFromGooglePlaceAsyncTask(String url , GetLocationCallback callback){

            this.url = url;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... params) {


            String placeDetail = null;

            StringBuffer buffer_string = new StringBuffer(url);

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(buffer_string.toString());


            try {
                HttpResponse response = httpClient.execute(httpGet);

                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity);

                placeDetail = result;


            } catch (IOException e) {
                e.printStackTrace();
            }



            return placeDetail;
        }


        @Override
        protected void onPostExecute(String placeDetail) {
            callback.done(placeDetail);
            super.onPostExecute(placeDetail);
        }
    }

    public class getPlaceDataFromPersonalDBAsyncTask extends AsyncTask<Void, Void,String>{

        private String place_id;
        private GetLocationCallback  callback;

        public getPlaceDataFromPersonalDBAsyncTask(String place_id, GetLocationCallback callback){

            this.place_id = place_id;
            this.callback = callback;

        }

        @Override
        protected String doInBackground(Void... params) {

            ArrayList<NameValuePair> dataToSend = new ArrayList<>();


            dataToSend.add(new BasicNameValuePair("place_id", place_id));

            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams, CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams, CONNECTION_TIMEOUT);

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS + "GetPlaceData.php");

            String result = null;

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                HttpResponse httpResponse = client.execute(post);

                HttpEntity entity = httpResponse.getEntity();
                result = EntityUtils.toString(entity);


            } catch (Exception e) {
                e.printStackTrace();
            }


            return result;
        }

        @Override
        protected void onPostExecute(String result) {

            callback.done(result);
            super.onPostExecute(result);

        }
    }

}
