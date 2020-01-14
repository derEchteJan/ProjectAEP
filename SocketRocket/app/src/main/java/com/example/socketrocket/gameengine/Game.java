package com.example.socketrocket.gameengine;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.example.socketrocket.gameengine.entity.Player;
import com.example.socketrocket.gameengine.images.BitmapManager;
import com.example.socketrocket.gameengine.input.touch.TouchEventHandler;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Game implements GameDelegate {

    private Activity parentActivity;
    private int deviceOrientation = 0;
    private EntityHanlder entityHandler;
    private Hud hud;
    private BitmapManager bitmapManager;

    public void init() {
        this.entityHandler = new EntityHanlder();
        this.hud = new Hud(this);
        TouchEventHandler.sharedInstance().removeAllObservers();
        TouchEventHandler.sharedInstance().addObserver(this.hud);
    }


    // MARK: - Methods

    // Game Life Cycle

    public void tick() {
        this.entityHandler.tickEntities();
        this.hud.tick();
    }

    public void render(Canvas canvas) {
        this.entityHandler.renderEntities(canvas);
        this.hud.render(canvas);
    }

    // Surface View

    public void handleTouchInput(MotionEvent e) {
        TouchEventHandler.sharedInstance().handleMotionEvent(e);
    }

    public void setParentActivity(Activity parent) {
        this.parentActivity = parent;
    }


    // MARK: - GameDelegate

    public void openMenu() {
        // TODO: Spiel und Hud Pausieren, menü anzeigen, Input Hierarchie anpassen -> Menü pushen
        this.parentActivity.finish(); // dummy
    }

    public void hideMenu(){
        // TODO: Spiel und Hud Fortsetzen, menü ausplenden, Input Hierarchie anpassen -> Menü popen
    }

    public void quitToMainMenu() {
        // todo: spielstand sichern und vorbereiten auf finish
        this.parentActivity.finish();
    }

    public Player getPlayer() {
        return this.entityHandler.getPlayer();
    }
}
