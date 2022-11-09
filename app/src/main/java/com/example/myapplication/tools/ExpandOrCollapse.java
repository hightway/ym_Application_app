package com.example.myapplication.tools;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class ExpandOrCollapse {

    public static void expand(final View v, int duration, int targetHeight) {
        int prevHeight  = v.getWidth();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        //valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        //valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static void collapse(final View v, int duration, int targetHeight) {
        int prevHeight  = v.getWidth();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        //valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int aa = (int) animation.getAnimatedValue();
                v.getLayoutParams().width = aa;
                v.requestLayout();
            }
        });
        //valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

}
