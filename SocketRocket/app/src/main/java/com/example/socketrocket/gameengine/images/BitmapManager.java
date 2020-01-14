package com.example.socketrocket.gameengine.images;

import android.graphics.Bitmap;

import java.util.HashMap;

public class BitmapManager {

    private HashMap<String, Bitmap> storedImages = new HashMap<>();

    public void addImage(String identifier, Bitmap image) {
        if(!this.storedImages.containsKey(identifier)) {
            this.storedImages.put(identifier, image);
        }
    }

}
