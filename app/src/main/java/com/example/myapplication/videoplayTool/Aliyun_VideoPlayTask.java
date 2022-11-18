package com.example.myapplication.videoplayTool;

import android.view.SurfaceView;
import android.widget.ImageView;

public class Aliyun_VideoPlayTask {

    private SurfaceView mSimpleExoPlayerView;
    private ImageView start;
    private String mVideoUrl;

    public Aliyun_VideoPlayTask(SurfaceView simpleExoPlayerView, ImageView start, String uri) {
        this.mSimpleExoPlayerView = simpleExoPlayerView;
        this.mVideoUrl = uri;
        this.start = start;
    }




    public ImageView getStart() {
        return start;
    }

    public void setStart(ImageView start) {
        this.start = start;
    }

    public SurfaceView getSimpleExoPlayerView() {
        return mSimpleExoPlayerView;
    }

    public void setSimpleExoPlayerView(SurfaceView mSimpleExoPlayerView) {
        this.mSimpleExoPlayerView = mSimpleExoPlayerView;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.mVideoUrl = videoUrl;
    }

}
