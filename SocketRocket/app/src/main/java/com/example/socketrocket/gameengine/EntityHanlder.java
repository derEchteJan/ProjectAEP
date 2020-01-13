package com.example.socketrocket.gameengine;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.socketrocket.gameengine.entity.Player;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class EntityHanlder {

    public static boolean tapControllsPlayer = true;
    private final double boundsSideMarginFactor = 1.00;

    private Camera camera = new Camera();
    private Player player = new Player(this);
    private Rect entityBounds;

    public EntityHanlder() {
        int boundsHeight = GameConstants.VIRTUAL_SCREEN_H;
        int boundsWidth = (int)(GameConstants.SCREEN_W * GameConstants.VIRTUAL_SCREEN_H / GameConstants.SCREEN_H); // * boundsSideFactor
        //boundsHeight -= 50; boundsWidth -= 50; // debug
        this.entityBounds = new Rect(0, 0, boundsWidth, boundsHeight);
        this.player.pX = entityBounds.centerX();
        this.player.pY = entityBounds.centerY();
        this.camera.pX = this.player.pX;
        this.camera.pY = this.player.pY;
        this.camera.setChild(this.player);
    }

    public void tickEntities() {
        this.player.tick();
        checkPlayerBounds();
        this.camera.tick();
    }

    public Camera getCamera() {
        return this.camera;
    }

    private void checkPlayerBounds() {
        if (player.pY < entityBounds.top + this.player.getRadius()) {
            player.pY = entityBounds.top + this.player.getRadius();
            player.vY = 0;
        }
        if (player.pY > entityBounds.bottom - this.player.getRadius()) {
            player.pY = entityBounds.bottom - this.player.getRadius();
            player.vY = 0;
        }
    }


    public void renderEntities(Canvas c) {
        // translate to virtual coordinates
        //this.camera.translateCanvas(c);
        // Draw field and contents
        //this.drawField(c); // out of service ;)
        player.render(c);
        camera.render(c);
        // translate back
        //this.camera.inverseTranslateCanvas(c);
    }

    private void drawField(Canvas c) {
        Paint p = new Paint();
        p.setColor(0xFF0000);
        p.setAlpha(0xFF);
        c.drawRect(this.camera.translateRect(this.entityBounds), p);
    }



    public Player getPlayer() {
        return this.player;
    }

}
