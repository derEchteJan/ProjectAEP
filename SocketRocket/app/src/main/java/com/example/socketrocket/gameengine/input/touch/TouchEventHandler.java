package com.example.socketrocket.gameengine.input.touch;

import android.view.MotionEvent;

import com.example.socketrocket.gameengine.GameConstants;

import java.util.HashMap;
import java.util.Stack;

public class TouchEventHandler {

    public static final int MAX_POINTERS_HANDLED = 5;
    private static final TouchEventHandler thisInstance = new TouchEventHandler();
    public static TouchEventHandler sharedInstance() { return thisInstance; };

    private Stack<TouchEventObserver> observers = new Stack<>();
    private HashMap<Integer, TouchEventObserver> capturedPointers = new HashMap<>();
    private int pointerCount = 0;


    // MARK: - methods

    public void handleMotionEvent(MotionEvent e) {
        TouchEvent.Action action = null;
        switch (e.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
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
                throw new RuntimeException("TouchEventHandler hat ein unerwartetes MotionEvent erhalten, code: " +e.getAction());
        }
        TouchEvent[] touchEventsForDebugPrinting = new TouchEvent[e.getPointerCount()];
        for (int i = 0; i < touchEventsForDebugPrinting.length; i++) {
            TouchEvent.Action actionToSet = i == e.getActionIndex() ? action : TouchEvent.Action.MOVE;
            TouchEvent nextEvent = new TouchEvent(i, e.getPointerId(i), actionToSet, e.getX(i), e.getY(i));
            boolean pointerWasCaptured = this.handOverToCapturedPointerOwner(nextEvent);
            if(!pointerWasCaptured) {
                this.handOverToObserverHierachy(nextEvent);
            }
            touchEventsForDebugPrinting[i] = nextEvent;
        }
        if( GameConstants.DEBUG_MODE)
            printEvents(touchEventsForDebugPrinting);
    }

    private boolean handOverToCapturedPointerOwner(TouchEvent event) {
        if (this.capturedPointers.containsKey(event.pointerId)) {
            this.capturedPointers.get(event.pointerId).handleTouchEvent(event);
            return true;
        }
        return false;
    }

    private void handOverToObserverHierachy(TouchEvent event) {
        if(this.observers.isEmpty()) return;
        for(int i = this.observers.size() - 1; i >= 0; i--) {
            TouchEventObserver next = observers.elementAt(i);
            boolean didHandleEvent = next.handleTouchEvent(event);
            if(didHandleEvent) {
                return;
            }
        }
    }

    private /*debug*/ void printEvents(TouchEvent[] events) {
        String result = "\n[\n";
        for (TouchEvent e: events) {
            result += "\t" + e + "\n";
        }
        result += "]";
        System.out.println(result);
    }


    // MARK: - Public Methods

    public int getCurrentPointerCount() {
        return this.pointerCount;
    }

    // MARK: Observers

    public void addObserver(TouchEventObserver observer) {
        if (observer != null && !this.observers.contains(observer)) {
            this.observers.push(observer);
        }
    }

    public void removeObserver(TouchEventObserver observer) {
        if (observer != null && this.observers.contains(observer)) {
            this.observers.remove(observer);
        }
    }

    public void removeAllObservers() {
        this.observers.removeAllElements();
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
