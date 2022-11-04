package com.example.myapplication.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.custom.KeyboardPopupWindow;
import com.example.myapplication.custom.VerificationCodeViewJava;
import com.example.myapplication.http.Api;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.NumberUtil;
import com.example.myapplication.tools.OkHttpUtil;
import com.stomhong.library.KeyboardTouchListener;
import com.stomhong.library.KeyboardUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PWD_Forget_Key_Activity extends BaseActivity implements VerificationCodeViewJava.Key_CallBack{

    @BindView(R.id.img_back)
    ImageView img_back;
    @BindView(R.id.tx_phone)
    TextView tx_phone;
    @BindView(R.id.input_code)
    VerificationCodeViewJava input_code;
    private String phoneNumber;
    /*@BindView(R.id.edit_put)
    EditText edit_put;*/
    private Context instance;

    private KeyboardPopupWindow keyboardPopupWindow;
    private boolean isUiCreated = false;


    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.pwd_forget_key_lay;
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
        tx_phone.setText(getString(R.string.pwd_reset_send) + NumberUtil.dealPhoneNumber(phoneNumber));

        input_code.setKey_CallBack(this);

        init_keyboard();
    }


    private void init_keyboard() {
        keyboardPopupWindow = new KeyboardPopupWindow(instance, getWindow().getDecorView(), input_code.editText, false);
        keyboardPopupWindow.setIs_Key_Input(true);
        //numberEt.setInputType(InputType.TYPE_NULL);//该设置会导致光标不可见
        input_code.editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keyboardPopupWindow != null) {
                    keyboardPopupWindow.show();
                }
            }
        });
        input_code.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("keyboard", "数字输入框是否有焦点——>" + hasFocus);
                //很重要，Unable to add window -- token null is not valid; is your activity running?
                if (keyboardPopupWindow != null && isUiCreated) {
                    // 需要等待页面创建完成后焦点变化才去显示自定义键盘
                    keyboardPopupWindow.refreshKeyboardOutSideTouchable(!hasFocus);
                }

                if (hasFocus) {//隐藏系统软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input_code.editText.getWindowToken(), 0);
                }

            }
        });


        //view加载完成时回调
        tx_phone.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (keyboardPopupWindow != null) {
                    keyboardPopupWindow.show();
                }
            }
        });


        input_code.setkeyboardPopupWindow(keyboardPopupWindow);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyboardPopupWindow != null && keyboardPopupWindow.isShowing()) {
                keyboardPopupWindow.dismiss();
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
        super.onDestroy();
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


    @Override
    public void key_back(String txt, EditText editText) {
        if (keyboardPopupWindow != null && keyboardPopupWindow.isShowing()) {
            keyboardPopupWindow.dismiss();
        }

        //请求网络
        DialogUtils.getInstance().showDialog(instance, "加载中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phoneNumber);
        map.put("code", txt);
        OkHttpUtil.postRequest(Api.HEAD + "forget_verify", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
                toast(err);
            }

            @Override
            public void un_login_err() {

            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    toast(jsonObject.getString("errMsg"));
                    if (jsonObject.getInt("errCode") == 200) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        if(data !=null){
                            String salt = data.getString("salt");
                            String token = data.getString("token");

                            if(!TextUtils.isEmpty(salt)){
                                Bundle bundle = new Bundle();
                                bundle.putString("phoneNumber", phoneNumber);
                                bundle.putString("key", txt);
                                bundle.putString("salt", salt);
                                bundle.putString("token", token);
                                startActivity_to(PWD_Forget_Set_Activity.class, bundle);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        /*Bundle bundle = new Bundle();
        bundle.putString("phoneNumber", phoneNumber);
        bundle.putString("key", txt);
        startActivity_to(PWD_Forget_Set_Activity.class, bundle);*/
    }




    /*@Override
    public void KeyBoardStateChange(int state, EditText editText) {

    }


    @Override
    public void inputHasOver(int onclickType, EditText editText) {

    }*/
}
