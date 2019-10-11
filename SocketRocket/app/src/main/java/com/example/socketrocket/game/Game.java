package com.example.socketrocket.game;

import android.graphics.Canvas;
import android.view.MotionEvent;

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
        this.handler.renderEntities(canvas);
        this.hud.render(canvas);
    }

    public void handleTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {
            boolean hudDidHanldeEvent = e.getAction() == MotionEvent.ACTION_DOWN ? this.hud.handleTouchEvent(e) : false;
            if(hudDidHanldeEvent == false) {
                this.handler.didTouch(e.getX(), e.getY());
            }
        }
    }
}
