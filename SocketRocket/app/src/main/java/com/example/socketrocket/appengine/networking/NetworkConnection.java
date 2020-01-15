package com.example.socketrocket.appengine.networking;

import org.json.JSONObject;

public class NetworkConnection {

    public static int sendTestRequest(NetworkRequestDelegate delegate) {
        // Beispiel für eine methode, alle weiteren nach diesem schema. /test liefert ein beispiel json objekt zurück
        // Die mock responses kann man im postman einstellen und hochladen
        return NetworkController.sharedInstance().generateRequest(delegate, "/test", "GET", null, null);
    }

    public static int sendRegistrationRequest(NetworkRequestDelegate delegate, String email, String username, String password) {
        try {
            JSONObject json = new JSONObject();
            json.accumulate("name", username);
            json.accumulate("email", email);
            json.accumulate("password", password);
            String jsonRaw = json.toString();
            return NetworkController.sharedInstance().generateRequest(delegate, "/register", "POST", null, jsonRaw);
        } catch (Exception e) {
            return NetworkRequestDelegate.INVALID_REQUEST_ID;
        }
    }

    public static int sendLoginRequest(NetworkRequestDelegate delegate, String email, String password) {
        try {
            JSONObject json = new JSONObject();
            json.accumulate("email", email);
            json.accumulate("password", password);
            String jsonRaw = json.toString();
            return NetworkController.sharedInstance().generateRequest(delegate, "/login", "GET", null, jsonRaw);
        } catch (Exception e) {
            return NetworkRequestDelegate.INVALID_REQUEST_ID;
        }
    }

    public static int sendLoadUserDataRequest() {
        // TODO: implementieren
        return 0;
    }

    public static int sendLoadHighscoresRequest() {
        // TODO: implementieren
        return 0;
    }

}
