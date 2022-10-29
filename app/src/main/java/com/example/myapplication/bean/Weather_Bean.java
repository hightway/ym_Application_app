package com.example.myapplication.bean;

import java.util.List;

public class Weather_Bean {


    public Integer errCode;
    public String errMsg;
    public DataBean data;

    public static class DataBean {
        public Integer status;
        public ResultBean result;
        public String message;

        public static class ResultBean {
            public LocationBean location;
            public NowBean now;
            public List<ForecastsBean> forecasts;

            public static class LocationBean {
                public String country;
                public String province;
                public String city;
                public String name;
                public String id;
            }

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
    }
}
