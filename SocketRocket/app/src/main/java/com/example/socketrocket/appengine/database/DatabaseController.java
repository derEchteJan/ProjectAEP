package com.example.socketrocket.appengine.database;

import android.content.Context;
import com.example.socketrocket.appengine.database.reflect.ReflectableObject;
import com.example.socketrocket.appengine.database.reflect.objects.*;

public class DatabaseController {

    protected static final int DATABASE_VERSION = 1;
    protected static final String APP_DB_PATH = "main.db";

    private static final DatabaseController thisInstance = new DatabaseController();
    protected static DatabaseController sharedInstance() { return thisInstance; }

    private DatabaseController() {}

    private ReflectableObjectHandler mainObjectHandler = null;
    private SQLiteHandle mainDBhandle = null;


    // MARK: - External

    // Management

    protected void initDatabase(Context context) {
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
