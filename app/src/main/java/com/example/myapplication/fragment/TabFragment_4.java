package com.example.myapplication.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.config.MessageActivity;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabFragment_4 extends BaseLazyFragment {

    @BindView(R.id.tx_unlogin)
    TextView tx_unlogin;

    @Override
    protected int setLayout() {
        return R.layout.tab_4_fragment_lay;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
    }


    @OnClick(R.id.tx_unlogin)
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
    }
}