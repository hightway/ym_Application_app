package com.example.myapplication.bean;

import java.util.List;

public class Hor_DateBean {

    public Integer errCode;
    public String errMsg;
    public DataBean data;

    public static class DataBean {
        public List<TopBean> top;
        public List<ListBean> list;

        public static class TopBean {
            public Integer id;
            public String title;
            public String draft;
            public String draft_url;
            public Integer free;
            public Integer top;
            public Integer play_volume;
            public Integer resource_duration;
            public String icon;
            public String updated_at;
            public LevelsBean levels;
            public PrivilegesBean privileges;
            public AlbumsBean albums;
            public AnchorsBean anchors;
            public CategorysBean categorys;
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

            public static class TagsBean {
                public Integer id;
                public String name;
                public Integer taggable_id;
                public String taggable_type;
            }
        }

        public static class ListBean {
            public Integer id;
            public String title;
            public String draft;
            public String draft_url;
            public Integer free;
            public Integer top;
            public Integer play_volume;
            public Integer resource_duration;
            public String icon;
            public String updated_at;
            public TopBean.LevelsBean levels;
            public TopBean.PrivilegesBean privileges;
            public TopBean.AlbumsBean albums;
            public TopBean.AnchorsBean anchors;
            public TopBean.CategorysBean categorys;
            public List<TopBean.TagsBean> tags;
        }
    }
}
