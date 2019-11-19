package com.example.socketrocket;

import android.content.Context;

import com.example.socketrocket.appengine.database.DatabaseConnection;
import com.example.socketrocket.appengine.database.reflect.objects.Score;
import com.example.socketrocket.appengine.networking.NetworkErrorType;
import com.example.socketrocket.appengine.networking.NetworkRequestDelegate;

import org.json.JSONObject;

public class TestDelegate implements NetworkRequestDelegate {

    // Beispiel wie man die NetworkConnection benutzen kann wenn man NetworkRequestDelegate implementiert

    public void runTest(Context context) {
        // TODO: Tests here
        //

        DatabaseConnection.sharedInstance().initDatabase(context);

        // make new score
        Score newScore = DatabaseConnection.sharedInstance().getScore(6);

        DatabaseConnection.sharedInstance().deleteScore(newScore);


        // Print all scores:
        for (Score score: DatabaseConnection.sharedInstance().getAllScores()) {
            System.out.println(score);
        }

        System.out.println(); // Breakpoint
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
