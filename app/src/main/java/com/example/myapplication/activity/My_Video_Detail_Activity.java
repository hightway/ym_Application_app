package com.example.myapplication.activity;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.aliyun.loader.MediaLoader;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.AliPlayerGlobalSettings;
import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.CacheConfig;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;
import com.aliyun.subtitle.SubtitleView;
import com.bumptech.glide.Glide;
import com.cicada.player.utils.ass.AssSubtitleView;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.adapter.MyPagerAdapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.PlayerInfo;
import com.example.myapplication.bean.Video;
import com.example.myapplication.tools.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import fr.castorflex.android.verticalviewpager.VerticalViewPager;

public class My_Video_Detail_Activity extends BaseActivity {

    @BindView(R.id.main_vp)
    VerticalViewPager vp;
    @BindView(R.id.lin_roll)
    LinearLayout lin_roll;

    private My_Video_Detail_Activity instance;
    private MyPagerAdapter myPagerAdapter;
    private int currentFlagPostion;//传递过来播放第几个
    private List<Video> list = new ArrayList<>();//播放列表
    private int mCurrentPosition;//当前正在播放第几个
    private AliPlayer aliPlayer;//当前正在播放的播放器

    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.my_video_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        initViewPager();

        Utils.init_Aliyun(MyApp.get_app_mAliPlayer(), MyApp.Aapp_context, lin_roll);
    }



    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    private void initViewPager() {
        currentFlagPostion = getIntent().getIntExtra("currentPostion", 0);

        //注意我这里的封面图是随便加的，和数据不匹配。
        Video video1 = new Video();
        video1.setVideoUrl("http://dpv.videocc.net/51b4303ed6/e/51b4303ed62737f339f98491747c63ee_3.mp4?pid=1560914841120X1051965");
        video1.setTitle("荒野大镖客2");
        video1.setImageUrl("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fx0.ifengimg.com%2Fres%2F2021%2F2FCE249A077D2397307DE73F6CD4697578862917_size1438_w1098_h718.png&refer=http%3A%2F%2Fx0.ifengimg.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671161545&t=648c62111a60a666a832ed78464b8660");

        Video video2 = new Video();
        video2.setVideoUrl("https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4");
        video2.setTitle("小龙虾");
        video2.setImageUrl("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg95.699pic.com%2Fphoto%2F30021%2F0738.jpg_wh860.jpg&refer=http%3A%2F%2Fimg95.699pic.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1671161584&t=41c514ad5459fd00aff669c86b0b18ff");

        list.add(video1);
        list.add(video2);
        list.add(video1);
        list.add(video2);
        list.add(video1);
        list.add(video2);
        list.add(video1);
        list.add(video2);

        myPagerAdapter = new MyPagerAdapter(instance, list);
        vp.setAdapter(myPagerAdapter);
        vp.setOffscreenPageLimit(3);
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int postition) {
                mCurrentPosition = postition;
                // 滑动界面，首先让之前的播放器暂停，并seek到0
                if (aliPlayer != null) {
                    aliPlayer.seekTo(0);
                    aliPlayer.pause();
                }

                //MediaLoader预加载
                //MediaLoader_Load(postition);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        vp.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                if (position != 0) {
                    return;
                }
                PlayerInfo playerInfo = myPagerAdapter.findPlayerInfo(mCurrentPosition);
                if (playerInfo != null) {
                    if (playerInfo.getAliPlayer() != null) {
                        playerInfo.getAliPlayer().start();
                        aliPlayer = playerInfo.getAliPlayer();
                    }
                }
            }
        });
        vp.setCurrentItem(currentFlagPostion);
    }


    private void MediaLoader_Load(int pos) {
        //若您需要自行控制预加载的实时请求，可以通过以下方法将此策略关闭
        AliPlayerGlobalSettings.enableNetworkBalance(false);
        if(list.size() == pos+1){
            return;
        }
        MediaLoader mediaLoader = MediaLoader.getInstance();
        /**
         * 设置加载状态回调
         */
        mediaLoader.setOnLoadStatusListener(new MediaLoader.OnLoadStatusListener() {
            @Override
            public void onError(String url, int code, String msg) {
                //加载出错
            }

            @Override
            public void onCompleted(String s) {
                //加载完成
            }

            @Override
            public void onCanceled(String s) {
                //加载取消
            }
        });

        /**
         * 开始加载文件。异步加载。可以同时加载多个视频文件。
         * @param url - 视频文件地址。
         * @param duration - 加载的时长大小，单位：毫秒。
         */
        mediaLoader.load(list.get(pos+1).getVideoUrl(), 50000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aliPlayer != null) {
            aliPlayer.release();
        }
        if (myPagerAdapter != null) {
            myPagerAdapter.mOnDestroy();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (aliPlayer != null) {
            aliPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (aliPlayer != null) {
            aliPlayer.start();
        }
    }

}
