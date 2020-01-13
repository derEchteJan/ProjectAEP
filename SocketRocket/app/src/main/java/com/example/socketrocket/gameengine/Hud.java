package com.example.socketrocket.gameengine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.socketrocket.gameengine.entity.Player;
import com.example.socketrocket.gameengine.input.touch.TouchEvent;
import com.example.socketrocket.gameengine.input.touch.TouchEventObserver;
import com.example.socketrocket.gameengine.input.touch.buttons.TouchEventButton;
import com.example.socketrocket.gameengine.input.touch.buttons.TouchEventSliderButton;

public class Hud implements TouchEventObserver {

    // Button Layout Constants
    private final double BUTTON_W_SCREEN_W_RATIO = 1.0/5.0;
    private final double TOP_BUTTON_H_SCREEN_H_RATIO = 1.0/2.0;

    private final GameDelegate gameDelegate;
    private TouchEventButton[] buttons;
    private Rect shieldBar, attackBar;
    private Paint shieldBarColor, attackBarColor, emptyBarColor;

    public Hud(GameDelegate game) {
        this.gameDelegate = game;
        this.initColors();
        this.initButtons();
        this.initBarFrames();
    }

    // MARK: Setup

    private void initButtons() {
        Rect hudFrame = new Rect(0, 0, GameConstants.SCREEN_W, GameConstants.SCREEN_H);
        int sideButtonWidth = (int)(GameConstants.SCREEN_W * this.BUTTON_W_SCREEN_W_RATIO);
        int specialButtonH = (int)((hudFrame.top - hudFrame.bottom) * this.TOP_BUTTON_H_SCREEN_H_RATIO);
        Rect specialButtonFrame = new Rect(hudFrame.right - sideButtonWidth, hudFrame.top, hudFrame.right, hudFrame.top - specialButtonH);
        TouchEventButton specialButton = new TouchEventButton(specialButtonFrame) {
            @Override public void onTouchDown(TouchEvent e) {
                gameDelegate.getPlayer().input.specialButtonDown = true;
            }
            @Override public void onTouchUp(TouchEvent e) {
                gameDelegate.getPlayer().input.specialButtonDown = false;
            }
        };
        Rect shieldButtonFrame = new Rect(hudFrame.right - sideButtonWidth, hudFrame.top - specialButtonH, hudFrame.right, hudFrame.bottom);
        TouchEventButton shieldButton = new TouchEventButton(shieldButtonFrame) {
            @Override public void onTouchDown(TouchEvent e) {
                gameDelegate.getPlayer().input.shieldButtonDown = true;
            }
            @Override public void onTouchUp(TouchEvent e) {
                gameDelegate.getPlayer().input.shieldButtonDown = false;
            }
        };

        Rect flyButtonFrame = new Rect(hudFrame.left, hudFrame.top, hudFrame.left + sideButtonWidth, hudFrame.bottom);
        TouchEventSliderButton flyButton = new TouchEventSliderButton(flyButtonFrame, TouchEventSliderButton.SliderDirection.UP) {
            @Override public void onValueChanged(double sliderValue) {
                gameDelegate.getPlayer().input.thrust = sliderValue;
            }
            @Override public void onTouchUp(TouchEvent e) {
                setSliderValue(0.0);
                gameDelegate.getPlayer().input.thrust = 0.0;
            }
        };
        flyButton.setBorderInsetFactor(0.2);
        Rect attackButtonFrame = new Rect(hudFrame.left + sideButtonWidth, hudFrame.top, hudFrame.right - sideButtonWidth, hudFrame.bottom);
        TouchEventButton attackButton = new TouchEventButton(attackButtonFrame) {
            @Override public void onTouchDown(TouchEvent e) {
                gameDelegate.getPlayer().input.attackButtonDown = true;
            }
            @Override public void onTouchUp(TouchEvent e) {
                gameDelegate.getPlayer().input.attackButtonDown = false;
            }
        };
        Rect menuButtonFrame = new Rect(20,20,160,160); // TODO: Vernünftig machen
        TouchEventButton menuButton = new TouchEventButton(menuButtonFrame) {
            @Override public void onTouchUpInside(TouchEvent e) {
                gameDelegate.openMenu();
            }
        };
        this.buttons = new TouchEventButton[] {menuButton, specialButton, shieldButton, attackButton, flyButton};
    }

    private void initBarFrames() {
        int barWidth = GameConstants.SCREEN_W / 4;
        int barHeight = GameConstants.SCREEN_H / 20;
        int barOffsetX1 = barWidth / 2;
        int barOffsetX2 = barOffsetX1 + 40 + barWidth;
        int barOffsetY = barHeight / 2;
        this.shieldBar = new Rect(barOffsetX1, barOffsetY, barOffsetX1 + barWidth, barOffsetY + barHeight);
        this.attackBar = new Rect(barOffsetX2, barOffsetY, barOffsetX2 + barWidth, barOffsetY + barHeight);
    }

    private void initColors() {
        this.shieldBarColor = new Paint();
        this.shieldBarColor.setColor(0x00FFFF);
        this.shieldBarColor.setAlpha(255);
        this.attackBarColor = new Paint();
        this.attackBarColor.setColor(0xB00000);
        this.attackBarColor.setAlpha(255);
        this.emptyBarColor = new Paint();
        this.emptyBarColor.setColor(0x505050);
        this.emptyBarColor.setAlpha(255);
    }


    // MARK: - Public Methods

    public void render(Canvas c) {
        this.drawShieldBar(c);
        this.drawAttackBar(c);
        if (GameConstants.DEBUG_MODE) {
            this.drawHudButtonsForDebug(c);
        }
    }

    public void tick() {
        //TODO: wer will was machen amk?!
    }


    // MARK: - Display

    private void drawShieldBar(Canvas c) {
        int fillPercent = this.gameDelegate.getPlayer().getShieldPercent();
        int fillBorderX = this.shieldBar.left + this.shieldBar.width() * fillPercent / 100;
        Rect filledRect = new Rect(this.shieldBar.left, this.shieldBar.top, fillBorderX, this.shieldBar.bottom);
        Rect unfilledRect = new Rect(fillBorderX + 1, this.shieldBar.top, this.shieldBar.right, this.shieldBar.bottom);
        c.drawRect(filledRect, this.shieldBarColor);
        c.drawRect(unfilledRect, this.emptyBarColor);
    }

    private void drawAttackBar(Canvas c) {
        int fillPercent = this.gameDelegate.getPlayer().getAttackPercent();
        int fillBorderX = this.attackBar.left + this.attackBar.width() * fillPercent / 100;
        Rect filledRect = new Rect(this.attackBar.left, this.attackBar.top, fillBorderX, this.attackBar.bottom);
        Rect unfilledRect = new Rect(fillBorderX + 1, this.attackBar.top, this.attackBar.right, this.attackBar.bottom);
        c.drawRect(filledRect, this.attackBarColor);
        c.drawRect(unfilledRect, this.emptyBarColor);
    }


    // MARK: - Input Handling

    public boolean handleTouchEvent(TouchEvent e) {
        for(TouchEventButton button: this.buttons) {
            if(button.handleTouchEvent(e)) return true;
        }
        return false;
    }


    // MARK: - Debug Stuff

    private void drawHudButtonsForDebug(Canvas c) {
        for(TouchEventButton button: this.buttons) {
            button.render(c);
        }
    }

}
