package com.example.socketrocket.appengine.database;

import android.content.Context;
import com.example.socketrocket.appengine.database.reflect.ReflectableObject;
import com.example.socketrocket.appengine.database.reflect.objects.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class DatabaseController {

    protected static final int DATABASE_VERSION = 1;
    protected static final String APP_DB_PATH = "main.db";

    protected DatabaseController() {}

    private ReflectableObjectHandler mainObjectHandler = null;
    private SQLiteHandle mainDBhandle = null;

    // MARK: - External

    // Management

    protected void initWithContext(Context context) {
        /* TODO: Datenbank initialisieren bei jedem Appstart
         *  - Wenn datei nicht da -> neu erzeugen
         *  - Tabellen ersetellen falls nötig
         *  - Migration durchführen
         *  - Bei fehlern löschen und neu aufsetzen
         * */
        this.mainDBhandle = new SQLiteHandle(context, APP_DB_PATH, DATABASE_VERSION);
        this.mainObjectHandler = new ReflectableObjectHandler();
        this.mainObjectHandler.setHandle(this.mainDBhandle);
        // create all tables if not exists
        ReflectableObject[] tablePrototypes = new ReflectableObject[] {new Score(), new Setting(), new User()};
        for (ReflectableObject prototype: tablePrototypes) {
            this.mainObjectHandler.createTable(prototype);
        }
    }

    protected boolean deleteDatabase() {
        if (this.mainDBhandle == null) return false;
        String path = this.mainDBhandle.getDatabasePath();
        try {
            Path p = Paths.get(path);
            Files.delete(p);
        } catch (IOException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            return false;
        }
        return true;
    }

    protected String getDatabasePath() {
        return this.mainDBhandle.getDatabasePath();
    }

    protected long getDatabaseSize() {
        if (this.mainDBhandle == null) return 0;
        String path = this.mainDBhandle.getDatabasePath();
        int available;
        try {
            InputStream fileStream = new FileInputStream(path);
            available = fileStream.available();
        } catch (FileNotFoundException e) {
            System.out.println("File Not found: " + path);
            return -1;
        } catch (IOException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            return -1;
        }
        return available;
    }


    // MARK: - Objects

    // Scores

    protected void addScore(Score score) {
        this.mainObjectHandler.insertObject(score);
    }

    protected Score getScore(int id) {
        return this.mainObjectHandler.getObject(new Score(), id);
    }

    protected Score[] getAllScores() {
        return this.mainObjectHandler.getObjects(new Score(), null);
    }

    protected void updateScore(Score score) {
        this.mainObjectHandler.updateObject(score);
    }

    protected void deleteScore(Score score) {
        this.mainObjectHandler.deleteObject(score);
    }

    protected void deleteAllScores() {
        this.mainObjectHandler.deleteObjects(new Score());
    }

    // Users

    protected void addUser(User user) {
        this.mainObjectHandler.insertObject(user);
    }

    protected User getUser(int id) {
        return this.mainObjectHandler.getObject(new User(), id);
    }

    protected User[] getAllUsers() {
        return this.mainObjectHandler.getObjects(new User(), null);
    }

    protected void updateUser(User user) {
        this.mainObjectHandler.updateObject(user);
    }

    protected void deleteUser(User user) {
        this.mainObjectHandler.deleteObject(user);
    }

    protected void deleteAllUsers() {
        this.mainObjectHandler.deleteObjects(new User());
    }

    // Settings

    protected void addSetting(Setting setting) {
        this.mainObjectHandler.insertObject(setting);
    }

    protected Setting getSetting(int id) {
        return this.mainObjectHandler.getObject(new Setting(), id);
    }

    protected Setting[] getAllSettings() {
        return this.mainObjectHandler.getObjects(new Setting(), null);
    }

    protected void updateSetting(Setting setting) {
        this.mainObjectHandler.updateObject(setting);
    }

    protected void deleteSetting(Setting setting) {
        this.mainObjectHandler.deleteObject(setting);
    }

    protected void deleteAllSettings() {
        this.mainObjectHandler.deleteObjects(new Setting());
    }

}
