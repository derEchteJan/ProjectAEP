package com.example.socketrocket.appengine.networking;

import android.app.Activity;

import com.example.socketrocket.appengine.BackgroundTaskHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class NetworkController {

    private static final String BASE_URL = "http://76313ec7-f618-40af-bdc1-c85c58cf4bff.mock.pstmn.io"; // mockup server

    private static String tokenKey = "Postman-Token";
    private static String tokenValue = "c3597b90-80a9-48ef-8ae6-69d9ce1f91d1,6502beb6-f442-440d-896c-79ecca7db99e";
    private static String contentTypeKey = "Content-Type";
    private static String contentTypeValue = "Application/json";
    private static int requestIdCounter = 0;

    // singleton instance
    private static final NetworkController instance = new NetworkController();
    protected static NetworkController sharedInstance() {
        return instance;
    }

    private NetworkController() {}


    // MARK: - Methods

    protected int generateRequest(NetworkRequestDelegate caller, String path, String httpMethod, String[][] headers, String payload) {
        if (caller == null) return NetworkRequestDelegate.INVALID_REQUEST_ID;
        int newRequestId = generateRequestId();
        URL url;
        try {
            url = new URL(BASE_URL + path);
        } catch (MalformedURLException e) {
            return NetworkRequestDelegate.INVALID_REQUEST_ID;
        }
        String[][] cleanedHeaders = cleanHeaders(headers);
        this.startRequestTaskThread(caller, newRequestId,url, httpMethod, cleanedHeaders, payload);
        return newRequestId;
    }

    protected void forwardDidRecieveResponse(final NetworkRequestDelegate caller, final int requestId, final String response) {
        final Activity contextOfCaller = (Activity) caller;
        final JSONObject[] results = this.parseResponse(response);
        if(results != null) {
            contextOfCaller.runOnUiThread(new Runnable() {
                public void run() {
                    caller.didRecieveNetworkResponse(requestId, results);
                }
            });
        } else { // parsing error
            // TODO: Fehler loggen
            contextOfCaller.runOnUiThread(new Runnable(){
                public void run(){
                    caller.didRecieveNetworkError(requestId, NetworkErrorType.badResponse);
                }
            });
        }
    }

    protected void forwardDidRecieveError(final NetworkRequestDelegate caller, final int requestId, final NetworkErrorType error) {
        // TODO: Fehler loggen
        final Activity contextOfCaller = (Activity) caller;
        contextOfCaller.runOnUiThread(new Runnable(){
            public void run(){
                caller.didRecieveNetworkError(requestId, error);
            }
        });

    }

    // MARK: - subs

    private void startRequestTaskThread(final NetworkRequestDelegate caller, final int requestId, final URL url, final String httpMethod, final String[][] headers, final String payload) {
        new Thread(new Runnable() { public void run(){
            HttpURLConnection connection = null;
            int statusCode = -1;
            try {
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(httpMethod);
                connection.setRequestProperty(contentTypeKey, contentTypeValue);
                for (String[] header: headers) connection.setRequestProperty(header[0], header[1]);
                statusCode = connection.getResponseCode();
                if (statusCode >= 300 || statusCode < 0) {
                    forwardDidRecieveError(caller, requestId, NetworkErrorType.httpStatus);
                }
            } catch (MalformedURLException | ProtocolException e) {
                // fatal, these should never happen, must be ensured by controller
                e.printStackTrace(); System.exit(0);
            } catch (IOException e) {
                forwardDidRecieveError(caller, requestId, NetworkErrorType.notReachable);
                return;
            }
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String response = "";
                for(String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) {
                    response += nextLine + '\n';
                }
                forwardDidRecieveResponse(caller, requestId, response);
            } catch (IOException e) {
                forwardDidRecieveError(caller, requestId, NetworkErrorType.connectionClosed);
            }
        }}).start();
    }

    private static String[][] cleanHeaders(String[][] headers) {
        if (headers == null) return new String[0][0];
        else {
            int entryCount = 0;
            for (String[] header: headers) {
                if (header != null && header.length == 2 && header[0] != null && header[1] != null) entryCount++;
            }
            String[][] cleanedHeaders = new String[entryCount + 1][2];
            int copyIndex = 0;
            for (int i = 0; i < entryCount; i++) {
                String[] header = headers[i];
                if (header != null && header.length == 2 && header[0] != null && header[1] != null) {
                    cleanedHeaders[i][0] = header[0];
                    cleanedHeaders[i][1] = header[1];
                } else {
                    copyIndex++;
                    i--;
                }
            }
            cleanedHeaders[entryCount + 1][0] = "token";
            cleanedHeaders[entryCount + 1][1] = "value";
            return cleanedHeaders;
        }
    }

    private JSONObject[] parseResponse(String rawData) {
        try {
            JSONObject singleObject = new JSONObject(rawData);
            return new JSONObject[] {singleObject};
        } catch(JSONException e1) {
            try {
                JSONArray jsonArray = new JSONArray(rawData);
                JSONObject[] results = new JSONObject[jsonArray.length()];
                for(int i = 0; i < jsonArray.length(); i++) {
                    results[i] = jsonArray.getJSONObject(i);
                }
                return results;
            } catch(JSONException e2) {
                return null;
            }
        }
    }

    private static int generateRequestId() {
        return requestIdCounter++;
    }
}
