package com.example.myapplication.tools;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telecom.Call;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.nirvana.tools.logger.uaid.HttpUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OkHttpUtil {

    public static final String TYPE_TXT = "txt";
    public static final String TYPE_XLS = "xls";
    public static final String TYPE_DOC = "doc";
    public String sError = "网络连接失败";

    /*public static void getRequest(String url, final OnRequestNetWorkListener mListener) {
        OkHttpUtils
                .get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {
                        mListener.notOk(e.getMessage());
                        DialogUtils.getInstance().dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        mListener.ok(response);
                        DialogUtils.getInstance().dismiss();
                    }
                });
    }*/


    public static void postRequest(String url, HashMap<String, String> params, final OnRequestNetWorkListener listener) {
        PostFormBuilder builder = OkHttpUtils.post().url(url);

        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            builder.addParams(key, value);
        }

        builder.addHeader("version", Api.version_code);
        builder.addHeader("platform", "Android");
        builder.addHeader("Authorization", "Bearer" + " " + UserConfig.instance().access_token);
        builder.build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                listener.notOk(e.getMessage());
                DialogUtils.getInstance().dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("errCode");
                    if (code == 422) {
                        //未登录
                        listener.un_login_err();
                    } else {
                        listener.ok(response, jsonObject);
                    }
                    DialogUtils.getInstance().dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void postRequest(String url, final OnRequestNetWorkListener listener) {
        PostFormBuilder builder = OkHttpUtils.post().url(url);

        builder.addHeader("version", Api.version_code);
        builder.addHeader("platform", "Android");
        builder.addHeader("Authorization", "Bearer" + " " + UserConfig.instance().access_token);
        builder.build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                listener.notOk(e.getMessage());
                DialogUtils.getInstance().dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("errCode");
                    if (code == 422) {
                        //未登录
                        listener.un_login_err();
                    } else {
                        listener.ok(response, jsonObject);
                    }
                    DialogUtils.getInstance().dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*
     * 传输文件用这个
     * */
    public static void postRequestFile(String url, ArrayList<File> arrayListFile, String fileParameter, HashMap<String, String> params, final OnRequestNetWorkListener listener) {
        PostFormBuilder builder = OkHttpUtils.post().url(url);

        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            builder.addParams(key, value);
        }

        builder.addHeader("version", Api.version_code);
        builder.addHeader("platform", "Android");
        builder.addHeader("Authorization", "Bearer" + " " + UserConfig.instance().access_token);

        if (arrayListFile.size() == 0) { //如果是空新建空文件
            //新建一个File类型的成员变量，传入文件名路径。
            File file = new File("/mnt/sdcard/wlgj.txt");
            //判断文件是否存在，存在就删除
            if (file.exists()) {
                file.delete();
            }
            try {
                //创建文件
                file.createNewFile();
                //给一个吐司提示，显示创建成功
            } catch (IOException e) {
                e.printStackTrace();
            }
            builder.addFile(fileParameter, file.getName(), file); //调用 addFile 方法上传文件
        } else {
            for (int i = 0; i < arrayListFile.size(); i++) { //遍历存储文件的集合
                File file = arrayListFile.get(i);
                builder.addFile(fileParameter, file.getName(), file); //调用 addFile 方法上传文件
            }
        }

        builder.build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                listener.notOk(e.getMessage());
                DialogUtils.getInstance().dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("errCode");
                    if (code == 422) {
                        //未登录
                        listener.un_login_err();
                    } else {
                        listener.ok(response, jsonObject);
                    }
                    DialogUtils.getInstance().dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /*
     * 一个页面有两个 DialogUtils.getInstance().dismiss(); 会冲突
     * 物资供应看板三级界面
     * */
    public static void postRequestNoDialog(String url, HashMap<String, String> params, final OnRequestNetWorkListener listener) {

        PostFormBuilder builder = OkHttpUtils.post().url(url);

        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            builder.addParams(key, value);
        }

        builder.addHeader("version", Api.version_code);
        builder.addHeader("platform", "Android");
        builder.addHeader("Authorization", "Bearer" + " " + UserConfig.instance().access_token);

        builder.build().execute(new StringCallback() {
            @Override
            public void onError(okhttp3.Call call, Exception e, int id) {
                listener.notOk(e.getMessage());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    int code = jsonObject.getInt("errCode");
                    if (code == 422) {
                        //未登录
                        listener.un_login_err();
                    } else {
                        listener.ok(response, jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 下载文件
     */
    /*public void downloadFile(final List<String> pathList, List<String> title, final int postion, final Context context) {
        // 为RequestParams设置文件下载后的保存路径
        String url = pathList.get(postion);
        System.out.println("文件下载:" + url);

        final String savePath = FilePathUtils.init(context).getFileSaveDir().getAbsolutePath() + url.substring(url.lastIndexOf("/"), url.length());
        System.out.println("savePath:" + savePath);
        HttpUtils utils = new HttpUtils();
        utils.download(url, savePath, true, false, new RequestCallBack<File>() {
            @Override
            public void onSuccess(ResponseInfo<File> responseInfo) {
                Log.d("=====", "=====a下载成功======");
                if (pathList.get(postion).contains(TYPE_TXT)) {
                    Intent intent = new Intent();
                    File file = new File(savePath);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), ".txt");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "您没有安装打开此文件的软件，请安装后再次打开", Toast.LENGTH_SHORT).show();
                    }
                } else if (pathList.get(postion).contains(TYPE_XLS)) {
                    Intent intent = new Intent();
                    File file = new File(savePath);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), ".xls");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "您没有安装打开此文件的软件，请安装后再次打开", Toast.LENGTH_SHORT).show();
                    }
                } else if (pathList.get(postion).contains(TYPE_DOC)) {
                    Intent intent = new Intent();
                    File file = new File(savePath);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), ".doc");
                    try {
                        context.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(context, "您没有安装打开此文件的软件，请安装后再次打开", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ToastUtils.toast(context, "不是以上的类型");
                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.d("=====", "=====下载失败======" + s + e.getMessage());
                DialogUtils.getInstance().dismiss();
            }

            @Override
            public void onLoading(long total, long current, boolean isUploading) {
                super.onLoading(total, current, isUploading);
                Log.d("====下载中==", total + "," + current);
            }

            @Override
            public void onStart() {
                super.onStart();
                Log.d("=====", "=====apk开始下载======");

            }
        });
    }*/


    OnRequestNetWorkListener listener;

    public void setOnRequestNetWorkListener(OnRequestNetWorkListener listener) {
        this.listener = listener;
    }

    public interface OnRequestNetWorkListener {
        void notOk(String err);
        void ok(String response, JSONObject jsonObject);
        void un_login_err();
    }

}
