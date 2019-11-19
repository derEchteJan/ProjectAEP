package com.example.socketrocket.gameengine.input.touch;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class TouchEventHandler {

    public static final int MAX_POINTERS_HANDLED = 5;
    private static final TouchEventHandler thisInstance = new TouchEventHandler();
    public static TouchEventHandler sharedInstance() { return thisInstance; };

    private ArrayList<TouchEventObserver> observers = new ArrayList<>();
    private HashMap<Integer, TouchEventObserver> capturedPointers = new HashMap<>();
    private int pointerCount = 0;


    // MARK: - methods

    public void handleMotionEvent(MotionEvent e) {
        TouchEvent.Action action = null;
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                this.pointerCount = e.getPointerCount() - 1;
                action = TouchEvent.Action.UP;
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                this.pointerCount = e.getPointerCount();
                action = TouchEvent.Action.DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                this.pointerCount = e.getPointerCount();
                action = TouchEvent.Action.MOVE;
                break;
            default:
                break;
        }
        TouchEvent[] touchEvents = new TouchEvent[e.getPointerCount()];
        for (int i = 0; i < touchEvents.length; i++) {
            TouchEvent.Action actionToSet = i == e.getActionIndex() ? action : TouchEvent.Action.MOVE;
            touchEvents[i] = new TouchEvent(i, e.getPointerId(i), actionToSet, e.getX(i), e.getY(i));
        }
        printEvents(touchEvents);
    }

    // TODO: REMOVE
    private /*debug*/ void printEvents(TouchEvent[] events) {
        String result = "\n[\n";
        for (TouchEvent e: events) {
            result += "\t" + e + "\n";
        }
        result += "]";
        System.out.println(result);
    }

    public int getCurrentPointerCount() {
        return this.pointerCount;
    }

    // MARK: Observers

    public void addObserver(TouchEventObserver observer) {
        if (observer != null && !this.observers.contains(observer)) {
            this.observers.add(observer);
        }
    }

    public void removeObserver(TouchEventObserver observer) {
        if (observer != null && this.observers.contains(observer)) {
            this.observers.remove(observer);
        }
    }

    public boolean requestCaptureForPointer(int pointerId, TouchEventObserver requestingObserver) {
        if (requestingObserver == null) return false;
        if (!this.capturedPointers.containsKey(pointerId)) {
            this.capturedPointers.put(pointerId, requestingObserver);
            return true;
        } else return false;
    }

    public boolean releaseCaptureForPointer(int pointerId, TouchEventObserver requestingObserver) {
        if (requestingObserver == null) return false;
        return this.capturedPointers.remove(pointerId, requestingObserver);
    }

}
