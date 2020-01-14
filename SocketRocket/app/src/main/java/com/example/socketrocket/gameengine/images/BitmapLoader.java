package com.example.socketrocket.gameengine.images;

public class BitmapLoader {

    private ImageAssetDelegate imageAssetDelegate;

    public void setDelegate(ImageAssetDelegate delegate){
        this.imageAssetDelegate = delegate;
    }

    public void setIdentifierList(String[] imageIdentifiers){
        for(String imageIdentifier: imageIdentifiers) {
            this.imageAssetDelegate.getImage(imageIdentifier);
        }
    }
}
