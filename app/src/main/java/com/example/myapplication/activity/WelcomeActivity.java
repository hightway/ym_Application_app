package com.example.myapplication.activity;

import static com.example.myapplication.config.Constant.THEME_KEY;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.bean.User_Msg_Bean;
import com.example.myapplication.config.OneKeyLoginActivity;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.wxapi.WxLogin;
import com.tencent.mm.opensdk.modelpay.PayReq;
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

    private WX_LoginBrocast wxBrocast;
    private UserConfig userConfig;


    private static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

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
        userConfig = UserConfig.instance();
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);

        //注册广播，接受微信登录返回的数据
        regieBrocast();

        //动态获取权限
        askPermission();
    }


    private void askPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            //Android 6.0申请权限
            ActivityCompat.requestPermissions(this, PERMISSION, 1);
        } else {
            //Toast.makeText(this, "成功", Toast.LENGTH_SHORT).show();
        }
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

    @OnClick(R.id.wx_login)
    public void wx_login(){
        WxLogin.longWx();
    }


    //微信支付
    private void go_wx_pay(String data) {
        IWXAPI api = WXAPIFactory.createWXAPI(WelcomeActivity.this, Api.WX_APP_ID, true);
        if (api != null) {
            //将应用的appid注册到微信
            api.registerApp(Api.WX_APP_ID);
            if(!TextUtils.isEmpty(data)){
                /*PayReq req = Wx_Pay_Util.init_json(data, mGson);
                if(req != null){
                    api.sendReq(req);
                }*/
            }
        }
    }



    private void regieBrocast() {
        IntentFilter inten = new IntentFilter();
        inten.addAction("WX_LOGIN");
        wxBrocast = new WX_LoginBrocast();
        registerReceiver(wxBrocast, inten);
    }

    public class WX_LoginBrocast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals("WX_LOGIN")) {
                String openid = intent.getStringExtra("openid");
                String unionid = intent.getStringExtra("unionid");
                String nickname = intent.getStringExtra("nickname");
                String headimgurl = intent.getStringExtra("headimgurl");
                wx_login(openid, unionid, nickname, headimgurl);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onUnBindReceiver();
    }

    private void onUnBindReceiver() {
        if (wxBrocast != null) {
            unregisterReceiver(wxBrocast);
        }
    }

    public void wx_login(String oid, String uid, String name, String icon) {
        //微信返回的 openid   unionid   nickname   headimgurl
        userConfig.wx_openId = oid;
        userConfig.wx_unionId = uid;
        userConfig.wx_nickname = name;
        userConfig.wx_user_icon = icon;

        wxLogin(oid, uid, name, icon);
    }

    public void wxLogin(String oid, String uid, String name, String icon){
        DialogUtils.getInstance().showDialog(this, "登录中...");
        HashMap<String, String> map = new HashMap<>();
        /*map.put("phone", phone);
        map.put("code", key);*/
        OkHttpUtil.postRequest(Api.HEAD + "login_code", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                        UserBean userBean = mGson.fromJson(response, UserBean.class);
                        UserBean.DataBean dataBean = userBean.getData();

                        userConfig.name = dataBean.getName();
                        userConfig.phone = dataBean.getPhone();
                        userConfig.access_token = dataBean.getAccess_token();
                        userConfig.user_id = dataBean.getUser_id();
                        userConfig.expires_in = dataBean.getExpires_in();
                        userConfig.token_type = dataBean.getToken_type();
                        //保存
                        UserConfig.instance().saveUserConfig(WelcomeActivity.this);

                        //跳转主页面
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
