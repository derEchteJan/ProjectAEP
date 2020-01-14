package com.example.socketrocket.appengine;

import android.app.Activity;

public abstract class BackgroundTaskHandler {

    private boolean invalidated;
    private Activity caller;

    public BackgroundTaskHandler(Activity caller) {
        if (caller == null)
            throw new IllegalArgumentException("calling Activity must be given in constructor!");
        this.caller = caller;
        this.invalidated = false;
    }


    // MARK: - Abstract Methods

    public abstract void inBackground();

    public abstract void onMainThread();


    // MARK: - Methods

    public void start() {
        if (this.invalidated) {
            throw new IllegalStateException("BackgroundTaskHandler is invalidated");
        }
        this.invalidated = true;
        new Thread(new Runnable() {
            public void run() {
                inBackground();
                caller.runOnUiThread(new Runnable() {
                    public void run() {
                        onMainThread();
                    }
                });
        }}).start();
    }
}
