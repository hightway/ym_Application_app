package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.adapter.Mainpage_Adapter;
import com.example.myapplication.aliyun_oss.AliyunOSSUtils;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.Oss_Bean;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.config.MessageActivity;
import com.example.myapplication.custom.MyViewPage;
import com.example.myapplication.fragment.TabFragment;
import com.example.myapplication.fragment.TabFragment_2;
import com.example.myapplication.fragment.TabFragment_3;
import com.example.myapplication.fragment.TabFragment_4;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.Location_Util;
import com.example.myapplication.tools.OkHttpUtil;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.tl_tabs)
    TabLayout tl_tabs;

    @BindView(R.id.vp_viewpage)
    MyViewPage vp_viewpage;

    private List<String> tab_name = new ArrayList<>();
    private List<Integer> tab_icon_sel = new ArrayList<>();
    private List<Integer> tab_icon = new ArrayList<>();
    private List<Fragment> fragmentList;
    private Mainpage_Adapter adapter;
    private Context instance;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.activity_main;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(MainActivity.this);
        //清楚余存的登录界面
        MyApp.getInstance().close_Activity();

        //加入当前页面到Activity集合中
        MyApp.getInstance().addActivity(this);
        initFragment();

        //获取ossl临时凭证并缓存两个小时
        getOssStsToken();


        //获取定位权限
        askPermission();
        //判断是否开启了手机定位服务
        //Location_Util.isLocationProviderEnabled(instance);
    }


    private void getOssStsToken() {
        DialogUtils.getInstance().showDialog(this, "初始化中...");
        OkHttpUtil.postRequest(Api.HEAD + "getOssStsToken", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
            }

            @Override
            public void ok(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
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

        TabFragment tabFragment = new TabFragment();
        tab_name.add(getString(R.string.tab_1));
        tab_icon_sel.add(R.mipmap.ic_home_pressed);
        tab_icon.add(R.mipmap.ic_home_normal);

        /*TabFragment_2 tabFragment_2 = new TabFragment_2();
        tab_name.add(getString(R.string.tab_2));
        tab_icon_sel.add(R.mipmap.ic_home_pressed);
        tab_icon.add(R.mipmap.ic_home_normal);*/

        TabFragment_3 tabFragment_3 = new TabFragment_3();
        tab_name.add(getString(R.string.tab_3));
        tab_icon_sel.add(R.mipmap.ic_home_pressed);
        tab_icon.add(R.mipmap.ic_home_normal);

        TabFragment_4 tabFragment_4 = new TabFragment_4();
        tab_name.add(getString(R.string.tab_4));
        tab_icon_sel.add(R.mipmap.ic_home_pressed);
        tab_icon.add(R.mipmap.ic_home_normal);

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
                    TextView textView = tab.getCustomView().findViewById(R.id.tabtext);
                    ImageView tabicon = tab.getCustomView().findViewById(R.id.tabicon);
                    textView.setTextColor(getResources().getColor(R.color.red3));
                    tabicon.setBackgroundResource(tab_icon_sel.get(index));
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
                    textView.setTextColor(getResources().getColor(R.color.black));
                    tabicon.setBackgroundResource(tab_icon.get(index));
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
            if (!isExit) {
                isExit = true;
                toast("再按一次退出程序");
                // 利用handler延迟发送更改状态信息
                Message msg = new Message();
                msg.what = EXIT_INFO;
                mHandle.sendMessageDelayed(msg, 2000);
            } else {
                MyApp.getInstance().close_Activity();
                finish();
                System.exit(0);
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }


    // 申请权限
    private String[] locationPermission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    private void askPermission() {
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Android 6.0申请权限
            ActivityCompat.requestPermissions(this, locationPermission, 1);
        } else {

        }*/

        if (EasyPermissions.hasPermissions(this, locationPermission)) {
            //有全部的权限，做对应的操作
            initLocation();
        } else {
            PermissionRequest request = new PermissionRequest.Builder(this, 1, locationPermission)
                    .setRationale("该App正常使用需要用到的权限")
                    .setNegativeButtonText("不可以哟")
                    .setPositiveButtonText("允许")
                    //.setTheme(R.style.myPermissionStyle)
                    .build();
            EasyPermissions.requestPermissions(request);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果交由 EasyPermissions处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //权限申请成功
        if (perms != null && perms.size() > 0) {
            StringBuffer stringBuffer = new StringBuffer();
            for (String item : perms) {
                stringBuffer.append(item).append(",");
            }
            //Toast.makeText(this, "该应用已允许权限:" + stringBuffer.toString().trim(), Toast.LENGTH_SHORT).show();
            initLocation();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //权限申请失败
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    //初始化
    @SuppressLint("MissingPermission")
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
        if(locationManager != null){
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
    // feature=新塘镇广深大道东161号广侨时代,
    // admin=广东省,
    // sub-admin=新塘镇,
    // locality=广州市,
    // thoroughfare=广深大道东,
    // postalCode=null,countryCode=CN,countryName=中国,hasLatitude=true,latitude=23.123588,hasLongitude=true,longitude=113.619181,
    // phone=null,url=null,extras=Bundle[mParcelledData.dataSize=88]]"
    public void handleCountryAndArea(Address address) {
        if(address != null){
            String addressLine = address.getCountryCode();
            String phone = address.getPhone();
            Locale locat = address.getLocale();
            String name = address.getCountryName();
            String lin_1 = address.getAddressLine(0);

            //获取成功后取消监听
            removeLocationListener();
        }
    }



}