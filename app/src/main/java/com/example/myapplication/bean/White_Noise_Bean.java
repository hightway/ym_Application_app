package com.example.myapplication.bean;

import java.util.List;

public class White_Noise_Bean {


    public Integer errCode;
    public String errMsg;
    public List<DataBean> data;

    public static class DataBean {
        public Integer id;
        public String title;
        public Integer free;
        public Integer play_volume;
        public Integer resource_duration;
        public String resource_type;
        public String resource_url;
        public String icon;
        public String updated_at;
        public LevelsBean levels;
        public PrivilegesBean privileges;
        public List<TagsBean> tags;

        public static class LevelsBean {
            public Integer level_id;
            public String level_name;
            public String level_icon;
            public Integer level;
        }

        public static class PrivilegesBean {
            public Integer privilege_id;
            public String privilege_name;
            public String privilege_icon;
            public String rule;
        }

        /*public static class TagsBean {
            public Integer id;
            public String name;
            public Integer taggable_id;
            public String taggable_type;
        }*/
    }
}
