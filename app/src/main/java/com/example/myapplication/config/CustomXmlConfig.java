package com.example.myapplication.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.tools.PopWindowUtil;
import com.example.myapplication.tools.WindowUtils;
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.AuthUIControlClickListener;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;
import com.nirvana.tools.core.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * xml文件方便预览
 * 可以通过addAuthRegisterXmlConfig一次性统一添加授权页的所有自定义view
 */
public class CustomXmlConfig extends BaseUIConfig {

    public Activity activity;
    private final String TAG = "log_aliyun";
    private boolean is_Check = false;

    public CustomXmlConfig(Activity act, PhoneNumberAuthHelper authHelper) {
        super(act, authHelper);
        this.activity = act;
    }
    @Override
    public void configAuthPage() {
        mAuthHelper.setUIClickListener(new AuthUIControlClickListener() {
            @Override
            public void onClick(String code, Context context, String jsonString) {
                JSONObject jsonObj = null;
                try {
                    if(!TextUtils.isEmpty(jsonString)) {
                        jsonObj = new JSONObject(jsonString);
                    }
                } catch (JSONException e) {
                    jsonObj = new JSONObject();
                }

                switch (code) {
                    //点击授权页默认样式的返回按钮
                    /*case ResultCode.CODE_ERROR_USER_CANCEL:
                        Log.e(TAG, "点击了授权页默认返回按钮");
                        mAuthHelper.quitLoginPage();
                        mActivity.finish();
                        break;*/
                    //点击授权页默认样式的切换其他登录方式 会关闭授权页
                    //如果不希望关闭授权页那就setSwitchAccHidden(true)隐藏默认的  通过自定义view添加自己的
                    /*case ResultCode.CODE_ERROR_USER_SWITCH:
                        Log.e(TAG, "点击了授权页默认切换其他登录方式");
                        break;*/
                    //点击一键登录按钮会发出此回调
                    //当协议栏没有勾选时 点击按钮会有默认toast 如果不需要或者希望自定义内容 setLogBtnToastHidden(true)隐藏默认Toast
                    //通过此回调自己设置toast
                    /*case ResultCode.CODE_ERROR_USER_LOGIN_BTN:
                        if (!jsonObj.optBoolean("isChecked")) {
                            Toast.makeText(mContext, R.string.custom_toast, Toast.LENGTH_SHORT).show();
                        }
                        break;*/
                    //checkbox状态改变触发此回调
                    case ResultCode.CODE_ERROR_USER_CHECKBOX:
                        //Log.e(TAG, "checkbox状态变为" + jsonObj.optBoolean("isChecked"));
                        if (!jsonObj.optBoolean("isChecked")) {
                            is_Check = false;
                        }else{
                            is_Check = true;
                        }
                        break;
                    //点击协议栏触发此回调
                    /*case ResultCode.CODE_ERROR_USER_PROTOCOL_CONTROL:
                        Log.e(TAG, "点击协议，" + "name: " + jsonObj.optString("name") + ", url: " + jsonObj.optString("url"));
                        break;*/
                    default:
                        break;
                }
            }
        });


        mAuthHelper.removeAuthRegisterXmlConfig();
        mAuthHelper.removeAuthRegisterViewConfig();
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }

        //弹窗样式
        updateScreenSize(authPageOrientation);
        int dialogHeight = (int) (mScreenHeightDp * 0.5f);
        //sdk默认控件的区域是marginTop50dp
        int designHeight = dialogHeight - 50;
        int unit = designHeight / 10;
        int logBtnHeight = (int) (unit * 1.2);


        mAuthHelper.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder()
                .setLayout(R.layout.custom_full_port, new AbstractPnsViewDelegate() {
                    @Override
                    public void onViewCreated(View view) {
                        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAuthHelper.quitLoginPage();
                            }
                        });

                        ImageView img_msm = (ImageView) findViewById(R.id.img_msm);
                        ImageView img_wx = (ImageView) findViewById(R.id.img_wx);
                        //短信登录
                        img_msm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!is_Check){
                                    Toast.makeText(mContext, R.string.custom_toast, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                PopWindowUtil.getInstance().getPopupWindow(activity, null, 0, 0, R.style.showPopupAnimation);
                                mAuthHelper.quitLoginPage();
                            }
                        });

                        //微信登录
                        img_wx.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if(!is_Check){
                                    Toast.makeText(mContext, R.string.custom_toast, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                //mAuthHelper.quitLoginPage();
                            }
                        });

                    }
                })
                .build());


        /*mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
                .setAppPrivacyOne("《自定义隐私协议》", "https://test.h5.app.tbmao.com/user")
                .setAppPrivacyTwo("《百度》", "https://www.baidu.com")
                .setAppPrivacyColor(Color.GRAY, Color.parseColor("#002E00"))
                .setNavHidden(true)
                .setLogoHidden(true)
                .setSloganHidden(true)
                .setSwitchAccHidden(true)
                .setPrivacyState(false)
                .setCheckboxHidden(true)
                .setLightColor(true)
                .setWebViewStatusBarColor(Color.TRANSPARENT)
                .setStatusBarColor(Color.TRANSPARENT)
                .setStatusBarUIFlag(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                .setWebNavTextSizeDp(20)
                .setNumberSizeDp(20)
                .setNumberColor(Color.BLACK)
                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setPageBackgroundPath("page_background_color")
                .setLogoImgPath("mytel_app_launcher")
                .setLogBtnBackgroundPath("login_btn_bg")
                .setScreenOrientation(authPageOrientation)

                .setLogBtnOffsetY(unit * 4)
                .setLogBtnHeight(logBtnHeight)
                .setLogBtnMarginLeftAndRight(30)
                .setLogBtnTextSizeDp(20)

                .create());*/

        //String packageName = AppUtils.getPackageName(mActivity);
        mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
                .setAppPrivacyOne("《用户协议》", "https://www.baidu.com")
                .setAppPrivacyTwo("《隐私政策》", "https://www.youdao.com")
                .setAppPrivacyColor(Color.GRAY, Color.parseColor("#787EE8"))
                .setWebViewStatusBarColor(Color.TRANSPARENT)

                .setLogoHidden(true)
                .setSloganHidden(true)

                .setNavHidden(true)
                //隐藏之前的切换按钮
                .setSwitchAccHidden(true)
                .setPrivacyState(false)
                .setCheckboxHidden(false)

                /*.setLogoImgPath("mytel_app_launcher")
                .setLogoOffsetY(0)
                .setLogoWidth(0)
                .setLogoHeight(0)*/

                .setNumFieldOffsetY(unit)
                .setNumberSizeDp(18)
                .setNumberColor(mContext.getResources().getColor(R.color.white))

                /*.setSloganText("为了您的账号安全，请先绑定手机号")
                .setSloganOffsetY(unit * 3)
                .setSloganTextSizeDp(11)*/

                .setLogBtnOffsetY(unit * 3)
                .setLogBtnHeight(logBtnHeight)
                .setLogBtnMarginLeftAndRight(30)
                .setLogBtnTextSizeDp(16)
                //.setLogBtnBackgroundPath("login_btn_bg")
                .setLogBtnBackgroundPath("button_bg")

                .setPageBackgroundPath("dialog_page_background")
                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setProtocolShakePath("protocol_shake")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setDialogHeight(dialogHeight)
                .setDialogBottom(true)
                .setScreenOrientation(authPageOrientation)
                .create());


    }
}
