package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.view.View;
import android.view.WindowManager;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class WelcomePage_Activity extends BaseActivity {


    private UserConfig config;
    private WelcomePage_Activity instance;

    @Override
    protected int getLayoutID() {
        //避免每次启动都走splash界面
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
        }

        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        return R.layout.welcome;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        instance = this;
        config = UserConfig.instance();
        config.getUserConfig(this);

        //可以做些网络请求，获取首页相关信息
    }


    /*private void get_recommend() {
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("password", password);
        OkHttpUtil.postRequest(Api.HEAD + "login_pwd", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                    toast(jsonObject.getString("errMsg"));
                    if(jsonObject.getInt("errCode") == 200){
                        //注册成功，返回账号密码至登录页面
                        UserBean userBean = mGson.fromJson(response, UserBean.class);
                        UserBean.DataBean dataBean = userBean.getData();

                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        UserConfig.instance().access_token = dataBean.getAccess_token();
                        UserConfig.instance().user_id = dataBean.getUser_id();
                        UserConfig.instance().expires_in = dataBean.getExpires_in();
                        UserConfig.instance().token_type = dataBean.getToken_type();
                        //保存
                        UserConfig.instance().saveUserConfig(instance);

                        //跳转主页面
                        startActivity(new Intent(instance, MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {
        if (config == null) {
            config = UserConfig.instance();
        }
        if (!config.isloaded) {
            mHandle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    config.isloaded = true;
                    startActivity_to(MainActivity.class);
                    finish();
                }
            }, 1800);
        }else{
            config.isloaded = true;
            startActivity_to(MainActivity.class);
            finish();
        }
    }
}
