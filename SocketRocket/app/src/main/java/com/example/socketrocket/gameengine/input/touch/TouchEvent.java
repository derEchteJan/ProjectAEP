package com.example.socketrocket.gameengine.input.touch;

public class TouchEvent {

    public static enum Action {
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

    @Override
    public String toString() {
        return "TouchEvent("+action+", at: (x: "+x+", y: "+y+"), pIdx: "+pointerIdx+", pId: "+pointerId+")";
    }

}
