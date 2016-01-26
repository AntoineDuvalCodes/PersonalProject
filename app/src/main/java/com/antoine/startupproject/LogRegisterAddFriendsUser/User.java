package com.antoine.startupproject.LogRegisterAddFriendsUser;

/**
 * Created by Antoine on 03/12/2015.
 */
public class User {

    public String username;
    public String mail;
    public String password;
    public String encodedimage;
    public String dataToChange;
    public String tag;
    public String phoneID;
    public int age;
    public int userphone;
    public int user_id;
    public int response;

    public User (int user_id,String username, int age, String mail, String password, int userphone, String encodedimage){

        this.user_id = user_id;
        this.username = username;
        this.age = age;
        this.userphone = userphone;
        this.mail = mail;
        this.password = password;
        this.encodedimage = encodedimage;
        this.dataToChange = "";


    }


    public User (String mail, String password, String phoneID) {

        this.mail = mail;
        this.password = password;
        this.phoneID = phoneID;
        this.age = -1;
        this.username = "";
        this.userphone = -1;
        this.encodedimage = "";
        this.dataToChange = "";

    }

    public User (int user_id,String username, int age,String mail, String password,int userphone, String encodedimage, String dataToChange, String tag){

        this.user_id = user_id;
        this.mail = mail;
        this.password = password;
        this.age = age;
        this.username = username;
        this.userphone = userphone;
        this.encodedimage = encodedimage;
        this.dataToChange = dataToChange;
        this.tag = tag;
    }

    public User (String mail){

        this.mail = mail;
    }


    public User(int response){

        this.response = response;

    }
}
