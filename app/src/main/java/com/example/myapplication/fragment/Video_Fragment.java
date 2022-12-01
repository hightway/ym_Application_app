package com.example.myapplication.fragment;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.UrlSource;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.Weather_Video_Bean;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Video_Fragment extends BaseLazyFragment {

    @BindView(R.id.surface_view)
    SurfaceView surface_view;
    @BindView(R.id.start)
    ImageView start;
    @BindView(R.id.img_gone)
    ImageView img_gone;
    private AliPlayer aliyunVodPlayer;
    private Weather_Video_Bean.DataBean.VideoListBean videoListBean;
    private boolean is_first = true;
    private boolean is_start;
    private boolean is_visible;

    public Video_Fragment(Weather_Video_Bean.DataBean.VideoListBean videoListBean) {
        this.videoListBean = videoListBean;
    }

    @Override
    protected int setLayout() {
        return R.layout.video_play_lay;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        if (!TextUtils.isEmpty(videoListBean.icon)) {
            img_gone.setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(videoListBean.icon)
                    .into(img_gone);
        }

        aliyunVodPlayer = AliPlayerFactory.createAliPlayer(getActivity());
        aliyunVodPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                //播放完成事件
                /*is_first = true;
                is_start = false;
                start.setVisibility(View.VISIBLE);*/

                /*if (handler != null) {
                    handler.removeMessages(0);
                }*/
            }
        });

        aliyunVodPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                //出错事件

            }
        });

        aliyunVodPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                //准备成功事件
                //aliyunVodPlayer.pause();
                is_first = false;
                is_start = false;
                start.setVisibility(View.VISIBLE);

                /*if (handler != null) {
                    handler.removeMessages(0);
                }
                second_now = 0;
                seekBar.setProgress(0);

                //计算时间
                getTime();*/
            }
        });

        aliyunVodPlayer.setOnVideoSizeChangedListener(new IPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(int width, int height) {
                //视频分辨率变化回调
            }
        });

        aliyunVodPlayer.setOnRenderingStartListener(new IPlayer.OnRenderingStartListener() {
            @Override
            public void onRenderingStart() {
                //首帧渲染显示事件
            }
        });

        aliyunVodPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {
                //其他信息的事件，type包括了：循环播放开始，缓冲位置，当前播放位置，自动播放开始等
                if (infoBean.getCode() == InfoCode.AutoPlayStart) {
                    //自动播放开始事件。
                    is_first = false;
                    is_start = true;
                    start.setVisibility(View.GONE);
                    img_gone.setVisibility(View.GONE);
                    //rel_bottom.setVisibility(View.GONE);
                }
            }
        });

        aliyunVodPlayer.setOnLoadingStatusListener(new IPlayer.OnLoadingStatusListener() {
            @Override
            public void onLoadingBegin() {
                //缓冲开始。
            }

            @Override
            public void onLoadingProgress(int percent, float kbps) {
                //缓冲进度
            }

            @Override
            public void onLoadingEnd() {
                //缓冲结束
            }
        });

        aliyunVodPlayer.setOnSeekCompleteListener(new IPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                //拖动结束
            }
        });

        /*aliyunVodPlayer.setOnSubtitleDisplayListener(new IPlayer.OnSubtitleDisplayListener() {
            @Override
            public void onSubtitleShow(long id, String data) {
                //显示字幕
            }

            @Override
            public void onSubtitleHide(long id) {
                //隐藏字幕
            }
        });*/

        aliyunVodPlayer.setOnTrackChangedListener(new IPlayer.OnTrackChangedListener() {
            @Override
            public void onChangedSuccess(TrackInfo trackInfo) {
                //切换音视频流或者清晰度成功
            }

            @Override
            public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
                //切换音视频流或者清晰度失败
            }
        });

        aliyunVodPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(int newState) {
                //播放器状态改变事件
            }
        });

        aliyunVodPlayer.setOnSnapShotListener(new IPlayer.OnSnapShotListener() {
            @Override
            public void onSnapShot(Bitmap bm, int with, int height) {
                //截图事件
            }
        });


        //显示画面
        surface_view.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                aliyunVodPlayer.setDisplay(holder);
                //aliyunVodPlayer.setSurface(holder.getSurface());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                aliyunVodPlayer.redraw();
                //aliyunVodPlayer.surfaceChanged();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                aliyunVodPlayer.setDisplay(null);
                //aliyunVodPlayer.setSurface(null);
            }
        });

        //初始化
        update_view();
    }


    //可见或不可见
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // 相当于onResume()方法--获取焦点
            is_visible = true;
        } else {
            // 相当于onpause()方法---失去焦点
            //暂停播放
            is_visible = false;
            //stop_Video();
        }
    }


    private void update_view() {
        if (videoListBean != null && !TextUtils.isEmpty(videoListBean.resource_url)) {
            //自动播放 如果开启了自动播放，则不需要调用aliPlayer.start()

            /*if (is_visible){
                aliyunVodPlayer.setAutoPlay(true);
            }else{
                aliyunVodPlayer.setAutoPlay(false);
            }*/

            aliyunVodPlayer.setAutoPlay(false);

            is_first = true;
            is_start = true;

            UrlSource urlSource = new UrlSource();
            urlSource.setUri(videoListBean.resource_url);
            //设置播放源
            aliyunVodPlayer.setDataSource(urlSource);
            //设置宽高比填充（将按照视频宽高比等比放大，充满view，不会有画面变形）
            aliyunVodPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
            //设置循环播放
            aliyunVodPlayer.setLoop(true);
            //准备播放
            aliyunVodPlayer.prepare();
        } else {
            //toast("播放地址无效");
        }
    }


    @OnClick(R.id.surface_view)
    public void surface_view() {
        //播放与暂停
        if (is_start) {
            // 开始播放。
            aliyunVodPlayer.pause();
            start.setVisibility(View.VISIBLE);
        } else {
            // 开始播放。
            /*if (is_first) {
                //第一次播放，从头开始
                aliyunVodPlayer.prepare();
            } else {
                //不是第一次，继续播放
                aliyunVodPlayer.start();
            }*/

            aliyunVodPlayer.start();
            start.setVisibility(View.GONE);
            img_gone.setVisibility(View.GONE);
        }
        is_start = !is_start;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (aliyunVodPlayer != null) {
            aliyunVodPlayer.stop();
            aliyunVodPlayer.release();
        }
    }

    public void start_Video() {
        if (aliyunVodPlayer != null) {
            aliyunVodPlayer.start();
            is_start = true;
            if (start != null) {
                start.setVisibility(View.GONE);
                img_gone.setVisibility(View.GONE);
            }
        }
    }

    public void stop_Video() {
        if (aliyunVodPlayer != null) {
            aliyunVodPlayer.pause();
            is_start = false;
            if (start != null) {
                start.setVisibility(View.VISIBLE);
            }
        }
    }

    /*@Override
    public void onPause() {
        super.onPause();
        if (aliyunVodPlayer != null) {
            aliyunVodPlayer.pause();
            is_start = false;
            if(start != null){
                start.setVisibility(View.VISIBLE);
            }
        }
    }*/
}
