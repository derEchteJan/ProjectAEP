package com.example.socketrocket.gameengine.input.touch;

import android.graphics.Rect;

public class TouchEvent {

    public enum Action {
        UP, DOWN, MOVE;
    }

    public final int pointerIdx;
    public final int pointerId;
    public final Action action;
    public final float x, y;

    public TouchEvent(int pointerIdx, int pointerId, Action action, float x, float y) {
        this.pointerIdx = pointerIdx;
        this.pointerId = pointerId;
        this.action = action;
        this.x = x; this.y = y;
    }

    public boolean isInBounds(Rect bounds) {
        return this.x >= bounds.left && this.x <= bounds.right && this.y <= bounds.bottom && this.y >= bounds.top;
    }

    @Override
    public String toString() {
        return "TouchEvent("+action+", at: (x: "+x+", y: "+y+"), pIdx: "+pointerIdx+", pId: "+pointerId+")";
    }

}
