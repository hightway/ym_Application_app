package com.example.myapplication.videoplayTool;

import android.view.SurfaceView;
import android.widget.ImageView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

public class VideoPlayTask {
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private ImageView start;
    private String mVideoUrl;

    public VideoPlayTask(SimpleExoPlayerView simpleExoPlayerView, String uri) {
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

    public SimpleExoPlayerView getSimpleExoPlayerView() {
        return mSimpleExoPlayerView;
    }

    public void setSimpleExoPlayerView(SimpleExoPlayerView mSimpleExoPlayerView) {
        this.mSimpleExoPlayerView = mSimpleExoPlayerView;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.mVideoUrl = videoUrl;
    }
}