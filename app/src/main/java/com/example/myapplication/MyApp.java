package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.example.myapplication.aliyun_oss.AliyunOSSUtils;
import com.example.myapplication.config.Get_AssetsUtil;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.videoplayTool.AppUtil;
import com.example.myapplication.wxapi.WxLogin;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class MyApp extends Application {

    public List<Activity> activityList = new LinkedList();
    public static MyApp instance;
    public static AliPlayer app_mAliPlayer;
    public static Context Aapp_context;


    public MyApp() {}
    //单例模式中获取唯一的MyApplication实例
    public static MyApp getInstance() {
        if (null == instance) {
            instance = new MyApp();
        }
        return instance;
    }

    //添加Activity到容器中
    public void addActivity(Activity activity) {
        activityList.add(activity);
    }

    //遍历所有Activity并finish
    public void finish_Activity() {
        for (Activity activity : activityList) {
            if(activity != null){
                activity.finish();
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Aapp_context = getApplicationContext();
        disableAPIDialog();

        //获取APP参数
        Get_AssetsUtil.setCode("dynamic", this);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);

        //获取用户个人信息
        UserConfig.instance().getUserConfig(Aapp_context);

        AppUtil.setApplicationContext(Aapp_context);

        //获取app版本号
        try {
            String vString = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (!TextUtils.isEmpty(vString)) {
                Api.version_code = vString;
            }
        } catch (PackageManager.NameNotFoundException e) {

        }

        //初始化微信SDK
        WxLogin.initWx(Aapp_context);

        //初始化阿里云播放器播放MP3
        get_app_mAliPlayer();
    }


    //初始化阿里云播放器播放MP3
    public static AliPlayer get_app_mAliPlayer() {
        if(app_mAliPlayer == null){
            app_mAliPlayer = AliPlayerFactory.createAliPlayer(Aapp_context);
            PlayerConfig config = app_mAliPlayer.getConfig();
            config.mDisableVideo = true;  //设置开启纯音频播放
            app_mAliPlayer.setConfig(config);
        }

        return app_mAliPlayer;
    }


    /**
     * 反射 禁止系统弹窗提示
     */
    @SuppressLint("SoonBlockedPrivateApi")
    private void disableAPIDialog(){
        try {
            Class aClass = Class.forName("android.content.pm.PackageParser$Package");
            Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
            declaredConstructor.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
            declaredMethod.setAccessible(true);
            Object activityThread = declaredMethod.invoke(null);
            Field mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
