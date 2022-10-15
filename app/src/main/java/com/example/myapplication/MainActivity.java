package com.example.myapplication;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
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
import com.example.myapplication.tools.OkHttpUtil;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

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
                    if(code == 200){
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
                    vp_viewpage.setCurrentItem(tab.getPosition(),true);
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

}