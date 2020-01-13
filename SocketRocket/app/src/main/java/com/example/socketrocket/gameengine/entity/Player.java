package com.example.socketrocket.gameengine.entity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.socketrocket.gameengine.Camera;
import com.example.socketrocket.gameengine.EntityHanlder;

public class Player extends Entity {

    public class Input {
        public double thrust = 0.0;
        public boolean shieldButtonDown = false;
        public boolean specialButtonDown = false;
        public boolean attackButtonDown = false;
    }

    // constants
    private final double gravity = -0.4;
    private final double maxThrust = 1.5;
    private final double minThrust = -1.0;
    private final double maxVerticalVelocity = 30.0;
    private final int shieldMaxCapacity = 100;
    private final int shieldCapacityUsageDecrement = 3;
    private final int shieldCapacityRegenerateIncrement = 2;
    private final int attackMaxCapacity = 300;
    private final int attackCapacityUsageIncrement = 3;
    private final int attackCapacityCooldownDecrement = 2;


    // other attributes
    public final Input input = new Input();
    private Paint mainBodyColor, shieldColor;
    private double radius = 20;
    private double shieldRadius = 30;
    private int shieldCapacity = shieldMaxCapacity;
    private int attackCapacity = 0;
    private boolean shieldActive = false;
    private boolean attackActive = false;


    // MARK: - Entity

    public Player(EntityHanlder parentHandler) {
        super(parentHandler);
        this.shieldColor = new Paint();
        this.shieldColor.setColor(0x00FFFF);
        this.shieldColor.setAlpha(255);
        this.mainBodyColor = new Paint();
        this.mainBodyColor.setColor(0xFFFFFF);
        this.mainBodyColor.setAlpha(255);
    }

    @Override
    public void tick() {
        super.tick();
        // apply acceleration
        double acc = this.input.thrust + this.gravity;
        this.vY += acc;
        if(this.vY > maxVerticalVelocity) this.vY = maxVerticalVelocity;
        if(this.vY < -maxVerticalVelocity) this.vY = -maxVerticalVelocity;
        // shield
        if(this.input.shieldButtonDown) {
            this.shieldActive = true;
            this.shieldCapacity -= this.shieldCapacityUsageDecrement;
            if(this.shieldCapacity < 0) {
                this.shieldCapacity = 0;
            }
        } else {
            this.shieldActive = false;
            this.shieldCapacity += this.shieldCapacityRegenerateIncrement;
            if(this.shieldCapacity > this.shieldMaxCapacity) {
                this.shieldCapacity = this.shieldMaxCapacity;
            }
        }
        if(this.input.attackButtonDown) {
            this.attackActive = true;
            this.attackCapacity += this.attackCapacityUsageIncrement;
            if(this.attackCapacity > this.attackMaxCapacity) {
                this.attackCapacity = attackMaxCapacity;
            }
        } else {
            this.attackActive = false;
            this.attackCapacity -= this.attackCapacityCooldownDecrement;
            if(this.attackCapacity < 0) {
                this.attackCapacity = 0;
            }
        }
    }

    public void render(Canvas canvas) {
        Camera translator = this.parentHandler.getCamera();
        float screenX = translator.translateX(this.pX);
        float screenY = translator.translateY(this.pY);
        // shield
        if(this.shieldActive) {
            int maxAlpha = 255, minAlpha = 60;
            this.shieldColor.setAlpha((maxAlpha-minAlpha) * this.shieldCapacity / this.shieldMaxCapacity + minAlpha);
            canvas.drawCircle(screenX, screenY, translator.translateLength(this.shieldRadius), this.shieldColor);
        }
        // body
        canvas.drawCircle(screenX, screenY, translator.translateLength(this.radius), this.mainBodyColor);
    }


    // MARK: - Getters

    public EntityHanlder getParentHandler() {
        return this.parentHandler;
    }

    public double getRadius() {
        return this.radius;
    }

    public int getShieldPercent() {
        return this.shieldCapacity * 100 / this.shieldMaxCapacity;
    }

    public int getAttackPercent() {
        return this.attackCapacity * 100 / this.attackMaxCapacity;
    }
}
