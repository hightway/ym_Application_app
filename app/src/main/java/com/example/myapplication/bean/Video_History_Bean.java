package com.example.myapplication.bean;

import java.util.List;

public class Video_History_Bean {


    public Integer errCode;
    public Integer time;
    public String errMsg;
    public List<DataBean> data;

    public static class DataBean {
        public String resource_type;
        public String resource_url;
        public String updated_at;
        public Integer resource_id;
        public String title;
        public Integer status;
        public Integer play_volume;
        public Integer resource_duration;
        public Integer free;
        public String resource_icon;
        public List<TagsBean> tagsList;
    }
}
