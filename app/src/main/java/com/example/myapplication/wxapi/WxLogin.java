package com.example.myapplication.wxapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.example.myapplication.http.Api;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WxLogin {

    public static IWXAPI api;
    public static Context mContext;

    /**
     * 急的初始化
     *
     * @param context
     */
    public static void initWx(Context context) {
        mContext = context;
        api = WXAPIFactory.createWXAPI(context, Api.WX_APP_ID, true);
        api.registerApp(Api.WX_APP_ID);

        //建议动态监听微信启动广播进行注册到微信
        context.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // 将该 app 注册到微信
                api.registerApp(Api.WX_APP_ID);
            }
        }, new IntentFilter(ConstantsAPI.ACTION_REFRESH_WXAPP));
    }

    public static void longWx() {
        if (mContext == null) {
            Toast.makeText(mContext, "你没有初始化,请在Application中做初始化动作", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!api.isWXAppInstalled()) {
            Toast.makeText(mContext, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = Api.SCOPE;
        req.state = Api.STATE;
        api.sendReq(req);
    }

}
