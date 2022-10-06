package com.example.myapplication.activity;

import static com.example.myapplication.config.Constant.THEME_KEY;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.bean.User_Msg_Bean;
import com.example.myapplication.config.MessageActivity;
import com.example.myapplication.config.OneKeyLoginActivity;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.OkHttpUtil;
import com.google.gson.Gson;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.tx_login)
    TextView tx_login;
    @BindView(R.id.tv_result)
    TextView tv_result;
    @BindView(R.id.wx_login)
    TextView wx_login;

    @Override
    protected int getLayoutID() {
        return R.layout.welcome_lay;
    }

    @Override
    public void viewClick(View v) {

    }


    @OnClick(R.id.tx_login)
    public void tx_login(){
        if(TextUtils.isEmpty(UserConfig.instance().access_token)){
            //一键登录
            Intent pIntent = new Intent(WelcomeActivity.this, OneKeyLoginActivity.class);
            pIntent.putExtra(THEME_KEY, 0);
            startActivity(pIntent);
        }else{
            //获取用户信息，验证token是否过期，未过期更新最新token，已过期请求失败要求重新登录
            getUserMsg();
        }
    }


    @Override
    protected void initView() {
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);

        //初始化，向微信终端注册 id
        //initWX();
    }


    /*private IWXAPI api;
    private static final String APP_ID = "wx88888888";
    private void initWX() {
        // 通过 WXAPIFactory 工厂，获取 IWXAPI 的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);

        // 将应用的 appId 注册到微信
        api.registerApp(APP_ID);

        //建议动态监听微信启动广播进行注册到微信
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 将该 app 注册到微信
                api.registerApp(APP_ID);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
    }*/


    private void getUserMsg() {
        DialogUtils.getInstance().showDialog(this, "加载中...");
        OkHttpUtil.postRequest(Api.HEAD + "user/info", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
                //跳转至登录
                Intent pIntent = new Intent(WelcomeActivity.this, OneKeyLoginActivity.class);
                pIntent.putExtra(THEME_KEY, 0);
                startActivity(pIntent);
            }

            @Override
            public void ok(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("errCode");
                    if(code == 200){
                        User_Msg_Bean user_msg_bean = mGson.fromJson(response, User_Msg_Bean.class);
                        User_Msg_Bean.DataBean dataBean = user_msg_bean.getData();
                        UserConfig.instance().age = dataBean.getAge();
                        UserConfig.instance().user_id = dataBean.getId();
                        UserConfig.instance().access_token = dataBean.getLast_token();
                        UserConfig.instance().register_type = dataBean.getRegister_type();
                        UserConfig.instance().avatar = dataBean.getAvatar();
                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        UserConfig.instance().email = dataBean.getEmail();
                        //保存
                        UserConfig.instance().saveUserConfig(WelcomeActivity.this);

                        //跳转主页面
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    }else{
                        //一键登录
                        Intent pIntent = new Intent(WelcomeActivity.this, OneKeyLoginActivity.class);
                        pIntent.putExtra(THEME_KEY, 0);
                        startActivity(pIntent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    /*@OnClick(R.id.wx_login)
    public void wx_login(){
        SendAuth.Req req = new SendAuth.Req();
        //scope 应用授权作用域，如获取用户个人信息则填写 snsapi_userinfo
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo_test";
        api.sendReq(req);
    }*/

}
