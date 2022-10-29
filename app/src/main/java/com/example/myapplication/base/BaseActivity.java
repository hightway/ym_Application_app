package com.example.myapplication.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.gson.Gson;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public Handler mHandle;
    public Gson mGson;
    public final int EXIT_INFO = 1010;
    public boolean isExit = false;
    private Dialog dialog;

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


    protected void startActivity_to(Class<?> cls) {
        startActivity_to(cls, null);
    }

    protected void startActivity_to(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
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

    protected void toast_long(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
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


    /**
     * dialog
     */
    public void showMyDialog(Context context, String title, String msg, String ok) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.act_dialog, null);

        if (dialog == null) {
            dialog = new AlertDialog.Builder(context, R.style.mdialog).create();
        }
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setContentView(layout);

        Button dialog_cancel = layout.findViewById(R.id.dialog_cancel);
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialog_ok = layout.findViewById(R.id.dialog_ok);
        dialog_ok.setText(ok);
        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog_Click();
            }
        });

        TextView dialog_text_about = layout.findViewById(R.id.dialog_text_about);
        dialog_text_about.setText(msg);
        TextView dialog_text = layout.findViewById(R.id.dialog_text);
        dialog_text.setText(title);
    }

    public void dialog_Click() {

    }


}
