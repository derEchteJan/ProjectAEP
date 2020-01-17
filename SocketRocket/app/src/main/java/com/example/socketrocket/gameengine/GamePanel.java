package com.example.socketrocket.gameengine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.socketrocket.GameActivity;

public class GamePanel extends SurfaceView implements SurfaceHolder.Callback {

    private GameLoopThread mainThread;
    private Game game;

    public GamePanel(Context context, Activity parentActivity) {
        super(context);
        this.getHolder().addCallback(this);
        this.game = new Game();
        this.game.setParentActivity(parentActivity);
        this.game.init();
        this.mainThread = new GameLoopThread(this.getHolder(), this);
        this.setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        return;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.mainThread = GameLoopThread.getCopyOf(this.mainThread);
        this.mainThread.start();
        this.mainThread.setRunning(true);
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
        this.game.handleTouchInput(event);
        return true;
    }

    public void update() {
        // TODO: Handle Tick Updates here
        this.game.tick();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // Fill Background with color
        canvas.drawColor(0xFF404040);
        this.game.render(canvas);
    }

}
