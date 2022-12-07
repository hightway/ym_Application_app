package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.adapter.Mainpage_Adapter;
import com.example.myapplication.aliyun_oss.AliyunOSSUtils;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.Oss_Bean;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.bean.User_Msg_Bean;
import com.example.myapplication.bean.Weather_Bean;
import com.example.myapplication.bean.Weather_Video_Bean;
import com.example.myapplication.config.BaseUIConfig;
import com.example.myapplication.custom.FloatWindow_View;
import com.example.myapplication.custom.MyViewPage;
import com.example.myapplication.custom.SlidingDrawerLayout;
import com.example.myapplication.custom.WrapSlidingDrawer;
import com.example.myapplication.fragment.TabFragment;
import com.example.myapplication.fragment.TabFragment_3;
import com.example.myapplication.fragment.TabFragment_4;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.plmd.BackHandledInterface;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.Login_Util;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.tools.StatusBarUtil;
import com.google.android.material.tabs.TabLayout;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.TokenResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

//implements EasyPermissions.PermissionCallbacks, BackHandledInterface
public class MainActivity extends BaseActivity {

    @BindView(R.id.tl_tabs)
    TabLayout tl_tabs;

    @BindView(R.id.vp_viewpage)
    MyViewPage vp_viewpage;
    @BindView(R.id.view_view)
    View view_view;

