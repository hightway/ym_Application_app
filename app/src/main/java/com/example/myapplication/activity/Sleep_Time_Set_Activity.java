package com.example.myapplication.activity;

import static com.aliyun.player.nativeclass.NativePlayerBase.getContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.ViewTreeObserver;
import android.view.Window;
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
import com.example.myapplication.bean.Audio_Detail_Bean;
import com.example.myapplication.bean.Hor_DateBean;
import com.example.myapplication.bean.Noise_Bean;
import com.example.myapplication.bean.Raw_Bean;
import com.example.myapplication.bean.User_Msg_Bean;
import com.example.myapplication.bean.Video_Detail_Bean;
import com.example.myapplication.bean.White_Noise_Bean;
import com.example.myapplication.custom.AudioRecordDemo;
import com.example.myapplication.custom.DatePickerAdapter;
import com.example.myapplication.custom.DialogFragment;
import com.example.myapplication.custom.ScrollPickerView;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.plmd.Alarm_CallBack;
import com.example.myapplication.plmd.AliPlayer_Noise_Callback;
import com.example.myapplication.plmd.Radio_Click;
import com.example.myapplication.plmd.Radio_Click_Set;
import com.example.myapplication.plmd.White_Noise_Cliack;
import com.example.myapplication.plmd.White_Noise_Cliack_Set;
import com.example.myapplication.plmd.setAlarm_CallBack;
import com.example.myapplication.receiver.AlarmReceiver;
import com.example.myapplication.receiver.TimeClose_Alarm_Receiver;
import com.example.myapplication.tools.Aliyun_Login_Util;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.Login_Util;
import com.example.myapplication.tools.MediaUtil;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.tools.Raw_Util;
import com.example.myapplication.tools.Utils;
import com.example.myapplication.videoplayTool.AppUtil;
import com.example.myapplication.videoplayTool.VideoPlayManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Sleep_Time_Set_Activity extends BaseActivity implements ScrollPickerView.OnItemSelectedListener, Alarm_CallBack, White_Noise_Cliack, Radio_Click {

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
    @BindView(R.id.img_meua)
    ImageView img_meua;
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
    private int MAX_MOVE_FOR_CLICK = 50;// ??????????????????,??????????????????
    private int MAX_LONG_PRESS_TIME = 2500;// ??????????????????,??????????????????
    private boolean pre_down = true;
    private boolean back_pre_down = true;
    private int current_pro;
    private int current_max;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private boolean sleep_monitoring_open;
    private boolean painless_arousal_open;
    private UserConfig userConfig;
    private boolean is_start_getTime;
    private DialogFragment dialogFragment;
    private Audio_CloseReceiver audio_closeReceiver;
    private float current_light;
    private float max_light = 255f;

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


        //?????????????????????????????????
        White_Noise_Cliack_Set.setWhite_Noise_Cliack(this);
        //????????????????????????
        Radio_Click_Set.setRadio_Cliack(this);


        userConfig = UserConfig.instance();
        //??????????????????
        get_Time();

        mDayAdapter = new DatePickerAdapter(0, 23, new DecimalFormat("00"));
        mAdapter = new DatePickerAdapter(0, 59, new DecimalFormat("00"));

        datepicker_year_1.setAdapter(mDayAdapter);
        datepicker_year_2.setAdapter(mAdapter);

        datepicker_year_1.setOnItemSelectedListener(this);
        datepicker_year_2.setOnItemSelectedListener(this);

        if (!TextUtils.isEmpty(userConfig.user_awaken_time)) {
            String[] arr = userConfig.user_awaken_time.split(":");
            if (arr.length == 2) {
                String awaken_time_h = arr[0];
                String awaken_time_min = arr[1];
                if (!TextUtils.isEmpty(awaken_time_h)) {
                    datepicker_year_1.setSelectedPosition(Integer.valueOf(awaken_time_h));
                }
                if (!TextUtils.isEmpty(awaken_time_min)) {
                    datepicker_year_2.setSelectedPosition(Integer.valueOf(awaken_time_min));
                }
            }
        } else {
            datepicker_year_1.setSelectedPosition(t_hour);
            datepicker_year_2.setSelectedPosition(t_minute);
        }

        //??????gif?????????
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
        //????????????????????????????????????(????????????)
        MyApp.Audio_Name = UserConfig.instance().get_RawAudio_Sel(instance);
        setAlarm_CallBack.setAlarm_CallBack(this);

        // ??????????????????
        getScreenBrightness(instance);

        //????????????
        askPermission();

        //??????????????????????????????
        regis_audio_close();
    }

    /**
     * 1.????????????????????????????????? ????????????????????????0-255???
     * **/
    private void getScreenBrightness(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        int defVal = 125;
        int light = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS, defVal);
        current_light = light;
    }

    private void regis_audio_close() {
        IntentFilter intentFilter = new IntentFilter("audio_close");
        audio_closeReceiver = new Audio_CloseReceiver();
        registerReceiver(audio_closeReceiver, intentFilter);
    }

    public class Audio_CloseReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            stop_noise_play();
        }
    }

    private void askPermission() {
        boolean isAllGranted = checkPermissionAllGranted(
                new String[]{
                        Manifest.permission.RECORD_AUDIO
                }
        );

        // ????????????????????????, ???????????????????????????
        if (isAllGranted) {
            //???????????????????????????????????????

        } else {
            // ????????????????????????, ????????????????????????????????????????????????????????????
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.RECORD_AUDIO
                    }, MY_PERMISSION_REQUEST_CODE
            );
        }
    }

    /**
     * ???????????????????????????????????????
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // ????????????????????????????????????, ??????????????? false
                return false;
            }
        }
        return true;
    }

    /**
     * ??? 3 ???: ??????????????????????????????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // ?????????????????????????????????????????????
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // ?????????????????????????????????, ?????????????????????

            } else {
                // ????????????????????????????????????????????????, ???????????????????????????????????????????????????????????????
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
                // ???????????????????????????
                //????????????
                setAlarm();
            }
        }
    }

    /**
     * ?????? APP ???????????????
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(instance, R.style.MyAlertButton);
        builder.setMessage("??????????????????????????????????????????????????? ??????????????? -> ????????? ????????????");
        builder.setPositiveButton("????????????", new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton("??????", null);
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
        // ??????????????????
        Calendar c = Calendar.getInstance();
        // ????????????????????????int???
        t_hour = c.get(Calendar.HOUR_OF_DAY);
        t_minute = c.get(Calendar.MINUTE);
    }


    @OnClick(R.id.tx_set_time)
    public void tx_set_time() {
        get_Time();
        String time_get = Utils.transfom_time(t_hour, t_minute, date_h, date_min);

        rel_time.setVisibility(View.GONE);
        tx_set_time.setVisibility(View.GONE);
        rel_data.setVisibility(View.VISIBLE);
        tx_time_num.setText(time_get + getString(R.string.call_up));

        String hours;
        if (date_h < 10) {
            hours = "0" + date_h;
        } else {
            hours = String.valueOf(date_h);
        }

        String min;
        if (date_min < 10) {
            min = "0" + date_min;
        } else {
            min = String.valueOf(date_min);
        }
        tx_clock_time.setText(hours + ":" + min);
        set_clock.setEnabled(true);

        start_getTime();
    }

    private void start_getTime() {
        is_start_getTime = true;
        Thread thread = new Thread(() -> {
            while (is_start_getTime) {
                try {
                    Thread.sleep(20 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                get_Time();
                String time_get = Utils.transfom_time(t_hour, t_minute, date_h, date_min);
                runOnUiThread(() -> tx_time_num.setText(time_get + getString(R.string.call_up)));
            }
        });
        thread.start();
    }

    /**
     * ??????????????????
     */
    public void stop_getTime() {
        is_start_getTime = false;
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
        stop_noise_play();
        //?????????????????????????????????
        White_Noise_Cliack_Set.setWhite_Noise_Cliack(null);
        //????????????????????????
        Radio_Click_Set.setRadio_Cliack(null);
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
        //?????????????????????????????????
        White_Noise_Cliack_Set.setWhite_Noise_Cliack(null);
        //????????????????????????
        Radio_Click_Set.setRadio_Cliack(null);
        //????????????
        stop_noise_play();
        //?????????????????????
        stop_getTime();

        if (audio_closeReceiver != null) {
            //????????????
            unregisterReceiver(audio_closeReceiver);
        }
    }

    @Override
    public void onItemSelected(View view, int position) {
        switch (view.getId()) {
            case R.id.datepicker_year_1: {
                date_h = mDayAdapter.getDate(position);
                //resetMaxDay();//????????????????????????????????????????????????
                break;
            }
            case R.id.datepicker_year_2: {
                date_min = mAdapter.getDate(position);
                //resetMaxDay();//????????????????????????????????????????????????
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
        //showpop
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        d_height = d.getHeight();
        getPopupWindow(instance, R.style.keyboard);

        /*Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "??????????????????");
        startActivityForResult(intent, 2);*/
    }


    public void getPopupWindow(Activity mContext, int anim) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_sleep_time, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int h = d_height / 5 * 2;
        popupWindow.setHeight(h);

        ImageView img_sleep = inflate.findViewById(R.id.img_sleep);
        ImageView img_rouse = inflate.findViewById(R.id.img_rouse);
        if (!TextUtils.isEmpty(userConfig.user_sleep_monitoring) && userConfig.user_sleep_monitoring.equals("1")) {
            img_sleep.setImageResource(R.mipmap.ic_switch_open);
            sleep_monitoring_open = true;
        } else {
            img_sleep.setImageResource(R.mipmap.ic_switch_close);
            sleep_monitoring_open = false;
        }
        img_sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sleep_monitoring_open) {
                    img_sleep.setImageResource(R.mipmap.ic_switch_close);
                } else {
                    img_sleep.setImageResource(R.mipmap.ic_switch_open);
                }
                sleep_monitoring_open = !sleep_monitoring_open;
            }
        });

        if (!TextUtils.isEmpty(userConfig.user_painless_arousal) && userConfig.user_painless_arousal.equals("1")) {
            img_rouse.setImageResource(R.mipmap.ic_switch_open);
            painless_arousal_open = true;
        } else {
            img_rouse.setImageResource(R.mipmap.ic_switch_close);
            painless_arousal_open = false;
        }
        img_rouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (painless_arousal_open) {
                    img_rouse.setImageResource(R.mipmap.ic_switch_close);
                } else {
                    img_rouse.setImageResource(R.mipmap.ic_switch_open);
                }
                painless_arousal_open = !painless_arousal_open;
            }
        });

        TextView tx_timed_close = inflate.findViewById(R.id.tx_timed_close);
        TextView tx_delay = inflate.findViewById(R.id.tx_delay);
        tx_timed_close.setText(userConfig.user_timed_close + getString(R.string.time_min));
        tx_timed_close.setTag(userConfig.user_timed_close);
        tx_delay.setText(userConfig.user_delay + getString(R.string.time_min));
        tx_delay.setTag(userConfig.user_delay);

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
                show_PopupWindow_Close(instance, R.style.keyboard, tx_timed_close);
            }
        });

        RelativeLayout rel_sleep = inflate.findViewById(R.id.rel_sleep);
        rel_sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_PopupWindow_Sleep(instance, R.style.keyboard, tx_delay);
            }
        });

        RelativeLayout rel_audio_list = inflate.findViewById(R.id.rel_audio_list);
        rel_audio_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Raw_Bean> list = Raw_Util.getRaw_List();
                if (list != null && list.size() > 0) {
                    show_RawList_PopupWindow(mContext, anim, list, tx_raw_name);
                } else {
                    toast(getString(R.string.raw_no));
                }
            }
        });

        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(anim);

        //??????????????? "adjust_Pan"??????????????????????????????????????????View????????????????????????
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        //popupWindow.showAtLocation(inflate, Gravity.NO_GRAVITY, 0, d_height / 2);
        popupWindow.showAtLocation(tx_start, Gravity.BOTTOM, 0, 0);
    }


    private void post_edit_info(boolean sleep_monitoring_open, boolean painless_arousal_open, String time_close, String time_delay, PopupWindow popupWindow) {
        String new_sleep = sleep_monitoring_open ? "1" : "0";
        String new_painless = painless_arousal_open ? "1" : "0";

        if (!TextUtils.isEmpty(userConfig.user_sleep_monitoring) && userConfig.user_sleep_monitoring.equals(new_sleep) &&
                !TextUtils.isEmpty(userConfig.user_painless_arousal) && userConfig.user_painless_arousal.equals(new_painless) &&
                !TextUtils.isEmpty(userConfig.user_timed_close) && userConfig.user_timed_close.equals(time_close) &&
                !TextUtils.isEmpty(userConfig.user_delay) && userConfig.user_delay.equals(time_delay)) {

            popupWindow.dismiss();
            return;
        }

        DialogUtils.getInstance().showDialog(this, "?????????...");
        HashMap<String, String> map = new HashMap<>();
        map.put("sleep_monitoring", new_sleep);
        map.put("painless_arousal", new_painless);
        if (!TextUtils.isEmpty(time_close)) {
            map.put("timed_close", time_close);
        }
        if (!TextUtils.isEmpty(time_delay)) {
            map.put("delay", time_delay);
        }
        OkHttpUtil.postRequest(Api.HEAD + "user/edit_info", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("????????????");

            }

            @Override
            public void un_login_err() {
                //?????????

            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    toast(jsonObject.getString("errMsg"));
                    if (code == 200) {
                        popupWindow.dismiss();

                        //?????????????????????
                        UserConfig.instance().user_sleep_monitoring = new_sleep;
                        UserConfig.instance().user_painless_arousal = new_painless;
                        if (!TextUtils.isEmpty(time_close)) {
                            UserConfig.instance().user_timed_close = time_close;
                        }
                        if (!TextUtils.isEmpty(time_delay)) {
                            UserConfig.instance().user_delay = time_delay;
                        }
                        userConfig.saveUserConfig(instance);
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

        int index_sel = -1;
        if(!TextUtils.isEmpty(MyApp.Audio_Name)){
            for(Raw_Bean raw_bean : list){
                if(MyApp.Audio_Name.equals(raw_bean.getRawName())){
                    index_sel = list.indexOf(raw_bean);
                    break;
                }
            }
        }

        ImageView img_miss = inflate.findViewById(R.id.tx_ok);

        RecyclerView recycle_raw = inflate.findViewById(R.id.recycle_raw);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        recycle_raw.setLayoutManager(layoutManager);
        More_mp3_Adapter listAdapter = new More_mp3_Adapter(mContext, list, index_sel);
        recycle_raw.setAdapter(listAdapter);
        listAdapter.setRaw_OnClick_CallBack(new More_mp3_Adapter.Raw_OnClick_CallBack() {
            @Override
            public void Raw_click(int pos) {
                listAdapter.sel_pos(pos, false);
                Raw_Bean raw_bean = list.get(pos);
                if (raw_bean != null) {
                    //img_miss.setTag(raw_bean);
                    //????????????
                    MediaUtil.stopRing();
                    //????????????
                    MediaPlayer mMediaPlayer = MediaUtil.playRing(mContext, raw_bean.getRawName());
                    if(mMediaPlayer != null){
                        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mediaPlayer) {
                                if(listAdapter != null){
                                    listAdapter.stop_music(true);
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void sel_Raw_click(int pos) {
                Raw_Bean raw_bean = list.get(pos);
                if (raw_bean != null) {
                    MyApp.Audio_Name = raw_bean.getRawName();
                    MyApp.Audio_Uri = raw_bean.getUri();
                    tx_raw_name.setText(raw_bean.getRawName());

                    //???????????????
                    UserConfig.instance().save_RawAudio_Sel(instance, MyApp.Audio_Name);
                    listAdapter.set_sel(pos);
                }
            }
        });

        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //????????????
                MediaUtil.stopRing();
                listAdapter.stop_all();

                popupWindow.dismiss();
                /*Raw_Bean raw_bean = (Raw_Bean) view.getTag();
                if (raw_bean != null) {
                    //????????????
                    MediaUtil.stopRing();
                    listAdapter.stop_all();

                    MyApp.Audio_Name = raw_bean.getRawName();
                    MyApp.Audio_Uri = raw_bean.getUri();
                    tx_raw_name.setText(raw_bean.getRawName());
                    //tx_raw_name.setTag(raw_bean.getUri());

                    //???????????????
                    UserConfig.instance().save_RawAudio_Sel(instance, MyApp.Audio_Name);
                    popupWindow.dismiss();
                } else {
                    toast(getString(R.string.sleep_tip_12));
                }*/
            }
        });

        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(anim);

        //??????????????? "adjust_Pan"??????????????????????????????????????????View????????????????????????
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.showAtLocation(tx_start, Gravity.BOTTOM, 0, 0);
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
         * ?????????????????????????????????????????????true????????????????????????false????????????????????????
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        seekbar_time.setMax(480);
        seekbar_time.setMin(1);

        String timed_close = (String) tx_timed_close.getTag();
        int time_sel = 1;
        if (!TextUtils.isEmpty(timed_close)) {
            int t = Integer.valueOf(timed_close);
            if (t > 0) {
                time_sel = t;
            } else {
                time_sel = 1;
            }
        }
        seekbar_time.setProgress(time_sel);
        tv_progress.setText(Utils.transfom_min(time_sel));

        int finalTime_sel = time_sel;
        tv_progress.postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams rel_params = (RelativeLayout.LayoutParams) tv_progress.getLayoutParams();
                int wid = seekbar_time.getWidth();
                float left = finalTime_sel * wid / 480;
                if (left <= 40) {
                    rel_params.leftMargin = 40;
                } else if (left >= (wid - 40)) {
                    rel_params.leftMargin = wid - 40;
                } else {
                    rel_params.leftMargin = (int) left;
                }
                tv_progress.setLayoutParams(rel_params);
            }
        }, 300);

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

        //??????????????? "adjust_Pan"??????????????????????????????????????????View????????????????????????
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.showAtLocation(tx_start, Gravity.BOTTOM, 0, 0);
    }

    private void show_PopupWindow_Sleep(Activity mContext, int anim, TextView tx_delay) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_sleep, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int h = d_height / 5 * 2;
        popupWindow.setHeight(h);

        int delay_t = 1;
        String delay_time = (String) tx_delay.getTag();
        if (delay_time != null) {
            int t = Integer.valueOf(delay_time);
            if (t > 0) {
                delay_t = t;
            } else {
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
        datepicker_close.setSelectedPosition(delay_t - 1);
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

        //??????????????? "adjust_Pan"??????????????????????????????????????????View????????????????????????
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.showAtLocation(tx_start, Gravity.BOTTOM, 0, 0);
    }


    //????????????
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

        //????????????????????????????????????????????????????????????????????????
        /*get_Time();
        Utils.transfom_time_long(t_hour, t_minute, date_h, date_min);*/

        //????????????????????????
        String time = tx_clock_time.getText().toString().trim();
        set_awaken_time(time);
        //????????????
        setMyAlarm(time, false);
        //??????????????????
        audioRecordDemo = new AudioRecordDemo(Sleep_Time_Set_Activity.this);
        audioRecordDemo.getNoiseLevel();
    }


    private void set_awaken_time(String awaken_time) {
        if (TextUtils.isEmpty(awaken_time)) {
            return;
        }
        if (!TextUtils.isEmpty(userConfig.user_awaken_time) && userConfig.user_awaken_time.equals(awaken_time)) {
            return;
        }

        //DialogUtils.getInstance().showDialog(this, "?????????...");
        HashMap<String, String> map = new HashMap<>();
        map.put("awaken_time", awaken_time);
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "user/edit_info", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("????????????");

            }

            @Override
            public void un_login_err() {
                //?????????

            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        //?????????????????????
                        UserConfig.instance().user_awaken_time = awaken_time;
                        userConfig.saveUserConfig(instance);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    //???????????????????????????
    private void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

    //???????????????????????????
    private void setMyAlarm(String alarm_time, boolean isdelay) {
        Intent alarm_intent = new Intent(instance, AlarmReceiver.class);
        alarm_intent.setAction(AlarmReceiver.BC_ACTION);
        alarm_intent.putExtra("alarm_time", alarm_time);
        alarm_intent.putExtra("alarm_light", current_light);
        pendingIntent = PendingIntent.getBroadcast(instance, 0, alarm_intent, 0);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        /*Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, date_h);
        ca.set(Calendar.MINUTE, date_min);
        ca.set(Calendar.SECOND, 0);

        long tt = ca.getTimeInMillis();
        long time = System.currentTimeMillis();//????????????????????????
        if(tt <= time){
            int h = 0;
            int m = 0;
            if(!TextUtils.isEmpty(Utils.time_h)){
                h = Integer.valueOf(Utils.time_h);
            }
            if(!TextUtils.isEmpty(Utils.time_m)){
                m = Integer.valueOf(Utils.time_m);
            }

            if(h > 0){
                int h_mill = h*60*60*1000;
                tt += h_mill;
            }
            if(m > 0){
                int m_mill = m*60*1000;
                tt += m_mill;
            }
        }*/

        long time = System.currentTimeMillis();//????????????????????????
        int h = 0;
        int m = 0;
        if (!TextUtils.isEmpty(Utils.get_time_h())) {
            h = Integer.valueOf(Utils.get_time_h());
        }
        if (!TextUtils.isEmpty(Utils.get_time_m())) {
            m = Integer.valueOf(Utils.get_time_m());
        }
        if (h > 0) {
            int h_mill = h * 60 * 60 * 1000;
            time += h_mill;
        }
        if (m > 0) {
            int m_mill = m * 60 * 1000;
            time += m_mill;
        }

        //????????????
        long ss = time % (1000 * 60);
        time -= ss;

        //android Api??????????????????????????????????????????
        if (Build.VERSION.SDK_INT < 19) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }

        //??????????????????
        changeAppBrightness(10);

        if (!isdelay) {
            //??????UI
            reset_view();
        }
    }

    /*private void setAppBrightness(float num) {
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = num;
        window.setAttributes(lp);
    }*/

    // ???????????????????????????window??????
    public void changeAppBrightness(int brightness) {
        if(brightness < 10){
            return;
        }
        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / max_light;
        }
        window.setAttributes(lp);
    }



    /*public String getTime_mill(){
        long time=System.currentTimeMillis()/1000;//?????????????????????10???????????????
        String str=String.valueOf(time);
        return str;
    }
    public static void timetodate(String time) {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
        Date date = new Date(currentTime);
        String t = formatter.format(date);

        SimpleDateFormat formatter_1 = new SimpleDateFormat("yyyy-MM-dd HH-mm");
        Date date_1 = new Date(currentTime);
        String tt = formatter_1.format(date_1);

        dateToStamp(tt);
    }
    public static String dateToStamp(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }*/


    public void set_ring() {
        /*Calendar calendar = Calendar.getInstance();
        //??????????????????
        int oldhour = calendar.get(Calendar.HOUR_OF_DAY);
        int oldminute = calendar.get(Calendar.MINUTE);

        //???????????????????????????????????????
        int minute = oldminute+5;
        int hour = oldhour;

        if (minute >= 60) {
            hour++;
            minute = minute - 60;
        }
        if (hour >= 24) {//???????????????hour??????24????????????????????????
            hour = hour - 24;
        }*/

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL);

        //????????????????????????putextra??????????????????????????????int???????????????string????????????????????????????????????????????????string???????????????
        intent.putExtra(AlarmClock.EXTRA_HOUR, date_h);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, date_min);
        //????????????????????????
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, getString(R.string.app_name));
        //?????? content: URI??????????????????????????????????????????????????? VALUE_RINGTONE_SILENT ?????????????????????
        //????????????????????????????????????????????? extra???
        //????????????????????????????????????
        /*String packageName = getApplication().getPackageName();
        Uri ringtoneUri = Uri.parse("android.resource://" + packageName + "/raw/bbb");
        Uri ringtoneUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.ring_song);
        intent.putExtra(AlarmClock.EXTRA_RINGTONE, ringtoneUri);*/

        //apk???????????????????????????
        /*File file = Utils.saveSong(instance, R.raw.ring_song);
        Uri ringtoneUri = settingRingertone(file.getAbsolutePath());
        if(ringtoneUri != null){
            intent.putExtra(AlarmClock.EXTRA_RINGTONE, ringtoneUri);
        }*/
        /*if(setAlarmUri != null){
            intent.putExtra(AlarmClock.EXTRA_RINGTONE, setAlarmUri);
        }*/

        //??????????????????????????????????????? extra
        /*ArrayList testDays = new ArrayList<>();
        testDays.add(Calendar.MONDAY);//??????
        testDays.add(Calendar.TUESDAY);//??????
        testDays.add(Calendar.FRIDAY);//??????
        intent.putExtra(AlarmClock.EXTRA_DAYS, testDays);*/

        //?????????true????????????startActivity()???????????????????????????????????????
        intent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
        //????????????????????????????????????????????????????????????//??????????????????????????????????????????
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
                        //??????
                        start_seekbar();
                        break;
                    case MotionEvent.ACTION_UP:
                        long mLastUpTime = System.currentTimeMillis();
                        int mUpX = (int) event.getX();
                        int mUpY = (int) event.getY();
                        int mx = Math.abs(mUpX - mDownX);
                        int my = Math.abs(mUpY - mDownY);

                        if ((mLastUpTime - mLastDownTime) >= MAX_LONG_PRESS_TIME) {
                            //????????????2s
                            stop_seekbar();
                        } else {
                            pre_down = false;
                            start_back_seekbar();
                        }

                        /*if (mx <= MAX_MOVE_FOR_CLICK && my <= MAX_MOVE_FOR_CLICK) {
                            if((mLastUpTime-mLastDownTime) >= MAX_LONG_PRESS_TIME) {
                                //????????????2s
                                stop_seekbar();
                            }else{
                                pre_down = false;
                                start_back_seekbar();
                            }
                        } else{
                            //?????????
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
        //????????????
        if (audioRecordDemo != null) {
            audioRecordDemo.sotp();
        }
        //????????????
        cancelAlarm();
        changeAppBrightness((int)current_light);
        //?????????????????????
        stop_getTime();
        set_clock.setVisibility(View.VISIBLE);
        set_clock_stop.setVisibility(View.GONE);
        set_clock_stop.pauseAnimation();
        tx_start.setText(getString(R.string.good_deream_11));
        tx_start.setTextColor(getResources().getColor(R.color.white));

        rel_time.setVisibility(View.VISIBLE);
        tx_set_time.setVisibility(View.VISIBLE);
        rel_data.setVisibility(View.GONE);
        toast(getString(R.string.sleep_tip_13));
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
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                current_pro += 10;
                int finalJ = current_pro;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekbar.setProgress(finalJ);

                        //??????????????????
                        float data = finalJ/2500f*current_light;
                        changeAppBrightness((int)data);
                    }
                });
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
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                current_pro -= 40;
                int finalJ = current_pro;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        seekbar.setProgress(finalJ);

                        //??????????????????
                        float data = finalJ/2500f*current_light;
                        changeAppBrightness((int)data);
                    }
                });

                if (current_pro < 40) {
                    back_pre_down = false;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lin_seekbar.setVisibility(View.GONE);
                            changeAppBrightness(10);
                        }
                    });
                }
            }
        });
        thread.start();
    }


    /**
     *  ??????????????????
     * */
    /*private Uri settingRingertone(String path2) {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        Uri newUri = null;
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path2);
        Cursor cursor = getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[] { path2 },null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            cv.put(MediaStore.Audio.Media.IS_RINGTONE, false);//?????????????????????true
            cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);//?????????????????????false
            cv.put(MediaStore.Audio.Media.IS_ALARM, true);//?????????????????????false
            cv.put(MediaStore.Audio.Media.IS_MUSIC, false);
            // ?????????????????????????????????????????????
            getContentResolver().update(uri, cv, MediaStore.MediaColumns.DATA + "=?",new String[] { path2 });
            newUri = ContentUris.withAppendedId(uri, Long.valueOf(_id));
            //????????????????????? RingtoneManager.TYPE_RINGTONE?????????????????????RingtoneManager.TYPE_NOTIFICATION???
            //???????????????:RingtoneManager.TYPE_ALARM?????????????????????RingtoneManager.TYPE_ALL
            RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM, newUri);
            Ringtone rt = RingtoneManager.getRingtone(this, newUri);
            rt.play();
            return newUri;
        }else {
            //insert ?????????????????????????????????????????????
            cv.put(MediaStore.Audio.AudioColumns.DATA,path2);
            newUri = getContentResolver().insert(uri, cv);
            return newUri;
        }
    }*/


    /**
     * ????????????
     * <p>
     * type RingtoneManager.TYPE_RINGTONE ????????????
     * RingtoneManager.TYPE_NOTIFICATION ????????????
     * RingtoneManager.TYPE_ALARM ????????????
     * <p>
     * ???????????????mp3?????????
     * title ???????????????
     */
    /*@SuppressLint("Range")
    public void setRing(Context context, int type, String path, String title) {

        Uri oldRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE); //????????????  ????????????
        Uri oldNotification = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION); //????????????  ????????????
        Uri oldAlarm = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM); //????????????  ????????????

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
                    Toast.makeText(context.getApplicationContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                case RingtoneManager.TYPE_NOTIFICATION:
                    Toast.makeText(context.getApplicationContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                    break;
                case RingtoneManager.TYPE_ALARM:
                    Toast.makeText(context.getApplicationContext(), "???????????????????????????", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }*/
    public void setAlarm() {
        //????????????
        //Uri oldAlarm = RingtoneManager.getActualDefaultRingtoneUri(getContext(), RingtoneManager.TYPE_ALARM); //????????????  ????????????

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

            //????????????
            set_ring();
        } catch (Exception t) {
            Log.d("ee", t.toString());
        }
    }


    @Override
    public void Alarm_Stop() {
        //??????????????????
        stop_getTime();
        //????????????????????????
        if (audioRecordDemo != null) {
            audioRecordDemo.sotp();
        }
        //????????????
        cancelAlarm();
        changeAppBrightness((int)current_light);

        set_clock.setVisibility(View.VISIBLE);
        set_clock_stop.setVisibility(View.GONE);
        set_clock_stop.pauseAnimation();
        tx_start.setText(getString(R.string.good_deream_11));
        tx_start.setTextColor(getResources().getColor(R.color.white));

        rel_time.setVisibility(View.VISIBLE);
        tx_set_time.setVisibility(View.VISIBLE);
        rel_data.setVisibility(View.GONE);

        //????????????????????????
        //startActivity(new Intent(instance, Sleep_Talk_Recorder.class));
    }

    @Override
    public void Alarm_delay() {
        //??????????????????
        stop_getTime();
        //????????????
        cancelAlarm();

        //???????????????????????????
        if (!TextUtils.isEmpty(userConfig.user_delay)) {
            int add_min = Integer.valueOf(userConfig.user_delay);
            int new_min = date_min += add_min;
            if (new_min >= 60) {
                date_h++;
                date_min = new_min - 60;
                if (date_h >= 24) {
                    date_h -= 24;
                }
            } else {
                date_min = new_min;
            }
            tx_set_time();

            mHandle.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //??????????????????
                    String time = tx_clock_time.getText().toString().trim();
                    //??????????????????
                    setMyAlarm(time, true);
                }
            }, 300);
        }
    }


    @OnClick(R.id.img_meua)
    public void img_meua() {
        dialogFragment = new DialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "ss");
    }


    @Override
    public void audio_click(Hor_DateBean.DataBean.ListBean dataBean) {
        if (dataBean == null) {
            return;
        }
        getradio_station_detail(dataBean.id);
    }

    @Override
    public void noise_click(White_Noise_Bean.DataBean dataBean) {
        if (dataBean == null) {
            return;
        }
        getwhite_noise_detail(dataBean.id);
    }

    /**
     * ??????????????????
     */
    private void getradio_station_detail(int id) {
        DialogUtils.getInstance().showDialog(this, "?????????...");
        HashMap<String, String> map = new HashMap<>();
        map.put("detail_id", String.valueOf(id));
        OkHttpUtil.postRequest(Api.HEAD + "radio_station/detail", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        Audio_Detail_Bean audio_detail_bean = mGson.fromJson(response, Audio_Detail_Bean.class);
                        Audio_Detail_Bean.DataBean dataBean = audio_detail_bean.data;
                        if (dataBean != null) {
                            //??????url
                            String resource_url = dataBean.resource_url;
                            if (!TextUtils.isEmpty(resource_url)) {
                                stop_noise_play();
                                start_noise_play(resource_url);

                                if (dialogFragment != null) {
                                    dialogFragment.set_dismiss();
                                    dialogFragment.dismiss();
                                }
                            }
                        }
                    } else {
                        toast(jsonObject.getString("errMsg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * ?????????????????????
     */
    private void getwhite_noise_detail(int id) {
        DialogUtils.getInstance().showDialog(this, "?????????...");
        HashMap<String, String> map = new HashMap<>();
        map.put("detail_id", String.valueOf(id));
        OkHttpUtil.postRequest(Api.HEAD + "white_noise/detail", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        Noise_Bean video_detail_bean = mGson.fromJson(response, Noise_Bean.class);
                        Noise_Bean.DataBean dataBean = video_detail_bean.data;
                        if (dataBean != null) {
                            //??????url
                            String resource_url = dataBean.resource_url;
                            if (!TextUtils.isEmpty(resource_url)) {
                                stop_noise_play();
                                start_noise_play(resource_url);

                                if (dialogFragment != null) {
                                    dialogFragment.dismiss();
                                }
                            }
                        }
                    } else {
                        toast(jsonObject.getString("errMsg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void start_noise_play(String url) {
        //?????????????????????
        Utils.init_Aliyun(MyApp.get_app_mAliPlayer(), url);
        if (!TextUtils.isEmpty(userConfig.user_timed_close)) {
            int num = Integer.valueOf(userConfig.user_timed_close);
            if (num > 0) {
                init_TimeClose_Alarm(num);
            }
        }
    }

    private void init_TimeClose_Alarm(int num) {
        //???????????????intent???TimeDemoActivity????????????????????????
        Intent intent = new Intent(instance, TimeClose_Alarm_Receiver.class);
        intent.setAction(TimeClose_Alarm_Receiver.BC_ACTION);
        long time = System.currentTimeMillis() + (num * 60 * 1000); //??????????????????????????????????????????
        //??????PendingIntent?????????intent
        PendingIntent pi = PendingIntent.getBroadcast(instance, 0, intent, 0);
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //?????????AlarmManager???????????????????????????PendingIntent
        //android Api??????????????????????????????????????????
        if (Build.VERSION.SDK_INT < 19) {
            manager.set(AlarmManager.RTC_WAKEUP, time, pi);
        } else {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, pi);
        }
    }

    /**
     * ??????????????????
     */
    private void stop_noise_play() {
        if (MyApp.app_mAliPlayer != null) {
            MyApp.app_mAliPlayer.release();
            MyApp.app_mAliPlayer = null;
        }
    }
}
