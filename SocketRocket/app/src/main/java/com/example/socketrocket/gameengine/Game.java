package com.example.socketrocket.gameengine;

import android.app.Activity;
import android.graphics.Canvas;
import android.view.MotionEvent;

import com.example.socketrocket.gameengine.entity.Player;
import com.example.socketrocket.gameengine.input.touch.TouchEventHandler;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Game implements GameDelegate {

    private Activity parentActivity;
    private int deviceOrientation = 0;
    private EntityHanlder handler;
    private Hud hud;

    public void init() {
        this.handler = new EntityHanlder();
        this.hud = new Hud(this);
        TouchEventHandler.sharedInstance().removeAllObservers();
        TouchEventHandler.sharedInstance().addObserver(this.hud);
    }

    public void tick() {
        this.handler.tickEntities();
        this.hud.tick();
    }


    public void render(Canvas canvas) {
        this.handler.renderEntities(canvas);
        this.hud.render(canvas);
    }

    public void handleTouchInput(MotionEvent e) {
        TouchEventHandler.sharedInstance().handleMotionEvent(e);

        // TODO: Handling via TouchEventHandler einbauen

        /*
        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {
            boolean hudDidHanldeEvent = e.getAction() == MotionEvent.ACTION_DOWN ? this.hud.handleTouchEvent(e) : false;
            if(hudDidHanldeEvent == false) {
                this.handler.didTouch(e.getX(), e.getY());
            }
        }*/

    }

    public void setParentActivity(Activity parent) {
        this.parentActivity = parent;
    }

    // MARK: - GameDelegate

    public void openMenu() {
        // TODO: Spiel und Hud Pausieren, menü anzeigen, Input Hierarchie anpassen -> Menü pushen
        this.parentActivity.finish();
    }

    public void hideMenu(){
        // TODO: Spiel und Hud Fortsetzen, menü ausplenden, Input Hierarchie anpassen -> Menü popen
    }

    public Player getPlayer() {
        return this.handler.getPlayer();
    }
}
