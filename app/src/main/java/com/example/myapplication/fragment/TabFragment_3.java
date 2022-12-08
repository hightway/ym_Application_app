package com.example.myapplication.fragment;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.activity.Sleep_Time_Set_Activity;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.User_Msg_Bean;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.Aliyun_Login_Util;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabFragment_3 extends BaseLazyFragment{

    @BindView(R.id.tx_sleep_time)
    TextView tx_sleep_time;

    @Override
    protected int setLayout() {
        return R.layout.tab_3_fragment_lay;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
    }


    @OnClick(R.id.tx_sleep_time)
    public void tx_sleep_time(){
        if(UserConfig.instance().user_id == 0 && TextUtils.isEmpty(UserConfig.instance().phone)){
            getUser_info();
        }else{
            startActivity(new Intent(getActivity(), Sleep_Time_Set_Activity.class));
        }
    }


    /**
     *  获取用户信息
     * */
    private void getUser_info() {
        DialogUtils.getInstance().showDialog(getActivity(), "加载中...");
        OkHttpUtil.postRequest(Api.HEAD + "user/info", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
                if(!TextUtils.isEmpty(err) && err.contains("401")){
                    //未登录，看详情需要登录
                    Aliyun_Login_Util.getInstance().initSDK(getActivity());
                }
            }

            @Override
            public void un_login_err() {
                //去登录

            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        User_Msg_Bean user_msg_bean = mgson.fromJson(response, User_Msg_Bean.class);
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
                            UserConfig.instance().user_timed_close = extends_bean.timed_close;
                            UserConfig.instance().user_delay = extends_bean.delay;
                        }

                        //保存
                        UserConfig.instance().saveUserConfig(getActivity());

                        startActivity(new Intent(getActivity(), Sleep_Time_Set_Activity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}