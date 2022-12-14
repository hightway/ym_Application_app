package com.example.myapplication.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewPage_Meua_Adapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.fragment.Anchor_Radio_Fragment;
import com.example.myapplication.fragment.History_Video_Fragment;
import com.example.myapplication.fragment.History_WhiteNoise_Fragment;
import com.example.myapplication.fragment.Play_History_Fragment;
import com.example.myapplication.fragment.White_Noise_Fragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class History_Activity extends BaseActivity {

    private History_Activity instance;

    @BindView(R.id.meua_tabs)
    TabLayout meua_tabs;
    @BindView(R.id.meua_viewpage)
    ViewPager meua_viewpage;
    @BindView(R.id.img_back)
    ImageView img_back;

    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.history_act;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);


        List<String> tab_name = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        tab_name.add(getString(R.string.meua_4));
        tab_name.add(getString(R.string.meua_1));
        tab_name.add(getString(R.string.meua_2));

        fragmentList.add(new History_Video_Fragment());

        fragmentList.add(new History_WhiteNoise_Fragment());

        fragmentList.add(new Anchor_Radio_Fragment());
        ViewPage_Meua_Adapter adapter = new ViewPage_Meua_Adapter(getSupportFragmentManager(), fragmentList, tab_name);
        meua_viewpage.setAdapter(adapter);
        meua_tabs.setupWithViewPager(meua_viewpage);
    }

    @OnClick(R.id.img_back)
    public void img_back(){
        finish();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }
}
