package com.example.myapplication.tools;

import android.os.CountDownTimer;
import android.widget.TextView;

public class VarCodeCountDownTimerUtil extends CountDownTimer{

    private TextView register_key;

    // 验证码倒计时类
        public VarCodeCountDownTimerUtil(long millisInFuture, long countDownInterval, TextView textView) {
            super(millisInFuture, countDownInterval);
            register_key = textView;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            register_key.setText("倒计时(" + millisUntilFinished / 1000 + ")");
        }

        @Override
        public void onFinish() {
            register_key.setText("获取验证码");
            register_key.setClickable(true);
        }


}
