package com.example.myapplication.plmd;

import com.example.myapplication.bean.White_Noise_Bean;

public class White_Noise_Cliack_Set {

    private static White_Noise_Cliack white_noise_cliack;

    public static void setWhite_Noise_Cliack(White_Noise_Cliack noise_cliack){
        white_noise_cliack = noise_cliack;
    }

    public static void set_noise_click(White_Noise_Bean.DataBean dataBean){
        if(white_noise_cliack != null){
            white_noise_cliack.noise_click(dataBean);
        }
    }

}
