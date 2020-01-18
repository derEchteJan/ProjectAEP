package com.example.socketrocket.appengine.database;

import android.content.Context;
import com.example.socketrocket.appengine.database.reflect.objects.*;

public class DatabaseConnection {

    private final DatabaseController controller;

    public DatabaseConnection(Context context) {
        this.controller = new DatabaseController();
        this.controller.initWithContext(context);
    }


    // MARK: - Methods

    public boolean deleteDatabase() {
        return this.controller.deleteDatabase();
    }

    public String getDatabasePath() {
        return this.controller.getDatabasePath();
    }

    public long getDatabaseSize() {
        return this.controller.getDatabaseSize();
    }


    // MARK: Objects

    // MARK: Score

    public void addScore(Score score) {
        this.controller.addScore(score);
    }

    public Score getScore(int id) {
        return this.controller.getScore(id);
    }

    public Score[] getAllScores() {
        return this.controller.getAllScores();
    }

    public void updateScore(Score score) {
        this.controller.updateScore(score);
    }

    public void deleteScore(Score score) {
        this.controller.deleteScore(score);
    }

    public void deleteAllScores() {
        this.controller.deleteAllScores();
    }

    // MARK: User

    public void addUser(User user) {
        this.controller.addUser(user);
    }

    public User getUser(int id) {
        return this.controller.getUser(id);
    }

    public User[] getAllUsers() {
        return this.controller.getAllUsers();
    }

    public void updateUser(User user) {
        this.controller.updateUser(user);
    }

    public void deleteUser(User user) {
        this.controller.deleteUser(user);
    }

    public void deleteAllUsers() {
        this.controller.deleteAllUsers();
    }

    // MARK: Setting

    public void addSetting(Setting setting) {
        this.controller.addSetting(setting);
    }

    public Setting getSetting(int id) {
        return this.controller.getSetting(id);
    }

    public Setting[] getAllSettings() {
        return this.controller.getAllSettings();
    }

    public void updateSetting(Setting setting) {
        this.controller.updateSetting(setting);
    }

    public void deleteSetting(Setting setting) {
        this.controller.deleteSetting(setting);
    }

    public void deleteAllSettings() {
        this.controller.deleteAllSettings();
    }

}
