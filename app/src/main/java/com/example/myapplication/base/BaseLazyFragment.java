package com.example.myapplication.base;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

public abstract class BaseLazyFragment extends BaseFragment{

    /**
     * 描述: 赖加载：子类传递id
     */
    View view = null;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mhandler == null){
            mhandler = new Handler();
        }
        if(mgson == null){
            mgson = new Gson();
        }
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(setLayout(), container, false);
            initView(view);
        }
        return view;
    }


    //初始化控件
    protected abstract void initView(View view);

}
