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
    private Rect flyButton, shieldButton, specialButton, attackButton, menuButton;
    private Rect shieldBar, attackBar;
    private Paint shieldBarColor, attackBarColor, emptyBarColor;
    private double flyButtonMaxInputBound;
    private double flyButtonInputValue; // -1.0 <-> 1.0
    private double flyButtonLastInputTapPosion;
    private TouchEventSliderButton testButton;

    public Hud(GameDelegate game) {
        this.gameDelegate = game;
        this.initColors();
        this.initButtonFrames();
        this.initBarFrames();
        this.flyButtonInputValue = 0.0;
        this.testButton = new TouchEventSliderButton(new Rect(200, 300, 1200 ,500), TouchEventSliderButton.SliderDirection.LEFT) {
            @Override public void onValueChanged(double sliderValue) {
                System.out.println("Hello There, "+sliderValue);
            }
        };
        testButton.setBorderInsetFactor(0.0);

    }

    // MARK: Setup

    private void initButtonFrames() {
        int sideButtonWidth = (int)(GameConstants.SCREEN_W * this.BUTTON_W_SCREEN_W_RATIO);
        Rect hudFrame = new Rect(0, 0, GameConstants.SCREEN_W, GameConstants.SCREEN_H);
        this.flyButton = new Rect(hudFrame.left, hudFrame.top, hudFrame.left + sideButtonWidth, hudFrame.bottom);
        int specialButtonH = (int)((hudFrame.top - hudFrame.bottom) * this.TOP_BUTTON_H_SCREEN_H_RATIO);
        this.specialButton = new Rect(hudFrame.right - sideButtonWidth, hudFrame.top, hudFrame.right, hudFrame.top - specialButtonH);
        this.shieldButton = new Rect(hudFrame.right - sideButtonWidth, hudFrame.top - specialButtonH, hudFrame.right, hudFrame.bottom);
        this.attackButton = new Rect(hudFrame.left + sideButtonWidth, hudFrame.top, hudFrame.right - sideButtonWidth, hudFrame.bottom);
        this.menuButton = new Rect(20,20,160,160); // TODO: Vernünftig machen
        this.flyButtonMaxInputBound = 0.8 * this.flyButton.height() / 2;
    }

    private void initBarFrames() {
        this.shieldBar = new Rect(200, 30, 800, 100);
        this.attackBar = new Rect(shieldBar.right + 40, shieldBar.top, shieldBar.right + 40 + shieldBar.width(), shieldBar.bottom);
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
            this.testButton.render(c);
        }
    }

    public void tick() {
        //TODO: balken/anzeigen updaten?
    }


    // MARK: - Display

    private void drawShieldBar(Canvas c) {
        Camera translator = this.gameDelegate.getPlayer().getParentHandler().getCamera(); // todo: benutzen
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
        if(this.testButton.handleTouchEvent(e)) return true;
        Player player = this.gameDelegate.getPlayer();
        // Menu Button
        if (isInBounds(e, this.menuButton)) {
            if (e.action == TouchEvent.Action.UP) {
                this.gameDelegate.openMenu();
            }
            return true;
        }
        // Flying Button
        else if (isInBounds(e, this.flyButton)) {
            if (e.action == TouchEvent.Action.UP) {
                this.flyButtonInputValue = 0.0;
                this.flyButtonLastInputTapPosion = this.flyButton.centerY();
            } else {
                this.flyButtonInputValue = this.calculateThrustForTapPosY(e.y);
                this.flyButtonLastInputTapPosion = e.y;
            }
            player.input.thrust = this.flyButtonInputValue;
            return true;
        }
        // Shield Button
        else if (isInBounds(e, this.shieldButton)) {
            player.input.shieldButtonDown = e.action == TouchEvent.Action.DOWN || e.action == TouchEvent.Action.MOVE;
            return true;
        } else if (isInBounds(e, this.attackButton)) {
            player.input.attackButtonDown = e.action == TouchEvent.Action.DOWN || e.action == TouchEvent.Action.MOVE;
            return true;
        }
        return false;
    }

    private double calculateThrustForTapPosY(double posY) {
        double sliderValue = (this.flyButton.centerY() - posY) / this.flyButtonMaxInputBound;
        if(sliderValue > 1.0) sliderValue = 1.0;
        else if(sliderValue < -1.0) sliderValue = - 1.0;
        return sliderValue;
    }

    private static boolean isInBounds(TouchEvent touch, Rect bounds) {
        return touch.x >= bounds.left && touch.x <= bounds.right && touch.y <= bounds.bottom && touch.y >= bounds.top;
    }


    // MARK: - Debug Stuff

    private void drawHudButtonsForDebug(Canvas c) {
        int alpha = 30;
        Paint flyColor = new Paint();
        flyColor.setColor(0xFFFFFF);
        flyColor.setAlpha(alpha);
        Paint shieldColor = new Paint();
        shieldColor.setColor(0x00FFFF);
        shieldColor.setAlpha(alpha);
        Paint specialColor = new Paint();
        specialColor.setColor(0xFFFF00);
        specialColor.setAlpha(alpha);
        Paint shootColor = new Paint();
        shootColor.setColor(0x101010);
        shootColor.setAlpha(alpha);
        Paint menuColor = new Paint();
        menuColor.setColor(0x000000);
        menuColor.setAlpha(alpha);
        c.drawRect(this.flyButton, flyColor);
        c.drawRect(this.shieldButton, shieldColor);
        c.drawRect(this.specialButton, specialColor);
        c.drawRect(this.attackButton, shootColor);
        c.drawCircle(this.menuButton.centerX(), this.menuButton.centerY(), this.menuButton.height()/2, menuColor);
        // slider input value
        if(this.flyButtonLastInputTapPosion != 0.0) {
            int inputFillMaxY = this.flyButtonLastInputTapPosion > this.flyButton.centerY() ? (int)this.flyButtonLastInputTapPosion - 3 : this.flyButton.centerY() - 3;
            int inputFillMinY = this.flyButtonLastInputTapPosion > this.flyButton.centerY() ? this.flyButton.centerY() + 3 : (int)this.flyButtonLastInputTapPosion + 3;
            c.drawRect(new Rect(this.flyButton.left, inputFillMaxY, this.flyButton.right, inputFillMinY), specialColor);
        }
        c.drawRect(new Rect(this.flyButton.left, this.flyButton.centerY() + 3, this.flyButton.right, this.flyButton.centerY() - 3), menuColor);
        c.drawRect(new Rect(this.flyButton.left, (int)this.flyButtonLastInputTapPosion + 3, this.flyButton.right, (int)this.flyButtonLastInputTapPosion - 3), menuColor);
        Paint textColor = new Paint();
        textColor.setColor(0xFFFFFF);
        textColor.setAlpha(255);
        textColor.setTextSize(60);
        c.drawText(String.format("%.2f", flyButtonInputValue), (float)(flyButton.right + 40), (float)(flyButton.centerY() + 15), textColor);
    }

}
