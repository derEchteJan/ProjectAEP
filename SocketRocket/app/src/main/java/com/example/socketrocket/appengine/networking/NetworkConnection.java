package com.example.socketrocket.appengine.networking;

public class NetworkConnection {

    public static int sendTestRequest(NetworkRequestDelegate delegate) {
        // Beispiel für eine methode, alle weiteren nach diesem schema. /test liefert ein beispiel json objekt zurück
        // Die mock responses kann man im postman einstellen und hochladen
        return NetworkController.sharedInstance().generateRequest(delegate, "/test", "GET", null, null);
    }

    public static int sendRegistrationRequest(String email, String username, String password) {
        // TODO: implementieren
        return 0;
    }

    public static int sendLoginRequest(String username, String password) {
        // TODO: implementieren
        return 0;
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
