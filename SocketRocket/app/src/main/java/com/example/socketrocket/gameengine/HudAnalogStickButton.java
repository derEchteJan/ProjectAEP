package com.example.socketrocket.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;

import static java.lang.Math.min;

public class HudAnalogStickButton {

    private float x, y, r;
    private int thumbPosX = 0, thumbPosY = 0;
    private final int thumbDistanceMax = 0xFF;
    private Paint primary, secondary;

    public HudAnalogStickButton() {
        this.r = min(GameConstants.SCREEN_H, GameConstants.SCREEN_W) / 2;
        this.x = r + 100;
        this.y = GameConstants.SCREEN_H - r - 100;
    }

    public void render(Canvas c) {
        c.drawCircle(this.x, this.y, this.r, this.primary);
        c.drawCircle(this.x, this.y, this.r / 2, this.secondary);
    }

    public boolean handleTapEvent(MotionEvent e) {
        return false;
    }
}
