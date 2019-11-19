package com.example.socketrocket.gameengine;

import android.graphics.Canvas;
import android.view.MotionEvent;

import com.example.socketrocket.gameengine.input.touch.TouchEventHandler;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Game {

    private EntityHanlder handler;
    private Hud hud;

    public void init() {
        this.handler = new EntityHanlder();
        this.hud = new Hud();
    }

    public void tick() {
        this.handler.tickEntities();
    }


    public void render(Canvas canvas) {
        this.hud.render(canvas);
        this.handler.renderEntities(canvas);
    }

    public void handleTouchEvent(MotionEvent e) {
        TouchEventHandler.sharedInstance().handleMotionEvent(e);

        // TODO: Handling via TocuhEventHandler einbaue


        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {
            boolean hudDidHanldeEvent = e.getAction() == MotionEvent.ACTION_DOWN ? this.hud.handleTouchEvent(e) : false;
            if(hudDidHanldeEvent == false) {
                this.handler.didTouch(e.getX(), e.getY());
            }
        }

    }
}
