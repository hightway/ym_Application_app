package com.example.myapplication.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
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

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabFragment_4 extends BaseLazyFragment {

    /*@BindView(R.id.tx_unlogin)
    TextView tx_unlogin;*/
    @BindView(R.id.scroll_down_layout)
    ScrollLayout mScrollLayout;
    /*@BindView(R.id.toolbar)
    Toolbar toolbar;*/
    @BindView(R.id.text_view)
    TextView text_view;
    @BindView(R.id.tx_top)
    TextView tx_top;
    @BindView(R.id.tx_search)
    TextView tx_search;
    @BindView(R.id.rel_search_bar)
    RelativeLayout rel_search_bar;


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

        /*toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mScrollLayout.getCurrentStatus() == ScrollLayout.Status.OPENED) {
                    mScrollLayout.scrollToClose();
                }
            }
        });*/

        //mScrollLayout.setMinOffset(Dp_Px_Util.Dp2Px(getActivity(), 50));
        mScrollLayout.setMinOffset(0);
        mScrollLayout.setMaxOffset(700);
        mScrollLayout.setExitOffset(700);
        //mScrollLayout.setMaxOffset((int) (Dp_Px_Util.getScreenHeight(getActivity()) * 0.5));
        //mScrollLayout.setExitOffset(Dp_Px_Util.dip2px(getActivity(), 50));
        //mScrollLayout.setToOpen();
        mScrollLayout.setToExit();
        mScrollLayout.setIsSupportExit(true);
        mScrollLayout.setAllowHorizontalScroll(false);

        mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener);
        //mScrollLayout.getBackground().setAlpha(0);

        //获取时间段
        //tx_search.setText(getString(getTodayFlag()));
        if(mhandler != null){
            mhandler.postDelayed(runnable, 1000*60*5);
        }
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //获取时间段
            if(tx_search != null){
                tx_search.setText(getString(getTodayFlag()));
            }

            mhandler.postDelayed(runnable, 1000*60*5);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        //获取时间段
        if(tx_search != null){
            tx_search.setText(getString(getTodayFlag()));
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mhandler != null){
            mhandler.removeCallbacks(runnable);
        }
    }

    public static int getTodayFlag(){
        // 获取系统时间
        Calendar c = Calendar.getInstance();
        // 提取他的时钟值，int型
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if(hour < 5){
            return R.string.date_1;
        }else if(hour < 12){
            return R.string.date_2;
        }else if(hour < 13){
            return R.string.date_3;
        }else if(hour < 16){
            return R.string.date_4;
        }else if(hour < 18){
            return R.string.date_5;
        }else if(hour < 24){
            return R.string.date_6;
        }
        return R.string.date_1;
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


    private ScrollLayout.Status status = ScrollLayout.Status.EXIT;
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
                status = ScrollLayout.Status.EXIT;
            }else if(currentStatus.equals(ScrollLayout.Status.OPENED)){
                status = ScrollLayout.Status.OPENED;
            }else if(currentStatus.equals(ScrollLayout.Status.CLOSED)){
                status = ScrollLayout.Status.CLOSED;
            }
        }

        @Override
        public void onChildScroll(int top) {
            if(status.equals(ScrollLayout.Status.CLOSED)){
                rel_search_bar.scrollTo(0, top);
            }
        }
    };


}