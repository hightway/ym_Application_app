package com.example.myapplication.plmd;

import com.example.myapplication.bean.Hor_DateBean;

public class Radio_Click_Set {

    private static Radio_Click radio_cliack;

    public static void setRadio_Cliack(Radio_Click callback){
        radio_cliack = callback;
    }

    public static void set_Click(Hor_DateBean.DataBean.ListBean dataBean){
        if(radio_cliack != null){
            radio_cliack.audio_click(dataBean);
        }
    }

}
