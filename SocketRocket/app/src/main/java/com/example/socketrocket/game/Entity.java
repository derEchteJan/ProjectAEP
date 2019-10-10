package com.example.socketrocket.game;

import android.graphics.Canvas;

public abstract class Entity {

    protected double pX, pY, vX, vY;

    public void tick() {
        this.pX += this.vX;
        this.pY += this.vY;
        double drag = 0.5;
        if (this.vX > 0) {
            this.vX -= drag;
        } else if (this.vX < 0) {
            this.vX += drag;
        }
        if (this.vY > 0) {
            this.vY -= drag;
        } else if (this.vY < 0) {
            this.vY += drag;
        }
    }
    public abstract void render(Canvas canvas);

}
