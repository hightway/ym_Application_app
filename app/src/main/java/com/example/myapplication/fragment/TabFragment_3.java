package com.example.myapplication.fragment;

import android.view.View;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseLazyFragment;

import butterknife.ButterKnife;

public class TabFragment_3 extends BaseLazyFragment {


    @Override
    protected int setLayout() {
        return R.layout.tab_3_fragment_lay;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
    }
}