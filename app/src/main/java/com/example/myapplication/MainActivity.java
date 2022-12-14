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
        //???????????????????????????
        //MyApp.getInstance().close_Activity();
        //?????????????????????Activity?????????
        //MyApp.getInstance().addActivity(this);

        initFragment();

        //??????ossl?????????????????????????????????
        getOssStsToken();


        //???????????????????????????????????????
        //Location_Util.isLocationProviderEnabled(instance);

        //getJson_data("city_code", instance);
        //getCity_ID("city_id", instance);


        //?????????aliyun SDK
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
                //??????????????????????????????.
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Toast.makeText(MainActivity.this,"????????????????????????????????????",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }
        }else {
            //SDK???23??????????????????.
            WindowUtils.showPopupWindow(this);
        }*/

        //???????????????????????????????????????
        getUser_info();

        view_view.setVisibility(View.GONE);
        //???????????????????????????????????????
        /*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, StatusBarUtil.getStatusBarHeight(instance));
        view_view.setLayoutParams(params);*/

        //??????????????????
        askPermission();


        /*View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.float_window, null);
        FloatWindow
                .with(getApplicationContext())
                .setView(view)
                .setWidth(100)                               //??????????????????
                .setHeight(Screen.width,0.2f)
                .setX(100)                                   //????????????????????????
                .setY(Screen.height,0.3f)
                .setDesktopShow(true)                        //????????????
                //.setViewStateListener(mViewStateListener)    //??????????????????????????????
                //.setPermissionListener(mPermissionListener)  //????????????????????????
                .build();*/


        // ?????????????????????
        /*if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                // ??????Activity???????????????
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 10);
            } else {
                // ??????6.0??????????????????
                initView_FloatWindow();
            }
        } else {
            // ??????6.0??????????????????
            initView_FloatWindow();
        }*/
    }

    /*@Override
    protected void onResume() {
        super.onResume();
        // ????????????
        if (Build.VERSION.SDK_INT >= 23) {
            if(Settings.canDrawOverlays(getApplicationContext())) {
                initView_FloatWindow();
            }
        } else {
            //??????6.0??????????????????
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
                new Throwable("????????????");
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

                        User_Msg_Bean.DataBean.Extends_Bean extends_bean = dataBean.getUser_extends();
                        if(extends_bean != null){
                            UserConfig.instance().user_awaken_time = extends_bean.awaken_time;
                            UserConfig.instance().user_bedtime = extends_bean.bedtime;
                            UserConfig.instance().user_sleep_monitoring = extends_bean.sleep_monitoring;
                            UserConfig.instance().user_painless_arousal = extends_bean.painless_arousal;
                            if(!TextUtils.isEmpty(extends_bean.timed_close)){
                                UserConfig.instance().user_timed_close = extends_bean.timed_close;
                            }
                            if(!TextUtils.isEmpty(extends_bean.delay)){
                                UserConfig.instance().user_delay = extends_bean.delay;
                            }
                        }
                        //??????
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
                        //???????????????
                        //accelerateLoginPage(5000);
                    }
                    if (ResultCode.CODE_START_AUTHPAGE_SUCCESS.equals(tokenRet.getCode())) {
                        Log.i("TAG", "????????????????????????" + s);
                    }*//*
                    if (ResultCode.CODE_SUCCESS.equals(tokenRet.getCode())) {
                        Log.i("TAG", "??????token?????????" + s);
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
                        //????????????

                    } else {
                        //????????????????????????
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
     * ???????????????
     *
     * @param
     */
    /*public void getLoginToken(int timeout) {
        mPhoneNumberAuthHelper.getLoginToken(this, timeout);
        //showLoadingDialog("?????????????????????");
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

        //???????????????
        if(!token.isEmpty()){
            post_data(token);
        }
    }


    private void post_data(String token) {
        DialogUtils.getInstance().showDialog(this, "?????????...");
        HashMap<String, String> map = new HashMap<>();
        map.put("access_token", token);

        OkHttpUtil.postRequest(Api.HEAD + "login_token", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("????????????");
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
                        //??????
                        UserConfig.instance().saveUserConfig(instance);

                        //????????????


                        //???????????????
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
            //??????assets???????????????
            AssetManager assetManager = context.getAssets();
            //????????????????????????????????????
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
        //DialogUtils.getInstance().showDialog(this, "????????????...");
        OkHttpUtil.postRequest(Api.HEAD + "getOssStsToken", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("????????????");
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

                        //?????????OSS
                        AliyunOSSUtils.getInstance(instance);

                        //??????
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
                    //????????????????????????
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


    //????????????
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
            toast("????????????????????????");
            // ??????handler??????????????????????????????
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
     * ???????????????????????????????????????
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // ????????????????????????????????????, ??????????????? false
                return false;
            }
        }
        return true;
    }

    // ????????????
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

        // ?????????3?????????????????????, ???????????????????????????
        if (isAllGranted) {
            //???????????????????????????????????????
            initLocation();
        }else{
            // ????????????????????????, ????????????????????????????????????????????????????????????
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
            //???????????????????????????????????????
            initLocation();
        } else {
            PermissionRequest request = new PermissionRequest.Builder(this, 1, locationPermission)
                    .setRationale("???App?????????????????????????????????")
                    .setNegativeButtonText("?????????")
                    .setPositiveButtonText("??????")
                    //.setTheme(R.style.myPermissionStyle)
                    .build();
            EasyPermissions.requestPermissions(request);
        }


        if (EasyPermissions.hasPermissions(this, permissions)) {
            //???????????????????????????????????????

        } else {
            PermissionRequest request = new PermissionRequest.Builder(this, 2, permissions)
                    .setRationale("???App?????????????????????????????????")
                    .setNegativeButtonText("?????????")
                    .setPositiveButtonText("??????")
                    //.setTheme(R.style.myPermissionStyle)
                    .build();
            EasyPermissions.requestPermissions(request);
        }*/
    }


    /**
     * ??? 3 ???: ??????????????????????????????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // ?????????????????????????????????????????????
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // ?????????????????????????????????, ?????????????????????
                initLocation();
            } else {
                // ????????????????????????????????????????????????, ???????????????????????????????????????????????????????????????
                openAppDetails();
            }
        }
    }

    /**
     * ?????? APP ???????????????
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(instance, R.style.MyAlertButton);
        builder.setMessage("??????????????????????????????????????????????????????????????? ??????????????? -> ????????? ????????????");
        builder.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton("??????", null);
        builder.show();
    }


    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //????????????????????? EasyPermissions??????
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //??????????????????
        if (perms != null && perms.size() > 0) {
            *//*StringBuffer stringBuffer = new StringBuffer();
            for (String item : perms) {
                stringBuffer.append(item).append(",");
            }
            Toast.makeText(this, "????????????????????????:" + stringBuffer.toString().trim(), Toast.LENGTH_SHORT).show();*//*

            if (requestCode == 1) {
                initLocation();
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //??????????????????
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }*/

    //?????????
    private void initLocation() {
        // ????????????
        removeLocationListener();
        //???????????????????????????
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return;
        //????????????????????????????????????
        List<String> providers = locationManager.getProviders(true);
        if (providers.size() <= 0) return;
        String locationProvider;
        if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //?????????Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //?????????GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        } else {
            return;
        }


        //??????Location
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
        // ????????????
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


    // ?????????????????? address = {Address@14043}
    // "Address[addressLines=[0:"???????????????????????????????????????????????????161???????????????"],
    // feature=????????????????????????161???????????????,address = {Address@14044} "Address[addressLines=[0:"???????????????????????????????????????????????????161???????????????"],feature=????????????????????????161???????????????,admin=?????????,sub-admin=?????????,locality=?????????,thoroughfare=???????????????,postalCode=null,countryCode=CN,countryName=??????,hasLatitude=true,latitude=23.123588,hasLongitude=true,longitude=113.619181,phone=null,url=null,extras=Bundle[mParcelledData.dataSize=88]]"
    // admin=?????????,
    // sub-admin=?????????,
    // locality=?????????,
    // thoroughfare=???????????????,
    // postalCode=null,countryCode=CN,countryName=??????,hasLatitude=true,latitude=23.123588,hasLongitude=true,longitude=113.619181,
    // phone=null,url=null,extras=Bundle[mParcelledData.dataSize=88]]"address = {Address@14044} "Address[addressLines=[0:"???????????????????????????????????????????????????161???????????????"],feature=????????????????????????161???????????????,admin=?????????,sub-admin=?????????,locality=?????????,thoroughfare=???????????????,postalCode=null,countryCode=CN,countryName=??????,hasLatitude=true,latitude=23.123588,hasLongitude=true,longitude=113.619181,phone=null,url=null,extras=Bundle[mParcelledData.dataSize=88]]"
    public void handleCountryAndArea(Address address) {
        if (address != null) {
            String locality = address.getSubLocality();
            String lin_1 = address.getAddressLine(0);
            Locale locale = address.getLocale();
            String city = address.getLocality();

            //???????????????????????????
            if (!TextUtils.isEmpty(locality) && TextUtils.isEmpty(city_code)) {
                getCity_code(locality);
            }

            //??????9???????????????id
            /*if(!TextUtils.isEmpty(city) && TextUtils.isEmpty(city_id)){
                getCity_id_num("??????");
            }*/

            //???????????????????????????
            removeLocationListener();
        }
    }


    private void getCity_code(String locality) {
        if (MyApp.city_code_map != null && !MyApp.city_code_map.isEmpty()) {
            for (String key : MyApp.city_code_map.keySet()) {
                if (key.equals(locality)) {
                    city_code = MyApp.city_code_map.get(key);
                    if(!TextUtils.isEmpty(city_code)){
                        //??????????????????
                        get_recommend(city_code);
                        //?????????????????????????????????
                        getCurrent_weather(city_code);
                    }

                    //????????????
                    return;
                }
            }
        }
    }


    private void get_recommend(String city_code) {
        DialogUtils.getInstance().showDialog(this, "?????????...");
        HashMap<String, String> map = new HashMap<>();
        map.put("districtCode", city_code);
        OkHttpUtil.postRequest(Api.HEAD + "recommend", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("????????????");
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
                //?????????
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
                    toast("????????????????????????");
                    // ??????handler??????????????????????????????
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
                        //??????????????????
                        getWeather(city_id);
                    }
                    //????????????
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
                            toast_long("????????????" + weatherinfo.getTemp() + "-------????????????" + weatherinfo.getWS() + weatherinfo.getWD());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }*/

}