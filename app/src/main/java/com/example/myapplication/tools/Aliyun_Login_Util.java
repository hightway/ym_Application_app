package com.example.myapplication.tools;

import static com.example.myapplication.config.MockRequest.getPhoneNumber;
import static com.nirvana.tools.core.ComponentSdkCore.getApplicationContext;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.config.BaseUIConfig;
import com.example.myapplication.config.ExecutorManager;
import com.example.myapplication.fragment.TabFragment_4;
import com.example.myapplication.http.Api;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;

public class Aliyun_Login_Util {

    private static Aliyun_Login_Util aliyun_login_util;
    private TokenResultListener mTokenResultListener;
    private PhoneNumberAuthHelper mPhoneNumberAuthHelper;
    private BaseUIConfig mUIConfig;
    private Activity mActivity;
    private TabFragment_4 tabFragment;

    public Aliyun_Login_Util() {

    }

    public static Aliyun_Login_Util getInstance() {
        if (aliyun_login_util == null) {
            aliyun_login_util = new Aliyun_Login_Util();
        }
        return aliyun_login_util;
    }


    public void initSDK(Activity activity, TabFragment_4 fragment){
        mActivity = activity;
        tabFragment = fragment;
        //初始化aliyun SDK
        sdkInit(Api.aliyun_key);
        //1
        mUIConfig = BaseUIConfig.init(6, mActivity, mPhoneNumberAuthHelper);
        //2
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(getApplicationContext(), mTokenResultListener);
        mPhoneNumberAuthHelper.checkEnvAvailable();
        mUIConfig.configAuthPage();

        //获取
        mPhoneNumberAuthHelper.getLoginToken(mActivity, 5000);
    }


    public void sdkInit(String secretInfo) {
        mTokenResultListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                TokenRet tokenRet = null;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    /*if (ResultCode.CODE_ERROR_ENV_CHECK_SUCCESS.equals(tokenRet.getCode())) {
                        //获取预取号
                        //accelerateLoginPage(5000);
                    }
                    if (ResultCode.CODE_START_AUTHPAGE_SUCCESS.equals(tokenRet.getCode())) {
                        Log.i("TAG", "唤起授权页成功：" + s);
                    }*/
                    if (ResultCode.CODE_SUCCESS.equals(tokenRet.getCode())) {
                        Log.i("TAG", "获取token成功：" + s);
                        //getResultWithToken(tokenRet.getToken());
                        put_Token(tokenRet.getToken());
                        mPhoneNumberAuthHelper.setAuthListener(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTokenFailed(String s) {
                mPhoneNumberAuthHelper.hideLoginLoading();
                TokenRet tokenRet = null;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    if (ResultCode.CODE_ERROR_USER_CANCEL.equals(tokenRet.getCode())) {
                        //取消登录

                    } else {
                        //跳转短信验证登录
                        //msm_login();
                        PopWindowUtil.getInstance().getPopupWindow(mActivity, null, 0, 0, R.style.showPopupAnimation);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPhoneNumberAuthHelper.setAuthListener(null);
            }
        };
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(mActivity, mTokenResultListener);
        mPhoneNumberAuthHelper.getReporter().setLoggerEnable(true);
        mPhoneNumberAuthHelper.setAuthSDKInfo(secretInfo);
    }

    private void put_Token(String token) {
        ExecutorManager.run(new Runnable() {
            @Override
            public void run() {
                //final String result = getPhoneNumber(token);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPhoneNumberAuthHelper.quitLoginPage();
                    }
                });
            }
        });

        tabFragment.put_Token(token);
    }


}
