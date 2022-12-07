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
import com.example.myapplication.bean.Raw_Bean;
import com.example.myapplication.tools.MediaUtil;
import com.example.myapplication.tools.VibrateUtil;

public class AlarmReceiver extends BroadcastReceiver {

    private String TAG = this.getClass().getSimpleName();
    public static final String BC_ACTION = "com.ex.action.BC_ACTION";
    private boolean isVirating = false;

    @Override
    public void onReceive(Context context, Intent intent) {

        String act = intent.getAction();
        if (!TextUtils.isEmpty(act) && act.equals(BC_ACTION)) {
            String audio_name = intent.getStringExtra("audio_name");

            //开启震动
            isVirating = true;
            VibrateUtil.vibrate(context, new long[]{800, 400, 800, 400}, 0);
            //关闭震动
            /*if (isVirating) {//防止多次关闭抛出异常，这里加个参数判断一下
                isVirating = false;
                VibrateUtil.virateCancle(context);
            }*/

            //铃声
            //MediaUtil.playRing(context, MyApp.Audio_Name, MyApp.Audio_Uri);

            if(!TextUtils.isEmpty(audio_name)){
                //铃声
                MediaUtil.playRing(context, audio_name, MyApp.Audio_Uri);
            }else{
                //铃声
                MediaUtil.playRing(context, R.raw.ring_song);
            }
        }
    }
}
