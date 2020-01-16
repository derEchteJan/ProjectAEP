package com.example.socketrocket.appengine.database;

import com.example.socketrocket.appengine.database.reflect.ReflectableObject;
import java.lang.reflect.Field;
import java.util.IllegalFormatException;

class ReflectableObjectHandler {

    private SQLiteHandle handle;


    // MARK: - Methods

    // MARK: Setup

    protected void setHandle(SQLiteHandle handle) {
        this.handle = handle;
    }

    // MARK: Tables

    protected void createTable(ReflectableObject prototype) {
        String query = "CREATE TABLE IF NOT EXISTS " + prototype.getTableName();
        String columns = "";
        int i = 0;
        for (Field field : prototype.getClass().getFields()) {
            if (i != 0) columns += ", ";
            columns += field.getName() + " " + getSQLType(field.getType());
            if (field.getName().equals(prototype.getPrimaryKeyName()))
                columns += " " + getPrimaryKeyExpression();
            i++;
        }
        query += "(" + columns + ")";
        System.out.println("Query: " + query);
        this.handle.executeQueryWrite(query);
    }

    protected void dropTable(ReflectableObject prototype) {
        String query = "DROP TABLE IF EXISTS " + prototype.getTableName();
        System.out.println("Query: " + query);
        this.handle.executeQueryWrite(query);
    }

    // MARK: Write

    protected void insertObject(ReflectableObject object) {
        String query = "INSERT INTO " + object.getTableName();
        String columns = "";
        String values = "";
        int i = 0;
        for (Field field : object.getClass().getFields()) {
            if (field.getName().equals(object.getPrimaryKeyName()) || !isValidType(field.getType())) continue;
            if (i != 0) {
                columns += ", ";
                values += ", ";
            }
            columns += field.getName();
            values += getFieldValue(field, object);
            i++;
        }
        query += "(" + columns + ") VALUES (" + values + ")";
        System.out.println("Query: " + query);
        this.handle.executeQueryWrite(query);
    }

    protected void updateObject(ReflectableObject object) {
        String query = "UPDATE " + object.getTableName() + " SET ";
        int i = 0;
        for (Field field : object.getClass().getFields()) {
            if (field.getName().equals(object.getPrimaryKeyName()) || !isValidType(field.getType())) continue;
            if (i != 0) query += ", ";
            query += field.getName() + " = " + getFieldValue(field, object);
            i++;
        }
        query += " WHERE " + object.getPrimaryKeyName() + " = " + object.getPrimaryKeyValue();;
        System.out.println("Query: " + query);
        this.handle.executeQueryWrite(query);
    }

    protected void deleteObject(ReflectableObject object) {
        String query = "DELETE FROM " + object.getTableName() + " WHERE " + object.getPrimaryKeyName() + " = " + object.getPrimaryKeyValue();
        System.out.println("Query: " + query);
        this.handle.executeQueryWrite(query);
    }

    protected void deleteObjects(ReflectableObject prototype) {
        String query = "DELETE FROM " + prototype.getTableName();
        System.out.println("Query: " + query);
        this.handle.executeQueryWrite(query);
    }

    // MARK: Read

    protected <T extends ReflectableObject> T getObject(T prototype, int id) {
        String columns = "";
        Field[] fields = prototype.getClass().getFields();
        int i = 0;
        for (Field field: fields) {
            if (i != 0) {
                columns += ", ";
            }
            columns += field.getName();
            i++;
        }
        String query = "SELECT " + columns + " FROM " + prototype.getTableName() + " WHERE " + prototype.getPrimaryKeyName() + " = " + id;
        System.out.println("Query: " + query);
        String[][] resultSet = this.handle.executeQueryRead(query);
        if (resultSet.length < 1) {
            return null;
        } else {
            String[] result = resultSet[0];
            for (i = 0; i < result.length; i++) {
                setFieldValue(fields[i], prototype, result[i]);
            }
            return prototype;
        }
    }

    protected <T extends ReflectableObject> T[] getObjects(T prototype, String whereStatement) {
        String columns = "";
        Field[] fields = prototype.getClass().getFields();
        int i = 0;
        for (Field field: fields) {
            if (i != 0) {
                columns += ", ";
            }
            columns += field.getName();
            i++;
        }
        String query = "SELECT " + columns + " FROM " + prototype.getTableName();
        if (whereStatement != null) query += " WHERE " + whereStatement;
        System.out.println("Query: " + query);
        String[][] resultSet = this.handle.executeQueryRead(query);
        T[] objects = (T[])prototype.getArrayInstance(resultSet.length);
        if (resultSet.length < 1) {
            return objects;
        } else {
            for (int n = 0; n < resultSet.length; n++) {
                String[] result = resultSet[n];
                T object = (T)prototype.getInstance();
                for (i = 0; i < result.length; i++) {
                    setFieldValue(fields[i], object, result[i]);
                }
                objects[n] = object;
            }
            return objects;
        }
    }


    // MARK: - SUBS

    private static String getFieldValue(Field f, ReflectableObject o) {
        try {
            String rawValue = f.get(o).toString();
            if (requiresEscaping(f.getType())) {
                rawValue = getEscapedValue(rawValue);
            }
            return rawValue;
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException in Object Broker on object: " + o.getClass() + ", on field: " + f.getName());
            return "null";
        }
    }

    private static void setFieldValue(Field f, ReflectableObject o, String rawValue) {
        Class<?> javaType = f.getType();
        try {
            if (javaType == String.class) {
                f.set(o, rawValue);
            } else if (javaType == int.class) {
                f.set(o, Integer.parseInt(rawValue));
            } else if (javaType == long.class) {
                f.set(o, Long.parseLong(rawValue));
            } else if (javaType == double.class) {
                f.set(o, Double.parseDouble(rawValue));
            } else if (javaType == float.class) {
                f.set(o, Float.parseFloat(rawValue));
            } else if (javaType == boolean.class) {
                f.set(o, Boolean.parseBoolean(rawValue));
            }
        } catch (IllegalAccessException | IllegalFormatException e) {
            System.out.println(e.getClass().getName() + " in Object Broker on object: " + o.getClass() + ", on field: " + f.getName());
        }
    }

    private static String getSQLType(Class<?> javaType) {
        if (javaType == String.class) {
            return "text";
        } else if (javaType == int.class) {
            return "integer";
        } else if (javaType == long.class) {
            return "bigint";
        } else if (javaType == double.class) {
            return "double";
        } else if (javaType == float.class) {
            return "float";
        } else if (javaType == boolean.class) {
            return "boolean";
        }
        return null;
    }

    private static boolean isValidType(Class<?> javaType) {
        return getSQLType(javaType) != null;
    }

    private static boolean requiresEscaping(Class<?> javaType) {
        return (javaType == String.class);
    }

    private static String getEscapedValue(String value) {
        return "\"" + value.replace("\"", "\'") + "\"";
    }

    private static String getPrimaryKeyExpression() {
        return "PRIMARY KEY AUTOINCREMENT";
    }

}
