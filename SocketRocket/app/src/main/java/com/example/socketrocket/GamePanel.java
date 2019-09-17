package com.example.socketrocket;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private MainThread mainThread;

    public GamePanel(Context context) {
        super(context);
        this.getHolder().addCallback(this);
        this.mainThread = new MainThread(this.getHolder(), this);
        this.setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        return;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mainThread.setRunning(true);
        this.mainThread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry) {
            try {
                this.mainThread.setRunning(false);
                this.mainThread.join();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO: Handle Tap Input here
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            System.out.println("TouchEvent at: " + event.getX() + ", " + event.getY());
        }
        return true;
    }

    public void update() {
        // TODO: Handle Tick Updates here
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Fill Background with color
        canvas.drawColor(Color.GRAY);
        // TODO: Handle Rendering here
    }
}
