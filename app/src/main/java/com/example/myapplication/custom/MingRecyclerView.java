package com.example.myapplication.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class MingRecyclerView extends RecyclerView {
    private int mFirstY;
    public MingRecyclerView(Context context) {
        super(context);
    }

    public MingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if(e.getAction()==MotionEvent.ACTION_DOWN)mFirstY= (int) e.getY();
        return super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public int getTouchPointY() {
        return mFirstY;
    }
}
