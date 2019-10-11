package com.example.socketrocket;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class MainThread extends Thread {

    public static final int MAX_FPS = 30;
    public static Canvas canvas;

    private double averageFPS = 0;
    private SurfaceHolder surfaceHolder;
    private GamePanel gamePanel;
    private boolean running;

    public MainThread(SurfaceHolder surfaceHolder, GamePanel gamePanel) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gamePanel = gamePanel;
    }

    private MainThread(MainThread copy) {
        this.averageFPS = 0;
        this.surfaceHolder = copy.surfaceHolder;
        this.gamePanel = copy.gamePanel;
        this.running = copy.running;
    }
    public static MainThread getCopyOf(MainThread other) {
        return new MainThread(other);
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        long startTime, waitTime, timeMillis, totalTime = 0;
        int frameCount = 0;
        long targetTime = 1000/MAX_FPS;

        while(this.running) {
            startTime = System.nanoTime();
            canvas = null;
            try {
                // Render and Tick
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (this.surfaceHolder) {
                    this.gamePanel.update();
                    this.gamePanel.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        this.surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            frameCount++;
            // Calculate wait time
            timeMillis = (System.nanoTime() - startTime) / 1_000_000;
            waitTime = targetTime - timeMillis;
            try {
               if(waitTime > 0) {
                   this.sleep(waitTime);
               }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Calculate average fps
            totalTime += System.nanoTime() - startTime;
            if(frameCount == MAX_FPS) {
                averageFPS = 1000/((totalTime/frameCount)/1_000_000);
                System.out.println("average fps: " +  averageFPS);
                frameCount = 0;
                totalTime = 0;
            }
        }
    }

}
