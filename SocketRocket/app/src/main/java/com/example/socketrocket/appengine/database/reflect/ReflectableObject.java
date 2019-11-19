package com.example.socketrocket.appengine.database.reflect;

import java.lang.reflect.Field;

public abstract class ReflectableObject {

    public int id = -1;

    public String getPrimaryKeyName() { return "id"; }
    public int getPrimaryKeyValue() { return this.id; }
    public String getTableName() { return this.getClass().getSimpleName(); }

    public abstract ReflectableObject getInstance();
    public abstract ReflectableObject[] getArrayInstance(int length);


    // MARK: - Object

    @Override public String toString() {
        String raw = this.getClass().getSimpleName() + ":{";
        Field[] fields = this.getClass().getFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (i != 0) raw += ", ";
            String name = "\"" + f.getName() + "\"";
            String value;
            try {
                value = f.get(this).toString();
            } catch (IllegalAccessException e) {
                value = "ERROR";
            }
            if (f.getType() == String.class) value = "\"" + value + "\"";
            raw += name + ":" + value;
        }
        raw += "}";
        return raw;
    }

}
