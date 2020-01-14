package com.example.socketrocket.gameengine.images;

import android.graphics.Bitmap;

public interface ImageAssetDelegate {
    abstract Bitmap getImage(String identifier);
}
