package com.example.myapplication.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.activity.Alarm_Activity;
import com.example.myapplication.activity.Sleep_Time_Set_Activity;
import com.example.myapplication.bean.Raw_Bean;
import com.example.myapplication.tools.MediaUtil;
import com.example.myapplication.tools.VibrateUtil;

public class AlarmReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getSimpleName();
    public static final String BC_ACTION = "com.ex.action.BC_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {

        String act = intent.getAction();
        if (!TextUtils.isEmpty(act) && act.equals(BC_ACTION)) {
            String alarm_time = intent.getStringExtra("alarm_time");
            float alarm_light = intent.getFloatExtra("alarm_light", 0f);

            //跳转界面
            Intent intent_act = new Intent(context, Alarm_Activity.class);
            intent_act.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent_act.putExtra("alarm_time", alarm_time);
            intent_act.putExtra("alarm_light", alarm_light);
            context.startActivity(intent_act);

        }
    }
}
