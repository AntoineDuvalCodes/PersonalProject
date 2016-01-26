package com.antoine.startupproject;

import android.content.Context;
import android.content.SharedPreferences;

import com.antoine.startupproject.LogRegisterAddFriendsUser.User;

/**
 * Created by Antoine on 02/12/2015.
 */
public class SavedToPreferences {

    SharedPreferences sharedPreferences;
    public static final String SP_NAME = "userDetails";

    public SavedToPreferences(Context context){
        sharedPreferences = context.getSharedPreferences(SP_NAME, 0);
    }

    public void storeDrawerAsSaw(boolean value){
        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        spEditor.putBoolean("user_learned_drawer", value);
        spEditor.commit();

    }


    public boolean isDrawerWasSee(){

        return sharedPreferences.getBoolean("user_learned_drawer", false);
    }



    public void setVisibleCategoriesOnMap(String jsonList){
        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        spEditor.putString("visibleCategoriesOnMap", jsonList);
        spEditor.commit();
    }

    public String getVisibleCategoriesOnMap(){

        return sharedPreferences.getString("visibleCategoriesOnMap", "null");
    }




    public void setUserVisiblility(boolean visible){
        SharedPreferences.Editor spEditor = sharedPreferences.edit();
        
        spEditor.putBoolean("isUserVisible", visible);
        spEditor.commit();
    }

    public boolean getUserVisibility (){

        return sharedPreferences.getBoolean("isUserVisible", false);
    }


    public void setTheFirstUtilisation(boolean value){
        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        spEditor.putBoolean("isTheFirstUtilisation", value);
        spEditor.commit();
    }

    public boolean isFistUtilisation(){

        return sharedPreferences.getBoolean("isTheFirstUtilisation", true);
    }

    public void storeUserData(User user){
        SharedPreferences.Editor spEditor = sharedPreferences.edit();


        spEditor.putInt("user_id",user.user_id);
        spEditor.putString("mail", user.mail);
        spEditor.putInt("age", user.age);
        spEditor.putString("username", user.username);
        spEditor.putString("password", user.password);
        spEditor.putInt("userPhone", user.userphone);
        spEditor.putString("image", user.encodedimage);
        spEditor.putString("password", user.password);
        spEditor.commit();

    }
    public void storeUserFriend(String jObjectString){

        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        spEditor.putString("MyFriendJson", jObjectString);
        spEditor.commit();



    }
    public String getFriendInLoggedInUser(){

        String jsonFriend = sharedPreferences.getString("MyFriendJson", "");


        return jsonFriend;
    }

    public User getLoggedInUser(){

        int user_id = sharedPreferences.getInt("user_id", -1);
        String mail = sharedPreferences.getString("mail", "");
        String username = sharedPreferences.getString("username","");
        String password = sharedPreferences.getString("password","");
        int age = sharedPreferences.getInt("age", -1);
        int userPhone = sharedPreferences.getInt("userPhone", -1);
        String encodedimage = sharedPreferences.getString("image", "");


        User storedUser = new User(user_id,username, age, mail,password, userPhone, encodedimage);

        return storedUser;
    }


    public void setUserLoggedIn(boolean loggedIn){
        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        spEditor.putBoolean("loggedIn", loggedIn);
        spEditor.commit();

    }

    public boolean getUserLoggedIn() {

        if (sharedPreferences.getBoolean("loggedIn", false) == true) {
            return true;
        }else{
            return false;
        }
    }

    public void clearUserData(){
        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        spEditor.clear();
        spEditor.commit();
    }

    public void setNewFriendRequest (String JsonFriendRequest){
        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        spEditor.putString("JsonFriendRequest", JsonFriendRequest);
        spEditor.commit();

    }

    public String getNewFriendRequest(){

        String jsonFriendRequest = sharedPreferences.getString("JsonFriendRequest", "");

        return jsonFriendRequest;
    }

    public void setFriendLike(String jsonFriendLike){

        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        spEditor.putString("jsonFriendLike", jsonFriendLike);
        spEditor.commit();

    }

    public String getFriendLike(){

        String friendLike = sharedPreferences.getString("jsonFriendLike", "");

        return friendLike;
    }

    public void setPlacesILiked(String jsonMyplaces){

        SharedPreferences.Editor spEditor = sharedPreferences.edit();

        spEditor.putString("placesILiked", jsonMyplaces);
        spEditor.commit();

    }
    public String getPlacesILiked(){

        String myPlaces = sharedPreferences.getString("placesILiked","");
        return myPlaces;
    }



}
