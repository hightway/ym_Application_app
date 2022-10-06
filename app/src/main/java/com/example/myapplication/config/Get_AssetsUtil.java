package com.example.myapplication.config;

import android.content.Context;
import android.content.res.AssetManager;

import com.example.myapplication.MyApp;
import com.example.myapplication.bean.App_AssetsBean;
import com.example.myapplication.http.Api;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Get_AssetsUtil {

    public static void setCode(String fileName, Context context){
        String code = getJson(fileName, context);
        App_AssetsBean bean = new Gson().fromJson(code, App_AssetsBean.class);
        Api.HEAD = bean.getApp_http();
        Api.WX_APP_ID = bean.getWX_APP_ID();
        Api.WX_SECRET = bean.getWX_SECRET();
        Api.wx_pay = bean.getWX_PAY();

        Api.hua_wei = bean.getHUA_WEI();
        Api.xiaomi_id = bean.getXIAOMI_ID();
        Api.xiaomi_pwd = bean.getXIAOMI_PWD();
    }

    public static String getJson(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public static String getJson_data(String fileName, Context context) {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
