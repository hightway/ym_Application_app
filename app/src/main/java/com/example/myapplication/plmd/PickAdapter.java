package com.example.myapplication.plmd;

public interface PickAdapter {

    /**
     * 返回数据总个数
     */
    int getCount();

    /**
     * 返回一条对应index的数据
     */
    String getItem(int position);

}
