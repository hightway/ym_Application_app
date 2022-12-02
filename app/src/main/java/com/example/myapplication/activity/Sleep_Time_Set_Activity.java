package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.custom.AudioRecordDemo;
import com.example.myapplication.custom.DatePickerAdapter;
import com.example.myapplication.custom.ScrollPickerView;
import com.example.myapplication.tools.Utils;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;

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
    @BindView(R.id.tx_db)
    public
    TextView tx_db;
    @BindView(R.id.tx_noise)
    public
    TextView tx_noise;
    @BindView(R.id.set_clock)
    ImageView set_clock;

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
    public void tx_set_time(){
        get_Time();
        String time = Utils.transfom_time(t_hour, t_minute, date_h, date_min);
        //String time = Utils.transfom_time(get_Time(), date_h+":"+date_min);

        rel_time.setVisibility(View.GONE);
        tx_set_time.setVisibility(View.GONE);
        rel_data.setVisibility(View.VISIBLE);
        tx_time_num.setText(time + getString(R.string.call_up));
        if(date_min<10){
            String min = "0" + date_min;
            tx_clock_time.setText(date_h + ":" + min);
        }else{
            tx_clock_time.setText(date_h + ":" + date_min);
        }

        //获取环境分贝
        audioRecordDemo = new AudioRecordDemo(Sleep_Time_Set_Activity.this);
        audioRecordDemo.getNoiseLevel();
    }


    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.query_button)
    public void query_button() {
        if(audioRecordDemo != null){
            audioRecordDemo.sotp();
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(audioRecordDemo != null){
            audioRecordDemo.sotp();
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
            case R.id.datepicker_close: {
                int date = close_adapter.getDate(position);

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
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        d_height = d.getHeight();
        getPopupWindow(instance, null, 0, 0, R.style.showPopupAnimation);

        /*Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置闹钟铃声");
        startActivityForResult(intent, 2);*/
    }


    public void getPopupWindow(Activity mContext, View view, int xOff, int yOff, int anim) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_sleep_time, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int h = d_height/5*2;
        popupWindow.setHeight(h);
        //点击非菜单部分退出
        /*inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });*/

        TextView img_miss = inflate.findViewById(R.id.tx_ok);
        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        RelativeLayout rel_close = inflate.findViewById(R.id.rel_close);
        rel_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_PopupWindow_Close(instance, R.style.showPopupAnimation);
            }
        });

        RelativeLayout rel_sleep = inflate.findViewById(R.id.rel_sleep);
        rel_sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                show_PopupWindow_Sleep(instance, R.style.showPopupAnimation);
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


    @SuppressLint("NewApi")
    private void show_PopupWindow_Close(Activity mContext, int anim) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_close, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int h = d_height/5*2;
        popupWindow.setHeight(h);

        TextView img_miss = inflate.findViewById(R.id.tx_ok);
        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        /**
         * 在进度变更时触发。第三个参数为true表示用户拖动，为false表示代码设置进度
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        TextView tv_progress = inflate.findViewById(R.id.tv_progress);
        SeekBar seekbar = inflate.findViewById(R.id.seekbar);
        seekbar.setMax(480);
        seekbar.setMin(1);
        seekbar.setProgress(5);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tv_progress.getLayoutParams();
                int w = seekbar.getWidth();
                float left = progress*w/480;
                if(left <= 40){
                    params.leftMargin = 40;
                }else if(left >= (w-40)){
                    params.leftMargin = w-40;
                }else{
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

    private void show_PopupWindow_Sleep(Activity mContext, int anim) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_sleep, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        int h = d_height/5*2;
        popupWindow.setHeight(h);

        TextView img_miss = inflate.findViewById(R.id.tx_ok);
        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        datepicker_close = inflate.findViewById(R.id.datepicker_close);
        close_adapter = new DatePickerAdapter(1, 10, new DecimalFormat("0"));
        datepicker_close.setAdapter(close_adapter);
        datepicker_close.setSelectedPosition(4);
        datepicker_close.setOnItemSelectedListener(this);

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
    public void set_clock(){
        File file = Utils.saveSong(instance, R.raw.ring_song);
        setRing(instance, RingtoneManager.TYPE_ALARM, file.getPath(), "的士速递sss");
        set_ring();
    }

    private void set_ring() {
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
        //intent.putExtra(AlarmClock.EXTRA_MESSAGE, "哈哈哈哈哈");
        //一个 content: URI，用于指定闹铃使用的铃声，也可指定 VALUE_RINGTONE_SILENT 以不使用铃声。
        //如需使用默认铃声，则无需指定此 extra。
        //根据手机本地路径获取铃声
        /*String packageName = getApplication().getPackageName();
        //Uri ringtoneUri = Uri.parse("android.resource://" + packageName + "/raw/ring_song.mp3");
        Uri ringtoneUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.ring_song);
        intent.putExtra(AlarmClock.EXTRA_RINGTONE, ringtoneUri);*/

        //apk包内文件设置为铃声
        /*File file = Utils.saveSong(instance, R.raw.ring_song);
        Uri ringtoneUri = settingRingertone(file.getAbsolutePath());
        if(ringtoneUri != null){
            intent.putExtra(AlarmClock.EXTRA_RINGTONE, ringtoneUri);
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
    }



    /**
     *  设置闹钟铃声
     * */
    private Uri settingRingertone(String path2) {
        // TODO Auto-generated method stub
        ContentValues cv = new ContentValues();
        Uri newUri = null;
        Uri uri = MediaStore.Audio.Media.getContentUriForPath(path2);
        Cursor cursor = getContentResolver().query(uri, null, MediaStore.MediaColumns.DATA + "=?", new String[] { path2 },null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            String _id = cursor.getString(0);
            cv.put(MediaStore.Audio.Media.IS_RINGTONE, true);//设置来电铃声为true
            cv.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);//设置通知铃声为false
            cv.put(MediaStore.Audio.Media.IS_ALARM, false);//设置闹钟铃声为false
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
    }


    /**
     *
     * 设置铃声
     *
     *              type RingtoneManager.TYPE_RINGTONE 来电铃声
     *             RingtoneManager.TYPE_NOTIFICATION 通知铃声
     *             RingtoneManager.TYPE_ALARM 闹钟铃声
     *
     * 下载下来的mp3全路径
     * title 铃声的名字
     */
    @SuppressLint("Range")
    public static void setRing(Context context, int type, String path, String title) {

        Uri oldRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE); //系统当前  通知铃声
        Uri oldNotification = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION); //系统当前  通知铃声
        Uri oldAlarm = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM); //系统当前  闹钟铃声

        File sdfile = new File(path);
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, sdfile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, title);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);

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
    }

}
