package com.example.myapplication.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.custom.KeyboardNormal;
import com.example.myapplication.custom.KeyboardPopupWindow;
import com.example.myapplication.custom.Keyboard_ABC_PopupWindow;
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

public class PWD_Forget_Set_Activity extends BaseActivity implements KeyboardPopupWindow.Keyboard_ABC_Callback{

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

    private Keyboard_ABC_PopupWindow keyboardPopupWindow;
    private boolean isUiCreated = false;
    private Keyboard_ABC_PopupWindow keyboardPopupWindow_2;
    private boolean sel_top = true;

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



        init_keyboard(edit_phone);
        //init_keyboard_num(edit_phone);

        init_keyboard_2(edit_phone_ok);
        //init_keyboard_num_2(edit_phone_ok);
    }


    /**
     * 禁止系统默认的软键盘弹出
     */
    /*private void forbidDefaultSoftKeyboard() {
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
    }*/



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
        //hideSoftWorldInput(edit_phone, true);
        if (keyboardPopupWindow != null) {
            keyboardPopupWindow.dismiss();
        }
        if (keyboardPopupWindow_2 != null) {
            keyboardPopupWindow_2.dismiss();
        }

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
        if(!TextUtils.isEmpty(salt)){
            map.put("salt", salt);
        }
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
                        if(mHandle != null){
                            mHandle.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MyApp.getInstance().finish_Activity();
                                    MyApp.getInstance().activityList.clear();
                                }
                            }, 500);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }



    private void init_keyboard(EditText edit_key_1) {
        keyboardPopupWindow = new Keyboard_ABC_PopupWindow(instance, getWindow().getDecorView(), edit_key_1, false);
        //keyboardPopupWindow.setKeyboard_NUM_Callback(this);
        //numberEt.setInputType(InputType.TYPE_NULL);//该设置会导致光标不可见
        edit_key_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_top = true;
                if(keyboardPopupWindow_2 != null && keyboardPopupWindow_2.isShowing()){
                    keyboardPopupWindow_2.dismiss();
                }
                if (keyboardPopupWindow != null) {
                    keyboardPopupWindow.show();
                }
            }
        });
        edit_key_1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("keyboard", "数字输入框是否有焦点——>" + hasFocus);
                //很重要，Unable to add window -- token null is not valid; is your activity running?
                if (keyboardPopupWindow != null && isUiCreated) {
                    // 需要等待页面创建完成后焦点变化才去显示自定义键盘
                    if(keyboardPopupWindow_2 != null && keyboardPopupWindow_2.isShowing()){
                        keyboardPopupWindow_2.dismiss();
                    }
                    sel_top = true;
                    keyboardPopupWindow.refreshKeyboardOutSideTouchable(!hasFocus);
                }

                if (hasFocus) {//隐藏系统软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_key_1.getWindowToken(), 0);
                }

            }
        });

        //view加载完成时回调
        edit_key_1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (keyboardPopupWindow != null) {
                    keyboardPopupWindow.show();
                }
            }
        });
    }

    private void init_keyboard_2(EditText edit_key_1) {
        keyboardPopupWindow_2 = new Keyboard_ABC_PopupWindow(instance, getWindow().getDecorView(), edit_key_1, false);
        //keyboardPopupWindow_2.setKeyboard_NUM_Callback(this);
        //numberEt.setInputType(InputType.TYPE_NULL);//该设置会导致光标不可见
        edit_key_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sel_top = false;
                if(keyboardPopupWindow != null && keyboardPopupWindow.isShowing()){
                    keyboardPopupWindow.dismiss();
                }

                if (keyboardPopupWindow_2 != null) {
                    keyboardPopupWindow_2.show();
                }
            }
        });
        edit_key_1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("keyboard", "数字输入框是否有焦点——>" + hasFocus);
                //很重要，Unable to add window -- token null is not valid; is your activity running?
                if (keyboardPopupWindow_2 != null && isUiCreated) {
                    // 需要等待页面创建完成后焦点变化才去显示自定义键盘
                    if(keyboardPopupWindow != null && keyboardPopupWindow.isShowing()){
                        keyboardPopupWindow.dismiss();
                    }
                    sel_top = false;
                    keyboardPopupWindow_2.refreshKeyboardOutSideTouchable(!hasFocus);
                }

                if (hasFocus) {//隐藏系统软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_key_1.getWindowToken(), 0);
                }
            }
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyboardPopupWindow != null && keyboardPopupWindow.isShowing()) {
                keyboardPopupWindow.dismiss();
                return true;
            }
            if (keyboardPopupWindow_2 != null && keyboardPopupWindow_2.isShowing()) {
                keyboardPopupWindow_2.dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        isUiCreated = true;
    }


    @Override
    protected void onDestroy() {
        if (keyboardPopupWindow != null) {
            keyboardPopupWindow.releaseResources();
        }
        if (keyboardPopupWindow_2 != null) {
            keyboardPopupWindow_2.releaseResources();
        }
        super.onDestroy();
    }


    /*@Override
    public void change_num() {
        if(sel_top){
            //切换数字键盘
            if (keyboardPopupWindow != null) {
                keyboardPopupWindow.dismiss();
            }
        }else{
            if (keyboardPopupWindow_num_2 != null) {
                keyboardPopupWindow_num_2.show();
            }
            if (keyboardPopupWindow_2 != null) {
                keyboardPopupWindow_2.dismiss();
            }
        }
    }*/


    @Override
    public void change_abc() {
        if(sel_top){
            if (keyboardPopupWindow != null) {
                keyboardPopupWindow.show();
            }
        }else{
            if (keyboardPopupWindow_2 != null) {
                keyboardPopupWindow_2.show();
            }
        }
    }



    /*private void init_keyboard_num(EditText edit_key_1) {
        keyboardPopupWindow_Num = new KeyboardPopupWindow(instance, getWindow().getDecorView(), edit_key_1, false, true);
        keyboardPopupWindow_Num.setKeyboard_NUM_Callback(this);
    }

    private void init_keyboard_num_2(EditText edit_key_1) {
        keyboardPopupWindow_num_2 = new KeyboardPopupWindow(instance, getWindow().getDecorView(), edit_key_1, false, true);
        keyboardPopupWindow_num_2.setKeyboard_NUM_Callback(this);
    }*/



}
