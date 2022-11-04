package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.example.myapplication.MainActivity;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.config.MessageActivity;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.IcallUtils;
import com.example.myapplication.tools.Login_Util;
import com.example.myapplication.tools.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Password_Login_Activity extends BaseActivity {

    private Context instance;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.edit_phone)
    EditText edit_phone;
    @BindView(R.id.edit_key)
    EditText edit_key;
    @BindView(R.id.register_commit)
    TextView register_commit;
    @BindView(R.id.tx_register)
    TextView tx_register;
    private ActivityResultLauncher launcher;

    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.password_login_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        MyApp.getInstance().addActivity(this);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent = result.getData();
                int result_code = result.getResultCode();
                if(result_code == -1){
                    String phone = intent.getStringExtra("phone");
                    String password = intent.getStringExtra("password");
                    edit_phone.setText(phone);
                    edit_key.setText(password);
                }
            }
        });
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

    @OnClick(R.id.tx_register)
    public void tx_register(){
        launcher.launch(new Intent(instance, Password_Register_Activity.class));

    }

    @OnClick(R.id.register_commit)
    public void register_commit(){
        String phone = edit_phone.getText().toString().trim();
        String password = edit_key.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            toast(getString(R.string.phoneNumber_null));
            return;
        }
        if (password.length() < 8 || password.length() > 20 || !IcallUtils.isPwd(password)) {
            toast(getString(R.string.code_length));
            return;
        }

        hideSoftWorldInput(edit_key, true);
        DialogUtils.getInstance().showDialog(this, "登录中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("password", password);
        OkHttpUtil.postRequest(Api.HEAD + "login_pwd", map, new OkHttpUtil.OnRequestNetWorkListener() {
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

                        //跳转主页面
                        startActivity(new Intent(instance, MainActivity.class));
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
