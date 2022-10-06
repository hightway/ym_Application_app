package com.example.myapplication.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class MyViewPage extends ViewPager {

    private boolean can_Scroll = true;
    public MyViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public MyViewPage(Context context) {
        super(context);
    }


    public void setNoScroll(boolean noScroll) {
        this.can_Scroll = noScroll;
    }


    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return can_Scroll && super.onTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return can_Scroll && super.onInterceptTouchEvent(ev);
    }


    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }


    @Override
    public void setCurrentItem(int item) {
        //表示切换的时候，不需要切换时间。
        super.setCurrentItem(item, false);
    }

}