    private List<String> tab_name = new ArrayList<>();
    private List<Integer> tab_icon_sel = new ArrayList<>();
    private List<Integer> tab_icon = new ArrayList<>();
    private List<Fragment> fragmentList;
    private Mainpage_Adapter adapter;
    private static MainActivity instance;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String city_code;
    private String city_id;
    public static PhoneNumberAuthHelper mPhoneNumberAuthHelper;
    public static TokenResultListener mTokenResultListener;
    private static final String[] PERMISSION = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };
    private TabFragment tabFragment;
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;


    public static MainActivity getInstance() {
        return instance;
    }


    //FLAG_LAYOUT_FULLSCREEN
    @Override
    protected int getLayoutID() {
        instance = this;
        setBar_color_transparent(R.color.transparent);
        return R.layout.activity_main;
    }

    @Override
    public void viewClick(View v) {

    }


    @Override
    protected void initView() {
        ButterKnife.bind(MainActivity.this);
        //清除余存的登录界面
        //MyApp.getInstance().close_Activity();
        //加入当前页面到Activity集合中
        //MyApp.getInstance().addActivity(this);

        initFragment();

        //获取ossl临时凭证并缓存两个小时
        getOssStsToken();


        //判断是否开启了手机定位服务
        //Location_Util.isLocationProviderEnabled(instance);

        //getJson_data("city_code", instance);
        //getCity_ID("city_id", instance);


        //初始化aliyun SDK
        /*sdkInit(Api.aliyun_key);
        //1
        mUIConfig = BaseUIConfig.init(6, this, mPhoneNumberAuthHelper);
        //2
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(getApplicationContext(), mTokenResultListener);
        mPhoneNumberAuthHelper.checkEnvAvailable();
        mUIConfig.configAuthPage();*/


        // WindowUtils
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.canDrawOverlays(MainActivity.this)) {
                WindowUtils.showPopupWindow(this);
            } else {
                //若没有权限，提示获取.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Toast.makeText(MainActivity.this,"需要取得权限以使用悬浮窗",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }else {
            //SDK在23以下，不用管.
            WindowUtils.showPopupWindow(this);
        }*/

        //获取用户信息（已登录情况）
        getUser_info();

        view_view.setVisibility(View.GONE);
        //计算高度，跟状态栏高度一致
        /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarUtil.getStatusBarHeight(instance));
        view_view.setLayoutParams(params);*/

        //获取定位权限
        askPermission();


        /*View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.float_window, null);
        FloatWindow
                .with(getApplicationContext())
                .setView(view)
                .setWidth(100)                               //设置控件宽高
                .setHeight(Screen.width,0.2f)
                .setX(100)                                   //设置控件初始位置
                .setY(Screen.height,0.3f)
                .setDesktopShow(true)                        //桌面显示
                //.setViewStateListener(mViewStateListener)    //监听悬浮控件状态改变
                //.setPermissionListener(mPermissionListener)  //监听权限申请结果
                .build();*/


        // 悬浮窗权限判断
        /*if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                // 启动Activity让用户授权
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            } else {
                // 执行6.0以上绘制代码
                initView_FloatWindow();
            }
        } else {
            // 执行6.0以下绘制代码
            initView_FloatWindow();
        }*/
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        // 权限判断
        if (Build.VERSION.SDK_INT >= 23) {
            if(Settings.canDrawOverlays(getApplicationContext())) {
                initView_FloatWindow();
            }
        } else {
            //执行6.0以下绘制代码
            initView_FloatWindow();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != floatWindow_view) {
            floatWindow_view.hideFloatWindow();
        }
    }*/


    private void getUser_info() {
        if (TextUtils.isEmpty(UserConfig.instance().access_token)) {
            return;
        }

        OkHttpUtil.postRequest(Api.HEAD + "user/info", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
            }

            @Override
            public void un_login_err() {
                //Login_Util.go_Login(WelcomeActivity.this);
            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        User_Msg_Bean user_msg_bean = mGson.fromJson(response, User_Msg_Bean.class);
                        User_Msg_Bean.DataBean dataBean = user_msg_bean.getData();
                        UserConfig.instance().age = dataBean.getAge();
                        UserConfig.instance().user_id = dataBean.getId();
                        UserConfig.instance().avatar = dataBean.getAvatar();
                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        //保存
                        UserConfig.instance().saveUserConfig(instance);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /*public void sdkInit(String secretInfo) {
        mTokenResultListener = new TokenResultListener() {
            @Override
            public void onTokenSuccess(String s) {
                TokenRet tokenRet = null;
                try {
                    tokenRet = TokenRet.fromJson(s);
                    *//*if (ResultCode.CODE_ERROR_ENV_CHECK_SUCCESS.equals(tokenRet.getCode())) {
                        //获取预取号
                        //accelerateLoginPage(5000);
                    }
                    if (ResultCode.CODE_START_AUTHPAGE_SUCCESS.equals(tokenRet.getCode())) {
                        Log.i("TAG", "唤起授权页成功：" + s);
                    }*//*
                    if (ResultCode.CODE_SUCCESS.equals(tokenRet.getCode())) {
                        Log.i("TAG", "获取token成功：" + s);
                        getResultWithToken(tokenRet.getToken());
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
    }*/


    /**
     * 拉起授权页
     *
     * @param
     */
    /*public void getLoginToken(int timeout) {
        mPhoneNumberAuthHelper.getLoginToken(this, timeout);
        //showLoadingDialog("正在唤起授权页");
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

        //上传服务器
        if(!token.isEmpty()){
            post_data(token);
        }
    }


    private void post_data(String token) {
        DialogUtils.getInstance().showDialog(this, "加载中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", token);

        OkHttpUtil.postRequest(Api.HEAD + "login_token", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
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
                        UserBean userBean = new Gson().fromJson(response, UserBean.class);
                        UserBean.DataBean dataBean = userBean.getData();

                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        UserConfig.instance().access_token = dataBean.getAccess_token();
                        UserConfig.instance().user_id = dataBean.getUser_id();
                        UserConfig.instance().expires_in = dataBean.getExpires_in();
                        UserConfig.instance().token_type = dataBean.getToken_type();
                        //保存
                        UserConfig.instance().saveUserConfig(instance);

                        //刷新数据


                        //跳转主页面
                        //startActivity(new Intent(instance, MainActivity.class));
                        //finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/


    /*private void getCity_ID(String fileName, Context context) {
        city_id_list = new ArrayList<>();
        try {
            //获取assets资源管理器
            AssetManager assetManager = context.getAssets();
            //通过管理器打开文件并读取
            BufferedReader bf = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                if(!TextUtils.isEmpty(line)){
                    city_id_list.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    private void getOssStsToken() {
        //DialogUtils.getInstance().showDialog(this, "初始化中...");
        OkHttpUtil.postRequest(Api.HEAD + "getOssStsToken", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
            }

            @Override
            public void un_login_err() {
                //Login_Util.go_Login(instance);
            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    toast(jsonObject.getString("errMsg"));
                    if (code == 200) {
                        Oss_Bean userBean = mGson.fromJson(response, Oss_Bean.class);
                        Oss_Bean.Oss_Bean_Data dataBean = userBean.getData();

                        UserConfig.instance().AssumedRoleId = dataBean.getAssumedRoleId();
                        UserConfig.instance().Bucket = dataBean.getBucket();
                        UserConfig.instance().OssRegion = dataBean.getOssRegion();

                        UserConfig.instance().AccessKeyId = dataBean.getAccessKeyId();
                        UserConfig.instance().AccessKeySecret = dataBean.getAccessKeySecret();
                        UserConfig.instance().Expiration = dataBean.getExpiration();
                        UserConfig.instance().SecurityToken = dataBean.getSecurityToken();

                        UserConfig.instance().SecurityToken = dataBean.getSecurityToken();

                        //初始化OSS
                        AliyunOSSUtils.getInstance(instance);

                        //保存
                        //UserConfig.instance().saveUserConfig(instance);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initFragment() {
        tab_name.clear();
        tab_icon_sel.clear();
        tab_icon.clear();

        fragmentList = new ArrayList<>();

        tabFragment = new TabFragment();
        tab_name.add(getString(R.string.tab_1));
        tab_icon_sel.add(R.mipmap.ic_home_pressed);
        tab_icon.add(R.mipmap.ic_home_normal);

        /*TabFragment_2 tabFragment_2 = new TabFragment_2();
        tab_name.add(getString(R.string.tab_2));
        tab_icon_sel.add(R.mipmap.ic_home_pressed);
        tab_icon.add(R.mipmap.ic_home_normal);*/

        TabFragment_3 tabFragment_3 = new TabFragment_3();
        tab_name.add(getString(R.string.tab_3));
        tab_icon_sel.add(R.mipmap.ic_home_pressed2);
        tab_icon.add(R.mipmap.ic_home_normal2);

        TabFragment_4 tabFragment_4 = new TabFragment_4();
        tab_name.add(getString(R.string.tab_4));
        tab_icon_sel.add(R.mipmap.ic_home_pressed3);
        tab_icon.add(R.mipmap.ic_home_normal3);

        fragmentList.add(tabFragment);
        //fragmentList.add(tabFragment_2);
        fragmentList.add(tabFragment_3);
        fragmentList.add(tabFragment_4);

        initTab();
    }

    private void initTab() {
        if (fragmentList != null && fragmentList.size() > 0) {
            adapter = new Mainpage_Adapter(getSupportFragmentManager(), fragmentList, instance, tab_name, tab_icon_sel, tab_icon);
            vp_viewpage.setAdapter(adapter);
            vp_viewpage.setNoScroll(false);
            //tl_tabs.setupWithViewPager(vp_viewpage);

            for (int i = 0; i < tab_name.size(); i++) {
                TabLayout.Tab tab = tl_tabs.newTab();
                tab.setCustomView(adapter.getTabView(i));
                tl_tabs.addTab(tab);
            }

            tl_tabs.setTabMode(TabLayout.MODE_FIXED);
            tl_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    View customView = tab.getCustomView();
                    if (customView == null) {
                        tab.setCustomView(R.layout.icon_view);
                    }

                    int index = tab.getPosition();
                    if (index == 0) {
                        view_view.setVisibility(View.GONE);
                    } else {
                        view_view.setVisibility(View.VISIBLE);
                    }
                    TextView textView = tab.getCustomView().findViewById(R.id.tabtext);
                    ImageView tabicon = tab.getCustomView().findViewById(R.id.tabicon);
                    textView.setTextColor(getResources().getColor(R.color.white));
                    //tabicon.setBackgroundResource(tab_icon_sel.get(index));
                    tabicon.setImageResource(tab_icon_sel.get(index));
                    vp_viewpage.setCurrentItem(tab.getPosition(), true);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    View customView = tab.getCustomView();
                    if (customView == null) {
                        tab.setCustomView(R.layout.icon_view);
                    }

                    int index = tab.getPosition();
                    TextView textView = tab.getCustomView().findViewById(R.id.tabtext);
                    ImageView tabicon = tab.getCustomView().findViewById(R.id.tabicon);
                    textView.setTextColor(getResources().getColor(R.color.white));
                    //tabicon.setBackgroundResource(tab_icon.get(index));
                    tabicon.setImageResource(tab_icon.get(index));
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }

                /*@Override
                public void onTabReselected(TabLayout.Tab tab) {
                    //选中状态再次选中
                    switch (tab.getPosition()) {
                        case 1:
                            if (TextUtils.isEmpty(userConfig.token)) {
                                startActivity(new Intent(base_instance, Login_new.class));
                                return;
                            }
                            break;
                    }
                }*/
            });

        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    //双击退出
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (tabFragment != null) {
                SlidingDrawerLayout drawer = tabFragment.getWrapSlidingDrawer();
                if (drawer != null && !drawer.getmIsAllShow()) {
                    drawer.hide();
                } else {
                    exit_sys();
                }
            } else {
                exit_sys();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit_sys() {
        if (!isExit) {
            isExit = true;
            toast("再按一次退出程序");
            // 利用handler延迟发送更改状态信息
            Message msg = new Message();
            msg.what = EXIT_INFO;
            mHandle.sendMessageDelayed(msg, 2000);
        } else {
            MyApp.getInstance().finish_Activity();
            MyApp.getInstance().activityList.clear();
            finish();
            System.exit(0);
        }
    }


    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    // 申请权限
    //private String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    //private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    private void askPermission() {
        //askPermission_more();

        boolean isAllGranted = checkPermissionAllGranted(
                new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
        );

        // 如果这3个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            //有全部的权限，做对应的操作
            initLocation();
        }else{
            // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, MY_PERMISSION_REQUEST_CODE
            );
        }


        /*if (EasyPermissions.hasPermissions(this, locationPermission)) {
            //有全部的权限，做对应的操作
            initLocation();
        } else {
            PermissionRequest request = new PermissionRequest.Builder(this, 1, locationPermission)
                    .setRationale("该App正常使用需要用到的权限")
                    .setNegativeButtonText("不允许")
                    .setPositiveButtonText("允许")
                    //.setTheme(R.style.myPermissionStyle)
                    .build();
            EasyPermissions.requestPermissions(request);
        }


        if (EasyPermissions.hasPermissions(this, permissions)) {
            //有全部的权限，做对应的操作

        } else {
            PermissionRequest request = new PermissionRequest.Builder(this, 2, permissions)
                    .setRationale("该App正常使用需要用到的权限")
                    .setNegativeButtonText("不允许")
                    .setPositiveButtonText("允许")
                    //.setTheme(R.style.myPermissionStyle)
                    .build();
            EasyPermissions.requestPermissions(request);
        }*/
    }


    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                initLocation();
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(instance, R.style.MyAlertButton);
        builder.setMessage("软件需要进行定位并访问“外部存储器”，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果交由 EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //权限申请成功
        if (perms != null && perms.size() > 0) {
            *//*StringBuffer stringBuffer = new StringBuffer();
            for (String item : perms) {
                stringBuffer.append(item).append(",");
            }
            Toast.makeText(this, "该应用已允许权限:" + stringBuffer.toString().trim(), Toast.LENGTH_SHORT).show();*//*

            if (requestCode == 1) {
                initLocation();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //权限申请失败
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }*/

    //初始化
    private void initLocation() {
        // 移除监听
        removeLocationListener();
        //获取地理位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return;
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);
        if (providers.size() <= 0) return;
        String locationProvider;
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            return;
        }


        //获取Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            getAddress(location);
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location == null) return;
                getAddress(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        // 设置监听
        locationManager.requestLocationUpdates(locationProvider, 1000, 0f, locationListener);
    }

    private void removeLocationListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private void getAddress(Location location) {
        mHandle.post(() -> {
            try {
                if (location != null) {
                    Geocoder gc = new Geocoder(this, Locale.getDefault());
                    List<Address> result = gc.getFromLocation(location.getLatitude(),
                            location.getLongitude(), 1);

                    if (result.size() > 0) {
                        Address address = result.get(0);
                        mHandle.post(() -> handleCountryAndArea(address));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    // 处理地址信息 address = {Address@14043}
    // "Address[addressLines=[0:"广东省广州市增城区新塘镇广深大道东161号广侨时代"],
    // feature=新塘镇广深大道东161号广侨时代,address = {Address@14044} "Address[addressLines=[0:"广东省广州市增城区新塘镇广深大道东161号广侨时代"],feature=新塘镇广深大道东161号广侨时代,admin=广东省,sub-admin=新塘镇,locality=广州市,thoroughfare=广深大道东,postalCode=null,countryCode=CN,countryName=中国,hasLatitude=true,latitude=23.123588,hasLongitude=true,longitude=113.619181,phone=null,url=null,extras=Bundle[mParcelledData.dataSize=88]]"
    // admin=广东省,
    // sub-admin=新塘镇,
    // locality=广州市,
    // thoroughfare=广深大道东,
    // postalCode=null,countryCode=CN,countryName=中国,hasLatitude=true,latitude=23.123588,hasLongitude=true,longitude=113.619181,
    // phone=null,url=null,extras=Bundle[mParcelledData.dataSize=88]]"address = {Address@14044} "Address[addressLines=[0:"广东省广州市增城区新塘镇广深大道东161号广侨时代"],feature=新塘镇广深大道东161号广侨时代,admin=广东省,sub-admin=新塘镇,locality=广州市,thoroughfare=广深大道东,postalCode=null,countryCode=CN,countryName=中国,hasLatitude=true,latitude=23.123588,hasLongitude=true,longitude=113.619181,phone=null,url=null,extras=Bundle[mParcelledData.dataSize=88]]"
    public void handleCountryAndArea(Address address) {
        if (address != null) {
            String locality = address.getSubLocality();
            String lin_1 = address.getAddressLine(0);
            Locale locale = address.getLocale();
            String city = address.getLocality();

            //获取六位数的区域码
            if (!TextUtils.isEmpty(locality) && TextUtils.isEmpty(city_code)) {
                getCity_code(locality);
            }

            //获取9位数的城市id
            /*if(!TextUtils.isEmpty(city) && TextUtils.isEmpty(city_id)){
                getCity_id_num("广州");
            }*/

            //获取成功后取消监听
            removeLocationListener();
        }
    }


    private void getCity_code(String locality) {
        if (MyApp.city_code_map != null && !MyApp.city_code_map.isEmpty()) {
            for (String key : MyApp.city_code_map.keySet()) {
                if (key.equals(locality)) {
                    city_code = MyApp.city_code_map.get(key);
                    if(!TextUtils.isEmpty(city_code)){
                        //获取首页视频
                        get_recommend(city_code);
                        //根据区域码获取天气情况
                        getCurrent_weather(city_code);
                    }

                    //停止循环
                    return;
                }
            }
        }
    }


    private void get_recommend(String city_code) {
        DialogUtils.getInstance().showDialog(this, "初始化...");
        HashMap<String, String> map = new HashMap<>();
        map.put("districtCode", city_code);
        OkHttpUtil.postRequest(Api.HEAD + "recommend", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
            }

            @Override
            public void un_login_err() {

            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    toast(jsonObject.getString("errMsg"));
                    if(jsonObject.getInt("errCode") == 200){
                        Weather_Video_Bean weather_video_bean = mGson.fromJson(response, Weather_Video_Bean.class);
                        if(tabFragment != null){
                            tabFragment.setVideo_data(weather_video_bean);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void getCurrent_weather(String city_code) {
        HashMap<String, String> map = new HashMap<>();
        map.put("districtCode", city_code);
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "current_weather", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {

            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        Weather_Bean weather_bean = mGson.fromJson(response, Weather_Bean.class);
                        Weather_Bean.DataBean.ResultBean resultBean = weather_bean.data.result;
                        Weather_Bean.DataBean.ResultBean.NowBean nowBean = resultBean.now;
                        if(tabFragment != null){
                            tabFragment.setWeather_data(resultBean);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void un_login_err() {
                //去登录
                //Login_Util.go_Login(instance);
            }
        });
    }


    /*private BaseLazyFragment mBackHandedFragment;
    @Override
    public void setSelectedFragment(BaseLazyFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }*/


    /*@Override
    public void onBackPressed() {
        if(mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()){
            if(getSupportFragmentManager().getBackStackEntryCount() == 0){
                //super.onBackPressed();
                if (!isExit) {
                    isExit = true;
                    toast("再按一次退出程序");
                    // 利用handler延迟发送更改状态信息
                    Message msg = new Message();
                    msg.what = EXIT_INFO;
                    mHandle.sendMessageDelayed(msg, 2000);
                } else {
                    MyApp.getInstance().finish_Activity();
                    MyApp.getInstance().activityList.clear();
                    finish();
                    System.exit(0);
                }
            }else{
                getSupportFragmentManager().popBackStack();
            }
        }
    }*/

    /*private void getCity_id_num(String locality) {
        if(city_id_list != null && city_id_list.size()>0){
            for (String key : city_id_list) {
                if(key.contains(locality)){
                    String[] arr_id = key.split(":");
                    city_id = arr_id[1];
                    if(!TextUtils.isEmpty(city_id)){
                        //获取城市天气
                        getWeather(city_id);
                    }
                    //停止循环
                    return;
                }
            }
        }
    }

    private void getWeather(String city_id) {
        String url = "http://www.weather.com.cn/data/sk/"+ city_id + ".html";
        //
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(okhttp3.Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            Weather_Bean weather_bean = mGson.fromJson(response, Weather_Bean.class);
                            Weather_Bean.WeatherinfoBean weatherinfo = weather_bean.getWeatherinfo();
                            toast_long("温度是：" + weatherinfo.getTemp() + "-------风况为：" + weatherinfo.getWS() + weatherinfo.getWD());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }*/

}