package com.example.myapplication.swipeDrawer_view;

import com.example.myapplication.swipeDrawer_view.SwipeDrawer;

public interface OnDrawerChange {
    void onChange(SwipeDrawer view, int state, float progress);
}
