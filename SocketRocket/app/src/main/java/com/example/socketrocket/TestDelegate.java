package com.example.socketrocket;

import com.example.socketrocket.appengine.networking.NetworkErrorType;
import com.example.socketrocket.appengine.networking.NetworkRequestDelegate;
import com.example.socketrocket.appengine.networking.NetworkConnection;

import org.json.JSONObject;

public class TestDelegate implements NetworkRequestDelegate {

    // Beispiel wie man die NetworkConnection benutzen kann wenn man NetworkRequestDelegate implementiert

    public void runTest() {
        System.out.println("<- start sending test request");
        NetworkConnection.sendTestRequest(this);
    }


    // MARK: - NetworkRequestDelegate

    @Override
    public void didRecieveNetworkResponse(int requestId, JSONObject data) {
        System.out.println("-> callback: did recieve response");
        System.out.println("result: \n" + data);
    }

    @Override
    public void didRecieveNetworkError(int requestId, NetworkErrorType errorType)  {
        System.out.println("-> callback did recieve error");
        System.out.println("error: " + errorType);
    }
}
