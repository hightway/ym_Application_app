package com.example.myapplication.bean;

import java.util.List;

public class Weather_Video_Bean {

    public Integer errCode;
    public String errMsg;
    public DataBean data;

    public static class DataBean {
        public Integer day;
        public String month;
        public String time_tag;
        public DailyPickBean daily_pick;
        public List<VideoListBean> video_list;
        public WeatherDataBean weather_data;

        public static class DailyPickBean {
            public Integer id;
            public String title;
            public String content;
            public String author;
        }

        public static class WeatherDataBean {
            public NowBean now;
            public List<ForecastsBean> forecasts;

            public static class NowBean {
                public String text;
                public Integer temp;
                public Integer feels_like;
                public Integer rh;
                public String wind_class;
                public String wind_dir;
                public String uptime;
            }

            public static class ForecastsBean {
                public String text_day;
                public String text_night;
                public Integer high;
                public Integer low;
                public String wc_day;
                public String wd_day;
                public String wc_night;
                public String wd_night;
                public String date;
                public String week;
            }
        }

        public static class VideoListBean {
            public Integer id;
            public String title;
            public Integer free;
            public Integer play_volume;
            public String resource_url;
            public Integer resource_duration;
            public String icon;
        }
    }
}
