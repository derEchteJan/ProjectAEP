package com.example.socketrocket.gameengine.input.touch.buttons;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.socketrocket.gameengine.input.touch.TouchEvent;
import com.example.socketrocket.gameengine.input.touch.TouchEventHandler;
import com.example.socketrocket.gameengine.input.touch.TouchEventObserver;

public abstract class TouchEventButton implements TouchEventObserver {

    public enum ActionType {
        onTouch, onTouchDown, onTouchUpInside;
    }

    protected final int noCapturedPointerId = -1;

    protected Rect frame;
    protected ActionType actionType;
    protected int capturedPointerId = noCapturedPointerId;

    public TouchEventButton(Rect frame) {
        this.frame = frame;
        this.actionType = ActionType.onTouchUpInside;
    }

    public abstract void action();


    // MARK: -  Methods

    public void render(Canvas c) {
        Paint p = new Paint();
        p.setColor(0xff00ff);
        p.setAlpha(0xFF);
        c.drawRect(this.frame, p);
    }

    public void setActionType(ActionType type) {
        this.actionType = type;
    }

    public boolean handleTouchEvent(TouchEvent e) {
        if(e.isInBounds(this.frame)) {
            switch(e.action) {
                case DOWN:
                    if (this.capturedPointerId == this.noCapturedPointerId) {
                        boolean didCapturePointer = TouchEventHandler.sharedInstance().requestCaptureForPointer(e.pointerId, this);
                        if (didCapturePointer) {
                            this.capturedPointerId = e.pointerId;
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


    // MARK: - Events

    protected void onTouchDown(TouchEvent e) {
        switch(this.actionType) {
            case onTouch:
            case onTouchDown:
                this.action();
                break;
            default:
                return;
        }
    }

    protected void onTouchMove(TouchEvent e) {
        switch(this.actionType) {
            case onTouch:
                this.action();
                break;
            default:
                return;
        }
    }

    protected void onTouchUp(TouchEvent e) {
        switch(this.actionType) {
            case onTouchUpInside:
                this.action();
                break;
            default:
                return;
        }
    }
}
