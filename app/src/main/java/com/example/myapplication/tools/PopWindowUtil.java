package com.example.myapplication.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.activity.PWD_Forget_Activity;
import com.example.myapplication.bean.UserBean;
import com.example.myapplication.config.MessageActivity;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PopWindowUtil {

    private static PopWindowUtil instance;

    private PopupWindow mPopupWindow;
    private ImageView img_sel_button;

    // 私有化构造方法，变成单例模式
    private PopWindowUtil() {

    }

    // 对外提供一个该类的实例，考虑多线程问题，进行同步操作
    public static PopWindowUtil getInstance() {
        if (instance == null) {
            synchronized (PopWindowUtil.class) {
                if (instance == null) {
                    instance = new PopWindowUtil();
                }
            }
        }
        return instance;
    }

    /**
     * @param cx
     *            activity
     * @param view
     *            传入需要显示在什么控件下
     * @param view1
     *            传入内容的view
     * @return
     */
    /*public PopWindowUtil makePopupWindow(Context cx, View view, View view1, int color) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wmManager=(WindowManager) cx.getSystemService(Context.WINDOW_SERVICE);
        wmManager.getDefaultDisplay().getMetrics(dm);
        int Hight = dm.heightPixels;

        mPopupWindow = new PopupWindow(cx);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable(color));
        view1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        // 设置PopupWindow的大小（宽度和高度）
        mPopupWindow.setWidth(view.getWidth());
        mPopupWindow.setHeight((Hight - view.getBottom()) * 2 / 3);
        // 设置PopupWindow的内容view
        mPopupWindow.setContentView(view1);
        mPopupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
        mPopupWindow.setTouchable(true); // 设置PopupWindow可触摸
        mPopupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸

        return instance;
    }*/

    /**
     * @param cx 此处必须为Activity的实例
     * @param view 显示在该控件之下
     * @param xOff 距离view的x轴偏移量
     * @param yOff 距离view的y轴偏移量
     * @param anim 弹出及消失动画
     * @return
     */
    /*public PopupWindow showLocationWithAnimation(final Context cx, View view,int xOff, int yOff, int anim) {
        // 弹出动画
        mPopupWindow.setAnimationStyle(anim);

        // 弹出PopupWindow时让后面的界面变暗
        WindowManager.LayoutParams parms = ((Activity) cx).getWindow().getAttributes();
        parms.alpha =0.5f;
        ((Activity) cx).getWindow().setAttributes(parms);

        //int[] positon = new int[2];
        //view.getLocationOnScreen(positon);
        // 弹窗的出现位置，在指定view之下
        //mPopupWindow.showAsDropDown(view, positon[0] + xOff, positon[1] + yOff);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // PopupWindow消失后让后面的界面变亮
                WindowManager.LayoutParams parms = ((Activity) cx).getWindow().getAttributes();
                parms.alpha =1.0f;
                ((Activity) cx).getWindow().setAttributes(parms);

                if (mListener != null) {
                    mListener.dissmiss();
                }
            }
        });

        return mPopupWindow;
    }*/



    private VarCodeCountDownTimerUtil mVarCodeCountDownTimer;
    private boolean isPwd_Login = false;
    private boolean is_sel = false;
    private boolean phone_sure = false;
    private boolean key_sure = false;
    private boolean pwd_sure = false;

    public PopupWindow getPopupWindow(Context mContext, View view, int xOff, int yOff, int anim) {
        is_sel = false;
        isPwd_Login = false;
        phone_sure = false;
        key_sure = false;
        pwd_sure = false;
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_pop, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //点击非菜单部分退出
        /*inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });*/

        //相对于屏幕的位置
        int[] position = new int[2];
        //view.getLocationOnScreen(position);

        ImageView img_miss = inflate.findViewById(R.id.img_miss);
        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        TextView tx_login_go = inflate.findViewById(R.id.tx_login_go);
        tx_login_go.setBackgroundResource(R.drawable.button_unsel_bg);
        tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle_un));
        tx_login_go.setEnabled(false);

        EditText edit_phone = inflate.findViewById(R.id.edit_phone);
        EditText edit_key = inflate.findViewById(R.id.edit_key);
        EditText edit_passworld = inflate.findViewById(R.id.edit_passworld);
        addTxt_Watch(edit_phone, edit_key, edit_passworld, tx_login_go, mContext);

        LinearLayout lin_agree_txt = inflate.findViewById(R.id.lin_agree_txt);
        TextView tx_title = inflate.findViewById(R.id.tx_title);
        TextView tx_pwd_forget = inflate.findViewById(R.id.tx_pwd_forget);
        tx_pwd_forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mContext.startActivity(new Intent(mContext, PWD_Forget_Activity.class));
                //popupWindow.dismiss();
            }
        });

        TextView register_key = inflate.findViewById(R.id.register_key);
        mVarCodeCountDownTimer = new VarCodeCountDownTimerUtil(60000, 1000, register_key);
        register_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setRegister_key(mContext, edit_phone, register_key);
            }
        });

        RelativeLayout login_input_key = inflate.findViewById(R.id.login_input_key);
        RelativeLayout login_input_pwd = inflate.findViewById(R.id.login_input_pwd);
        img_sel_button = inflate.findViewById(R.id.img_sel_button);
        img_sel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (is_sel) {
                    img_sel_button.setImageResource(R.mipmap.simple_sel);
                } else {
                    img_sel_button.setImageResource(R.mipmap.simple_sel_on);
                }
                is_sel = !is_sel;
            }
        });

        TextView tx_longin_set = inflate.findViewById(R.id.tx_longin_set);
        tx_longin_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPwd_Login) {
                    login_input_key.setVisibility(View.VISIBLE);
                    login_input_pwd.setVisibility(View.GONE);
                    tx_title.setText(mContext.getString(R.string.login_way));
                    tx_login_go.setText(mContext.getString(R.string.login_regis));
                    tx_longin_set.setText(mContext.getString(R.string.pwd_login));
                    tx_pwd_forget.setVisibility(View.GONE);
                } else {
                    login_input_key.setVisibility(View.GONE);
                    login_input_pwd.setVisibility(View.VISIBLE);
                    tx_title.setText(mContext.getString(R.string.account_login));
                    tx_login_go.setText(mContext.getString(R.string.login));
                    tx_longin_set.setText(mContext.getString(R.string.yzm_login));
                    tx_pwd_forget.setVisibility(View.VISIBLE);
                }
                isPwd_Login = !isPwd_Login;
                check_btn(tx_login_go, mContext);
            }
        });

        tx_login_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftWorldInput(edit_key, true, mContext);
                if (isPwd_Login) {
                    String phone = edit_phone.getText().toString().trim();
                    String password = edit_passworld.getText().toString().trim();
                    if (TextUtils.isEmpty(phone)) {
                        toast(mContext, mContext.getString(R.string.phoneNumber_null));
                        return;
                    }
                    if (!NumberUtil.isCellPhone(phone)) {
                        toast(mContext, mContext.getString(R.string.phoneNumber_type));
                        return;
                    }
                    if (password.length() < 8 || password.length() > 20 || !IcallUtils.isPwd(password)) {
                        toast(mContext, mContext.getString(R.string.code_length));
                        return;
                    }

                    if (!is_sel) {
                        /*showMyDialog(mContext, mContext.getString(R.string.title), mContext.getString(R.string.content),
                                mContext.getString(R.string.ok), edit_phone, edit_passworld, edit_key, popupWindow);*/

                        toast(mContext, mContext.getString(R.string.agree_first));
                        //动画抖动
                        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.protocol_shake);
                        lin_agree_txt.startAnimation(animation);

                        return;
                    }

                    //账号密码登陆
                    pwd_login(mContext, edit_phone, edit_passworld, popupWindow);
                } else {
                    //短信验证登录
                    String phone = edit_phone.getText().toString().trim();
                    String key = edit_key.getText().toString().trim();
                    if (TextUtils.isEmpty(phone)) {
                        toast(mContext, mContext.getString(R.string.phoneNumber_null));
                        return;
                    }
                    if (!NumberUtil.isCellPhone(phone)) {
                        toast(mContext, mContext.getString(R.string.phoneNumber_type));
                        return;
                    }
                    if (TextUtils.isEmpty(key)) {
                        toast(mContext, mContext.getString(R.string.keyCode_null));
                        return;
                    }
                    if (!is_sel) {
                        /*showMyDialog(mContext, mContext.getString(R.string.title), mContext.getString(R.string.content),
                                mContext.getString(R.string.ok), edit_phone, edit_passworld, edit_key, popupWindow);*/

                        toast(mContext, mContext.getString(R.string.agree_first));
                        //动画抖动
                        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.protocol_shake);
                        lin_agree_txt.startAnimation(animation);

                        return;
                    }
                    msm_login(mContext, edit_phone, edit_key, popupWindow);
                }
            }
        });

        /*TextView txt = inflate.findViewById(R.id.txt);
        //获取状态栏高度
        int result = 0;
        int resourceId = cx.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = cx.getResources().getDimensionPixelSize(resourceId);
        }
        LinearLayout.LayoutParams par = (LinearLayout.LayoutParams) txt.getLayoutParams();
        par.setMargins(position[0], position[1] - result, 0, 0);*/


        popupWindow.setBackgroundDrawable(null);
        //popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(anim);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //pop消失监听
                if (mVarCodeCountDownTimer != null) {
                    mVarCodeCountDownTimer.cancel();
                }
            }
        });

        //解决软件盘
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        popupWindow.showAtLocation(inflate, Gravity.NO_GRAVITY, 0, 0);
        return mPopupWindow;
    }


    private void check_btn(TextView tx_login_go, Context mContext) {
        if(isPwd_Login){
            if(phone_sure && pwd_sure){
                tx_login_go.setBackgroundResource(R.drawable.button_bg);
                tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle));
                tx_login_go.setEnabled(true);
            }else{
                tx_login_go.setBackgroundResource(R.drawable.button_unsel_bg);
                tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle_un));
                tx_login_go.setEnabled(false);
            }
        }else{
            if(phone_sure && key_sure){
                tx_login_go.setBackgroundResource(R.drawable.button_bg);
                tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle));
                tx_login_go.setEnabled(true);
            }else{
                tx_login_go.setBackgroundResource(R.drawable.button_unsel_bg);
                tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle_un));
                tx_login_go.setEnabled(false);
            }
        }
    }


    private void addTxt_Watch(EditText edit_phone, EditText edit_key, EditText edit_passworld, TextView tx_login_go, Context mContext) {
        edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                phone_sure = false;
                if (editable != null) {
                    String code = editable.toString().trim();
                    if (!TextUtils.isEmpty(code)) {
                        if (NumberUtil.isCellPhone(code)) {
                            phone_sure = true;
                            //密码
                            if (isPwd_Login) {
                                if (pwd_sure) {
                                    tx_login_go.setBackgroundResource(R.drawable.button_bg);
                                    tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle));
                                    tx_login_go.setEnabled(true);
                                }
                            } else {
                                //验证码
                                if (key_sure) {
                                    tx_login_go.setBackgroundResource(R.drawable.button_bg);
                                    tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle));
                                    tx_login_go.setEnabled(true);
                                }
                            }
                        } else {
                            phone_sure = false;
                            tx_login_go.setBackgroundResource(R.drawable.button_unsel_bg);
                            tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle_un));
                            tx_login_go.setEnabled(false);
                        }
                    }
                }
            }
        });


        edit_passworld.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                pwd_sure = false;
                if (editable != null) {
                    String code = editable.toString().trim();
                    //8位数起的密码
                    if (!TextUtils.isEmpty(code) && code.length() >= 8) {
                        pwd_sure = true;
                        if(isPwd_Login && phone_sure){
                            tx_login_go.setBackgroundResource(R.drawable.button_bg);
                            tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle));
                            tx_login_go.setEnabled(true);
                        }
                    }else{
                        pwd_sure = false;
                        tx_login_go.setBackgroundResource(R.drawable.button_unsel_bg);
                        tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle_un));
                        tx_login_go.setEnabled(false);
                    }
                }
            }
        });


        edit_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                key_sure = false;
                if (editable != null) {
                    String code = editable.toString().trim();
                    //五位数验证码
                    if (!TextUtils.isEmpty(code) && code.length() == 5) {
                        key_sure = true;
                        if(!isPwd_Login && phone_sure){
                            tx_login_go.setBackgroundResource(R.drawable.button_bg);
                            tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle));
                            tx_login_go.setEnabled(true);
                        }
                    }else{
                        key_sure = false;
                        tx_login_go.setBackgroundResource(R.drawable.button_unsel_bg);
                        tx_login_go.setTextColor(mContext.getResources().getColor(R.color.get_code_cocle_un));
                        tx_login_go.setEnabled(false);
                    }
                }
            }
        });
    }


    private void msm_login(Context mContext, EditText edit_phone, EditText edit_key, PopupWindow popupWindow) {
        String phone = edit_phone.getText().toString().trim();
        String key = edit_key.getText().toString().trim();

        DialogUtils.getInstance().showDialog(mContext, "登录中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("phone", phone);
        map.put("code", key);
        OkHttpUtil.postRequest(Api.HEAD + "login_code", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                    int code = jsonObject.getInt("errCode");
                    toast(mContext, jsonObject.getString("errMsg"));
                    if (code == 200) {
                        UserBean userBean = new Gson().fromJson(response, UserBean.class);
                        UserBean.DataBean dataBean = userBean.getData();

                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        UserConfig.instance().access_token = dataBean.getAccess_token();
                        UserConfig.instance().user_id = dataBean.getUser_id();
                        UserConfig.instance().expires_in = dataBean.getExpires_in();
                        UserConfig.instance().token_type = dataBean.getToken_type();
                        //保存
                        UserConfig.instance().saveUserConfig(mContext);

                        //pop隐藏
                        popupWindow.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void pwd_login(Context mContext, EditText edit_phone, EditText edit_key, PopupWindow popupWindow) {
        String phone = edit_phone.getText().toString().trim();
        String password = edit_key.getText().toString().trim();

        //hideSoftWorldInput(edit_key, true);
        DialogUtils.getInstance().showDialog(mContext, "登录中...");
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
                    toast(mContext, jsonObject.getString("errMsg"));
                    if (jsonObject.getInt("errCode") == 200) {
                        //注册成功，返回账号密码至登录页面
                        UserBean userBean = new Gson().fromJson(response, UserBean.class);
                        UserBean.DataBean dataBean = userBean.getData();

                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        UserConfig.instance().access_token = dataBean.getAccess_token();
                        UserConfig.instance().user_id = dataBean.getUser_id();
                        UserConfig.instance().expires_in = dataBean.getExpires_in();
                        UserConfig.instance().token_type = dataBean.getToken_type();
                        //保存
                        UserConfig.instance().saveUserConfig(mContext);

                        //pop隐藏
                        popupWindow.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void setRegister_key(Context context, EditText edit_phone, TextView register_key) {
        String phone = edit_phone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            toast(context, context.getString(R.string.phoneNumber_null));
            return;
        }

        DialogUtils.getInstance().showDialog(context, "加载中...");
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
                    toast(context, jsonObject.getString("errMsg"));

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


    protected void toast(Context context, CharSequence msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void hideSoftWorldInput(EditText edit, boolean b, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (!b) {
            imm.showSoftInput(edit, InputMethodManager.SHOW_FORCED);   //SHOW_FORCED表示强制显示
        } else {
            imm.hideSoftInputFromWindow(edit.getWindowToken(), 0);   //强制隐藏键盘
        }
    }


    /**
     * dialog
     */
    private Dialog dialog;

    public void showMyDialog(Context context, String title, String msg, String ok, EditText edit_phone,
                             EditText edit_passworld, EditText edit_key, PopupWindow popupWindow) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.act_dialog, null);

        if (dialog == null) {
            dialog = new AlertDialog.Builder(context, R.style.mdialog).create();
        }
        dialog.show();
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setContentView(layout);

        Button dialog_cancel = layout.findViewById(R.id.dialog_cancel);
        dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button dialog_ok = layout.findViewById(R.id.dialog_ok);
        dialog_ok.setText(ok);
        dialog_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                img_sel_button.setImageResource(R.mipmap.simple_sel_on);
                is_sel = true;
                if (isPwd_Login) {
                    //账号密码登陆
                    pwd_login(context, edit_phone, edit_passworld, popupWindow);
                } else {
                    //短信验证登录
                    msm_login(context, edit_phone, edit_key, popupWindow);
                }
            }
        });

        TextView dialog_text_about = layout.findViewById(R.id.dialog_text_about);
        dialog_text_about.setText(msg);
        TextView dialog_text = layout.findViewById(R.id.dialog_text);
        dialog_text.setText(title);
    }


    private interface OnDissmissListener {
        void dissmiss();
    }

    private OnDissmissListener mListener;

    public void setOnDissmissListener(OnDissmissListener listener) {
        mListener = listener;
    }
}

