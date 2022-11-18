package com.example.myapplication.custom;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.viewpager.widget.ViewPager;

import com.aliyun.player.nativeclass.DisplayViewHelper;
import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewPage_Meua_Adapter;
import com.example.myapplication.fragment.Anchor_Radio_Fragment;
import com.example.myapplication.fragment.Play_History_Fragment;
import com.example.myapplication.fragment.White_Noise_Fragment;
import com.example.myapplication.plmd.Put_Top;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class MyDialogFragment extends DialogFragment implements View.OnClickListener {

    public Activity mContext;
    private boolean is_top = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NO_FRAME,R.style.MyMiddleDialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //去掉默认样式中的title
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getDialog().getWindow();
        //设置dialog背景透明
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //放置位置
        window.setGravity(Gravity.BOTTOM);
        //给dialog设置弹出动画
        window.setWindowAnimations(R.style.showPopupAnimation);
        // 设置点击dialog外的时候dialog消失
        getDialog().setCanceledOnTouchOutside(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_meua, container, false);

        TabLayout tl_tabs = view.findViewById(R.id.meua_tabs);
        ViewPager meua_viewpage = view.findViewById(R.id.meua_viewpage);
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

        return view;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mContext = activity;
    }


    @Override
    public void onStart() {
        super.onStart();

        //设置dialog的大小
        /*WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity= Gravity.BOTTOM;
        window.setAttributes(lp);*/

        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.7);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        getDialog().setCanceledOnTouchOutside(true); //点击边际可消失

    }


    @Override
    public void onClick(View view) {

    }

    /*@Override
    public void go_top() {
        if(is_top){
            return;
        }

        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.9);
        //getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);

        WindowManager.LayoutParams attributes = getDialog().getWindow().getAttributes();
        attributes.height = dialogHeight;
        getDialog().getWindow().setAttributes(attributes);

        is_top = true;
    }

    @Override
    public void go_buttom() {
        if(!is_top){
            return;
        }
        int dialogHeight = (int) (mContext.getResources().getDisplayMetrics().heightPixels * 0.7);
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, dialogHeight);
        is_top = false;
    }*/
}
