package com.example.myapplication.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.tools.NumberUtil;

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


    @Override
    protected int getLayoutID() {
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

        Bundle bundle = new Bundle();
        bundle.putString("phoneNumber", number);
        startActivity_to(PWD_Forget_Key_Activity.class, bundle);

    }

}
