package com.example.myapplication.plmd;

import com.example.myapplication.activity.Alarm_Activity;

public class setAlarm_CallBack {

    private static Alarm_CallBack alarm_callBack;

    public static void setAlarm_CallBack(Alarm_CallBack callBack){
        alarm_callBack = callBack;
    }

    public static void Alarm_Stop(){
        alarm_callBack.Alarm_Stop();
    }

    public static void Alarm_delay(){
        alarm_callBack.Alarm_delay();
    }

}
