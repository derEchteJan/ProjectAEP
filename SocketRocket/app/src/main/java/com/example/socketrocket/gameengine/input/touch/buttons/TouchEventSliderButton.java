package com.example.socketrocket.gameengine.input.touch.buttons;

import android.graphics.Canvas;
import android.graphics.Paint;
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
    private double sliderValue = 0.0;
    private double borderInsetFactor = 0.0;

    public TouchEventSliderButton(Rect frame, SliderDirection sliderDirection) {
        super(frame);
        this.sliderDirection = sliderDirection;
    }

    public abstract void onValueChanged(double sliderValue);


    // MARK: - TouchEventButton Override

    @Override
    public void onTouchDown(TouchEvent e) {
        this.sliderValue = this.getSliderValueForTouchEvent(e);
        this.onValueChanged(this.sliderValue);
    }

    @Override
    public void onTouchMove(TouchEvent e) {
        this.sliderValue = this.getSliderValueForTouchEvent(e);
        this.onValueChanged(this.sliderValue);
    }

    @Override
    public void onTouchUp(TouchEvent e) {
        return;
    }


    // MARK: - Methods

    public void setBorderInsetFactor(double borderInsetFactor) {
        this.borderInsetFactor = borderInsetFactor;
    }

    @Override
    public void render(Canvas c) {
        int alpha = 40;
        Paint highlightColor = new Paint();
        highlightColor.setColor(0x00FFFF);
        highlightColor.setAlpha(alpha);
        Paint mainColor = new Paint();
        mainColor.setColor(0xFFFFFF);
        mainColor.setAlpha(alpha);
        Paint borderColor = new Paint();
        borderColor.setColor(0x000000);
        borderColor.setAlpha(alpha);
        Paint textColor = new Paint();
        textColor.setColor(0xFFFFFF);
        textColor.setAlpha(255);
        textColor.setTextSize(60);


        c.drawRect(this.frame, mainColor);
        if(this.sliderValue != 0.0) {

        }
        c.drawText(String.format("%.2f", this.sliderValue), (float)(this.frame.centerX() + 40), (float)(this.frame.centerY() + 15), textColor);
    }

    public void setSliderValue(double sliderValue) {
        this.sliderValue = sliderValue;
    }

    public double getSliderValue() {
        return this.sliderValue;
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
