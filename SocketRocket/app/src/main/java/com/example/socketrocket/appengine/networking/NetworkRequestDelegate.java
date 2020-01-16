package com.example.socketrocket.appengine.networking;

import android.app.Activity;

import com.example.socketrocket.appengine.networking.NetworkErrorType;

import org.json.JSONObject;


public abstract class NetworkRequestDelegate extends Activity {

    public static final int INVALID_REQUEST_ID = -1;

    public abstract void didRecieveNetworkResponse(int requestId, JSONObject data);
    public abstract void didRecieveNetworkError(int requestId, NetworkErrorType errorType);

}
