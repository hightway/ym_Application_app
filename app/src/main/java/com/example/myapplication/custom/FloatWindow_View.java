package com.example.myapplication.custom;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.tools.ExpandOrCollapse;
import com.example.myapplication.tools.Utils;

import java.lang.reflect.Field;

public class FloatWindow_View implements View.OnTouchListener, View.OnClickListener {

    private Context mContext;
    private WindowManager.LayoutParams mWindowParams;
    private WindowManager mWindowManager;

    private View mFloatLayout;
    private float mInViewX;
    private float mInViewY;
    private float mDownInScreenX;
    private float mDownInScreenY;
    private float mInScreenX;
    private float mInScreenY;
    private boolean btn_open = true;
    private int x;
    private RelativeLayout rel_round;
    private ImageRound image_round_1;
    private ExpandOrCollapse mAnimationManager;
    private int prevWidth = 0;
    private RelativeLayout rel_root;
    private VoisePlayingIcon voise_icon;

    public FloatWindow_View(Context context) {
        this.mContext = context;
        initFloatWindow();
    }

    private void initFloatWindow() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if(inflater == null)
            return;

        mFloatLayout = (View) inflater.inflate(R.layout.float_window, null);
        rel_round = mFloatLayout.findViewById(R.id.rel_round);
        image_round_1 = mFloatLayout.findViewById(R.id.image_round_1);
        rel_root = mFloatLayout.findViewById(R.id.rel_root);
        voise_icon = mFloatLayout.findViewById(R.id.voise_icon);


        rel_round.setOnClickListener(this);
        mFloatLayout.setOnTouchListener(this);

        mWindowParams = new WindowManager.LayoutParams();
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {//8.0?????????
            mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else{
            mWindowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        mWindowParams.format = PixelFormat.RGBA_8888;
        mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowParams.gravity = Gravity.START | Gravity.TOP;
        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        mWindowParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return floatLayoutTouch(motionEvent);
    }

    private boolean floatLayoutTouch(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // ????????????View?????????????????????View??????????????????
                mInViewX = motionEvent.getX();
                mInViewY = motionEvent.getY();
                // ????????????????????????????????????????????????????????????
                mDownInScreenX = motionEvent.getRawX();
                mDownInScreenY = motionEvent.getRawY() - getSysBarHeight(mContext);
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY() - getSysBarHeight(mContext);
                break;
            case MotionEvent.ACTION_MOVE:
                // ??????????????????????????????
                mInScreenX = motionEvent.getRawX();
                mInScreenY = motionEvent.getRawY() - getSysBarHeight(mContext);
                mWindowParams.x = (int) (mInScreenX- mInViewX);
                mWindowParams.y = (int) (mInScreenY - mInViewY);
                // ????????????????????????????????????????????????
                mWindowManager.updateViewLayout(mFloatLayout, mWindowParams);
                break;
            case MotionEvent.ACTION_UP:
                // ??????????????????????????????xDownInScreen???xInScreen????????????yDownInScreen???yInScreen??????????????????????????????????????????
                if (mDownInScreenX == mInScreenX && mDownInScreenY == mInScreenY){
                    //????????????

                }
                break;
        }
        return true;
    }

    public void showFloatWindow(){
        if (mFloatLayout.getParent() == null){
            DisplayMetrics metrics = new DisplayMetrics();
            // ????????????????????????????????????????????????
            mWindowManager.getDefaultDisplay().getMetrics(metrics);
            mWindowParams.x = metrics.widthPixels;
            mWindowParams.y = metrics.heightPixels/2 - getSysBarHeight(mContext);
            mWindowManager.addView(mFloatLayout, mWindowParams);
        }
    }

    /*public void updateText(final String s) {
        infoText.setText(s);
    }*/

    public void hideFloatWindow(){
        if (mFloatLayout.getParent() != null){
            if(voise_icon != null){
                voise_icon.setVisibility(View.GONE);
                voise_icon.stop();
            }
            mWindowManager.removeView(mFloatLayout);
        }
    }

    public void gone_FloatWindow(){
        if (mFloatLayout != null)
            mFloatLayout.setVisibility(View.GONE);
    }

    public void visible_FloatWindow(){
        if (mFloatLayout != null)
            mFloatLayout.setVisibility(View.VISIBLE);
    }

    public void setFloatLayoutAlpha(boolean alpha){
        if (alpha)
            mFloatLayout.setAlpha((float) 0.5);
        else
            mFloatLayout.setAlpha(1);
    }

    // ???????????????????????????
    public static int getSysBarHeight(Context contex) {
        Class<?> c;
        Object obj;
        Field field;
        int x;
        int sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = contex.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rel_round:
                image_round();
                break;
        }
    }


    public void image_round() {
        //???????????????
        if (x == 0) {
            x = Utils.get_X(rel_round, image_round_1);
        }

        if (prevWidth == 0) {
            prevWidth = rel_root.getWidth();
        }

        if (btn_open) {
            mAnimationManager.collapse(rel_root, 900, Utils.dp2px(mContext, 84));

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rel_round, "translationX", 0f, x);
            ObjectAnimator rotation_animator = ObjectAnimator.ofFloat(rel_round, "rotation", 0f, 360f);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(objectAnimator, rotation_animator);
            animSet.setDuration(900);
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

        } else {
            mAnimationManager.expand(rel_root, 900, prevWidth);

            voise_icon.setVisibility(View.GONE);
            voise_icon.stop();
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rel_round, "translationX", x, 0f);
            ObjectAnimator rotation_animator = ObjectAnimator.ofFloat(rel_round, "rotation", 360f, 0f);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(objectAnimator, rotation_animator);
            animSet.setDuration(900);
            animSet.start();
        }
        btn_open = !btn_open;
    }







}