package com.example.myapplication.base;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.custom.LoadingDialog;
import com.google.gson.Gson;


public abstract class BaseFragment extends Fragment {

    public LayoutInflater inflater;
    protected Activity mActivity;
    private LoadingDialog progressDialog;
    public Handler mhandler;
    public Gson mgson;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(setLayout(), container, false);
        if(mhandler == null){
            mhandler = new Handler();
        }
        if(mgson == null){
            mgson = new Gson();
        }
        this.inflater = inflater;

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViewById(view);
        setViewData(view);
        setClickEvent(view);
    }


    protected abstract int setLayout();
    /**
     * findViewById
     */
    public void findViewById(View view) {}

    /**
     * setViewData
     */
    public void setViewData(View view) {}

    /**
     * setClickEvent
     */
    public void setClickEvent(View view) {}


    // Toast
    protected void toast(CharSequence msg) {
        if(mActivity != null){
            Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void showPrograssDialog(Context mContext, String text){
        if (progressDialog == null) {
            progressDialog = new LoadingDialog(mContext, text);
        }
        progressDialog.show();
    }

    /**
     * 隐藏系统加载框
     */
    public Boolean dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                return true;//取消成功
            }
        }
        //已经取消过了，不需要取消
        return false;
    }


    /**
     *  复制功能
     * */
    public void copy_token(String str) {
        if(mActivity == null){
            return;
        }
        ClipboardManager cm = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(str);
        toast(getString(R.string.copy_success));
    }


    /**
     *  强制隐藏软键盘
     * */
    public void hideSoftWorldInput(EditText edit, boolean b){
        if(mActivity == null){
            return;
        }
        InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(!b){
            imm.showSoftInput(edit,InputMethodManager.SHOW_FORCED);   //SHOW_FORCED表示强制显示
        }else{
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);   //强制隐藏键盘
        }
    }

}
