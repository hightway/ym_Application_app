package com.example.myapplication.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.R;
import com.mobile.auth.gatewayauth.AuthRegisterViewConfig;
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.CustomInterface;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;
import com.nirvana.tools.core.AppUtils;

public class DialogPortConfig extends BaseUIConfig {
    /**
     * εΊη¨εε
     */
    private String mPackageName;

    public DialogPortConfig(Activity activity, PhoneNumberAuthHelper authHelper) {
        super(activity, authHelper);
        mPackageName = AppUtils.getPackageName(activity);
    }

    @Override
    public void configAuthPage() {
        mAuthHelper.removeAuthRegisterXmlConfig();
        mAuthHelper.removeAuthRegisterViewConfig();
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
        if (Build.VERSION.SDK_INT == 26) {
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        updateScreenSize(authPageOrientation);
        int dialogWidth = (int) (mScreenWidthDp * 0.8f);
        int dialogHeight = (int) (mScreenHeightDp * 0.65f) - 50;
        int unit = dialogHeight / 10;
        int logBtnHeight = (int) (unit * 1.2);

        mAuthHelper.addAuthRegistViewConfig("switch_msg", new AuthRegisterViewConfig.Builder()
                .setView(initSwitchView(unit * 6))
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
                .setCustomInterface(new CustomInterface() {
                    @Override
                    public void onClick(Context context) {
                        Toast.makeText(mContext, "εζ’ε°η­δΏ‘η»ε½ζΉεΌ", Toast.LENGTH_SHORT).show();
                        Intent pIntent = new Intent(mActivity, MessageActivity.class);
                        mActivity.startActivityForResult(pIntent, 1002);
                        mAuthHelper.quitLoginPage();
                    }
                })
                .build());

        mAuthHelper.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder()
                .setLayout(R.layout.custom_port_dialog_action_bar, new AbstractPnsViewDelegate() {
                    @Override
                    public void onViewCreated(View view) {
                        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAuthHelper.quitLoginPage();
                            }
                        });
                    }
                })
                .build());

        mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
                .setAppPrivacyOne("γθͺε?δΉιη§εθ??γ", "https://www.baidu.com")
                .setAppPrivacyTwo("γθͺε?δΉιη§εθ??γ2","https://baijiahao.baidu.com/s?id=1693920988135022454&wfr=spider&for=pc")
                .setAppPrivacyThree("γθͺε?δΉιη§εθ??γ3","http://www.npc.gov.cn/zgrdw/npc/cwhhy/13jcwh/node_35014.htm")
                .setAppPrivacyColor(Color.GRAY, Color.parseColor("#002E00"))
                .setPrivacyConectTexts(new String[]{",","","ε"})
                .setPrivacyOperatorIndex(2)
                .setPrivacyState(false)
                .setCheckboxHidden(true)
                .setNavHidden(true)
                .setSwitchAccHidden(true)
                .setNavReturnHidden(true)
                .setDialogBottom(false)
                //θͺε?δΉεθ??ι‘΅θ·³θ½¬εθ??οΌιθ¦ε¨ζΈεζδ»Άιη½?θͺε?δΉintent-filterοΌδΈιθ¦θͺε?δΉεθ??ι‘΅οΌεθ―·δΈθ¦ιη½?ProtocolAction
                .setProtocolAction("com.aliqin.mytel.protocolWeb")
                .setPackageName(mPackageName)
                .setNavColor(Color.TRANSPARENT)
                .setWebNavColor(Color.BLUE)

                .setLogoOffsetY(0)
                .setLogoWidth(42)
                .setLogoHeight(42)
                .setLogoImgPath("mytel_app_launcher")

                .setNumFieldOffsetY(unit + 10)
                //θ?Ύη½?ε­δ½ε€§ε°οΌδ»₯DpδΈΊεδ½οΌδΈεδΊSpοΌδΈδΌιηη³»η»ε­δ½εεθεε
                .setNumberSizeDp(17)

                .setLogBtnWidth(dialogWidth - 30)
                .setLogBtnMarginLeftAndRight(15)
                .setLogBtnHeight(logBtnHeight)
                .setLogBtnTextSizeDp(16)
                .setLogBtnBackgroundPath("login_btn_bg")

                .setLogBtnOffsetY(unit * 4)
                .setSloganText("δΈΊδΊζ¨ηθ΄¦ε·ε?ε¨οΌθ―·εη»ε?ζζΊε·")
                .setSloganOffsetY(unit * 3)
                .setSloganTextSizeDp(11)

                .setPageBackgroundPath("dialog_page_background")

                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("γ")
                .setVendorPrivacySuffix("γ")
                .setDialogWidth(dialogWidth)
                .setDialogHeight(dialogHeight)
                .setScreenOrientation(authPageOrientation)
                .create());
    }
}
