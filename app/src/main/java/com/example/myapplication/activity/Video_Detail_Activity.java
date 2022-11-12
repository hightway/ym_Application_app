package com.example.myapplication.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.adapter.VideoViewPagerAdapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.custom.ImageRound;
import com.example.myapplication.custom.MyCircleProgress;
import com.example.myapplication.custom.VoisePlayingIcon;
import com.example.myapplication.fragment.Video_Fragment;
import com.example.myapplication.tools.ExpandOrCollapse;
import com.example.myapplication.tools.Utils;
import com.example.myapplication.videoplayTool.AppUtil;
import com.example.myapplication.videoplayTool.VideoPlayManager;
import com.example.myapplication.videoplayTool.VideoPlayTask;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Video_Detail_Activity extends BaseActivity {

    private Video_Detail_Activity instance;
    @BindView(R.id.image_round)
    ImageRound image_round;
    @BindView(R.id.image_round_1)
    ImageRound image_round_1;
    @BindView(R.id.rel_round)
    RelativeLayout rel_round;
    @BindView(R.id.rel_root)
    RelativeLayout rel_root;
    @BindView(R.id.voise_icon)
    VoisePlayingIcon voise_icon;
    @BindView(R.id.circle_progress)
    MyCircleProgress circle_progress;

    @BindView(R.id.viewpager2)
    ViewPager2 mViewPager2;

    private boolean btn_open = true;
    private int x;
    private ExpandOrCollapse mAnimationManager;
    private int prevWidth = 0;
    private boolean threadFlag = false;
    private VideoViewPagerAdapter mVideoViewPagerAdapter;
    private boolean onFragmentResume;
    private boolean onFragmentVisible;

    @Override
    protected int getLayoutID() {
        instance = this;
        setBar_color_transparent(R.color.transparent);
        return R.layout.video_detail_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        //布局的状态发生变化或者可见性发生变化才会调用
        /*view.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //处理完后remove掉，至于为什么，后面有解释
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });*/

        //view重绘时回调
        /*image_round_1.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                x = Utils.get_X(rel_round, image_round_1);
            }
        });*/

        mAnimationManager = new ExpandOrCollapse();


        //进度圆环
        circle_progress.SetMax(500);
        Thread thread = new Thread(() -> {
            int j = 0;
            while (!threadFlag && j < 500) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                j++;
                int finalJ = j;
                runOnUiThread(() -> circle_progress.SetCurrent(finalJ));
            }
        });
        thread.start();


        List<String> urls = new ArrayList<>();
        urls.add("http://dpv.videocc.net/51b4303ed6/e/51b4303ed62737f339f98491747c63ee_3.mp4?pid=1560914841120X1051965");
        urls.add("https://vfx.mtime.cn/Video/2019/01/15/mp4/190115161611510728_480.mp4");
        urls.add("http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4");
        urls.add("http://vfx.mtime.cn/Video/2019/03/14/mp4/190314102306987969.mp4");
        urls.add("http://vfx.mtime.cn/Video/2019/03/14/mp4/190314223540373995.mp4");
        urls.add("http://vfx.mtime.cn/Video/2019/03/17/mp4/190317150237409904.mp4");
        urls.add("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319125415785691.mp4");
        urls.add("http://vfx.mtime.cn/Video/2019/03/19/mp4/190319104618910544.mp4");
        urls.add("http://vfx.mtime.cn/Video/2019/03/18/mp4/190318214226685784.mp4");
        urls.add("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312143927981075.mp4");
        urls.add("http://vfx.mtime.cn/Video/2019/03/12/mp4/190312083533415853.mp4");

        //init viewpager2
        initViewPage2(urls);
    }

    private void initViewPage2(List<String> urls) {
        mVideoViewPagerAdapter = new VideoViewPagerAdapter(instance);
        //mVideoViewPagerAdapter.setDataList(VideoPlayManager.buildTestVideoUrls());
        mVideoViewPagerAdapter.setDataList(urls);
        mViewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        mViewPager2.setAdapter(mVideoViewPagerAdapter);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d("Video_Play_TAG", " on page selected = " + position);
                View itemView = mViewPager2.findViewWithTag(position);
                SimpleExoPlayerView simpleExoPlayerView = itemView.findViewById(R.id.video_view);
                VideoPlayManager.getInstance(AppUtil.getApplicationContext()).setCurVideoPlayTask(new VideoPlayTask(simpleExoPlayerView,
                        mVideoViewPagerAdapter.getUrlByPos(position)));


                VideoPlayManager.getInstance(AppUtil.getApplicationContext()).startPlay();
                /*if(onFragmentResume && onFragmentVisible) {
                    VideoPlayManager.getInstance(AppUtil.getApplicationContext()).startPlay();
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume = true;
        if(onFragmentVisible) {
            VideoPlayManager.getInstance(AppUtil.getApplicationContext()).resumePlay();
        }
        //Log.d("Video_Play_TAG", " video fragment Resume ");
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentResume = false;
        VideoPlayManager.getInstance(AppUtil.getApplicationContext()).pausePlay();
        //Log.d("Video_Play_TAG", " video fragment Pause ");
    }


    @Override
    protected void onDestroy() {
        threadFlag = true;
        super.onDestroy();
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.image_round)
    public void image_round(){
        //只获取一次
        if(x == 0){
            x = Utils.get_X(rel_round, image_round_1);
        }

        if(prevWidth == 0){
            prevWidth = rel_root.getWidth();
        }

        if(btn_open){
            mAnimationManager.collapse(rel_root, 1200, Utils.dp2px(instance, 80));

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rel_round, "translationX", 0f, x);
            ObjectAnimator rotation_animator = ObjectAnimator.ofFloat(rel_round, "rotation", 0f, 360f);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(objectAnimator, rotation_animator);
            animSet.setDuration(1200);
            animSet.start();
            animSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    voise_icon.setVisibility(View.VISIBLE);
                    voise_icon.start();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

        }else{
            mAnimationManager.expand(rel_root, 1200, prevWidth);

            voise_icon.setVisibility(View.GONE);
            voise_icon.stop();
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rel_round, "translationX", x, 0f);
            ObjectAnimator rotation_animator = ObjectAnimator.ofFloat(rel_round, "rotation", 360f, 0f);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(objectAnimator, rotation_animator);
            animSet.setDuration(1200);
            animSet.start();
        }
        btn_open = !btn_open;
    }

}
