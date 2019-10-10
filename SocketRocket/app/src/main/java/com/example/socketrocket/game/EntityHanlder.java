package com.example.socketrocket.game;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class EntityHanlder {

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
        if (player.pX <= xmin) {
            player.pX = xmin;
            player.vX = 0;
        }
        if (player.pY <= ymin) {
            player.pY = ymin;
            player.vY = 0;
        }
        if (player.pX >= xmax) {
            player.pX = xmax;
            player.vX = 0;
        }
        if (player.pY >= ymax) {
            player.pY = ymax;
            player.vY = 0;
        }
    }


    public void renderEntities(Canvas c) {
        c.translate((float) camera.pX - 400, (float) camera.pY - 500);
        // alle entities drawen
        drawField(c);
        player.render(c);
        camera.render(c);
        // wieder zurück
        c.translate((float)(-camera.pX + 400), (float)(-camera.pY + 500));
    }

    private void drawField(Canvas c) {
        Paint p = new Paint();
        p.setAlpha(255);
        p.setColor(0xAAAAAAAA);
        c.drawRect(new Rect(xmin, ymin, xmax, ymax),p);
    }

    public void didTouch(float screenX, float screenY) {
        double touchX = this.camera.inverseTranslateX(screenX);
        double touchY = this.camera.inverseTranslateY(screenY);
        int dx = (int)(touchX - player.pX);
        int dy = (int)(touchY - player.pY);
        double maxV = 50;
        this.player.vX = dx > 0 ? min(dx / 10, maxV) : max(dx / 10, -maxV);
        this.player.vY = dy > 0 ? min(dy / 10, maxV) : max(dy / 10, -maxV);
    }
}
