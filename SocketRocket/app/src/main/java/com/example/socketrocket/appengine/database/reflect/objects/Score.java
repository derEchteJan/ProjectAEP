package com.example.socketrocket.appengine.database.reflect.objects;

import com.example.socketrocket.appengine.database.reflect.ReflectableObject;

public class Score extends ReflectableObject {

    public long score = 0;
    public String playerName = "";
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
