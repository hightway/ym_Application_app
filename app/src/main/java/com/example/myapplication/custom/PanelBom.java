package com.example.myapplication.custom;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.myapplication.R;

public class PanelBom extends LinearLayout implements GestureDetector.OnGestureListener {

    GestureDetector mGesture = null;
    private boolean isScrolling = false;
    private int MAX_HEIGHT = 80;//拖动的最大高度,当前布局位于父布局下面-80位置，这个仅仅是调试参数，这个变量是动态设置的。
    private float mScrollX; // 滑块滑动距离
    public PanelBom(Context context) {
        super(context);
        init();
    }
    public PanelBom(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    //初始化一些参数
    public void init(){
        mGesture = new GestureDetector(this);
        mGesture.setIsLongpressEnabled(false);
        setBackgroundResource(R.drawable.save_bg);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction() && isScrolling == true) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)getLayoutParams();
            // 缩回去
            if (layoutParams.bottomMargin < -MAX_HEIGHT / 2) {
                new AsynMove().execute(-20);//负--往下
            } else {
                new AsynMove().execute(20);//正--往上
            }
        }
        return mGesture.onTouchEvent(event);
    }
    //Touch down时触发
    @Override
    public boolean onDown(MotionEvent e) {
        mScrollX = 0;
        isScrolling = false;
        // 将之改为true，不然事件不会向下传递.
        return true;
    }
    //Touch了还没有滑动时触发
    @Override
    public void onShowPress(MotionEvent e) {

    }
    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        // 说明在上面，要往下
        if (layoutParams.bottomMargin >= 0) {
            new AsynMove().execute(-20);//负--往下
        } else {
            new AsynMove().execute(20);//正--往上
        }
        return true;
    }


    //Touch了滑动时触发
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        isScrolling = true;
        mScrollX += distanceY;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)getLayoutParams();
        layoutParams.bottomMargin += mScrollX;
        if (layoutParams.bottomMargin >= 0) {
            isScrolling = false;// 拖过头了不需要再执行AsynMove了
            layoutParams.bottomMargin = 0;
        } else if (layoutParams.bottomMargin <= -MAX_HEIGHT) {
            // 拖过头了不需要再执行AsynMove了
            isScrolling = false;
            layoutParams.bottomMargin = -MAX_HEIGHT;
        }
        setLayoutParams(layoutParams);
        return false;
    }


    //Touch了不移动一直Touch down时触发
    @Override
    public void onLongPress(MotionEvent e) {

    }


    //Touch了滑动一点距离后，up时触发。
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }


    //异步更新布局的位置
    class AsynMove extends AsyncTask<Integer, Integer, Void> {
        @Override
        protected Void doInBackground(Integer... params) {
            int times = 0;
            int divi = Math.abs(params[0]);
            if (MAX_HEIGHT % divi == 0)// 整除
                times = MAX_HEIGHT / Math.abs(params[0]);
            else
                times = MAX_HEIGHT / divi + 1;// 有余数

            for (int i = 0; i < times; i++) {
                publishProgress(params[0]);
                try {
                    Thread.sleep(divi);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
            if (values[0] < 0) {
                layoutParams.bottomMargin = Math.max(layoutParams.bottomMargin + values[0], -MAX_HEIGHT);
            } else {
                layoutParams.bottomMargin = Math.min(layoutParams.bottomMargin + values[0], 0);
            }
            setLayoutParams(layoutParams);

            super.onProgressUpdate(values);
        }
    }

}
