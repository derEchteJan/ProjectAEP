package com.example.socketrocket.appengine.networking;

import android.app.Activity;

import com.example.socketrocket.appengine.BackgroundTaskHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class NetworkController {

    //private static final String BASE_URL = "http://76313ec7-f618-40af-bdc1-c85c58cf4bff.mock.pstmn.io"; // mockup server
    private static final String BASE_URL = "http://172.20.10.10"; // raspberry pi via ip
    private static String tokenKey = "token";
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

    protected int generateRequest(NetworkRequestDelegate caller, String path, String httpMethod, String[][] headers, String payload, String token) {
        if (caller == null) return NetworkRequestDelegate.INVALID_REQUEST_ID;
        int newRequestId = generateRequestId();
        URL url;
        try {
            url = new URL(BASE_URL + path);
        } catch (MalformedURLException e) {
            return NetworkRequestDelegate.INVALID_REQUEST_ID;
        }
        String[][] cleanedHeaders = cleanHeaders(headers, token);
        this.startRequestTaskThread(caller, newRequestId,url, httpMethod, cleanedHeaders, payload);
        return newRequestId;
    }

    // NetworkRequestDelegate

    protected void forwardDidRecieveResponse(final NetworkRequestDelegate caller, final int requestId, final String response) {
        final Activity contextOfCaller = (Activity) caller;
        final JSONObject[] results = this.parseResponse(response);
        if(results != null) {
            contextOfCaller.runOnUiThread(new Runnable() {
                public void run() {
                    caller.didRecieveNetworkResponse(requestId, results);
                }
            });
        } else {
            // Parsing error
            contextOfCaller.runOnUiThread(new Runnable(){
                public void run(){
                    caller.didRecieveNetworkError(requestId, new NetworkError(NetworkError.Type.badResponse, "JSON Parsing Error"));
                }
            });
        }
    }

    protected void forwardDidRecieveError(final NetworkRequestDelegate caller, final int requestId, final NetworkError error) {
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
            // Debug delay
            //try{Thread.sleep(1000);}catch(InterruptedException e){}
            //
            HttpURLConnection connection = null;
            int statusCode;
            try {
                // open connection
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod(httpMethod);
                connection.setRequestProperty(contentTypeKey, contentTypeValue);
                // set headers
                for (String[] header: headers) connection.setRequestProperty(header[0], header[1]);
                connection.setUseCaches(false);
                // send request payload
                if(payload != null) {
                    connection.setDoOutput(true);
                    DataOutputStream wr = new DataOutputStream (
                            connection.getOutputStream());
                    wr.writeBytes(payload);
                    wr.close();
                }
                statusCode = connection.getResponseCode();
                if (statusCode != 200) {
                    forwardDidRecieveError(caller, requestId, new NetworkError(NetworkError.Type.httpStatus, connection.getResponseMessage(), statusCode));
                    return;
                }
                // read response payload
                String payload = "";
                try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    for(String nextLine = reader.readLine(); nextLine != null; nextLine = reader.readLine()) payload += nextLine + '\n';
                } catch (IOException e) {
                    // connection closed
                    forwardDidRecieveError(caller, requestId, new NetworkError(NetworkError.Type.connectionClosed));
                    return;
                }
                forwardDidRecieveResponse(caller, requestId, payload);
                return;
            } catch (MalformedURLException | ProtocolException e) {
                // fatal, should never happen, must be ensured by connection / caller
                e.printStackTrace(); System.exit(0);
            } catch (IOException e) {
                forwardDidRecieveError(caller, requestId, new NetworkError(NetworkError.Type.notReachable));
                return;
            }
        }}).start();
    }

    private static String[][] cleanHeaders(String[][] headers, String token) {
        if (headers == null && token == null) return new String[0][0];
        else {
            int entryCount = 0;
            if(headers != null) {
                for (String[] header: headers) {
                    if (header != null && header.length == 2 && header[0] != null && header[1] != null) entryCount++;
                }
            }
            String[][] cleanedHeaders = new String[entryCount + (token == null ? 0:1)][2];
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
            if(token != null) {
                cleanedHeaders[entryCount] = new String[2];
                cleanedHeaders[entryCount][0] = tokenKey;
                cleanedHeaders[entryCount][1] = token;
            }
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
