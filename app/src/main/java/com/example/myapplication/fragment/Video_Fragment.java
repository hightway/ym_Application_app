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
                //??????????????????
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
                //????????????

            }
        });

        aliyunVodPlayer.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                //??????????????????
                //aliyunVodPlayer.pause();
                is_first = false;
                is_start = false;
                start.setVisibility(View.VISIBLE);

                /*if (handler != null) {
                    handler.removeMessages(0);
                }
                second_now = 0;
                seekBar.setProgress(0);

                //????????????
                getTime();*/
            }
        });

        aliyunVodPlayer.setOnVideoSizeChangedListener(new IPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(int width, int height) {
                //???????????????????????????
            }
        });

        aliyunVodPlayer.setOnRenderingStartListener(new IPlayer.OnRenderingStartListener() {
            @Override
            public void onRenderingStart() {
                //????????????????????????
            }
        });

        aliyunVodPlayer.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {
                //????????????????????????type??????????????????????????????????????????????????????????????????????????????????????????
                if (infoBean.getCode() == InfoCode.AutoPlayStart) {
                    //???????????????????????????
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
                //???????????????
            }

            @Override
            public void onLoadingProgress(int percent, float kbps) {
                //????????????
            }

            @Override
            public void onLoadingEnd() {
                //????????????
            }
        });

        aliyunVodPlayer.setOnSeekCompleteListener(new IPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete() {
                //????????????
            }
        });

        /*aliyunVodPlayer.setOnSubtitleDisplayListener(new IPlayer.OnSubtitleDisplayListener() {
            @Override
            public void onSubtitleShow(long id, String data) {
                //????????????
            }

            @Override
            public void onSubtitleHide(long id) {
                //????????????
            }
        });*/

        aliyunVodPlayer.setOnTrackChangedListener(new IPlayer.OnTrackChangedListener() {
            @Override
            public void onChangedSuccess(TrackInfo trackInfo) {
                //???????????????????????????????????????
            }

            @Override
            public void onChangedFail(TrackInfo trackInfo, ErrorInfo errorInfo) {
                //???????????????????????????????????????
            }
        });

        aliyunVodPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(int newState) {
                //???????????????????????????
            }
        });

        aliyunVodPlayer.setOnSnapShotListener(new IPlayer.OnSnapShotListener() {
            @Override
            public void onSnapShot(Bitmap bm, int with, int height) {
                //????????????
            }
        });


        //????????????
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

        //?????????
        update_view();
    }


    //??????????????????
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // ?????????onResume()??????--????????????
            is_visible = true;
        } else {
            // ?????????onpause()??????---????????????
            //????????????
            is_visible = false;
            //stop_Video();
        }
    }


    private void update_view() {
        if (videoListBean != null && !TextUtils.isEmpty(videoListBean.resource_url)) {
            //???????????? ????????????????????????????????????????????????aliPlayer.start()

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
            //???????????????
            aliyunVodPlayer.setDataSource(urlSource);
            //?????????????????????????????????????????????????????????????????????view???????????????????????????
            aliyunVodPlayer.setScaleMode(IPlayer.ScaleMode.SCALE_ASPECT_FILL);
            //??????????????????
            aliyunVodPlayer.setLoop(true);
            //????????????
            aliyunVodPlayer.prepare();
        } else {
            //toast("??????????????????");
        }
    }


    @OnClick(R.id.surface_view)
    public void surface_view() {
        //???????????????
        if (is_start) {
            // ???????????????
            aliyunVodPlayer.pause();
            start.setVisibility(View.VISIBLE);
        } else {
            // ???????????????
            /*if (is_first) {
                //??????????????????????????????
                aliyunVodPlayer.prepare();
            } else {
                //??????????????????????????????
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
