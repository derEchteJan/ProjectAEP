package com.example.socketrocket.appengine.networking;

import org.json.JSONObject;

public interface NetworkRequestDelegate {

    public static final int INVALID_REQUEST_ID = -1;

    void didRecieveNetworkResponse(int requestId, JSONObject[] data);
    void didRecieveNetworkError(int requestId, NetworkError error);

}
