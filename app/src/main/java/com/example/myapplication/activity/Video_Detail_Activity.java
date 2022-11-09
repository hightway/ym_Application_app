package com.example.myapplication.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.custom.ImageRound;
import com.example.myapplication.custom.MyCircleProgress;
import com.example.myapplication.custom.VoisePlayingIcon;
import com.example.myapplication.tools.ExpandOrCollapse;
import com.example.myapplication.tools.Utils;

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

    private boolean btn_open = true;
    private int x;
    private ExpandOrCollapse mAnimationManager;
    private int prevWidth = 0;
    private boolean threadFlag = false;

    @Override
    protected int getLayoutID() {
        instance = this;
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
