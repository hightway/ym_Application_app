package com.example.myapplication.fragment;

import static com.example.myapplication.config.MockRequest.getPhoneNumber;
import static com.nirvana.tools.core.ComponentSdkCore.getApplicationContext;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.config.ExecutorManager;
import com.example.myapplication.config.MessageActivity;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.plmd.Pop_Show_Interface;
import com.example.myapplication.plmd.Pop_Show_Set;
import com.example.myapplication.tools.Aliyun_Login_Util;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.Dp_Px_Util;
import com.example.myapplication.tools.Login_Util;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.tools.PopWindowUtil;
import com.google.gson.Gson;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.yinglan.scrolllayout.ScrollLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabFragment_4 extends BaseLazyFragment {

    @BindView(R.id.tx_unlogin)
    TextView tx_unlogin;
    @BindView(R.id.tx_login)
    TextView tx_login;
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
    public boolean onBackPressed() {
        return false;
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
        if (mhandler != null) {
            mhandler.postDelayed(runnable, 1000 * 60 * 5);
        }

        //设置回调
        //Pop_Show_Set.setCallBack(this);
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //获取时间段
            if (tx_search != null) {
                tx_search.setText(getString(getTodayFlag()));
            }

            mhandler.postDelayed(runnable, 1000 * 60 * 5);
        }
    };


    @Override
    public void onResume() {
        super.onResume();
        //获取时间段
        if (tx_search != null) {
            tx_search.setText(getString(getTodayFlag()));
        }

        if(!TextUtils.isEmpty(UserConfig.instance().access_token)){
            tx_login.setVisibility(View.GONE);
            tx_unlogin.setVisibility(View.VISIBLE);
        }else{
            tx_login.setVisibility(View.VISIBLE);
            tx_unlogin.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mhandler != null) {
            mhandler.removeCallbacks(runnable);
        }
    }

    public static int getTodayFlag() {
        // 获取系统时间
        Calendar c = Calendar.getInstance();
        // 提取他的时钟值，int型
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour < 5) {
            return R.string.date_1;
        } else if (hour < 12) {
            return R.string.date_2;
        } else if (hour < 13) {
            return R.string.date_3;
        } else if (hour < 16) {
            return R.string.date_4;
        } else if (hour < 18) {
            return R.string.date_5;
        } else if (hour < 24) {
            return R.string.date_6;
        }
        return R.string.date_2;
    }


    @OnClick(R.id.tx_login)
    public void tx_login() {
        /*if(MainActivity.getInstance() == null){
            return;
        }
        MainActivity.mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(getApplicationContext(), MainActivity.mTokenResultListener);
        MainActivity.mPhoneNumberAuthHelper.checkEnvAvailable();
        MainActivity.mUIConfig.configAuthPage();
        MainActivity.mPhoneNumberAuthHelper.getLoginToken(getActivity(), 5000);*/

        Aliyun_Login_Util.getInstance().initSDK(getActivity(), this);
    }

    @OnClick(R.id.tx_unlogin)
    public void tx_unlogin() {
        //退出登陆
        DialogUtils.getInstance().showDialog(getActivity(), "退出登录中...");
        OkHttpUtil.postRequest(Api.HEAD + "logout", new OkHttpUtil.OnRequestNetWorkListener() {
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
                    toast(jsonObject.getString("errMsg"));
                    if (code == 200) {
                        //退出登录成功，清除用户信息
                        UserConfig.instance().exit(getActivity());

                        tx_login.setVisibility(View.VISIBLE);
                        tx_unlogin.setVisibility(View.GONE);

                        //MyApp.getInstance().close_Activity();
                        //System.exit(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @OnClick(R.id.text_view)
    public void text_view() {
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
            } else if (currentStatus.equals(ScrollLayout.Status.OPENED)) {
                status = ScrollLayout.Status.OPENED;
            } else if (currentStatus.equals(ScrollLayout.Status.CLOSED)) {
                status = ScrollLayout.Status.CLOSED;
            }
        }

        @Override
        public void onChildScroll(int top) {
            if (status.equals(ScrollLayout.Status.CLOSED)) {
                rel_search_bar.scrollTo(0, top);
            }
        }
    };


    /*@Override
    public void pop_show() {
        PopupWindow popupWindow = PopWindowUtil.getInstance().getPopupWindow(getActivity(), rel_search_bar, 0, 0, R.style.showPopupAnimation);
    }*/


    public void put_Token(String token){
        //拿到token
        getResultWithToken(token);
    }

    public void getResultWithToken(final String token) {
        /*ExecutorManager.run(new Runnable() {
            @Override
            public void run() {
                final String result = getPhoneNumber(token);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPhoneNumberAuthHelper.quitLoginPage();
                    }
                });
            }
        });*/

        //上传服务器
        if(!token.isEmpty()){
            post_data(token);
        }
    }


    private void post_data(String token) {
        DialogUtils.getInstance().showDialog(getActivity(), "加载中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", token);

        OkHttpUtil.postRequest(Api.HEAD + "login_token", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                    toast(jsonObject.getString("errMsg"));
                    if(code == 200){
                        UserBean userBean = new Gson().fromJson(response, UserBean.class);
                        UserBean.DataBean dataBean = userBean.getData();

                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        UserConfig.instance().access_token = dataBean.getAccess_token();
                        UserConfig.instance().user_id = dataBean.getUser_id();
                        UserConfig.instance().expires_in = dataBean.getExpires_in();
                        UserConfig.instance().token_type = dataBean.getToken_type();
                        //保存
                        UserConfig.instance().saveUserConfig(getActivity());

                        //刷新数据


                        //跳转主页面
                        //startActivity(new Intent(instance, MainActivity.class));
                        //finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}