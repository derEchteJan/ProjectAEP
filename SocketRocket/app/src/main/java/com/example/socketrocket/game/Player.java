package com.example.socketrocket.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Player extends Entity {

    private long ticks = 0;

    // MARK: Entity
    @Override public void tick() {
        super.tick();
        this.ticks++;
        if (ticks % 20 == 0) {
            System.out.println("player: " + (int)this.pX + " " + (int)this.pY + " v: " + (int)this.vX + " " + (int)this.vY);
        }
    }

    public void render(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(0xFFFFFF);
        p.setAlpha(255);
        canvas.drawRect(new Rect((int)this.pX - 20, (int)this.pY - 20, (int)this.pX + 20, (int)this.pY + 20), p);
    }
}
