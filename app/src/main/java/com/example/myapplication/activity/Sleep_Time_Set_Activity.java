package com.example.myapplication.activity;

import static com.aliyun.player.nativeclass.NativePlayerBase.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.adapter.More_Radio_Adapter;
import com.example.myapplication.adapter.More_mp3_Adapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.Raw_Bean;
import com.example.myapplication.bean.User_Msg_Bean;
import com.example.myapplication.custom.AudioRecordDemo;
import com.example.myapplication.custom.DatePickerAdapter;
import com.example.myapplication.custom.ScrollPickerView;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.receiver.AlarmReceiver;
import com.example.myapplication.tools.Aliyun_Login_Util;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.Login_Util;
import com.example.myapplication.tools.MediaUtil;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.tools.Raw_Util;
import com.example.myapplication.tools.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Sleep_Time_Set_Activity extends BaseActivity implements ScrollPickerView.OnItemSelectedListener {

    private Sleep_Time_Set_Activity instance;

    @BindView(R.id.query_button)
    ImageView query_button;
    @BindView(R.id.datepicker_year_1)
    ScrollPickerView datepicker_year_1;
    @BindView(R.id.datepicker_year_2)
    ScrollPickerView datepicker_year_2;
    @BindView(R.id.img_more)
    ImageView img_more;
    @BindView(R.id.img_bg_water)
    ImageView img_bg_water;
    @BindView(R.id.tx_set_time)
    TextView tx_set_time;
    @BindView(R.id.rel_time)
    RelativeLayout rel_time;

    @BindView(R.id.rel_data)
    RelativeLayout rel_data;
    @BindView(R.id.tx_time_num)
    TextView tx_time_num;
    @BindView(R.id.tx_clock_time)
    TextView tx_clock_time;
    @BindView(R.id.tx_start)
    TextView tx_start;
    @BindView(R.id.tx_db)
    public
    TextView tx_db;
    @BindView(R.id.tx_noise)
    public
    TextView tx_noise;
    @BindView(R.id.set_clock)
    ImageView set_clock;
    @BindView(R.id.set_clock_stop)
    LottieAnimationView set_clock_stop;
    @BindView(R.id.seekbar)
    SeekBar seekbar;
    @BindView(R.id.lin_seekbar)
    LinearLayout lin_seekbar;

    private DatePickerAdapter mDayAdapter;
    private DatePickerAdapter mAdapter;
    private int t_hour;
    private int t_minute;
    private int d_height;
    private ScrollPickerView datepicker_close;
    private DatePickerAdapter close_adapter;
    private int date_h;
    private int date_min;
    private AudioRecordDemo audioRecordDemo;
    private static final int MY_PERMISSION_REQUEST_CODE = 9999;
    private long mLastDownTime;
    private int mDownX;
    private int mDownY;
    private int MAX_MOVE_FOR_CLICK = 50;// 最长改变距离,超过则算移动
    private int MAX_LONG_PRESS_TIME = 2500;// 最长改变距离,超过则算移动
    private boolean pre_down = true;
    private boolean back_pre_down = true;
    private int current_pro;
    private int current_max;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private boolean sleep_monitoring_open;
    private boolean painless_arousal_open;

    @Override
    protected int getLayoutID() {
        setBar_color_transparent(R.color.transparent);
        instance = this;
        return R.layout.sleep_set_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        //获取当前时间
        get_Time();

        mDayAdapter = new DatePickerAdapter(0, 23, new DecimalFormat("00"));
        mAdapter = new DatePickerAdapter(0, 59, new DecimalFormat("00"));

        datepicker_year_1.setAdapter(mDayAdapter);
        datepicker_year_2.setAdapter(mAdapter);

        datepicker_year_1.setOnItemSelectedListener(this);
        datepicker_year_2.setOnItemSelectedListener(this);
        datepicker_year_1.setSelectedPosition(t_hour);
        datepicker_year_2.setSelectedPosition(t_minute);

        //加载gif背景图
        String url = "file:///android_asset/gif_1.gif";
        Glide.with(instance)
                .asGif()
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                .load(url)
                .into(img_bg_water);

        /*FrameAnimation frameAnimation = new FrameAnimation(img_bg_water, getRes(), 50, true);
        frameAnimation.setAnimationListener(new FrameAnimation.AnimationListener() {
            @Override
            public void onAnimationStart() {
                //Log.d(TAG, "start");
            }

            @Override
            public void onAnimationEnd() {
                //Log.d(TAG, "end");
            }

            @Override
            public void onAnimationRepeat() {
                //Log.d(TAG, "repeat");
            }
        });*/

        set_clock.setEnabled(false);
        //获取上次设置并保存的数据
        MyApp.Audio_Name = UserConfig.instance().get_RawAudio_Sel(instance);

        //获取权限
        askPermission();
    }

    private void askPermission() {
        boolean isAllGranted = checkPermissionAllGranted(
                new String[]{
                        Manifest.permission.RECORD_AUDIO
                }
        );

        // 如果权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            //有全部的权限，做对应的操作

        } else {
            // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.RECORD_AUDIO
                    }, MY_PERMISSION_REQUEST_CODE
            );
        }
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    /**
     * 第 3 步: 申请权限结果返回处理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码

            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    public void get_Write_Setting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(instance)) {
                toast(getString(R.string.write_setting));
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 200);
            } else {
                // 如果有权限做些什么
                //设置闹铃
                setAlarm();
            }

        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(instance, R.style.MyAlertButton);
        builder.setMessage("软件需要获取环境音等相关权限，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }


    /*private int[] getRes() {
        TypedArray typedArray = getResources().obtainTypedArray(R.array.c);
        int len = typedArray.length();
        int[] resId = new int[len];
        for (int i = 0; i < len; i++) {
            resId[i] = typedArray.getResourceId(i, -1);
        }
        typedArray.recycle();
        return resId;
    }*/

    private void get_Time() {
        // 获取系统时间
        Calendar c = Calendar.getInstance();
        // 提取他的时钟值，int型
        t_hour = c.get(Calendar.HOUR_OF_DAY);
        t_minute = c.get(Calendar.MINUTE);
    }


    @OnClick(R.id.tx_set_time)
    public void tx_set_time() {
        get_Time();
        String time = Utils.transfom_time(t_hour, t_minute, date_h, date_min);
        //String time = Utils.transfom_time(get_Time(), date_h+":"+date_min);

        rel_time.setVisibility(View.GONE);
        tx_set_time.setVisibility(View.GONE);
        rel_data.setVisibility(View.VISIBLE);
        tx_time_num.setText(time + getString(R.string.call_up));
        if (date_min < 10) {
            String min = "0" + date_min;
            tx_clock_time.setText(date_h + ":" + min);
        } else {
            tx_clock_time.setText(date_h + ":" + date_min);
        }

        //获取环境分贝
        audioRecordDemo = new AudioRecordDemo(Sleep_Time_Set_Activity.this);
        audioRecordDemo.getNoiseLevel();

        set_clock.setEnabled(true);
    }


    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.query_button)
    public void query_button() {
        if (audioRecordDemo != null) {
            audioRecordDemo.sotp();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioRecordDemo != null) {
            audioRecordDemo.sotp();
        }
        if (set_clock_stop != null) {
            set_clock_stop.cancelAnimation();
        }
    }

    @Override
    public void onItemSelected(View view, int position) {
        switch (view.getId()) {
            case R.id.datepicker_year_1: {
                date_h = mDayAdapter.getDate(position);
                //resetMaxDay();//根据年月计算日期的最大值，并刷新
                break;
            }
            case R.id.datepicker_year_2: {
                date_min = mAdapter.getDate(position);
                //resetMaxDay();//根据年月计算日期的最大值，并刷新
                break;
            }
            default: {
                break;
            }
        }
    }


    /*private void resetMaxDay() {
        int newMaxDay = getMaxDay(mSelectedYear, mSelectedMonth);
        if (newMaxDay != mDayAdapter.getMaxValue()) {
            mDayAdapter.setMaxValue(newMaxDay);
            datepicker_year_2.invalidate();
        }
    }*/

    /*private int getMaxDay(int year, int month) {
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else if (month == 2) {
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return 29;
            } else {
                return 28;
            }
        }
        return 31;
    }*/


    @OnClick(R.id.img_more)
    public void img_more() {
        //获取设置信息
        getUser_info();

        /*Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置闹钟铃声");
        startActivityForResult(intent, 2);*/
    }

    public void show_pop_data(User_Msg_Bean.DataBean.Extends_Bean extends_bean){
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        d_height = d.getHeight();
        getPopupWindow(instance, R.style.showPopupAnimation, extends_bean);
    }


    private void getUser_info() {
        DialogUtils.getInstance().showDialog(this, "加载中...");
        OkHttpUtil.postRequest(Api.HEAD + "user/info", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
                if(!TextUtils.isEmpty(err) && err.contains("401")){
                    //未登录，看详情需要登录
                    Aliyun_Login_Util.getInstance().initSDK(instance);
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
                        User_Msg_Bean user_msg_bean = mGson.fromJson(response, User_Msg_Bean.class);
                        User_Msg_Bean.DataBean dataBean = user_msg_bean.getData();
                        UserConfig.instance().age = dataBean.getAge();
                        UserConfig.instance().user_id = dataBean.getId();
                        UserConfig.instance().avatar = dataBean.getAvatar();
                        UserConfig.instance().name = dataBean.getName();
                        UserConfig.instance().phone = dataBean.getPhone();
                        //保存
                        UserConfig.instance().saveUserConfig(instance);

                        User_Msg_Bean.DataBean.Extends_Bean extends_bean = dataBean.getUser_extends();
                        //展示pop
                        show_pop_data(extends_bean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public void getPopupWindow(Activity mContext, int anim, User_Msg_Bean.DataBean.Extends_Bean extends_bean) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_sleep_time, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int h = d_height / 5 * 2;
        popupWindow.setHeight(h);

        ImageView img_sleep = inflate.findViewById(R.id.img_sleep);
        ImageView img_rouse = inflate.findViewById(R.id.img_rouse);
        if(!TextUtils.isEmpty(extends_bean.sleep_monitoring) && extends_bean.sleep_monitoring.equals("1")){
            img_sleep.setImageResource(R.mipmap.ic_switch_open);
            sleep_monitoring_open = true;
        }else{
            img_sleep.setImageResource(R.mipmap.ic_switch_close);
            sleep_monitoring_open = false;
        }
        img_sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sleep_monitoring_open){
                    img_sleep.setImageResource(R.mipmap.ic_switch_close);
                }else{
                    img_sleep.setImageResource(R.mipmap.ic_switch_open);
                }
                sleep_monitoring_open = !sleep_monitoring_open;
            }
        });

        if(!TextUtils.isEmpty(extends_bean.painless_arousal) && extends_bean.painless_arousal.equals("1")){
            img_rouse.setImageResource(R.mipmap.ic_switch_open);
            painless_arousal_open = true;
        }else{
            img_rouse.setImageResource(R.mipmap.ic_switch_close);
            painless_arousal_open = false;
        }
        img_rouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(painless_arousal_open){
                    img_rouse.setImageResource(R.mipmap.ic_switch_close);
                }else{
                    img_rouse.setImageResource(R.mipmap.ic_switch_open);
                }
                painless_arousal_open = !painless_arousal_open;
            }
        });

        TextView tx_timed_close = inflate.findViewById(R.id.tx_timed_close);
        TextView tx_delay = inflate.findViewById(R.id.tx_delay);
        tx_timed_close.setText(extends_bean.timed_close + getString(R.string.time_min));
        tx_timed_close.setTag(extends_bean.timed_close);
        tx_delay.setText(extends_bean.delay + getString(R.string.time_min));
        tx_delay.setTag(extends_bean.delay);

        TextView tx_raw_name = inflate.findViewById(R.id.tx_raw_name);
        tx_raw_name.setText(MyApp.Audio_Name);
        //tx_raw_name.setTag(MyApp.Audio_Uri);

        TextView img_miss = inflate.findViewById(R.id.tx_ok);
        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time_close = (String) tx_timed_close.getTag();
                String time_delay = (String) tx_delay.getTag();
                post_edit_info(sleep_monitoring_open, painless_arousal_open, time_close, time_delay, popupWindow);
            }
        });

        RelativeLayout rel_close = inflate.findViewById(R.id.rel_close);
        rel_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_PopupWindow_Close(instance, R.style.showPopupAnimation, tx_timed_close);
            }
        });

        RelativeLayout rel_sleep = inflate.findViewById(R.id.rel_sleep);
        rel_sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_PopupWindow_Sleep(instance, R.style.showPopupAnimation, tx_delay);
            }
        });

        RelativeLayout rel_audio_list = inflate.findViewById(R.id.rel_audio_list);
        rel_audio_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Raw_Bean> list =  Raw_Util.getRaw_List();
                if (list != null && list.size() > 0){
                    show_RawList_PopupWindow(mContext, anim, list, tx_raw_name);
                }else{
                    toast(getString(R.string.raw_no));
                }
            }
        });

        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(anim);

        //解决软件盘 "adjust_Pan"在使用时获取焦点的控件下边的View将会被软键盘覆盖
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //popupWindow.showAtLocation(inflate, Gravity.NO_GRAVITY, 0, d_height / 2);
        popupWindow.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
    }


    private void post_edit_info(boolean sleep_monitoring_open, boolean painless_arousal_open, String time_close, String time_delay, PopupWindow popupWindow) {
        DialogUtils.getInstance().showDialog(this, "提交中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("sleep_monitoring", sleep_monitoring_open?"1" : "0");
        map.put("painless_arousal", painless_arousal_open?"1" : "0");
        if(!TextUtils.isEmpty(time_close)){
            map.put("timed_close", time_close);
        }
        if(!TextUtils.isEmpty(time_delay)){
            map.put("delay", time_delay);
        }
        OkHttpUtil.postRequest(Api.HEAD + "user/edit_info", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");

            }

            @Override
            public void un_login_err() {
                //去登录

            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    toast(jsonObject.getString("errMsg"));
                    if (code == 200) {
                        popupWindow.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void show_RawList_PopupWindow(Activity mContext, int anim, List<Raw_Bean> list, TextView tx_raw_name) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_list_lay, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int h = d_height / 9 * 8;
        popupWindow.setHeight(h);

        TextView img_miss = inflate.findViewById(R.id.tx_ok);

        RecyclerView recycle_raw = inflate.findViewById(R.id.recycle_raw);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycle_raw.setLayoutManager(layoutManager);
        More_mp3_Adapter listAdapter = new More_mp3_Adapter(mContext, list);
        recycle_raw.setAdapter(listAdapter);
        listAdapter.setRaw_OnClick_CallBack(new More_mp3_Adapter.Raw_OnClick_CallBack() {
            @Override
            public void Raw_click(int pos) {
                listAdapter.sel_pos(pos);
                Raw_Bean raw_bean = list.get(pos);
                if(raw_bean != null){
                    img_miss.setTag(raw_bean);

                    //停止播放
                    MediaUtil.stopRing();
                    //播放音乐
                    MediaUtil.playRing(mContext, raw_bean.getRawName());
                }
            }
        });

        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //停止播放
                MediaUtil.stopRing();
                listAdapter.stop_all();

                Raw_Bean raw_bean = (Raw_Bean) view.getTag();
                if(raw_bean != null){
                    MyApp.Audio_Name = raw_bean.getRawName();
                    MyApp.Audio_Uri = raw_bean.getUri();
                    tx_raw_name.setText(raw_bean.getRawName());
                    //tx_raw_name.setTag(raw_bean.getUri());

                    //保存的数据
                    UserConfig.instance().save_RawAudio_Sel(instance, MyApp.Audio_Name);
                }
                popupWindow.dismiss();
            }
        });

        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(anim);

        //解决软件盘 "adjust_Pan"在使用时获取焦点的控件下边的View将会被软键盘覆盖
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
    }


    @SuppressLint("NewApi")
    private void show_PopupWindow_Close(Activity mContext, int anim, TextView tx_timed_close) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_close, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int h = d_height / 5 * 2;
        popupWindow.setHeight(h);

        TextView tv_progress = inflate.findViewById(R.id.tv_progress);
        SeekBar seekbar_time = inflate.findViewById(R.id.seekbar);
        TextView img_miss = inflate.findViewById(R.id.tx_ok);
        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pro = seekbar_time.getProgress();
                tx_timed_close.setText(Utils.transfom_min(pro));
                tx_timed_close.setTag(String.valueOf(pro));
                popupWindow.dismiss();
            }
        });

        /**
         * 在进度变更时触发。第三个参数为true表示用户拖动，为false表示代码设置进度
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        seekbar_time.setMax(480);
        seekbar_time.setMin(1);

        String timed_close = (String) tx_timed_close.getTag();
        int time_sel = 1;
        if(!TextUtils.isEmpty(timed_close)){
            int t = Integer.valueOf(timed_close);
            if(t > 0){
                time_sel = t;
            }else{
                time_sel = 1;
            }
        }
        seekbar_time.setProgress(time_sel);
        tv_progress.setText(Utils.transfom_min(time_sel));
        seekbar_time.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_progress.getLayoutParams();
                int w = seekbar_time.getWidth();
                float left = progress * w / 480;
                if (left <= 40) {
                    params.leftMargin = 40;
                } else if (left >= (w - 40)) {
                    params.leftMargin = w - 40;
                } else {
                    params.leftMargin = (int) left;
                }
                tv_progress.setLayoutParams(params);
                tv_progress.setText(Utils.transfom_min(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(anim);

        //解决软件盘 "adjust_Pan"在使用时获取焦点的控件下边的View将会被软键盘覆盖
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
    }

    private void show_PopupWindow_Sleep(Activity mContext, int anim, TextView tx_delay) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_sleep, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int h = d_height / 5 * 2;
        popupWindow.setHeight(h);

        int delay_t = 1;
        String delay_time = (String) tx_delay.getTag();
        if(delay_time != null){
            int t = Integer.valueOf(delay_time);
            if(t > 0){
                delay_t = t;
            }else{
                delay_t = 1;
            }
        }

        datepicker_close = inflate.findViewById(R.id.datepicker_close);
        TextView img_miss = inflate.findViewById(R.id.tx_ok);
        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time = (int) view.getTag();
                tx_delay.setText(time + getString(R.string.time_min));
                tx_delay.setTag(String.valueOf(time));
                popupWindow.dismiss();
            }
        });

        close_adapter = new DatePickerAdapter(1, 30, new DecimalFormat("0"));
        datepicker_close.setAdapter(close_adapter);
        datepicker_close.setSelectedPosition(delay_t-1);
        datepicker_close.setOnItemSelectedListener(new ScrollPickerView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(View view, int position) {
                switch (view.getId()) {
                    case R.id.datepicker_close: {
                        int date = close_adapter.getDate(position);
                        img_miss.setTag(date);
                        break;
                    }
                }
            }
        });

        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(anim);

        //解决软件盘 "adjust_Pan"在使用时获取焦点的控件下边的View将会被软键盘覆盖
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.showAtLocation(inflate, Gravity.BOTTOM, 0, 0);
    }


    //透明函数
    /*private void backgroundAlpha(float f) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = f;
        getWindow().setAttributes(lp);
    }*/

    @OnClick(R.id.set_clock)
    public void set_clock() {
        /*File file = Utils.saveSong(instance, R.raw.ring_song);
        setRing(instance, RingtoneManager.TYPE_ALARM, file.getAbsolutePath(), "gggg");*/

        //get_Write_Setting();

        //设置闹钟
        setMyAlarm();
    }

    //取消定时任务的执行
    private void cancelAlarm(){
        if(alarmManager != null){
            alarmManager.cancel(pendingIntent);
        }
    }

    //设置定时执行的任务
    private void setMyAlarm() {
        Intent alarm_intent = new Intent(instance, AlarmReceiver.class);
        alarm_intent.setAction(AlarmReceiver.BC_ACTION);
        alarm_intent.putExtra("audio_name", MyApp.Audio_Name);
        pendingIntent = PendingIntent.getBroadcast(instance, 0, alarm_intent, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, date_h);
        ca.set(Calendar.MINUTE, date_min);
        ca.set(Calendar.SECOND, 1);

        //android Api的改变不同版本中设置有所不同
        if (Build.VERSION.SDK_INT < 19) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(), pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, ca.getTimeInMillis(), pendingIntent);
        }

        //更新UI
        reset_view();
    }


    public void set_ring() {
        /*Calendar calendar = Calendar.getInstance();
        //获取系统时间
        int oldhour = calendar.get(Calendar.HOUR_OF_DAY);
        int oldminute = calendar.get(Calendar.MINUTE);

        //这两个是自己设置的闹钟时间
        int minute = oldminute+5;
        int hour = oldhour;

        if (minute >= 60) {
            hour++;
            minute = minute - 60;
        }
        if (hour >= 24) {//我用的手机hour大于24是设置不了闹钟的
            hour = hour - 24;
        }*/

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL);

        //下面设置闹钟时间putextra我用的是第二个参数是int的那个，用string的那个我的会不起作用，就算转换为string类型也不行
        intent.putExtra(AlarmClock.EXTRA_HOUR, date_h);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, date_min);
        //响铃时提示的信息
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.app_name));
        //一个 content: URI，用于指定闹铃使用的铃声，也可指定 VALUE_RINGTONE_SILENT 以不使用铃声。
        //如需使用默认铃声，则无需指定此 extra。
        //根据手机本地路径获取铃声
        /*String packageName = getApplication().getPackageName();
        Uri ringtoneUri = Uri.parse("android.resource://" + packageName + "/raw/bbb");
        Uri ringtoneUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.ring_song);
        intent.putExtra(AlarmClock.EXTRA_RINGTONE, ringtoneUri);*/

        //apk包内文件设置为铃声
        /*File file = Utils.saveSong(instance, R.raw.ring_song);
        Uri ringtoneUri = settingRingertone(file.getAbsolutePath());
        if(ringtoneUri != null){
            intent.putExtra(AlarmClock.EXTRA_RINGTONE, ringtoneUri);
        }*/
        /*if(setAlarmUri != null){
            intent.putExtra(AlarmClock.EXTRA_RINGTONE, setAlarmUri);
        }*/

        //对于一次性闹铃，无需指定此 extra
        /*ArrayList testDays = new ArrayList<>();
        testDays.add(Calendar.MONDAY);//周一
        testDays.add(Calendar.TUESDAY);//周二
        testDays.add(Calendar.FRIDAY);//周五
        intent.putExtra(AlarmClock.EXTRA_DAYS, testDays);*/

        //如果为true，则调用startActivity()不会进入手机的闹钟设置界面
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        //没有这一句会报错，是系统要求添加这一句，//用于指定该闹铃触发时是否振动
        intent.putExtra(AlarmClock.EXTRA_VIBRATE, true);
        startActivity(intent);

        toast(getString(R.string.clock_success));

        /*try {
            RingtoneManager.setActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM, oldAlarm);
            Settings.System.putString(mCr, Settings.System.ALARM_ALERT, oldAlarm.toString());
        } catch (Throwable t) {

        }*/

        reset_view();
    }

    private void reset_view() {
        set_clock.setVisibility(View.GONE);
        set_clock_stop.setVisibility(View.VISIBLE);
        set_clock_stop.playAnimation();
        tx_start.setText(getString(R.string.on_stop));
        tx_start.setTextColor(getResources().getColor(R.color.get_code_cocle_un));

        set_clock_stop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastDownTime = System.currentTimeMillis();
                        mDownX = (int) event.getX();
                        mDownY = (int) event.getY();
                        //开始
                        start_seekbar();
                        break;
                    case MotionEvent.ACTION_UP:
                        long mLastUpTime = System.currentTimeMillis();
                        int mUpX = (int) event.getX();
                        int mUpY = (int) event.getY();
                        int mx = Math.abs(mUpX - mDownX);
                        int my = Math.abs(mUpY - mDownY);

                        if ((mLastUpTime - mLastDownTime) >= MAX_LONG_PRESS_TIME) {
                            //长按超过2s
                            stop_seekbar();
                        } else {
                            pre_down = false;
                            start_back_seekbar();
                        }

                        /*if (mx <= MAX_MOVE_FOR_CLICK && my <= MAX_MOVE_FOR_CLICK) {
                            if((mLastUpTime-mLastDownTime) >= MAX_LONG_PRESS_TIME) {
                                //长按超过2s
                                stop_seekbar();
                            }else{
                                pre_down = false;
                                start_back_seekbar();
                            }
                        } else{
                            //移动了
                            pre_down = false;
                            start_back_seekbar();
                        }*/
                        break;
                }
                return true;
            }
        });
    }

    private void stop_seekbar() {
        pre_down = false;
        lin_seekbar.setVisibility(View.GONE);
        //停止闹铃,停止录音
        if (audioRecordDemo != null) {
            audioRecordDemo.sotp();
        }
        cancelAlarm();
        set_clock.setVisibility(View.VISIBLE);
        set_clock_stop.setVisibility(View.GONE);
        set_clock_stop.pauseAnimation();
        tx_start.setText(getString(R.string.good_deream_11));
        tx_start.setTextColor(getResources().getColor(R.color.white));
    }


    private void start_seekbar() {
        pre_down = true;
        lin_seekbar.setVisibility(View.VISIBLE);
        current_max = 2500;
        seekbar.setMax(current_max);

        current_pro = 0;
        seekbar.setProgress(current_pro);
        Thread thread = new Thread(() -> {
            while (pre_down && current_pro < current_max) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                current_pro += 100;
                int finalJ = current_pro;
                runOnUiThread(() -> seekbar.setProgress(finalJ));
            }
        });
        thread.start();
    }


    private void start_back_seekbar() {
        back_pre_down = true;
        lin_seekbar.setVisibility(View.VISIBLE);
        current_max = 2500;
        seekbar.setMax(current_max);

        seekbar.setProgress(current_pro);
        Thread thread = new Thread(() -> {
            while (back_pre_down && current_pro >= 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                current_pro -= 200;
                int finalJ = current_pro;
                runOnUiThread(() -> seekbar.setProgress(finalJ));

                if (current_pro < 200) {
                    back_pre_down = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lin_seekbar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
        thread.start();
    }


    /**
     *  设置闹钟铃声
     * */
    /*private Uri settingRingertone(String path2) {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        Uri newUri = null;
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path2);
        Cursor cursor = getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[] { path2 },null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            cv.put(MediaStore.Audio.Media.IS_RINGTONE, false);//设置来电铃声为true
            cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);//设置通知铃声为false
            cv.put(MediaStore.Audio.Media.IS_ALARM, true);//设置闹钟铃声为false
            cv.put(MediaStore.Audio.Media.IS_MUSIC, false);
            // 把需要设为铃声的歌曲更新铃声库
            getContentResolver().update(uri, cv, MediaStore.MediaColumns.DATA + "=?",new String[] { path2 });
            newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
            //来电铃声则为： RingtoneManager.TYPE_RINGTONE、通知铃声为：RingtoneManager.TYPE_NOTIFICATION、
            //闹钟铃声为:RingtoneManager.TYPE_ALARM、所有铃声为：RingtoneManager.TYPE_ALL
            RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM, newUri);
            Ringtone rt = RingtoneManager.getRingtone(this, newUri);
            rt.play();
            return newUri;
        }else {
            //insert 这里还有一点问题，故没有写上来
            cv.put(MediaStore.Audio.AudioColumns.DATA,path2);
            newUri = getContentResolver().insert(uri, cv);
            return newUri;
        }
    }*/


    /**
     * 设置铃声
     * <p>
     * type RingtoneManager.TYPE_RINGTONE 来电铃声
     * RingtoneManager.TYPE_NOTIFICATION 通知铃声
     * RingtoneManager.TYPE_ALARM 闹钟铃声
     * <p>
     * 下载下来的mp3全路径
     * title 铃声的名字
     */
    /*@SuppressLint("Range")
    public void setRing(Context context, int type, String path, String title) {

        Uri oldRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE); //系统当前  通知铃声
        Uri oldNotification = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION); //系统当前  通知铃声
        Uri oldAlarm = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM); //系统当前  闹钟铃声

        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, title);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, false);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(sdfile.getAbsolutePath());
        Uri newUri = null;
        String deleteId = "";
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[] { path },null);
            if (cursor.moveToFirst()) {
                deleteId = cursor.getString(cursor.getColumnIndex("_id"));
            }
            //LogTool.e("AGameRing", "deleteId:" + deleteId);

            context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + sdfile.getAbsolutePath() + "\"", null);
            newUri = context.getContentResolver().insert(uri, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (newUri != null) {

            String ringStoneId = "";
            String notificationId = "";
            String alarmId = "";
            if (null != oldRingtoneUri) {
                ringStoneId = oldRingtoneUri.getLastPathSegment();
            }

            if (null != oldNotification) {
                notificationId = oldNotification.getLastPathSegment();
            }

            if (null != oldAlarm) {
                alarmId = oldAlarm.getLastPathSegment();
            }

            Uri setRingStoneUri;
            Uri setNotificationUri;
            Uri setAlarmUri;

            if (type == RingtoneManager.TYPE_RINGTONE || ringStoneId.equals(deleteId)) {
                setRingStoneUri = newUri;
            } else {
                setRingStoneUri = oldRingtoneUri;
            }

            if (type == RingtoneManager.TYPE_NOTIFICATION || notificationId.equals(deleteId)) {
                setNotificationUri = newUri;
            } else {
                setNotificationUri = oldNotification;
            }

            if (type == RingtoneManager.TYPE_ALARM || alarmId.equals(deleteId)) {
                setAlarmUri = newUri;
            } else {
                setAlarmUri = oldAlarm;
            }

            //RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, setRingStoneUri);
            //RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, setNotificationUri);
            RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, setAlarmUri);

            switch (type) {
                case RingtoneManager.TYPE_RINGTONE:
                    Toast.makeText(context.getApplicationContext(), "设置来电铃声成功！", Toast.LENGTH_SHORT).show();
                    break;
                case RingtoneManager.TYPE_NOTIFICATION:
                    Toast.makeText(context.getApplicationContext(), "设置通知铃声成功！", Toast.LENGTH_SHORT).show();
                    break;
                case RingtoneManager.TYPE_ALARM:
                    Toast.makeText(context.getApplicationContext(), "设置闹钟铃声成功！", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }*/
    public void setAlarm() {
        //默认闹铃
        //Uri oldAlarm = RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM); //系统当前  闹钟铃声

        String filename = "ring_song";
        //File file = new File(Environment.getExternalStorageDirectory(), "/Your_Directory_Name");
        File file = new File(instance.getExternalFilesDir(Environment.DIRECTORY_ALARMS), "/My_Directory_Name");
        if (!file.exists()) {
            file.mkdirs();
        }

        //String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Your_Directory_Name";
        String path = instance.getExternalFilesDir(Environment.DIRECTORY_ALARMS).getAbsolutePath() + "/My_Directory_Name";
        File f = new File(path + "/", filename + ".mp3");

        //Uri mUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/raw/" + filename);
        Uri mUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.ring_song);

        ContentResolver mCr = getContext().getContentResolver();
        AssetFileDescriptor soundFile;
        try {
            soundFile = mCr.openAssetFileDescriptor(mUri, "r");
        } catch (FileNotFoundException e) {
            soundFile = null;
        }

        try {
            byte[] readData = new byte[1024];
            FileInputStream fis = soundFile.createInputStream();
            FileOutputStream fos = new FileOutputStream(f);
            int i = fis.read(readData);

            while (i != -1) {
                fos.write(readData, 0, i);
                i = fis.read(readData);
            }

            fos.close();
        } catch (IOException io) {
        }

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, filename);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.MediaColumns.SIZE, f.length());
        values.put(MediaStore.Audio.Media.ARTIST, R.string.app_name);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);


        Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
        getContext().getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + f.getAbsolutePath() + "\"", null);
        Uri newUri = mCr.insert(uri, values);

        try {
            RingtoneManager.setActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM, newUri);
            Settings.System.putString(mCr, Settings.System.ALARM_ALERT, newUri.toString());
            //Toast.makeText(getContext(), "success", Toast.LENGTH_SHORT).show();

            //设置闹铃
            set_ring();
        } catch (Exception t) {
            Log.d("ee", t.toString());
        }
    }

}
