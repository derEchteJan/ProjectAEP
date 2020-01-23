package com.example.socketrocket;

import android.app.Activity;

import com.example.socketrocket.appengine.database.reflect.objects.Score;

import java.util.ArrayList;

public class AppController {

    private ArrayList<Score> scoreCache = new ArrayList<>();
    private Activity context;

    public AppController(Activity context) {
        
    }

    protected void startLoadingHighscores() {

    }

}
