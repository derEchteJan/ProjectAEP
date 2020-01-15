package com.example.socketrocket.appengine.networking;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkConnection {

    public static int sendTestRequest(NetworkRequestDelegate delegate) {
        return NetworkController.sharedInstance().generateRequest(delegate, "/test", "GET", null, null);
    }

    public static int sendRegistrationRequest(NetworkRequestDelegate delegate, String name, String email, String password) {
        try {
            JSONObject json = new JSONObject();
            json.accumulate("email", email);
            json.accumulate("name", name);
            json.accumulate("password", password);
            String jsonRaw = json.toString();
            return NetworkController.sharedInstance().generateRequest(delegate, "/signup", "POST", null, jsonRaw)   ;
        } catch (JSONException e) {
            // TODO: log error
            return NetworkRequestDelegate.INVALID_REQUEST_ID;
        }
    }

    public static int sendLoginRequest(NetworkRequestDelegate delegate, String name, String password) {
        try {
            JSONObject json = new JSONObject();
            json.accumulate("name", name);
            json.accumulate("password", password);
            String jsonRaw = json.toString();
            return NetworkController.sharedInstance().generateRequest(delegate, "/login", "GET", null, jsonRaw);
        } catch (JSONException e) {
            // TODO: log error
            return NetworkRequestDelegate.INVALID_REQUEST_ID;
        }
    }

    public static int sendLoadUserDataRequest(NetworkRequestDelegate delegate) {
        return NetworkController.sharedInstance().generateRequest(delegate, "/user", "GET", null, null);
    }

    public static int sendLoadHighscoresRequest(NetworkRequestDelegate delegate) {
        return NetworkController.sharedInstance().generateRequest(delegate, "/highscores", "GET", null, null);
    }

}
