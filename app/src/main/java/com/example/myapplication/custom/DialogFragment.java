package com.example.myapplication.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewPage_Meua_Adapter;
import com.example.myapplication.fragment.Anchor_Radio_Fragment;
import com.example.myapplication.fragment.Play_History_Fragment;
import com.example.myapplication.fragment.White_Noise_Fragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment;

public class DialogFragment extends ViewPagerBottomSheetDialogFragment {

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        final View contentView = View.inflate(getContext(), R.layout.layout_meua, null);
        ViewPager meua_viewpage = contentView.findViewById(R.id.meua_viewpage);
        TabLayout tl_tabs = contentView.findViewById(R.id.meua_tabs);
        // ...
        BottomSheetUtils.setupViewPager(meua_viewpage);

        List<String> tab_name = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        tab_name.add(getString(R.string.meua_1));
        tab_name.add(getString(R.string.meua_2));
        tab_name.add(getString(R.string.meua_3));
        fragmentList.add(new White_Noise_Fragment());
        fragmentList.add(new Anchor_Radio_Fragment());
        fragmentList.add(new Play_History_Fragment());
        ViewPage_Meua_Adapter adapter = new ViewPage_Meua_Adapter(getChildFragmentManager(), fragmentList, tab_name);
        meua_viewpage.setAdapter(adapter);
        tl_tabs.setupWithViewPager(meua_viewpage);

        dialog.setContentView(contentView);

        //设置背景透明
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

}