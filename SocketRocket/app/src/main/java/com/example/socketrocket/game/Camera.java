package com.example.socketrocket.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Camera extends Entity {

    private Entity child;
    private double screenWidth = 1400, screenHeight = 2000;
    private double followFactor = 0.5;

    public void setChild(Entity child) {
        this.child = child;
    }

    public void setScreenDimensions(double w, double h) {
        this.screenWidth = w; this.screenHeight = h;
    }

    public float translateX(double x) {
        return (float) (x - this.pX + this.screenWidth / 2);
    }
    public float translateY(double y) {
        return (float) (y - this.pY + this.screenHeight / 2);
    }

    public double inverseTranslateX(float screenX) {
        return (double)(screenX - this.screenWidth/2)+this.pX;
    }
    public double inverseTranslateY(float screenY) {
        return (double)(screenY - this.screenWidth/2)+this.pY;
    }

    // MARK: - Entity

    @Override public void tick() {
        if (this.child != null) {
            /*
            double dx = this.child.pX - this.pX;
            double dy = this.child.pY - this.pY;
            this.vX = dx * this.followFactor;
            this.vY = dy * this.followFactor;
            */
            this.vX = this.child.vX;
            this.vY = this.child.vY;
        }
        //super.tick();
    }

    public void render(Canvas canvas) {
        // Debug only
        Paint p = new Paint();
        p.setColor(0);
        p.setAlpha(200);
        canvas.drawRect(new Rect((int)this.pX - 10, (int)this.pY - 10, (int)this.pX + 10, (int)this.pY + 10), p);
        //canvas.drawLine((float)this.pX - 20, (float)this.pY - 20, (float)this.pX + 20, (float)this.pY + 20, p);
        // Debug
        return;
    }

}
