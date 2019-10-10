package com.example.socketrocket.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class Game {

    private EntityHanlder handler;

    public void init() {
        this.handler = new EntityHanlder();
    }

    public void tick() {
        this.handler.tickEntities();
    }


    public void render(Canvas canvas) {
        this.handler.renderEntities(canvas);
    }

    public void handleTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN || e.getAction() == MotionEvent.ACTION_MOVE) {
            //System.out.println("TouchEvent at: " + e.getX() + ", " + e.getY());
            this.handler.didTouch(e.getX(), e.getY());
            //System.out.println("vx: "+(int)player.vX + " vy: "+(int)player.vY);
        }
    }
}
