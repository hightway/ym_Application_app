package com.example.myapplication.activity;

import android.view.View;
import android.webkit.WebView;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;

public class WebVeiwActivity extends BaseActivity {

    @Override
    protected int getLayoutID() {
        return R.layout.auth_webview_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        WebView webview = findViewById(R.id.webview);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }
}
