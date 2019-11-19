package com.example.socketrocket.appengine.database;

import android.content.Context;
import com.example.socketrocket.appengine.database.reflect.objects.*;

public class DatabaseConnection {

    private static final DatabaseConnection instance = new DatabaseConnection();
    public static DatabaseConnection sharedInstance() {
        return instance;
    }

    private DatabaseConnection() {}


    // MARK: - Methods

    public void initDatabase(Context context) {
        DatabaseController.sharedInstance().initDatabase(context);
    }


    // MARK: Objects

    // MARK: Score

    public void addScore(Score score) {
        DatabaseController.sharedInstance().addScore(score);
    }

    public Score getScore(int id) {
        return DatabaseController.sharedInstance().getScore(id);
    }

    public Score[] getAllScores() {
        return DatabaseController.sharedInstance().getAllScores();
    }

    public void updateScore(Score score) {
        DatabaseController.sharedInstance().updateScore(score);
    }

    public void deleteScore(Score score) {
        DatabaseController.sharedInstance().deleteScore(score);
    }

    public void deleteAllScores() {
        DatabaseController.sharedInstance().deleteAllScores();
    }

    // MARK: User

    public void addUser(User user) {
        DatabaseController.sharedInstance().addUser(user);
    }

    public User getUser(int id) {
        return DatabaseController.sharedInstance().getUser(id);
    }

    public User[] getAllUsers() {
        return DatabaseController.sharedInstance().getAllUsers();
    }

    public void updateUser(User user) {
        DatabaseController.sharedInstance().updateUser(user);
    }

    public void deleteUser(User user) {
        DatabaseController.sharedInstance().deleteUser(user);
    }

    public void deleteAllUsers() {
        DatabaseController.sharedInstance().deleteAllUsers();
    }

    // MARK: Setting

    public void addSetting(Setting setting) {
        DatabaseController.sharedInstance().addSetting(setting);
    }

    public Setting getSetting(int id) {
        return DatabaseController.sharedInstance().getSetting(id);
    }

    public Setting[] getAllSettings() {
        return DatabaseController.sharedInstance().getAllSettings();
    }

    public void updateSetting(Setting setting) {
        DatabaseController.sharedInstance().updateSetting(setting);
    }

    public void deleteSetting(Setting setting) {
        DatabaseController.sharedInstance().deleteSetting(setting);
    }

    public void deleteAllSettings() {
        DatabaseController.sharedInstance().deleteAllSettings();
    }

}
