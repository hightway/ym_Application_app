package com.example.myapplication.adapter;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.CacheConfig;
import com.aliyun.player.source.UrlSource;
import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.bean.PlayerInfo;
import com.example.myapplication.bean.Video;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MyPagerAdapter extends PagerAdapter {

    ArrayList<PlayerInfo> playerInfoList = new ArrayList<>();
    private LinkedList<View> mViewCache = new LinkedList<>();
    private Context context;
    private List<Video> mlist;

    public MyPagerAdapter(Context context, List<Video> list){
        this.context = context;
        this.mlist = list;
    }

    protected PlayerInfo instantiatePlayerInfo(int position) {
        AliPlayer aliyunVodPlayer = AliPlayerFactory.createAliPlayer(context);
        PlayerInfo playerInfo = new PlayerInfo();
        playerInfo.setPlayURL(mlist.get(position).getVideoUrl());
        playerInfo.setAliPlayer(aliyunVodPlayer);
        playerInfo.setPosition(position);
        playerInfoList.add(playerInfo);
        return playerInfo;
    }

    public PlayerInfo findPlayerInfo(int position) {
        for (int i = 0; i < playerInfoList.size(); i++) {
            PlayerInfo playerInfo = playerInfoList.get(i);
            if (playerInfo.getPosition() == position) {
                return playerInfo;
            }
        }
        return null;
    }

    public void mOnDestroy() {
        for (PlayerInfo playerInfo : playerInfoList) {
            if (playerInfo.getAliPlayer() != null) {
                playerInfo.getAliPlayer().release();
            }
        }
        playerInfoList.clear();
    }

    protected void destroyPlayerInfo(int position) {
        while (true) {
            PlayerInfo playerInfo = findPlayerInfo(position);
            if (playerInfo == null)
                break;
            if (playerInfo.getAliPlayer() == null)
                break;
            playerInfo.getAliPlayer().release();
            playerInfoList.remove(playerInfo);
        }
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view;
        if (mViewCache.size() == 0) {
            view = LayoutInflater.from(container.getContext()).inflate(R.layout.item_main_viewpage, null, false);
        } else {
            view = mViewCache.removeFirst();
        }
        view.setId(position);

        //TextView videoTitle = view.findViewById(R.id.item_main_video_title);
        final ImageView coverPicture = view.findViewById(R.id.item_main_cover_picture);
        //SurfaceView surfaceView = view.findViewById(R.id.item_main_surface_view);
        TextureView textureView = view.findViewById(R.id.item_main_surface_view);

        /*if (!TextUtils.isEmpty(mlist.get(position).getImageUrl())) {
            coverPicture.setVisibility(View.VISIBLE);
            Glide.with(context).load(mlist.get(position).getImageUrl()).into(coverPicture);
        }*/

        final PlayerInfo playerInfo = instantiatePlayerInfo(position);

        /*surfaceView.setZOrderOnTop(true);
        //surfaceView.setZOrderMediaOverlay(true);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                playerInfo.getAliPlayer().setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                playerInfo.getAliPlayer().redraw();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                playerInfo.getAliPlayer().setDisplay(null);
            }
        });*/

        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                playerInfo.getAliPlayer().setSurface(new Surface(surface));
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                playerInfo.getAliPlayer().surfaceChanged();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                playerInfo.getAliPlayer().setSurface(null);
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });

        UrlSource urlSource = new UrlSource();
        urlSource.setUri(mlist.get(position).getVideoUrl());
        //设置播放源
        playerInfo.getAliPlayer().setDataSource(urlSource);
        //准备播放
        playerInfo.getAliPlayer().prepare();

        //开启缓存
        CacheConfig cacheConfig = new CacheConfig();
        //开启缓存功能
        cacheConfig.mEnable = true;
        //能够缓存的单个文件最大时长。超过此长度则不缓存
        cacheConfig.mMaxDurationS = 300;
        //缓存目录的位置
        cacheConfig.mDir = "hbw";
        //缓存目录的最大大小。超过此大小，将会删除最旧的缓存文件
        cacheConfig.mMaxSizeMB = 200;
        //设置缓存配置给到播放器
        playerInfo.getAliPlayer().setCacheConfig(cacheConfig);

        playerInfo.getAliPlayer().setLoop(true);
        playerInfo.getAliPlayer().setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared() {
                // 视频准备成功之后影响封面图
                /*if (!TextUtils.isEmpty(mlist.get(position).getImageUrl())) {
                    coverPicture.setVisibility(View.GONE);
                }*/
            }
        });
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        destroyPlayerInfo(position);
        View contentView = (View) object;
        container.removeView(contentView);
        mViewCache.add(contentView);
    }
}