package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.config.MessageActivity;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.IcallUtils;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.tools.VarCodeCountDownTimerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Password_Register_Activity extends BaseActivity {

    private Context instance;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.edit_phone)
    EditText edit_phone;
    @BindView(R.id.edit_key)
    EditText edit_key;
    @BindView(R.id.edit_passworld)
    EditText edit_passworld;
    @BindView(R.id.edit_repassworld)
    EditText edit_repassworld;
    @BindView(R.id.register_key)
    TextView register_key;
    @BindView(R.id.register_commit)
    TextView register_commit;

    private VarCodeCountDownTimerUtil mVarCodeCountDownTimer;

    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.password_register_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        mVarCodeCountDownTimer = new VarCodeCountDownTimerUtil(60000, 1000, register_key);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    //获取验证码
    @OnClick(R.id.register_key)
    public void setRegister_key(){
        String phone = edit_phone.getText().toString();
        if(TextUtils.isEmpty(phone)){
            toast(getString(R.string.phoneNumber_null));
            return;
        }

        DialogUtils.getInstance().showDialog(this, "加载中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("type", "register");
        OkHttpUtil.postRequest(Api.HEAD + "sendVerificationCode", map, new OkHttpUtil.OnRequestNetWorkListener() {
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

                    register_key.setClickable(false);
                    register_key.setSelected(true);
                    // 设置验证码倒计时
                    mVarCodeCountDownTimer.start();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @OnClick(R.id.back)
    public void back(){
        finish();
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mVarCodeCountDownTimer != null) {
            mVarCodeCountDownTimer.cancel();
        }
    }

    //注册
    @OnClick(R.id.register_commit)
    public void register_commit(){
        String phone = edit_phone.getText().toString().trim();
        String key = edit_key.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            toast(getString(R.string.phoneNumber_null));
            return;
        }
        if (TextUtils.isEmpty(key)) {
            toast(getString(R.string.keyCode_null));
            return;
        }

        String password = edit_passworld.getText().toString();
        String repass = edit_repassworld.getText().toString();
        if (password.length() < 8 || password.length() > 15 || !IcallUtils.isPwd(password)) {
            toast(getString(R.string.code_length));
            return;
        }
        if (!repass.equals(password)) {
            toast(getString(R.string.code_same));
            return;
        }

        hideSoftWorldInput(edit_passworld, true);
        DialogUtils.getInstance().showDialog(this, "注册中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", key);
        map.put("password", password);
        map.put("password_confirmation", repass);
        OkHttpUtil.postRequest(Api.HEAD + "register", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                        //注册成功，返回账号密码至登录页面
                        UserBean userBean = mGson.fromJson(response, UserBean.class);
                        UserBean.DataBean dataBean = userBean.getData();

                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        UserConfig.instance().access_token = dataBean.getAccess_token();
                        UserConfig.instance().user_id = dataBean.getUser_id();
                        UserConfig.instance().expires_in = dataBean.getExpires_in();
                        UserConfig.instance().token_type = dataBean.getToken_type();
                        //保存
                        UserConfig.instance().saveUserConfig(instance);

                        //跳转登录页面
                        Intent intent = new Intent();
                        intent.putExtra("phone", phone);
                        intent.putExtra("password", password);
                        setResult(-1, intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
