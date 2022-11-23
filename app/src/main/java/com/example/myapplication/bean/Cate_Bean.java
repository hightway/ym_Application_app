package com.example.myapplication.bean;

import java.util.List;

public class Cate_Bean {


    public Integer errCode;
    public Integer time;
    public String errMsg;
    public List<DataBean> data;

    public static class DataBean {
        public Integer id;
        public String title;
        public String desc;
    }
}
