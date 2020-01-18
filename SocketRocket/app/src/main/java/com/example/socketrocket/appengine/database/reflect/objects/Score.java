package com.example.socketrocket.appengine.database.reflect.objects;

import com.example.socketrocket.appengine.database.reflect.ReflectableObject;

public class Score extends ReflectableObject {

    public String user_name = "";
    public long amount = 0;
    public long timestamp = 0;


    // MARK: - ReflectableObject

    @Override
    public ReflectableObject getInstance() {
        return new Score();
    }

    @Override
    public ReflectableObject[] getArrayInstance(int length) {
        return new Score[length];
    }

}
