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

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        long startTime, waitTime, totalTime = 0;
        long timeMillis = 1000/MAX_FPS;
        int frameCount = 0;
        long targetTime = 1000/MAX_FPS;

        while(this.running) {
            startTime = System.nanoTime();
            canvas = null;
            try {
                // Render and Tick
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
                frameCount = 0;
                System.out.println("average fps: " +  averageFPS);
            }
        }
    }

}
