package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseLazyFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabFragment_2 extends BaseLazyFragment {


    @Override
    protected int setLayout() {
        return R.layout.tab_2_fragment_lay;
    }


    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

    }
}