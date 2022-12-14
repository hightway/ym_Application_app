package com.example.myapplication.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.example.myapplication.http.Api;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.NumberUtil;
import com.example.myapplication.tools.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PWD_Forget_Activity extends BaseActivity {


    @BindView(R.id.img_back)
    ImageView img_back;
    @BindView(R.id.tx_login)
    TextView tx_login;
    @BindView(R.id.edit_phone)
    EditText edit_phone;

    private Context instance;
    private KeyboardPopupWindow keyboardPopupWindow;
    private boolean isUiCreated = false;

    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.pwd_forget_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        MyApp.getInstance().activityList.clear();
        MyApp.getInstance().addActivity(this);

        ButterKnife.bind(this);

        tx_login.setEnabled(false);
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
                        if (NumberUtil.isCellPhone(code)) {
                            tx_login.setBackgroundResource(R.drawable.button_bg);
                            tx_login.setTextColor(getResources().getColor(R.color.get_code_cocle));
                            tx_login.setEnabled(true);
                        } else {
                            tx_login.setBackgroundResource(R.drawable.button_unsel_bg);
                            tx_login.setTextColor(getResources().getColor(R.color.get_code_cocle_un));
                            tx_login.setEnabled(false);
                        }
                    }
                }
            }
        });

        init_keyboard();
    }


    private void init_keyboard() {
        keyboardPopupWindow = new KeyboardPopupWindow(instance, getWindow().getDecorView(), edit_phone, false, false);
        //numberEt.setInputType(InputType.TYPE_NULL);//?????????????????????????????????
        edit_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keyboardPopupWindow != null) {
                    keyboardPopupWindow.show();
                }
            }
        });
        edit_phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("keyboard", "????????????????????????????????????>" + hasFocus);
                //????????????Unable to add window -- token null is not valid; is your activity running?
                if (keyboardPopupWindow != null && isUiCreated) {
                    // ????????????????????????????????????????????????????????????????????????
                    keyboardPopupWindow.refreshKeyboardOutSideTouchable(!hasFocus);
                }

                if (hasFocus) {//?????????????????????
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_phone.getWindowToken(), 0);
                }

            }
        });


        //view?????????????????????
        edit_phone.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (keyboardPopupWindow != null) {
                    keyboardPopupWindow.show();
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

    @OnClick(R.id.tx_login)
    public void tx_login() {
        String number = edit_phone.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            toast(getString(R.string.phoneNumber_null));
            return;
        }

        if (!NumberUtil.isCellPhone(number)) {
            toast(getString(R.string.phoneNumber_type));
            return;
        }

        if (keyboardPopupWindow != null && keyboardPopupWindow.isShowing()) {
            keyboardPopupWindow.dismiss();
        }

        //???????????????
        getKey(number);
    }

    private void getKey(String phone) {

        /*Bundle bundle = new Bundle();
        bundle.putString("phoneNumber", phone);
        startActivity_to(PWD_Forget_Key_Activity.class, bundle);*/

        DialogUtils.getInstance().showDialog(instance, "?????????...");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("type", "forget");
        OkHttpUtil.postRequest(Api.HEAD + "send_code", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                    if (jsonObject.getInt("errCode") == 200) {
                        Bundle bundle = new Bundle();
                        bundle.putString("phoneNumber", phone);
                        startActivity_to(PWD_Forget_Key_Activity.class, bundle);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
