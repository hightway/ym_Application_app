package com.example.myapplication.config;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.activity.Password_Login_Activity;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.tools.VarCodeCountDownTimerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageActivity extends BaseActivity {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.edit_phone)
    EditText edit_phone;
    @BindView(R.id.edit_key)
    EditText edit_key;
    @BindView(R.id.register_key)
    TextView register_key;
    @BindView(R.id.register_commit)
    TextView register_commit;
    private String phone;
    @BindView(R.id.tx_login)
    TextView tx_login;

    private VarCodeCountDownTimerUtil mVarCodeCountDownTimer;
    private String key;
    //private ActivityResultLauncher launcher;
    private Context instance;

    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.activity_message;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        mVarCodeCountDownTimer = new VarCodeCountDownTimerUtil(60000, 1000, register_key);
        MyApp.getInstance().addActivity(this);

        //代替startActivityforResult
        /*launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                int result_code = result.getResultCode();
                if(result_code == -1){
                    finish();
                }
            }
        });*/
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.back)
    public void back(){
        finish();
    }

    @OnClick(R.id.register_key)
    public void register_key(){
        phone = edit_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            toast(getString(R.string.phoneNumber_null));
            return;
        }

        DialogUtils.getInstance().showDialog(this, "加载中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("type", "login");
        OkHttpUtil.postRequest(Api.HEAD + "sendVerificationCode", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
            }

            @Override
            public void ok(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
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


    //登录
    @OnClick(R.id.register_commit)
    public void register_commit(){
        phone = edit_phone.getText().toString().trim();
        key = edit_key.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            toast(getString(R.string.phoneNumber_null));
            return;
        }
        if (TextUtils.isEmpty(key)) {
            toast(getString(R.string.keyCode_null));
            return;
        }

        DialogUtils.getInstance().showDialog(this, "登录中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", key);
        OkHttpUtil.postRequest(Api.HEAD + "login_code", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                        UserBean userBean = mGson.fromJson(response, UserBean.class);
                        UserBean.DataBean dataBean = userBean.getData();

                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        UserConfig.instance().access_token = dataBean.getAccess_token();
                        UserConfig.instance().user_id = dataBean.getUser_id();
                        UserConfig.instance().expires_in = dataBean.getExpires_in();
                        UserConfig.instance().token_type = dataBean.getToken_type();
                        //保存
                        UserConfig.instance().saveUserConfig(MessageActivity.this);

                        //跳转主页面
                        startActivity(new Intent(MessageActivity.this, MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if (mVarCodeCountDownTimer != null) {
            mVarCodeCountDownTimer.cancel();
        }
    }

    @OnClick(R.id.tx_login)
    public void tx_login(){
        startActivity(new Intent(instance, Password_Login_Activity.class));
        //launcher.launch(new Intent(instance, Password_Login_Activity.class));
    }


}
