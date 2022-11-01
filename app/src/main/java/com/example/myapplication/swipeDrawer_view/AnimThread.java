package com.example.myapplication.swipeDrawer_view;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
public class AnimThread extends Thread {

    private static int FPS = 100;

    private int setDuration;
    private boolean isStop = false;
    private boolean isEnd = false;
    private boolean isAlive = true;

    private Interpolator animInterpolator;

    public AnimThread(int duration, Interpolator interpolator) {
        setDuration = duration;
        animInterpolator = interpolator == null ? new AccelerateDecelerateInterpolator() : interpolator;
        onInit();
        start();
    }

    protected void onInit() {}

    protected void onEnd() {}

    protected void onUpdate(float value) {}

    public void setStop() {
        isStop = true;
    }

    public void setEnd() {
        isEnd = true;
    }

    public void setAlive() {
        isAlive = false;
    }

    public static void setFPS(int num) {
        FPS = num;
    }

    public boolean getStop() {
        return isStop;
    }

    public boolean getEnd() {
        return isEnd;
    }

    public boolean getAlive() {
        return isAlive;
    }

    public static int getFPS() {
        return FPS;
    }

    public float getInterpolation(float t) {
        if (animInterpolator != null) {
            return animInterpolator.getInterpolation(t);
        } else {
            return t;
        }
    }

    @Override
    public void run() {
        if (setDuration > 0) {
            int sSleep = Math.round(1000 / FPS);
            int iNum = 0;
            float eNum = ((float) setDuration / 1000) * FPS;
            float value = 0;
            while (iNum < eNum) {
                if (isEnd) break;
                if (isStop) return;
                try {
                    Thread.sleep(sSleep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                value = getInterpolation(iNum / eNum);
                onUpdate(value);
                iNum++;
            }
            if (value < 1) {
                onUpdate(1);
            }
        } else {
            onUpdate(1);
        }
        onEnd();
    }

    public static boolean isThread(AnimThread thread) {
        if (thread != null) {
            return thread.getAlive() || thread.isAlive();
        } else {
            return false;
        }
    }

}