package com.example.socketrocket.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Camera extends Entity {

    private Entity child;
    private double screenMidX, screenMidY;
    private double followFactor;
    private boolean showFocusPoint;

    public Camera() {
        super();
        this.screenMidX = GameConstants.SCREEN_W/2;
        this.screenMidY = GameConstants.SCREEN_H/2;
        this.followFactor = 0.5;
        this.showFocusPoint = GameConstants.DEBUG_MODE;
    }


    // MARK: - External

    public void setChild(Entity child) {
        this.child = child;
    }


    // MARK: - Display <-> Virtual Coordinate Translation

    // MARK: Virtual to Display

    public float translateX(double x) {
        return (float)(x - this.pX + this.screenMidX);
    }

    public float translateY(double y) {
        return (float)(y - this.pY + this.screenMidY);
    }

    public void translateCanvas(Canvas c) {
        c.translate((float)(this.screenMidX - this.pX), (float)(this.screenMidY - this.pY));
    }

    // MARK: Display to Virtual

    public double inverseTranslateX(float screenX) {
        return (double)(screenX - this.screenMidX) + this.pX;
    }

    public double inverseTranslateY(float screenY) {
        return (double)(screenY - this.screenMidY) + this.pY;
    }

    public void inverseTranslateCanvas(Canvas c) {
        c.translate((float)(-this.screenMidX + this.pX), (float)(-this.screenMidY + this.pY));
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
        if (!this.showFocusPoint) return;
        // Show camera position in debug mode
        int cornerOffset = 40;
        int lineWidth = 10;
        int lineLength = 22;
        Paint p = new Paint();
        p.setColor(0x101010);
        p.setAlpha(128);
        canvas.drawRect(new Rect((int)this.pX - cornerOffset, (int)this.pY - cornerOffset, (int)this.pX - cornerOffset + lineLength, (int)this.pY - cornerOffset + lineWidth), p);
        canvas.drawRect(new Rect((int)this.pX + cornerOffset - lineLength, (int)this.pY - cornerOffset, (int)this.pX + cornerOffset, (int)this.pY - cornerOffset + lineWidth), p);
        canvas.drawRect(new Rect((int)this.pX - cornerOffset, (int)this.pY + cornerOffset - lineWidth, (int)this.pX - cornerOffset + lineLength, (int)this.pY + cornerOffset), p);
        canvas.drawRect(new Rect((int)this.pX + cornerOffset - lineLength, (int)this.pY + cornerOffset - lineWidth, (int)this.pX + cornerOffset, (int)this.pY + cornerOffset), p);
        canvas.drawRect(new Rect((int)this.pX - cornerOffset, (int)this.pY - cornerOffset + lineWidth, (int)this.pX - cornerOffset + lineWidth, (int)this.pY - cornerOffset + lineLength), p);
        canvas.drawRect(new Rect((int)this.pX - cornerOffset, (int)this.pY + cornerOffset - lineLength, (int)this.pX - cornerOffset + lineWidth, (int)this.pY + cornerOffset - lineWidth), p);
        canvas.drawRect(new Rect((int)this.pX + cornerOffset - lineWidth, (int)this.pY - cornerOffset + lineWidth, (int)this.pX + cornerOffset, (int)this.pY - cornerOffset + lineLength), p);
        canvas.drawRect(new Rect((int)this.pX + cornerOffset - lineWidth, (int)this.pY + cornerOffset - lineLength, (int)this.pX + cornerOffset, (int)this.pY + cornerOffset - lineWidth), p);
    }

}
