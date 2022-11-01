package com.example.myapplication.swipeDrawer_view;

public interface OnDrawerState {
    void onStart(int type);
    void onMove(int type, float progress);
    void onOpen(int type);
    void onClose(int type);
    void onCancel(int type);
}
