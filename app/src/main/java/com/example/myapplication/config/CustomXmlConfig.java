package com.example.myapplication.config;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.plmd.Pop_Show_Interface;
import com.example.myapplication.plmd.Pop_Show_Set;
import com.example.myapplication.tools.PopWindowUtil;
import com.example.myapplication.tools.WindowUtils;
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;
import com.nirvana.tools.core.AppUtils;

/**
 * xml文件方便预览
 * 可以通过addAuthRegisterXmlConfig一次性统一添加授权页的所有自定义view
 */
public class CustomXmlConfig extends BaseUIConfig {

    public Activity activity;

    public CustomXmlConfig(Activity act, PhoneNumberAuthHelper authHelper) {
        super(act, authHelper);
        this.activity = act;
    }
    @Override
    public void configAuthPage() {
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

                        TextView tv_switch = (TextView) findViewById(R.id.tv_switch);
                        tv_switch.setVisibility(View.VISIBLE);
                        findViewById(R.id.tv_switch).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Toast.makeText(mContext, "切换到短信登录方式", Toast.LENGTH_SHORT).show();
                                /*Intent pIntent = new Intent(mActivity, MessageActivity.class);
                                mActivity.startActivityForResult(pIntent, 1002);*/

                                //PopupWindow popupWindow = PopWindowUtil.getInstance().getPopupWindow(activity, null, 0, 0, R.style.showPopupAnimation);
                                //WindowUtils.showPopupWindow(activity);

                                Pop_Show_Set.setPop_show();

                                mAuthHelper.quitLoginPage();
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
                .setAppPrivacyOne("《百度》", "https://www.baidu.com")
                .setAppPrivacyTwo("《有道》", "https://www.youdao.com")
                .setAppPrivacyColor(Color.GRAY, Color.parseColor("#ff026ED2"))
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

                /*.setSloganText("为了您的账号安全，请先绑定手机号")
                .setSloganOffsetY(unit * 3)
                .setSloganTextSizeDp(11)*/

                .setLogBtnOffsetY(unit * 3)
                .setLogBtnHeight(logBtnHeight)
                .setLogBtnMarginLeftAndRight(30)
                .setLogBtnTextSizeDp(16)
                .setLogBtnBackgroundPath("login_btn_bg")

                .setPageBackgroundPath("dialog_page_background")
                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setDialogHeight(dialogHeight)
                .setDialogBottom(true)
                .setScreenOrientation(authPageOrientation)
                .create());


    }
}
