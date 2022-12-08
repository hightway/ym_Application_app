package com.example.myapplication.activity;

import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.plmd.setAlarm_CallBack;
import com.example.myapplication.tools.MediaUtil;
import com.example.myapplication.tools.VibrateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Alarm_Activity extends BaseActivity {

    private Alarm_Activity instance;
    private long mLastDownTime;
    private int mDownX;
    private int mDownY;
    private int MAX_MOVE_FOR_CLICK = 50;// 最长改变距离,超过则算移动
    private int MAX_LONG_PRESS_TIME = 2500;// 最长改变距离,超过则算移动
    private boolean pre_down = true;
    private boolean back_pre_down = true;
    private int current_pro;
    private int current_max;


    @BindView(R.id.set_clock_stop)
    LottieAnimationView set_clock_stop;
    @BindView(R.id.lin_seekbar)
    LinearLayout lin_seekbar;
    @BindView(R.id.seekbar)
    SeekBar seekbar;
    @BindView(R.id.tx_delay)
    TextView tx_delay;
    private boolean isVirating;

    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.alarm_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        //set_clock_stop.setVisibility(View.VISIBLE);
        set_clock_stop.playAnimation();

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
                        break;
                }
                return true;
            }
        });


        //开启震动和铃声
        isVirating = true;
        VibrateUtil.vibrate(instance, new long[]{800, 400, 800, 400}, 0);

        if (!TextUtils.isEmpty(MyApp.Audio_Name)) {
            //铃声
            MediaUtil.playRing(instance, MyApp.Audio_Name, MyApp.Audio_Uri);
        } else {
            //铃声
            MediaUtil.playRing(instance, R.raw.ring_song);
        }

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


    private void stop_seekbar() {
        pre_down = false;
        lin_seekbar.setVisibility(View.INVISIBLE);

        //暂停json动效
        //set_clock_stop.setVisibility(View.GONE);
        set_clock_stop.pauseAnimation();
        //停止震动和铃声
        //防止多次关闭抛出异常，加个参数判断一下
        if (isVirating) {
            isVirating = false;
            VibrateUtil.virateCancle(instance);
        }
        MediaUtil.stopRing();

        toast(getString(R.string.sleep_tip_14));
        mHandle.postDelayed(new Runnable() {
            @Override
            public void run() {
                pre_down = false;
                back_pre_down = false;
                setAlarm_CallBack.Alarm_Stop();
                finish();
            }
        }, 400);
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
                            lin_seekbar.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
        thread.start();
    }


    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (set_clock_stop != null) {
            set_clock_stop.cancelAnimation();
        }
    }


    @OnClick(R.id.tx_delay)
    public void tx_delay() {
        pre_down = false;
        back_pre_down = false;
        setAlarm_CallBack.Alarm_delay();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //这里写你要在用户按下返回键同时执行的动作
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
