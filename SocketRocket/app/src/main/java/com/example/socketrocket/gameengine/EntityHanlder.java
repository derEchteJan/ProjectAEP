package com.example.socketrocket.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class EntityHanlder {

    public static boolean tapControllsPlayer = true;

    private Camera camera = new Camera();
    private Player player = new Player();
    int xmin = 30, ymin = 30, xmax = 1200, ymax = 1800;


    public EntityHanlder() {
        this.player.pX = xmin + (xmax - xmin)/2;
        this.player.pY = ymin + (ymax - ymin)/2;
        this.camera.pX = player.pX;
        this.camera.pY = this.player.pY;
        this.camera.setChild(this.player);
    }

    public void tickEntities() {
        this.player.tick();
        checkPlayerBounds();
        this.camera.tick();
    }

    private void checkPlayerBounds() {
        boolean bounce = Math.random() <= 0.40;
        if (player.pX <= xmin) {
            player.pX = xmin;
            player.vX = bounce ? -player.vX : 0;
        }
        if (player.pY <= ymin) {
            player.pY = ymin;
            player.vY = bounce ? -player.vY : 0;
        }
        if (player.pX >= xmax) {
            player.pX = xmax;
            player.vX = bounce ? -player.vX : 0;
        }
        if (player.pY >= ymax) {
            player.pY = ymax;
            player.vY = bounce ? -player.vY : 0;
        }
    }


    public void renderEntities(Canvas c) {
        // translate to virtual coordinates
        this.camera.translateCanvas(c);
        // Draw field and contents
        this.drawField(c);
        player.render(c);
        camera.render(c);
        // translate back
        this.camera.inverseTranslateCanvas(c);
    }

    private void drawField(Canvas c) {
        Paint p = new Paint();
        p.setColor(0x707070);
        p.setAlpha(0xFF);
        c.drawRect(new Rect(xmin, ymin, xmax, ymax), p);
    }

    public void didTouch(float screenX, float screenY) {
        double touchX = this.camera.inverseTranslateX(screenX);
        double touchY = this.camera.inverseTranslateY(screenY);
        if(tapControllsPlayer) {
            // move player towards touch
            int dx = (int)(touchX - player.pX);
            int dy = (int)(touchY - player.pY);
            double maxV = 50;
            this.player.vX = dx > 0 ? min(dx / 10, maxV) : max(dx / 10, -maxV);
            this.player.vY = dy > 0 ? min(dy / 10, maxV) : max(dy / 10, -maxV);
        } else {
            // move camera towards touch
            int dx = (int)(touchX - camera.pX);
            int dy = (int)(touchY - camera.pY);
            double maxV = 20;
            this.camera.pX += dx > 0 ? min(dx / 16, maxV) : max(dx / 16, -maxV);
            this.camera.pY += dy > 0 ? min(dy / 16, maxV) : max(dy / 16, -maxV);
        }

    }

}
