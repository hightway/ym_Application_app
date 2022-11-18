package com.example.myapplication.videoplayTool;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.bean.InfoCode;
import com.aliyun.player.nativeclass.TrackInfo;
import com.aliyun.player.source.UrlSource;
import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

public class Aliyun_VideoPlayManager {

    private volatile static Aliyun_VideoPlayManager mInstance = null;
    private Context mContext;
    private Aliyun_VideoPlayTask mCurVideoPlayTask;
    private AliPlayer aliyunVodPlayer;
    private boolean is_start;
    private boolean is_first = true;

    /**
     * 双重检测
     *
     * @return
     */
    public static Aliyun_VideoPlayManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (Aliyun_VideoPlayManager.class) {
                if (mInstance == null) {
                    mInstance = new Aliyun_VideoPlayManager(context);
                }
            }
        }
        return mInstance;
    }

    public Aliyun_VideoPlayManager(Context context) {
        this.mContext = context;
    }

    /**
     * 开始播放
     */
    public void startPlay() {
        stopPlay();
        if (mCurVideoPlayTask == null) {
            Log.e("Video_Play_TAG", "start play task is null");
            return;
        }

        aliyunVodPlayer = AliPlayerFactory.createAliPlayer(mContext);
        //自动播放
        aliyunVodPlayer.setAutoPlay(true);
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
                aliyunVodPlayer.pause();
                is_first = false;
                is_start = false;
                mCurVideoPlayTask.getStart().setVisibility(View.VISIBLE);

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
                    mCurVideoPlayTask.getStart().setVisibility(View.GONE);
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
        mCurVideoPlayTask.getSimpleExoPlayerView().getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                aliyunVodPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                aliyunVodPlayer.redraw();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                aliyunVodPlayer.setDisplay(null);
            }
        });

        go_play();


        /*//创建带宽对象
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        //根据当前宽带来创建选择磁道工厂对象
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        //传入工厂对象，以便创建选择磁道对象
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        LoadControl loadControl = new DefaultLoadControl();
        mSimpleExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector, loadControl);
        //设置是否循环播放
        mSimpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ONE);

        //配置数据源
        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, "Exo_Video_Play"));
        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        //获取代理url
        String proxyUrl = getProxy().getProxyUrl(mCurVideoPlayTask.getVideoUrl());
        //Log.d("Video_Play_TAG", "start play orginal url = " + mCurVideoPlayTask.getVideoUrl() + " , proxy url = " + proxyUrl);
        Uri proxyUri = Uri.parse(proxyUrl);

        //配置数据源
        MediaSource mediaSource = new ExtractorMediaSource(proxyUri, mediaDataSourceFactory, extractorsFactory, null, null);
        mSimpleExoPlayer.prepare(mediaSource);

        //隐藏播放工具
        mCurVideoPlayTask.getSimpleExoPlayerView().setUseController(false);
        //设置播放视频的宽高为Fit模式
        mCurVideoPlayTask.getSimpleExoPlayerView().setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        //绑定player和playerView
        mCurVideoPlayTask.getSimpleExoPlayerView().setPlayer(mSimpleExoPlayer);
        mSimpleExoPlayer.setPlayWhenReady(true);*/

        //预缓存
        //DataSpec dataSpec = new DataSpec(uri, 0, 100 * 1024, null);

        //静音播放
        //mSimpleExoPlayer.setVolume(0f);
        //恢复音量
        /*float currentvolume = mSimpleExoPlayer.getVolume();
        mSimpleExoPlayer.setVolume(currentvolume);*/
    }


    public void go_play() {
        //开始播放
        //获取代理url
        if (!TextUtils.isEmpty(mCurVideoPlayTask.getVideoUrl())) {
            /*if (handler != null) {
                handler.removeMessages(0);
            }
            second_now = 0;
            seekBar.setProgress(0);*/
            is_first = true;
            is_start = false;

            //String proxyUrl = getProxy().getProxyUrl(mCurVideoPlayTask.getVideoUrl());
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(mCurVideoPlayTask.getVideoUrl());
            //设置播放源
            aliyunVodPlayer.setDataSource(urlSource);
            //设置循环播放
            aliyunVodPlayer.setLoop(true);
            //准备播放
            aliyunVodPlayer.prepare();
            aliyunVodPlayer.start();
        }
    }


    /**
     * 停止播放
     */
    public void stopPlay() {
        /*if(aliyunVodPlayer != null) {
            aliyunVodPlayer.release();
            aliyunVodPlayer = null;
        }*/

        if (aliyunVodPlayer != null) {
            aliyunVodPlayer.stop();
            aliyunVodPlayer.release();
        }
    }

    public void resumePlay() {
        if (aliyunVodPlayer != null) {
            aliyunVodPlayer.start();
            is_start = true;
            mCurVideoPlayTask.getStart().setVisibility(View.GONE);
        } else {
            startPlay();
        }
    }

    public void pausePlay() {
        /*if(aliyunVodPlayer != null) {
            aliyunVodPlayer.setPlayWhenReady(false);
        }*/

        if (aliyunVodPlayer != null) {
            aliyunVodPlayer.pause();
            is_start = false;
            mCurVideoPlayTask.getStart().setVisibility(View.VISIBLE);
        }
    }

    /********************************************* VideoCache start ***************************************/
    private HttpProxyCacheServer mHttpProxyCacheServer;

    public HttpProxyCacheServer getProxy() {
        if (mHttpProxyCacheServer == null) {
            mHttpProxyCacheServer = newProxy();
        }
        return mHttpProxyCacheServer;
    }

    private HttpProxyCacheServer newProxy() {
        //缓存大小512M,缓存文件20
        return new HttpProxyCacheServer.Builder(mContext.getApplicationContext())
                .maxCacheSize(512 * 1024 * 1024)
                .maxCacheFilesCount(20)
                .fileNameGenerator(new VideoFileNameGenerator())
                .cacheDirectory(new File(mContext.getFilesDir() + "/videoCache/"))
                .build();
    }

    /********************************************* VideoCache end ***************************************/
    public Aliyun_VideoPlayTask getCurVideoPlayTask() {
        return mCurVideoPlayTask;
    }

    public void setCurVideoPlayTask(Aliyun_VideoPlayTask mCurVideoPlayTask) {
        this.mCurVideoPlayTask = mCurVideoPlayTask;
    }

}
