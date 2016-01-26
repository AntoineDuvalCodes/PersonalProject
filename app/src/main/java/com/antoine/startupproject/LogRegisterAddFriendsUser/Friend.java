package com.antoine.startupproject.LogRegisterAddFriendsUser;

import java.util.ArrayList;

/**
 * Created by Antoine on 03/12/2015.
 */
public class Friend {

    public String friendPicture;
    public String friendUsername;
    public String tag;
    String fromMail;
    String fromUsername;

    public int friendID;
    public int My_ID;
    public int response;
    public int request_id;


    public Friend (int My_ID, int friendID, String fromMail, String fromUsername ,String tag){

        this.My_ID = My_ID;
        this.friendID = friendID;
        this.fromMail = fromMail;
        this.fromUsername = fromUsername;
        this.tag = tag;
    }

    public Friend(int friendID, int request_id, String friendUsername, String friendPicture){

        this.friendID = friendID;
        this.request_id = request_id;
        this.friendUsername = friendUsername;
        this.friendPicture = friendPicture;

    }

    public Friend (int friendID, String friendUsername, String friendPicture){

        this.friendUsername = friendUsername;
        this.friendID = friendID;
        this.friendPicture = friendPicture;
    }
    public Friend (int response){

        this.response = response;

    }

    public Friend (int My_ID, int friendID, int request_id,String tag){

        this.My_ID = My_ID;
        this.friendID = friendID;
        this.request_id = request_id;
        this.tag = tag;


    }
    public Friend (int My_ID, int friendID, String tag){

        this.My_ID = My_ID;
        this.friendID = friendID;
        this.tag = tag;
    }

    public Friend (int friendID, String friendUsername, String friendPicture,int request_id ) {

        this.friendID = friendID;
        this.friendUsername = friendUsername;
        this.friendPicture = friendPicture;
        this.request_id = request_id;
    }
}
