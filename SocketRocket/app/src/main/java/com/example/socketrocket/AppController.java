package com.example.socketrocket;

import android.app.Activity;

import com.example.socketrocket.appengine.database.reflect.objects.Score;
import com.example.socketrocket.appengine.networking.NetworkConnection;
import com.example.socketrocket.appengine.networking.NetworkError;
import com.example.socketrocket.appengine.networking.NetworkRequestDelegate;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppController implements NetworkRequestDelegate {

    private NetworkRequestDelegate parentActivity;
    private ArrayList<Score> scoreCache = new ArrayList<>();
    private int retryCount = 3;
    private long retryDelay = 1000;
    private boolean hasResults = false;
    private boolean isLoading = false;

    public AppController(NetworkRequestDelegate parentActivity) {
        this.parentActivity = parentActivity;
    }

    protected void loadDataOnAppstart() {
        if(this.isLoading) {
            //System.out.println("AppController is already loading");
            return;
        } else {
            this.startLoadingHighscores();
        }
    }

    private void startLoadingHighscores() {
        this.isLoading = true;
        //System.out.println("Starting highscores request..");
        NetworkConnection.sendLoadAllHighscoresRequest(parentActivity);
    }

    protected boolean getIsLoading() {
        return this.isLoading;
    }

    protected boolean getHasResults() {
        return this.hasResults;
    }

    protected ArrayList<Score> getCachedScores() {
        return this.scoreCache;
    }


    // MARK: - NetworkRequestDelegate

    @Override
    public void didRecieveNetworkResponse(int requestId, JSONObject[] data) {
        this.isLoading = false;
        this.parseScoreResult(data);
    }

    @Override
    public void didRecieveNetworkError(int requestId, NetworkError error) {
        if(this.retryCount > 0) {
            new Thread(new Runnable() {
                public void run() {
                    //System.out.println("network error, retry");
                    retryCount -= 1;
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.exit(0);
                    }
                    startLoadingHighscores();
                }
            }).start();
        } else {
            System.out.println("AppController: finished score loading with error");
            this.isLoading = false;
        }
    }

    private void parseScoreResult(JSONObject[] data) {
        try {
            this.scoreCache = new ArrayList<>();
            for (JSONObject o : data) {
                Score newScore = new Score();
                newScore.user_name = (String)o.get("user_name");
                newScore.timestamp = Long.parseLong((String)o.get("timestamp"));
                newScore.amount = Long.parseLong((String)o.get("amount"));
                this.scoreCache.add(newScore);
            }
        } catch (Exception e) {
            this.scoreCache = new ArrayList<>();
            this.hasResults = false;
        }
        System.out.println("AppController: finished score loading with " + this.scoreCache.size() + " results.");
        this.hasResults = true;
    }
}
