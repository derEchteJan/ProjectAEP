package com.example.socketrocket.networking;

import org.json.JSONObject;

public interface NetworkRequestDelegate {

    public static final int INVALID_REQUEST_ID = -1;

    public abstract void didRecieveNetworkResponse(int requestId, JSONObject data);
    public abstract void didRecieveNetworkError(int requestId, NetworkErrorType errorType);

}
