package com.example.socketrocket.appengine.database.reflect.objects;

import com.example.socketrocket.appengine.database.reflect.ReflectableObject;

public class User extends ReflectableObject {

    public long userId;
    public String name;
    public String email;
    public String password;
    public String token;




    // MARK: - ReflectableObject

    @Override
    public ReflectableObject getInstance() {
        return new User();
    }

    @Override
    public ReflectableObject[] getArrayInstance(int length) {
        return new User[length];
    }

}
