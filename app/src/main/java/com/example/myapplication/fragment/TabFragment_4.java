package com.example.myapplication.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.config.MessageActivity;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.Dp_Px_Util;
import com.example.myapplication.tools.OkHttpUtil;
import com.yinglan.scrolllayout.ScrollLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabFragment_4 extends BaseLazyFragment {

    /*@BindView(R.id.tx_unlogin)
    TextView tx_unlogin;*/
    @BindView(R.id.scroll_down_layout)
    ScrollLayout mScrollLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.text_view)
    TextView text_view;

    @Override
    protected int setLayout() {
        //return R.layout.tab_4_fragment_lay;
        return R.layout.tab_4;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        /*toolbar.getBackground().setAlpha(0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mScrollLayout.getCurrentStatus() == ScrollLayout.Status.OPENED) {
                    mScrollLayout.scrollToClose();
                }
            }
        });

        mScrollLayout.setMinOffset(Dp_Px_Util.Dp2Px(getActivity(), 50));
        mScrollLayout.setMaxOffset(400);
        mScrollLayout.setExitOffset(400);
        //mScrollLayout.setMaxOffset((int) (Dp_Px_Util.getScreenHeight(getActivity()) * 0.5));
        //mScrollLayout.setExitOffset(Dp_Px_Util.dip2px(getActivity(), 50));
        //mScrollLayout.setToOpen();
        mScrollLayout.setToExit();
        mScrollLayout.setIsSupportExit(true);
        mScrollLayout.setAllowHorizontalScroll(false);

        mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener);
        //mScrollLayout.getBackground().setAlpha(0);
    }


    /*@OnClick(R.id.tx_unlogin)
    public void tx_unlogin(){
        //退出登陆
        DialogUtils.getInstance().showDialog(getActivity(), "退出登录中...");
        OkHttpUtil.postRequest(Api.HEAD + "logout", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
            }

            @Override
            public void ok(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("errCode");
                    toast(jsonObject.getString("errMsg"));
                    if(code == 200){
                        //退出登录成功，清除用户信息
                        UserConfig.instance().exit(getActivity());
                        MyApp.getInstance().close_Activity();
                        System.exit(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/


    @OnClick(R.id.text_view)
    public void text_view(){
        mScrollLayout.setToOpen();
    }


    private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
                float precent = 255 * currentProgress;
                if (precent > 255) {
                    precent = 255;
                } else if (precent < 0) {
                    precent = 0;
                }
                //mScrollLayout.getBackground().setAlpha(255 - (int) precent);
                //toolbar.getBackground().setAlpha(255 - (int) precent);
            }
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                //finish();
            }
        }

        @Override
        public void onChildScroll(int top) {
        }
    };


}