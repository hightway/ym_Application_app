package com.example.myapplication.bean;

import java.util.List;

public class His_DateBean {


    public Integer errCode;
    public Integer time;
    public String errMsg;
    public List<DataBean> data;

    public static class DataBean {
        public String resource_type;
        public String updated_at;
        public Integer free;
        public String title;
        public Integer resource_id;
        public Integer status;
        public String resource_icon;
        public Integer anchor_id;
        public String anchor_name;
        public String anchor_avatar;
        public Integer resource_duration;
        public List<TagsBean> tags;

        /*public static class TagsBean {
            public Integer id;
            public String name;
        }*/
    }
}
