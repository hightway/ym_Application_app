package com.example.myapplication.plmd;

public class Pop_Show_Set {

    public static Pop_Show_Interface pop_show_interface;

    public static void setCallBack(Pop_Show_Interface pop){
        pop_show_interface =  pop;
    }

    public static void setPop_show(){
        pop_show_interface.pop_show();
    }

}
