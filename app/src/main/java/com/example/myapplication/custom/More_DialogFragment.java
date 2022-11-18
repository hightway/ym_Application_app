package com.example.myapplication.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewPage_Meua_Adapter;
import com.example.myapplication.fragment.Anchor_Radio_Fragment;
import com.example.myapplication.fragment.More_Radio_Fragment;
import com.example.myapplication.fragment.Play_History_Fragment;
import com.example.myapplication.fragment.White_Noise_Fragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment;

public class More_DialogFragment extends ViewPagerBottomSheetDialogFragment {

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        final View contentView = View.inflate(getContext(), R.layout.layout_meua_more, null);
        ViewPager meua_viewpage = contentView.findViewById(R.id.meua_viewpage);
        TabLayout tl_tabs = contentView.findViewById(R.id.meua_tabs);
        // ...
        BottomSheetUtils.setupViewPager(meua_viewpage);

        List<String> tab_name = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        tab_name.add("心灵鸡汤");
        tab_name.add("睡前助眠");
        tab_name.add("创伤疗愈");
        tab_name.add("情感文学");
        fragmentList.add(new More_Radio_Fragment());
        fragmentList.add(new More_Radio_Fragment());
        fragmentList.add(new More_Radio_Fragment());
        fragmentList.add(new More_Radio_Fragment());
        ViewPage_Meua_Adapter adapter = new ViewPage_Meua_Adapter(getChildFragmentManager(), fragmentList, tab_name);
        meua_viewpage.setAdapter(adapter);
        tl_tabs.setupWithViewPager(meua_viewpage);

        dialog.setContentView(contentView);

        //设置背景透明
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

}