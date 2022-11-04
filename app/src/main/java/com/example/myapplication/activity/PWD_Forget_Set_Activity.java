package com.example.myapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.custom.KeyboardNormal;
import com.example.myapplication.http.Api;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.IcallUtils;
import com.example.myapplication.tools.NumberUtil;
import com.example.myapplication.tools.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PWD_Forget_Set_Activity extends BaseActivity {

    private PWD_Forget_Set_Activity instance;
    private Activity activity;

    @BindView(R.id.img_back)
    ImageView img_back;
    @BindView(R.id.tx_login)
    TextView tx_login;
    @BindView(R.id.edit_phone)
    EditText edit_phone;
    @BindView(R.id.edit_phone_ok)
    EditText edit_phone_ok;

    private String phoneNumber;
    private String key;
    private String salt;
    private String token;
    private boolean pwd_input;
    private boolean pwd_sure;

    @Override
    protected int getLayoutID() {
        instance = this;
        activity = this;
        return R.layout.pwd_forget_set_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        MyApp.getInstance().addActivity(this);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        phoneNumber = intent.getStringExtra("phoneNumber");
        key = intent.getStringExtra("key");
        salt = intent.getStringExtra("salt");
        token = intent.getStringExtra("token");

        edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    String code = editable.toString().trim();
                    if (!TextUtils.isEmpty(code)) {
                        if (code.length() >= 8) {
                            pwd_input = true;
                            if(pwd_sure){
                                tx_login.setBackgroundResource(R.drawable.button_bg);
                                tx_login.setTextColor(getResources().getColor(R.color.get_code_cocle));
                                tx_login.setEnabled(true);
                            }
                        } else {
                            pwd_input = false;
                            tx_login.setBackgroundResource(R.drawable.button_unsel_bg);
                            tx_login.setTextColor(getResources().getColor(R.color.get_code_cocle_un));
                            tx_login.setEnabled(false);
                        }
                    }
                }
            }
        });

        edit_phone_ok.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    String code = editable.toString().trim();
                    if (!TextUtils.isEmpty(code)) {
                        if (code.length() >= 8) {
                            pwd_sure = true;
                            if(pwd_input){
                                tx_login.setBackgroundResource(R.drawable.button_bg);
                                tx_login.setTextColor(getResources().getColor(R.color.get_code_cocle));
                                tx_login.setEnabled(true);
                            }
                        } else {
                            pwd_sure = false;
                            tx_login.setBackgroundResource(R.drawable.button_unsel_bg);
                            tx_login.setTextColor(getResources().getColor(R.color.get_code_cocle_un));
                            tx_login.setEnabled(false);
                        }
                    }
                }
            }
        });



        /*edit_phone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new KeyboardNormal(activity, instance, edit_phone).showKeyboard();
                return false;
            }
        });

        edit_phone_ok.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                new KeyboardNormal(activity, instance, edit_phone_ok).showKeyboard();
                return false;
            }
        });*/

        //forbidDefaultSoftKeyboard();
    }


    /**
     * 禁止系统默认的软键盘弹出
     */
    private void forbidDefaultSoftKeyboard() {
        if (edit_phone == null) {
            return;
        }
        if (android.os.Build.VERSION.SDK_INT > 10) {//4.0以上，使用反射的方式禁止系统自带的软键盘弹出
            try {
                Class<EditText> cls = EditText.class;
                Method setShowSoftInputOnFocus;
                setShowSoftInputOnFocus = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(edit_phone, false);
                setShowSoftInputOnFocus.invoke(edit_phone_ok, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.img_back)
    public void img_back() {
        finish();
    }

    @OnClick(R.id.tx_login)
    public void tx_login() {
        hideSoftWorldInput(edit_phone, true);
        String password = edit_phone.getText().toString().trim();
        String repass = edit_phone_ok.getText().toString().trim();

        if (password.length() < 8 || password.length() > 20 || !IcallUtils.isPwd(password)) {
            toast(getString(R.string.code_length));
            return;
        }
        if (!repass.equals(password)) {
            toast(getString(R.string.code_same));
            return;
        }

        DialogUtils.getInstance().showDialog(instance, "加载中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phoneNumber);
        map.put("salt", salt);
        map.put("password", password);
        map.put("password_confirmation", repass);
        OkHttpUtil.postRequest(Api.HEAD + "user/forget_password", map, token, new OkHttpUtil.OnRequestNetWorkListener() {
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
                    if (jsonObject.getInt("errCode") == 200) {
                        MyApp.getInstance().finish_Activity();
                        MyApp.getInstance().activityList.clear();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

}
