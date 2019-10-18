package com.example.socketrocket.game.input.touch;

import android.view.MotionEvent;

public interface TouchEventObserver {
    public abstract boolean handleTouchEvent(TouchEvent e);
}
