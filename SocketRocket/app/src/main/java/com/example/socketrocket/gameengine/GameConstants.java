package com.example.socketrocket.gameengine;

import android.content.res.Resources;

public class GameConstants {

    // MARK: - Feature Toggle

    public static final boolean DEBUG_MODE = true;


    // MARK: - Display

    public static final int SCREEN_W = Resources.getSystem().getDisplayMetrics().widthPixels;
    public static final int SCREEN_H = Resources.getSystem().getDisplayMetrics().heightPixels;

    public static final int VIRTUAL_SCREEN_H = 1440;

    public static final int notify = getNotify();

    private static int getNotify() {
        return 0;
    }

}
