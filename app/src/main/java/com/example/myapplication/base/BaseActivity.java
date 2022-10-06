package com.example.myapplication.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public Handler mHandle;
    public Gson mGson;
    public final int EXIT_INFO = 1010;
    public boolean isExit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());

        if(mHandle == null){
            mHandle = new Handler(callback);
        }

        if (mGson == null) {
            mGson = new Gson();
        }

        //竖屏锁定
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //抽象方法
        initView();
        setListener();
        initData();
    }


    Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case EXIT_INFO:
                    isExit = false;
                    break;
            }
            return false;
        }
    };

    @Override
    public void onClick(View view) {
        if (fastClick())
            viewClick(view);
    }
    private long lastClick = 0;
    private boolean fastClick() {
        if (System.currentTimeMillis() - lastClick <= 500) {
            return false;
        }
        lastClick = System.currentTimeMillis();
        return true;
    }


    protected void toast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    protected abstract int getLayoutID();
    public abstract void viewClick(View v);
    protected abstract void initView();
    /**
     * add Listener
     */
    protected abstract void setListener();
    /**
     * 初始化数据
     */
    protected abstract void initData();


    public void hideSoftWorldInput(EditText edit, boolean b){
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(!b){
            imm.showSoftInput(edit,InputMethodManager.SHOW_FORCED);   //SHOW_FORCED表示强制显示
        }else{
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);   //强制隐藏键盘
        }
    }

}
