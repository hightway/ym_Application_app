package com.example.myapplication.activity;

import static com.example.myapplication.config.Constant.THEME_KEY;
import static com.example.myapplication.config.MockRequest.getPhoneNumber;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.bean.User_Msg_Bean;
import com.example.myapplication.config.BaseUIConfig;
import com.example.myapplication.config.ExecutorManager;
import com.example.myapplication.config.MessageActivity;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.Login_Util;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.wxapi.WxLogin;
import com.google.gson.Gson;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;
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
    ImageView wx_login;
    @BindView(R.id.img_sel_button)
    ImageView img_sel_button;

    private WX_LoginBrocast wxBrocast;
    private UserConfig userConfig;
    private boolean is_sel;
    private Context instance;

    private static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    private int login_type = 1;
    private int mUIType = 6;
    private PhoneNumberAuthHelper mPhoneNumberAuthHelper;
    private TokenResultListener mTokenResultListener;
    private ProgressDialog mProgressDialog;
    private BaseUIConfig mUIConfig;


    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.welcome_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        userConfig = UserConfig.instance();
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);

        //????????????????????????????????????????????????
        regieBrocast();

        //??????????????????
        //askPermission();

        //?????????aliyun SDK
        sdkInit(Api.aliyun_key);
        mUIConfig = BaseUIConfig.init(mUIType, this, mPhoneNumberAuthHelper);
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(getApplicationContext(), mTokenResultListener);
        mPhoneNumberAuthHelper.checkEnvAvailable();
        mUIConfig.configAuthPage();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mUIConfig.onResume();
    }


    @OnClick(R.id.img_sel_button)
    public void img_sel_button(){
        if(is_sel){
            img_sel_button.setImageResource(R.mipmap.simple_sel);
        }else{
            img_sel_button.setImageResource(R.mipmap.simple_sel_on);
        }
        is_sel = !is_sel;
    }


    @OnClick(R.id.tx_login)
    public void tx_login(){
        if(!is_sel){
            login_type = 1;
            showMyDialog(instance, getString(R.string.title), getString(R.string.content), getString(R.string.ok));
            return;
        }

        aliyun_login();
    }

    /**
     *  ?????????????????????
     * */
    private void aliyun_login() {

        //??????token
        //oneKeyLogin();

        getLoginToken(5000);

        /*if(TextUtils.isEmpty(UserConfig.instance().access_token)){
            //????????????
            Intent pIntent = new Intent(WelcomeActivity.this, OneKeyLoginActivity.class);
            pIntent.putExtra(THEME_KEY, 0);
            startActivity(pIntent);

            //??????token
            oneKeyLogin();

        }else{
            //???????????????????????????token????????????????????????????????????token??????????????????????????????????????????
            getUserMsg();
        }*/
    }


    public void sdkInit(String secretInfo) {
        mTokenResultListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                //hideLoadingDialog();
                TokenRet tokenRet = null;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    //
                    if (ResultCode.CODE_ERROR_ENV_CHECK_SUCCESS.equals(tokenRet.getCode())) {
                        //???????????????
                        //accelerateLoginPage(5000);
                    }

                    if (ResultCode.CODE_START_AUTHPAGE_SUCCESS.equals(tokenRet.getCode())) {
                        Log.i("TAG", "????????????????????????" + s);
                    }

                    if (ResultCode.CODE_SUCCESS.equals(tokenRet.getCode())) {
                        Log.i("TAG", "??????token?????????" + s);
                        getResultWithToken(tokenRet.getToken());
                        mPhoneNumberAuthHelper.setAuthListener(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailed(String s) {
                //hideLoadingDialog();
                mPhoneNumberAuthHelper.hideLoginLoading();
                TokenRet tokenRet = null;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    if (ResultCode.CODE_ERROR_USER_CANCEL.equals(tokenRet.getCode())) {
                        //???????????????????????? ??????????????????app?????????
                        //finish();

                        //????????????????????????
                        //msm_login();
                    } else {
                        //????????????????????????
                        msm_login();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPhoneNumberAuthHelper.setAuthListener(null);
            }
        };
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(this, mTokenResultListener);
        mPhoneNumberAuthHelper.getReporter().setLoggerEnable(true);
        mPhoneNumberAuthHelper.setAuthSDKInfo(secretInfo);
        //
        //mPhoneNumberAuthHelper.checkEnvAvailable(PhoneNumberAuthHelper.SERVICE_TYPE_LOGIN);
    }


    public void msm_login(){
        //????????????????????????
        Toast.makeText(getApplicationContext(), "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
        Intent pIntent = new Intent(instance, MessageActivity.class);
        startActivity(pIntent);
    }


    /**
     * ??????app??????????????????????????????
     */
    /*private void oneKeyLogin() {
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(getApplicationContext(), mTokenResultListener);
        mPhoneNumberAuthHelper.checkEnvAvailable();
        mUIConfig.configAuthPage();
        //??????????????????????????????
        //mPhoneNumberAuthHelper.closeAuthPageReturnBack(true);
        //???????????????????????????
        //mPhoneNumberAuthHelper.keepAuthPageLandscapeFullSreen(true);
        //???????????????????????????????????????????????????????????????
        //mPhoneNumberAuthHelper.expandAuthPageCheckedScope(true);
        getLoginToken(5000);
    }*/

    /**
     * ???????????????
     * @param timeout ????????????
     */
    public void getLoginToken(int timeout) {
        mPhoneNumberAuthHelper.getLoginToken(this, timeout);
        //showLoadingDialog("?????????????????????");
    }

    public void getResultWithToken(final String token) {
        ExecutorManager.run(new Runnable() {
            @Override
            public void run() {
                final String result = getPhoneNumber(token);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPhoneNumberAuthHelper.quitLoginPage();
                    }
                });
            }
        });

        //???????????????
        if(!token.isEmpty()){
            post_data(token);
        }
    }

    private void post_data(String token) {
        DialogUtils.getInstance().showDialog(this, "?????????...");
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", token);

        OkHttpUtil.postRequest(Api.HEAD + "login_token", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("????????????");
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
                        UserBean userBean = mGson.fromJson(response, UserBean.class);
                        UserBean.DataBean dataBean = userBean.getData();

                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        UserConfig.instance().access_token = dataBean.getAccess_token();
                        UserConfig.instance().user_id = dataBean.getUser_id();
                        UserConfig.instance().expires_in = dataBean.getExpires_in();
                        UserConfig.instance().token_type = dataBean.getToken_type();
                        //??????
                        UserConfig.instance().saveUserConfig(instance);

                        //???????????????
                        startActivity(new Intent(instance, MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    @Override
    public void dialog_Click() {
        super.dialog_Click();
        img_sel_button.setImageResource(R.mipmap.simple_sel_on);
        is_sel = true;

        if(login_type == 1){
            aliyun_login();
        }else{
            WxLogin.longWx();
        }
    }


    /*private void askPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Android 6.0????????????
            ActivityCompat.requestPermissions(this, PERMISSION, 1);
        } else {

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            //Android 6.0????????????
            ActivityCompat.requestPermissions(this, PERMISSION, 1);
        } else {

        }
    }*/




    /*private IWXAPI api;
    private static final String APP_ID = "wx88888888";
    private void initWX() {
        // ?????? WXAPIFactory ??????????????? IWXAPI ?????????
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);

        // ???????????? appId ???????????????
        api.registerApp(APP_ID);

        //?????????????????????????????????????????????????????????
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // ?????? app ???????????????
                api.registerApp(APP_ID);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
    }*/


    /*private void getUserMsg() {
        DialogUtils.getInstance().showDialog(this, "?????????...");
        OkHttpUtil.postRequest(Api.HEAD + "user/info", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("????????????");
            }

            @Override
            public void un_login_err() {
                //Login_Util.go_Login(WelcomeActivity.this);
            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if(code == 200){
                        User_Msg_Bean user_msg_bean = mGson.fromJson(response, User_Msg_Bean.class);
                        User_Msg_Bean.DataBean dataBean = user_msg_bean.getData();
                        UserConfig.instance().age = dataBean.getAge();
                        UserConfig.instance().user_id = dataBean.getId();
                        //UserConfig.instance().access_token = dataBean.getLast_token();
                        //UserConfig.instance().register_type = dataBean.getRegister_type();
                        UserConfig.instance().avatar = dataBean.getAvatar();
                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        //UserConfig.instance().email = dataBean.getEmail();
                        //??????
                        UserConfig.instance().saveUserConfig(WelcomeActivity.this);

                        //???????????????
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    }else{
                        //????????????
                        *//*Intent pIntent = new Intent(WelcomeActivity.this, OneKeyLoginActivity.class);
                        pIntent.putExtra(THEME_KEY, 0);
                        startActivity(pIntent);*//*
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

    }

    @OnClick(R.id.wx_login)
    public void wx_login(){
        if(!is_sel){
            login_type = 2;
            showMyDialog(instance, getString(R.string.title), getString(R.string.content), getString(R.string.ok));
            return;
        }
        WxLogin.longWx();
    }


    //????????????
    private void go_wx_pay(String data) {
        IWXAPI api = WXAPIFactory.createWXAPI(WelcomeActivity.this, Api.WX_APP_ID, true);
        if (api != null) {
            //????????????appid???????????????
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
        //??????????????? openid   unionid   nickname   headimgurl
        userConfig.wx_openId = oid;
        userConfig.wx_unionId = uid;
        userConfig.wx_nickname = name;
        userConfig.wx_user_icon = icon;

        wxLogin(oid, uid, name, icon);
    }

    public void wxLogin(String oid, String uid, String name, String icon){
        DialogUtils.getInstance().showDialog(this, "?????????...");
        HashMap<String, String> map = new HashMap<>();
        /*map.put("phone", phone);
        map.put("code", key);*/
        OkHttpUtil.postRequest(Api.HEAD + "login_code", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("????????????");
            }

            @Override
            public void un_login_err() {
                Login_Util.go_Login(WelcomeActivity.this);
            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
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
                        //??????
                        UserConfig.instance().saveUserConfig(WelcomeActivity.this);

                        //???????????????
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    /*public void showLoadingDialog(String hint) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mProgressDialog.setMessage(hint);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
    }

    public void hideLoadingDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }*/

}
