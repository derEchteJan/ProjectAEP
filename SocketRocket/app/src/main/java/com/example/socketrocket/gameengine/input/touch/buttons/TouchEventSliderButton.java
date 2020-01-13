package com.example.socketrocket.gameengine.input.touch.buttons;

import android.graphics.Rect;
import android.text.method.Touch;

import com.example.socketrocket.gameengine.input.touch.TouchEvent;
import com.example.socketrocket.gameengine.input.touch.TouchEventHandler;

public abstract class TouchEventSliderButton extends TouchEventButton {

    private final double SLIDER_VALUE_MAX = 1.0, SLIDER_VALUE_MIN = -1.0;

    public enum SliderDirection {
        UP, DOWN, LEFT, RIGHT;
    }

    private SliderDirection sliderDirection;
    private double borderInsetFactor = 0.0;

    public TouchEventSliderButton(Rect frame, SliderDirection sliderDirection) {
        super(frame);
        this.sliderDirection = sliderDirection;
    }

    public abstract void onValueChanged(double sliderValue);


    // MARK: - TouchEventButton

    @Override
    public void action() {} // not used for slider button

    @Override
    protected void onTouchDown(TouchEvent e) {
        this.onValueChanged(this.getSliderValueForTouchEvent(e));
    }

    @Override
    protected void onTouchMove(TouchEvent e) {
        this.onValueChanged(this.getSliderValueForTouchEvent(e));
    }

    @Override
    protected void onTouchUp(TouchEvent e) {
        return;
    }


    // MARK: - Methods

    public void setBorderInsetFactor(double borderInsetFactor) {
        this.borderInsetFactor = borderInsetFactor;
    }


    // MARK: - Subs

    private double getSliderValueForTouchEvent(TouchEvent e) {
        double min, max, middle, value;
        switch(this.sliderDirection) {
            case DOWN:
                min = this.frame.top;
                max = this.frame.bottom;
                value = e.y;
                break;
            case LEFT:
                min = this.frame.right;
                max = this.frame.left;
                value = e.x;
                break;
            case UP:
                min = this.frame.bottom;
                max = this.frame.top;
                value = e.y;
                break;
            case RIGHT:
                min = this.frame.left;
                max = this.frame.right;
                value = e.x;
                break;
            default: // never
                return 0;
        }
        middle = (min + max) / 2;
        if (this.borderInsetFactor != 0.0) {
            max -= (max - middle) * this.borderInsetFactor;
        }
        double resultValue = (middle - value)/(middle - max);
        if(resultValue > this.SLIDER_VALUE_MAX) resultValue = this.SLIDER_VALUE_MAX;
        if(resultValue < this.SLIDER_VALUE_MIN) resultValue = this.SLIDER_VALUE_MIN;
        return resultValue;
    }

}
