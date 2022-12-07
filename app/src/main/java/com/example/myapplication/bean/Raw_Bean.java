package com.example.myapplication.bean;

import android.net.Uri;

import java.io.Serializable;

public class Raw_Bean implements Serializable {

    private Uri uri;
    private String rawName;
    private String time_duraton;


    public Raw_Bean(String rawName, String time_duraton, Uri uri){
        this.rawName = rawName;
        this.time_duraton = time_duraton;
        this.uri = uri;
    }


    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getRawName() {
        return rawName;
    }

    public void setRawName(String rawName) {
        this.rawName = rawName;
    }

    public String getTime_duraton() {
        return time_duraton;
    }

    public void setTime_duraton(String time_duraton) {
        this.time_duraton = time_duraton;
    }
}
