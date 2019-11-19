package com.example.socketrocket.gameengine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

public class Hud {

    // nur zur demo, hud button klasse kommt noch
    public final int buttonLeft = 30;
    public final int buttonRight = 160;
    public final int buttonBottom = 30;
    public final int buttonTop = 160;

    public void render(Canvas c) {
        Paint p = new Paint();
        p.setColor(0x000000);
        p.setAlpha(0xFF);
        c.drawRect(new Rect(buttonLeft, buttonTop, buttonRight, buttonBottom), p);
        Paint p2 = new Paint();
        p2.setColor(EntityHanlder.tapControllsPlayer ? 0xFFFFFF : 0x303030);
        p2.setAlpha(0xFF);
        c.drawRect(new Rect(buttonLeft + 10, buttonTop - 10, buttonRight - 10, buttonBottom + 10), p2);
    }

    public boolean handleTouchEvent(MotionEvent e) {
        if(e.getX() >= buttonLeft && e.getX() <= buttonRight && e.getY() >= buttonBottom && e.getY() <= buttonTop) {
            this.buttonAction();
            return true;
        } else return false;
    }

    private void buttonAction() {
        EntityHanlder.tapControllsPlayer = !EntityHanlder.tapControllsPlayer;
    }

}
