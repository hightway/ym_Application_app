package com.example.myapplication.activity;

import android.content.Intent;
import android.view.View;
import android.view.WindowManager;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.http.UserConfig;

public class WelcomePage_Activity extends BaseActivity {


    private UserConfig config;
    private WelcomePage_Activity instance;

    @Override
    protected int getLayoutID() {
        //避免每次启动都走splash界面
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
        }

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        return R.layout.welcome;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        instance = this;
        config = UserConfig.instance();
        config.getUserConfig(this);

        //可以做些网络请求

    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        if (config == null) {
            config = UserConfig.instance();
        }
        if (!config.isloaded) {
            mHandle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    config.isloaded = true;
                    startActivity_to(MainActivity.class);
                    finish();
                }
            }, 1800);
        }else{
            config.isloaded = true;
            startActivity_to(MainActivity.class);
            finish();
        }
    }
}
