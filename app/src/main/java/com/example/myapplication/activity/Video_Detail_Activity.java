package com.example.myapplication.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;
import com.aliyun.subtitle.SubtitleView;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.adapter.Mainpage_Adapter;
import com.example.myapplication.adapter.VideoViewPagerAdapter;
import com.example.myapplication.adapter.ViewPage_Adapter;
import com.example.myapplication.adapter.ViewPage_Meua_Adapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.custom.DialogFragment;
import com.example.myapplication.custom.ImageRound;
import com.example.myapplication.custom.MyCircleProgress;
import com.example.myapplication.custom.MyDialogFragment;
import com.example.myapplication.custom.VoisePlayingIcon;
import com.example.myapplication.fragment.Anchor_Radio_Fragment;
import com.example.myapplication.fragment.Play_History_Fragment;
import com.example.myapplication.fragment.Video_Fragment;
import com.example.myapplication.fragment.White_Noise_Fragment;
import com.example.myapplication.tools.ExpandOrCollapse;
import com.example.myapplication.tools.Utils;
import com.example.myapplication.videoplayTool.AppUtil;
import com.example.myapplication.videoplayTool.VideoPlayManager;
import com.example.myapplication.videoplayTool.VideoPlayTask;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;

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
    @BindView(R.id.img_4)
    ImageView img_4;
    @BindView(R.id.layout_point)
    LinearLayout layout_point;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.lin_roll)
    LinearLayout lin_roll;

    private boolean btn_open = true;
    private int x;
    private ExpandOrCollapse mAnimationManager;
    private int prevWidth = 0;
    private boolean threadFlag = false;
    private VideoViewPagerAdapter mVideoViewPagerAdapter;
    private boolean onFragmentResume;
    private boolean onFragmentVisible;
    private int pos = 0;

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




        Utils.init_Aliyun(MyApp.get_app_mAliPlayer(), MyApp.Aapp_context, lin_roll);


        /*AliPlayer mAliPlayer = AliPlayerFactory.createAliPlayer(instance);
        mAliPlayer.setOnSubtitleDisplayListener(new IPlayer.OnSubtitleDisplayListener() {
            @Override
            public void onSubtitleExtAdded(int trackIndex, String url) { }

            @Override
            public void onSubtitleShow(int trackIndex, long id, String data) {
                // ass 字幕
                assSubtitleView.show(id,data);

                // srt 字幕
                SubtitleView.Subtitle subtitle = new SubtitleView.Subtitle();
                subtitle.id = id + "";
                subtitle.content = data;
                subtitleView.show(subtitle);

            }

            @Override
            public void onSubtitleHide(int trackIndex, long id) {
                // ass 字幕
                assSubtitleView.dismiss(id);

                // srt 字幕
                subtitleView.dismiss(id + "");
            }

            @Override
            public void onSubtitleHeader(int trackIndex, String header) { }
        });*/
    }

    private void initViewPage2(List<String> urls) {
        mVideoViewPagerAdapter = new VideoViewPagerAdapter(instance);
        mVideoViewPagerAdapter.setDataList(urls);
        mViewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        mViewPager2.setAdapter(mVideoViewPagerAdapter);
        mViewPager2.setOffscreenPageLimit(2);
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

                //开始播放
                VideoPlayManager.getInstance(AppUtil.getApplicationContext()).startPlay();

                for(int i=0; i<layout_point.getChildCount(); i++){
                    if(i == position){
                        layout_point.getChildAt(i).setBackgroundResource(R.drawable.point_enable);
                    }else{
                        layout_point.getChildAt(i).setBackgroundResource(R.drawable.point_disable);
                    }
                }

                int l_height = layout_point.getHeight();
                int s_height = scrollView.getHeight();
                if(l_height > s_height){
                    View view = layout_point.getChildAt(position);
                    int top = view.getTop();
                    int height = view.getHeight();
                    if(pos < position){
                        if((top + height) >= s_height){
                            scrollView.scrollBy(0, height+14);
                        }
                    }else{
                        if(l_height - top >= s_height){
                            scrollView.scrollBy(0, -(height+14));
                        }
                    }
                }

                pos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });


        for(int i=0; i<urls.size(); i++){
            //创建下标小白点，然后用LinearLayout作为父容器，把小白点加进去
            View view = new View(instance);
            view.setBackgroundResource(R.drawable.point_disable);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(8, 28);
            if(i!=0){
                lp.topMargin = 14;
            }
            layout_point.addView(view,lp);
        }
        //设置小白点的默认位置0是选中状态的白点。
        layout_point.getChildAt(0).setBackgroundResource(R.drawable.point_enable);
    }


    @Override
    public void onResume() {
        super.onResume();
        onFragmentResume = true;
        if(onFragmentVisible) {
            VideoPlayManager.getInstance(AppUtil.getApplicationContext()).resumePlay();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onFragmentResume = false;
        VideoPlayManager.getInstance(AppUtil.getApplicationContext()).pausePlay();
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


    @OnClick(R.id.img_4)
    public void img_4(){
        //getPopupWindow(instance, R.style.showPopupAnimation);

        /*MyDialogFragment customLoseDialog = new MyDialogFragment();
        customLoseDialog.show(getSupportFragmentManager(), "lose");*/

        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "ss");

        /*BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(instance);
        //BottomSheetDialogFragment mBottomSheetDialog = new BottomSheetDialogFragment();

        View view1 = getLayoutInflater().inflate(R.layout.layout_meua, null);
        view1.setBackgroundResource(R.color.transparent);
        mBottomSheetDialog.setContentView(view1);

        TabLayout tl_tabs = mBottomSheetDialog.getWindow().findViewById(R.id.meua_tabs);
        ViewPager meua_viewpage = mBottomSheetDialog.getWindow().findViewById(R.id.meua_viewpage);
        List<String> tab_name = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        tab_name.add(getString(R.string.meua_1));
        tab_name.add(getString(R.string.meua_2));
        tab_name.add(getString(R.string.meua_3));
        fragmentList.add(new White_Noise_Fragment());
        fragmentList.add(new Anchor_Radio_Fragment());
        fragmentList.add(new Play_History_Fragment());
        ViewPage_Meua_Adapter adapter = new ViewPage_Meua_Adapter(getSupportFragmentManager(), fragmentList, tab_name);
        meua_viewpage.setAdapter(adapter);
        tl_tabs.setupWithViewPager(meua_viewpage);

        mBottomSheetDialog.show();*/






    }


    public void getPopupWindow(Activity mContext, int anim) {
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        int d_height = d.getHeight();

        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_meua, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(d_height*4/5);

        //点击非菜单部分退出
        /*inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });*/

        /*TextView img_miss = inflate.findViewById(R.id.tx_ok);
        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });*/

        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(anim);

        //解决软件盘 "adjust_Pan"在使用时获取焦点的控件下边的View将会被软键盘覆盖
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.showAtLocation(inflate, Gravity.NO_GRAVITY, 0, d_height*4/5);
    }

}
