package com.example.myapplication.custom;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.ViewPage_Meua_Adapter;
import com.example.myapplication.bean.Cate_Bean;
import com.example.myapplication.bean.Hor_DateBean;
import com.example.myapplication.fragment.Anchor_Radio_Fragment;
import com.example.myapplication.fragment.More_Radio_Fragment;
import com.example.myapplication.fragment.Play_History_Fragment;
import com.example.myapplication.fragment.White_Noise_Fragment;
import com.example.myapplication.http.Api;
import com.example.myapplication.tools.OkHttpUtil;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import biz.laenger.android.vpbs.BottomSheetUtils;
import biz.laenger.android.vpbs.ViewPagerBottomSheetDialogFragment;

public class More_DialogFragment extends ViewPagerBottomSheetDialogFragment {

    private ViewPager meua_viewpage;
    private TabLayout tl_tabs;

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        final View contentView = View.inflate(getContext(), R.layout.layout_meua_more, null);
        meua_viewpage = contentView.findViewById(R.id.meua_viewpage);
        tl_tabs = contentView.findViewById(R.id.meua_tabs);
        // ...
        BottomSheetUtils.setupViewPager(meua_viewpage);

        //获取分类
        getData();
        dialog.setContentView(contentView);
        //设置背景透明
        ((View) contentView.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }



    private void getData() {
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "category_list", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
            }

            @Override
            public void un_login_err() {

            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        Cate_Bean cate_bean = new Gson().fromJson(response, Cate_Bean.class);
                        List<Cate_Bean.DataBean> dataBeanList = cate_bean.data;
                        if(dataBeanList != null && dataBeanList.size() > 0){
                            setView(dataBeanList);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setView(List<Cate_Bean.DataBean> dataBeanList) {
        List<String> tab_name = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        for(Cate_Bean.DataBean dataBean : dataBeanList){
            tab_name.add(dataBean.title);
            fragmentList.add(new More_Radio_Fragment(dataBean.id));
        }

        ViewPage_Meua_Adapter adapter = new ViewPage_Meua_Adapter(getChildFragmentManager(), fragmentList, tab_name);
        meua_viewpage.setAdapter(adapter);
        tl_tabs.setupWithViewPager(meua_viewpage);
    }

}