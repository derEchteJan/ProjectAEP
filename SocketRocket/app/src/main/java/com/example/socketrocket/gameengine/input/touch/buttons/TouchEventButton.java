package com.example.socketrocket.gameengine.input.touch.buttons;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import com.example.socketrocket.gameengine.input.touch.*;

public abstract class TouchEventButton implements TouchEventObserver {

    protected final int noCapturedPointerId = -1;

    protected Rect frame;
    protected int capturedPointerId;
    protected boolean isDown;

    public TouchEventButton(Rect frame) {
        this.frame = frame;
        this.capturedPointerId = noCapturedPointerId;
        this.isDown = false;
    }


    // MARK: - Override

    public void onTouchDown(TouchEvent e) {}

    public void onTouchMove(TouchEvent e) {}

    public void onTouchUp(TouchEvent e) {}

    public void onTouchUpInside(TouchEvent e) {}


    // MARK: -  Methods

    public void render(Canvas c) {
        if(this.isDown) {
            Paint mainColor = new Paint();
            mainColor.setColor(0x909090);
            mainColor.setAlpha(64);
            c.drawRect(this.frame, mainColor);
        }
    }

    public boolean handleTouchEvent(TouchEvent e) {
        if(e.isInBounds(this.frame)) {
            switch(e.action) {
                case DOWN:
                    if (this.capturedPointerId == this.noCapturedPointerId) {
                        boolean didCapturePointer = TouchEventHandler.sharedInstance().requestCaptureForPointer(e.pointerId, this);
                        if (didCapturePointer) {
                            this.capturedPointerId = e.pointerId;
                            this.isDown = true;
                            this.onTouchDown(e);
                            return true;
                        }
                    }
                    return false;
                case MOVE:
                    if (this.capturedPointerId == e.pointerId && this.capturedPointerId != this.noCapturedPointerId) {
                        this.onTouchMove(e);
                        return true;
                    }
                    return false;
                case UP:
                    if (this.capturedPointerId == e.pointerId && this.capturedPointerId != this.noCapturedPointerId) {
                        boolean didReleasePointer = TouchEventHandler.sharedInstance().releaseCaptureForPointer(e.pointerId, this);
                        if (didReleasePointer) {
                            this.capturedPointerId = this.noCapturedPointerId;
                            this.isDown = false;
                            this.onTouchUpInside(e);
                            this.onTouchUp(e);
                            return true;
                        }
                    }
                    return false;
                default: // never
                    return false;
            }
        } else {
            switch(e.action) {
                case DOWN:
                    return false;
                case MOVE:
                    if (this.capturedPointerId == e.pointerId && this.capturedPointerId != this.noCapturedPointerId) {
                        this.onTouchMove(e);
                        return true;
                    }
                    return false;
                case UP:
                    if (this.capturedPointerId == e.pointerId && this.capturedPointerId != this.noCapturedPointerId) {
                        boolean didReleasePointer = TouchEventHandler.sharedInstance().releaseCaptureForPointer(e.pointerId, this);
                        if (didReleasePointer) {
                            this.capturedPointerId = this.noCapturedPointerId;
                            this.isDown = false;
                            this.onTouchUp(e);
                            return true;
                        }
                    }
                    return false;
                default: // never
                    return false;
            }
        }
    }
}
