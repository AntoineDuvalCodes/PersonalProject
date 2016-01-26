package com.antoine.startupproject.LogRegisterAddFriendsUser;

import org.json.JSONException;

/**
 * Created by Antoine on 28/10/2015.
 */
public interface GetFriendCallbackObject {

    public abstract  void done(Friend response) throws JSONException;

}
