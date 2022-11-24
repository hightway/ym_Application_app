package com.example.myapplication.videoplayTool;

import android.view.SurfaceView;
import android.widget.ImageView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

public class VideoPlayTask {
    private SimpleExoPlayerView mSimpleExoPlayerView;
    private ImageView start;
    private ImageView img_video_pic;
    private String mVideoUrl;

    public VideoPlayTask(SimpleExoPlayerView simpleExoPlayerView, ImageView img_video_pic, ImageView start, String uri) {
        this.mSimpleExoPlayerView = simpleExoPlayerView;
        this.mVideoUrl = uri;
        this.img_video_pic = img_video_pic;
        this.start = start;
    }




    public ImageView getStart() {
        return start;
    }

    public void setStart(ImageView start) {
        this.start = start;
    }

    public ImageView getImg_video_pic() {
        return img_video_pic;
    }

    public void setImg_video_pic(ImageView img_video_pic) {
        this.img_video_pic = img_video_pic;
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