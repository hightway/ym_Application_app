package com.example.myapplication.swipeDrawer_view;

import android.view.View;

public class ViewUtils {

    public View view = null;
    public View mask = null;
    public int type = -1;
    public int width = 0;
    public int height = 0;
    public int left = 0;
    public int top = 0;
    public int right = 0;
    public int bottom = 0;
    public int paddingLeft = 0;
    public int paddingTop = 0;
    public int paddingRight = 0;
    public int paddingBottom = 0;
    public boolean intercept = true;

    public ViewUtils(View v, int t) {
        view = v;
        type = t;
    }

    public ViewUtils(View v, int t, boolean u) {
        view = v;
        type = t;
        Update(u);
    }

    public void Update(boolean isAll) {
        width = view.getMeasuredWidth();
        height = view.getMeasuredHeight();
        if (isAll) {
            left = view.getLeft();
            top = view.getTop();
            right = view.getRight();
            bottom = view.getBottom();
            paddingLeft = view.getPaddingLeft();
            paddingTop = view.getPaddingTop();
            paddingRight = view.getPaddingRight();
            paddingBottom = view.getPaddingBottom();
        }
    }

    public void setMask(View view) {
        mask = view;
    }

    public void setScale(float scale) {
        view.setScaleX(scale);
        view.setScaleY(scale);
        if (mask != null) {
            mask.setScaleX(scale);
            mask.setScaleY(scale);
        }
    }

    public void setRotation(float rotation) {
        view.setRotation(rotation);
        if (mask != null) {
            mask.setRotation(rotation);
        }
    }

    public void setLeft(int l) {
        left = l;
        view.setLeft(left);
        right = l + width;
        view.setRight(right);
        upMask();
    }

    public void setTop(int t) {
        top = t;
        view.setTop(top);
        bottom = t + height;
        view.setBottom(bottom);
        upMask();
    }

    public void setRight(int r) {
        right = r;
        view.setRight(right);
        left = r - width;
        view.setLeft(left);
        upMask();
    }

    public void setBottom(int b) {
        bottom = b;
        view.setBottom(bottom);
        top = b - height;
        view.setTop(top);
        upMask();
    }

    public void setVisibility(boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    public void setFront() {
        view.bringToFront();
        if (mask != null) {
            mask.bringToFront();
        }
    }

    public void upMask(){
        if (mask != null) {
            mask.setLeft(left);
            mask.setRight(right);
            mask.setTop(top);
            mask.setBottom(bottom);
        }
    }

    public boolean isScrollLeft() {
        return !view.canScrollHorizontally(-1);
    }

    public boolean isScrollTop() {
        return !view.canScrollVertically(-1);
    }

    public boolean isScrollRight() {
        return !view.canScrollHorizontally(1);
    }

    public boolean isScrollBottom() {
        return !view.canScrollVertically(1);
    }


    public boolean isActionDown(int downX, int downY, int[] location) {
        int[] iLocation = getLocation();
        int getX = iLocation[0] - location[0];
        int getY = iLocation[1] - location[1];
        return downX > getX && downY > getY && downX < (getX + width) && downY < (getY + height);
    }

    int[] getLocation() {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return location;
    }

}
