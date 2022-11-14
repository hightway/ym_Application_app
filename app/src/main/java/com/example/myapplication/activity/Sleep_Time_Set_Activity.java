package com.example.myapplication.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.custom.DatePickerAdapter;
import com.example.myapplication.custom.ScrollPickerView;
import com.example.myapplication.tools.IcallUtils;
import com.example.myapplication.tools.NumberUtil;
import com.example.myapplication.tools.PopWindowUtil;
import com.example.myapplication.tools.Utils;
import com.example.myapplication.tools.VarCodeCountDownTimerUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
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


    private DatePickerAdapter mDayAdapter;
    private DatePickerAdapter mAdapter;
    private int t_hour;
    private int t_minute;

    @Override
    protected int getLayoutID() {
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
    }

    private void get_Time() {
        // 获取系统时间
        Calendar c = Calendar.getInstance();
        // 提取他的时钟值，int型
        t_hour = c.get(Calendar.HOUR_OF_DAY);
        t_minute = c.get(Calendar.MINUTE);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.query_button)
    public void query_button() {
        finish();
    }


    @Override
    public void onItemSelected(View view, int position) {
        switch (view.getId()) {
            case R.id.datepicker_year_1: {
                int date = mDayAdapter.getDate(position);
                //resetMaxDay();//根据年月计算日期的最大值，并刷新
                break;
            }
            case R.id.datepicker_year_2: {
                int date = mAdapter.getDate(position);
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
        getPopupWindow(instance, null, 0, 0, R.style.showPopupAnimation);
        /*Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "设置闹钟铃声");
        startActivityForResult(intent, 2);*/
    }


    public void getPopupWindow(Activity mContext, View view, int xOff, int yOff, int anim) {
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        int d_height = d.getHeight();

        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_sleep_time, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(d_height / 2);
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
                set_ring();
                popupWindow.dismiss();
            }
        });

        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(anim);

        //解决软件盘 "adjust_Pan"在使用时获取焦点的控件下边的View将会被软键盘覆盖
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.showAtLocation(inflate, Gravity.NO_GRAVITY, 0, d_height / 2);
    }

    private void set_ring() {
        /*ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());

        //values.put(MediaStore.MediaColumns.TITLE, file.getName());
        //values.put(MediaStore.MediaColumns.SIZE, file.length());

        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");

        //values.put(MediaStore.Audio.Media.ARTIST, "Madonna");

        //values.put(MediaStore.Audio.Media.DURATION, 230);

        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);

        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);

        values.put(MediaStore.Audio.Media.IS_ALARM, false);

        values.put(MediaStore.Audio.Media.IS_MUSIC, false);

        Uri uri = MediaStore.Audio.Media.getContentUriForPath(file.getAbsolutePath());

        Uri newUri = this.getContentResolver().insert(uri, values);

        RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_RINGTONE, newUri);*/


        Calendar calendar = Calendar.getInstance();//Calendar是添加Java库里面的import java.util.Calendar;
        //获取系统时间
        int oldhour = calendar.get(Calendar.HOUR_OF_DAY);//时区不同，获取的时间相差八个小时，但有时候时间又会相同，还没搞懂，可能是时区的原因，如果出错了就在hour后面加个8就行了
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
        }

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL);

        //下面设置闹钟时间putextra我用的是第二个参数是int的那个，用string的那个我的会不起作用，就算转换为string类型也不行
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        //响铃时提示的信息
        //intent.putExtra(AlarmClock.EXTRA_MESSAGE, "哈哈哈哈哈");
        //一个 content: URI，用于指定闹铃使用的铃声，也可指定 VALUE_RINGTONE_SILENT 以不使用铃声。
        //如需使用默认铃声，则无需指定此 extra。
        /* //根据手机本地路径获取铃声
        String packageName = getApplication().getPackageName();
        Uri ringtoneUri = Uri.parse("android.resource://" + packageName + "/" + resId);
        intent.putExtra(AlarmClock.EXTRA_RINGTONE, ringtoneUri);*/

        //apk包内文件设置为铃声
        Uri path = Utils.saveSong(instance, R.raw.ring_song);
        intent.putExtra(AlarmClock.EXTRA_RINGTONE, path.toString());

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
    }


}
