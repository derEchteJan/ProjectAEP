package com.example.socketrocket.appengine.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHandle extends SQLiteOpenHelper {

    public SQLiteHandle(Context context, String path, int version) {
        super(context, path, null, version);
    }


    // MARK: - External

    protected void executeQueryWrite(String query) {
        SQLiteDatabase writeHandle = this.getWritableDatabase();
        writeHandle.execSQL(query);
    }

    protected String[][] executeQueryRead(String query) {
        SQLiteDatabase writeHandle = this.getReadableDatabase();
        Cursor cursor = writeHandle.rawQuery(query, null);
        String[][] dataSets = new String[cursor.getCount()][cursor.getColumnCount()];
        cursor.moveToFirst();
        for (int dataSetIndex = 0; dataSetIndex < dataSets.length; dataSetIndex++) {
            String[] dataSet = dataSets[dataSetIndex];
            for (int columnIndex = 0; columnIndex < dataSet.length; columnIndex++) {
                dataSet[columnIndex] = cursor.getString(columnIndex);
            }
            cursor.moveToNext();
        }
        cursor.close();
        return dataSets;
    }


    // MARK: - SQLiteOpenHelper

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("Created database: " + db.getPath());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: Migration behandeln
        System.out.println("Upgrading db \"" + db.getPath() + "\" from version " + oldVersion + " to " + newVersion);
    }
}
