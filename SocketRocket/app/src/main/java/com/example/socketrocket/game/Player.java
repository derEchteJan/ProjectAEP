package com.example.socketrocket.game;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Player extends Entity {

    private String name = "Jeff"; // für später
    private final double drag = 0.8;


    // MARK: - Entity

    @Override public void tick() {
        super.tick();
        // apply drag
        double sum = this.vX > 0 ? this.vX + (this.vY > 0 ? this.vY : -this.vY) : -this.vX + (this.vY > 0 ? this.vY : -this.vY); // muss man nicht verstehen..
        double dragComponentX = this.vX/sum;
        double dragComponentY = this.vY/sum;
        if(this.vX != 0) this.vX -= this.drag * dragComponentX;
        if(this.vY != 0) this.vY -= this.drag * dragComponentY;
    }

    public void render(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(0xFFFFFF);
        p.setAlpha(255);
        canvas.drawCircle((float)this.pX, (float)this.pY, 20, p);
    }
}
