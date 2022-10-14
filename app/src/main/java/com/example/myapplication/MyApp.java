package com.example.myapplication;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.example.myapplication.aliyun_oss.AliyunOSSUtils;
import com.example.myapplication.config.Get_AssetsUtil;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
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
    public void close_Activity() {
        for (Activity activity : activityList) {
            if(activity != null){
                activity.finish();
            }
        }
    }



    @Override
    public void onCreate() {
        super.onCreate();
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
        UserConfig.instance().getUserConfig(this);

        //获取app版本号
        try {
            String vString = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            if (!TextUtils.isEmpty(vString)) {
                Api.version_code = vString;
            }
        } catch (PackageManager.NameNotFoundException e) {

        }

        //初始化微信SDK
        WxLogin.initWx(this);
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
