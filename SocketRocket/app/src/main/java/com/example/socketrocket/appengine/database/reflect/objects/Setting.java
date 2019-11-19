package com.example.socketrocket.appengine.database.reflect.objects;

import com.example.socketrocket.appengine.database.reflect.ReflectableObject;

public class Setting extends ReflectableObject {

    public String key;
    public String value;


    // MARK: - ReflectableObject

    @Override
    public ReflectableObject getInstance() {
        return new Setting();
    }

    @Override
    public ReflectableObject[] getArrayInstance(int length) {
        return new Setting[length];
    }

}
