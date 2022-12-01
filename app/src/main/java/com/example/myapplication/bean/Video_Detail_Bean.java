package com.example.myapplication.bean;

import java.io.Serializable;
import java.util.List;

public class Video_Detail_Bean implements Serializable {


    public Integer errCode;
    public Integer time;
    public String errMsg;
    public DataBean data;

    public static class DataBean implements Serializable{
        public DetailBean detail;
        public List<DetailBean> next_list;
        public List<WhiteNoisesBean> white_noises;

        public static class DetailBean implements Serializable{
            public Integer id;
            public String title;
            public String desc;
            public Integer free;
            public Integer top;
            public Integer status;
            public Integer play_volume;
            public String resource_url;
            public Integer resource_duration;
            public String icon;
            public String created_at;
            public String updated_at;
            public String resource_uuid;
            public List<TagsBean> tags;
            public LevelsBean levels;
            public PrivilegesBean privileges;

            public static class LevelsBean implements Serializable{
                public Integer level_id;
                public String level_name;
                public String level_icon;
                public Integer level;
                public Integer member_id;
            }

            public static class PrivilegesBean implements Serializable{
                public Integer privilege_id;
                public String privilege_name;
                public String privilege_icon;
                public String rule;
            }

            public static class TagsBean implements Serializable{
                public Integer id;
                public String name;
                public Integer taggable_id;
                public String taggable_type;
            }
        }

        /*public static class NextListBean implements Serializable{
            public Integer id;
            public String title;
            public Integer free;
            public Integer top;
            public Integer play_volume;
            public Integer resource_duration;
            public String icon;
            public String updated_at;
            public DetailBean.LevelsBean levels;
            public DetailBean.PrivilegesBean privileges;
            public List<DetailBean.TagsBean> tags;
        }*/

        public static class WhiteNoisesBean implements Serializable{
            public Integer id;
            public String title;
            public Integer free;
            public Integer play_volume;
            public Integer resource_duration;
            public String resource_url;
            public String resource_type;
            public String icon;
            public String updated_at;
            public String draft_url;
            public DetailBean.LevelsBean levels;
            public DetailBean.PrivilegesBean privileges;
            public List<DetailBean.TagsBean> tags;
        }
    }
}
