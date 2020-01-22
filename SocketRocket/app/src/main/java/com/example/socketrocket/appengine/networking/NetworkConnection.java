package com.example.socketrocket.appengine.networking;

import com.example.socketrocket.appengine.database.reflect.objects.Score;

import org.json.JSONException;
import org.json.JSONObject;

public class NetworkConnection {

    // only for debugging
    public static int sendTestRequest(NetworkRequestDelegate delegate) {
        return NetworkController.sharedInstance().generateRequest(delegate, "/", "GET", null, null, null);
    }

    public static int sendRegistrationRequest(NetworkRequestDelegate delegate, String email, String username, String password) {
        try {
            JSONObject json = new JSONObject();
            json.accumulate("name", username);
            json.accumulate("email", email);
            json.accumulate("password", password);
            String jsonRaw = json.toString();
            return NetworkController.sharedInstance().generateRequest(delegate, "/signup", "POST", null, jsonRaw);
        } catch (JSONException e) {
            // TODO: log error
            return NetworkRequestDelegate.INVALID_REQUEST_ID;
        }
    }

    public static int sendSignUpRequest(NetworkRequestDelegate delegate, String name, String email, String password) {
        try {
            JSONObject json = new JSONObject();
            json.accumulate("name", name);
            json.accumulate("email", email);
            json.accumulate("password", password);
            String jsonRaw = json.toString();
            return NetworkController.sharedInstance().generateRequest(delegate, "/signup.php", "GET", null, jsonRaw, null);
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
            return NetworkController.sharedInstance().generateRequest(delegate, "/login.php", "GET", null, jsonRaw, null);
        } catch (JSONException e) {
            // TODO: log error
            return NetworkRequestDelegate.INVALID_REQUEST_ID;
        }
    }

    public static int sendPostNewHighscoreRequest(NetworkRequestDelegate delegate, String token, Score highscore) {
        return -1; // TODO: Implementieren
    }

    public static int sendLoadMyHighscoresRequest(NetworkRequestDelegate delegate, String token) {
        return NetworkController.sharedInstance().generateRequest(delegate, "/myscores.php", "GET", null, null, token);
    }

    public static int sendLoadAllHighscoresRequest(NetworkRequestDelegate delegate) {
        return NetworkController.sharedInstance().generateRequest(delegate, "/topscores-all.php", "GET", null, null, null);
    }

}
