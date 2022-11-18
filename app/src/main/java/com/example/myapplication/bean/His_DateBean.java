package com.example.myapplication.bean;

public class His_DateBean {

    private String txt;
    private int img;

    public His_DateBean(String date, int img) {
        this.txt = date;
        this.img = img;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

}
