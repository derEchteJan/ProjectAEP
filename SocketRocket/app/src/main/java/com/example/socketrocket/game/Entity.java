package com.example.socketrocket.game;

import android.graphics.Canvas;

public abstract class Entity {

    protected double pX, pY, vX, vY;
    protected long lifetime = 0;

    public void tick() {
        this.lifetime++;
        this.pX += this.vX;
        this.pY += this.vY;
    }
    public abstract void render(Canvas canvas);

}
