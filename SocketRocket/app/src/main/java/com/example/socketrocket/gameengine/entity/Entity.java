package com.example.socketrocket.gameengine.entity;

import android.graphics.Canvas;

import com.example.socketrocket.gameengine.EntityHanlder;

public abstract class Entity {

    protected EntityHanlder parentHandler;
    public double pX, pY, vX, vY;
    protected long lifetime = 0;

    public Entity(EntityHanlder parentHandler) {
        this.parentHandler = parentHandler;
    }

    public void tick() {
        this.lifetime++;
        this.pX += this.vX;
        this.pY += this.vY;
    }
    public abstract void render(Canvas canvas);

}
