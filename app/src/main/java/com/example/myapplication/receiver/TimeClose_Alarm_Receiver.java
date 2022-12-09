package com.example.myapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.example.myapplication.activity.Alarm_Activity;

public class TimeClose_Alarm_Receiver extends BroadcastReceiver {

    public static final String BC_ACTION = "com.ex.action.timeclose";

    @Override
    public void onReceive(Context context, Intent intent) {
        String act = intent.getAction();
        if (!TextUtils.isEmpty(act) && act.equals(BC_ACTION)) {
            //发送广播
            Intent intent_close = new Intent();
            intent_close.setAction("audio_close");
            context.sendBroadcast(intent_close);

        }
    }
}
