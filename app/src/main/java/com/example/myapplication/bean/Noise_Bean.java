package com.example.myapplication.bean;

public class Noise_Bean {


    public Integer errCode;
    public Integer time;
    public String errMsg;
    public DataBean data;

    public static class DataBean {
        public Integer id;
        public String title;
        public Integer free;
        public Integer status;
        public Integer play_volume;
        public String desc;
        public String resource_url;
        public Integer resource_duration;
        public String icon;
        public String created_at;
        public String updated_at;
        public String resource_uuid;
        public String resource_type;
        public LevelsBean levels;
        public PrivilegesBean privileges;

        public static class LevelsBean {
            public Integer level_id;
            public String level_name;
            public String level_icon;
            public Integer level;
            public Integer member_id;
        }

        public static class PrivilegesBean {
            public Integer privilege_id;
            public String privilege_name;
            public String privilege_icon;
            public String rule;
        }
    }
}
