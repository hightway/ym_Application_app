package com.example.myapplication.bean;

import java.util.List;

public class Audio_DateBean {

    public Integer errCode;
    public Integer time;
    public String errMsg;
    public List<Hor_DateBean.DataBean.ListBean> data;

    /*public static class DataBean {
        public Integer id;
        public String title;
        public String draft;
        public String draft_url;
        public Integer free;
        public Integer top;
        public Integer play_volume;
        public Integer resource_duration;
        public String resource_type;
        public String icon;
        public String updated_at;
        public LevelsBean levels;
        public PrivilegesBean privileges;
        public AlbumsBean albums;
        public AnchorsBean anchors;
        public CategorysBean categorys;

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

        public static class AlbumsBean {
            public Integer album_id;
            public String album_title;
            public String album_icon;
        }

        public static class AnchorsBean {
            public Integer anchor_id;
            public String anchor_name;
            public String anchor_avatar;
        }

        public static class CategorysBean {
            public Integer category_id;
            public String category_title;
        }
    }*/
}
