package com.example.myapplication.custom;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

import com.example.myapplication.R;
import com.example.myapplication.tools.StatusBarUtil;

public class SlidingDrawerLayout extends RelativeLayout {

    public static final int DIRECTION_BOTTOM = 0;
    public static final int DIRECTION_TOP = 1;
    public static final int DIRECTION_LEFT = 2;
    public static final int DIRECTION_RIGHT = 3;
    private int mDirection = DIRECTION_BOTTOM;//上下左右四个方向抽屉
    private boolean mIsAllShow = true;
    private ValueAnimator mAnim;
    private int mVisibleLength = 0;//隐藏后可视部分长度
    int lastY;//上一个事件Y坐标
    int lastX;//上一个事件Y坐标
    private View mBodyView;
    private GestureDetector mGestureDetector;
    private Context context;

    public SlidingDrawerLayout(Context context) {
        super(context);
    }

    public SlidingDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlidingDrawerLayout);
        mDirection = a.getInt(R.styleable.SlidingDrawerLayout_direction, DIRECTION_BOTTOM);
        mVisibleLength = (int) a.getDimension(R.styleable.SlidingDrawerLayout_visiableLength, 36);
    }

    public SlidingDrawerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void init() {
        mBodyView = getChildAt(0);
        mGestureDetector = new GestureDetector(getContext(), new MoreGestureListener());
        post(new Runnable() {
            @Override
            public void run() {
                hide();//默认隐藏
            }
        });
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        Log.d("SlidingDrawerLayout", "ev.action = " + ev.getAction());
        int distance = getDistance(ev);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //判断落在子view上
                if (!isInView(mBodyView, ev)) {
                    return super.onTouchEvent(ev);
                }
                cancleAnimator();
                mGestureDetector.onTouchEvent(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                caulMargin(distance);
                if (isAnimationDirectionChange()) { //移动超过中间动画方向改变
                    mIsAllShow = false;
                } else {
                    mIsAllShow = true;
                }
                mGestureDetector.onTouchEvent(ev);//为什么不放在最前面，因为会比关闭/打开动画先触发
                return true;
            case MotionEvent.ACTION_UP:
                calAnimation();
                mGestureDetector.onTouchEvent(ev);
                return true;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private int getDistance(MotionEvent ev) {
        int y = (int) ev.getRawY();
        int x = (int) ev.getRawX();
        int distance = 0;
        switch (mDirection) {
            case DIRECTION_BOTTOM:
                distance = lastY - y;
                break;
            case DIRECTION_TOP:
                distance = y - lastY;
                break;
            case DIRECTION_LEFT:
                distance = x - lastX;
                break;
            case DIRECTION_RIGHT:
                distance = lastX - x;
                break;
        }
        lastY = y;
        lastX = x;
        return distance;
    }

    /**
     * 动画的方法是否改变,高度/宽度 减去可以长度的一半为分界线
     *
     * @return
     */
    private boolean isAnimationDirectionChange() {
        switch (mDirection) {
            case DIRECTION_BOTTOM:
            case DIRECTION_TOP:
                return getBodyMargin() > (getHeight() - mVisibleLength) / 2;
            case DIRECTION_LEFT:
            case DIRECTION_RIGHT:
                return getBodyMargin() > (getWidth() - mVisibleLength) / 2;
        }
        return false;
    }

    private void calAnimation() {
        if (mIsAllShow) {
            //startAnimator(0);
            startAnimator(StatusBarUtil.getStatusBarHeight(context));
        } else {
            startAnimator(getMaxLength());
        }
    }

    private void caulMargin(int distance) {
        Log.d("SlidingDrawerLayout", "getBodyMrgin = " + getBodyMargin());
        Log.d("SlidingDrawerLayout", "distance = " + distance);
        int margin = getBodyMargin() - distance;
        if (isAllowMargin(margin)) {//拖动区域限制
            setBodyMargin(margin);
        }
    }


    /**
     * 收起或打开动画
     *
     * @param end 结束高度
     */
    public void startAnimator(final int end) {

        if (mAnim != null && mAnim.isRunning()) {
            mAnim.cancel();
        }
        mAnim = ValueAnimator.ofInt(getBodyMargin(), end);
        mAnim.setDuration(200);
        mAnim.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnim.setTarget(this);
        mAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (Integer) animation.getAnimatedValue();
                setBodyMargin(currentValue);
                if (currentValue == end) {//切换状态
                    mIsAllShow = !mIsAllShow;
                }
            }
        });
        mAnim.start();

    }

    public void cancleAnimator() {
        if (mAnim != null && mAnim.isRunning()) {
            mAnim.cancel();
        }
    }


    /**
     * 返回是否在允许范围内
     *
     * @param margin
     * @return
     */
    private boolean isAllowMargin(int margin) {

        return margin >= 0 && margin <= (getHeight() - mVisibleLength);
    }

    public int getBodyMargin() {

        LayoutParams paramsBody = (LayoutParams) mBodyView.getLayoutParams();
        int topMargin = paramsBody.topMargin;

        switch (mDirection) {
            case DIRECTION_BOTTOM:
                topMargin = paramsBody.topMargin;
                break;
            case DIRECTION_TOP:
                topMargin = paramsBody.bottomMargin;
                break;
            case DIRECTION_LEFT:
                topMargin = paramsBody.rightMargin;
                break;
            case DIRECTION_RIGHT:
                topMargin = paramsBody.leftMargin;
                break;
        }
        return topMargin;
    }

    public void setBodyMargin(int margin) {
        Log.d("SlidingDrawerLayout", "margin = " + margin);
        LayoutParams paramsBody = (LayoutParams) mBodyView.getLayoutParams();
        switch (mDirection) {
            case DIRECTION_BOTTOM:
                paramsBody.topMargin = margin;
                break;
            case DIRECTION_TOP:
                paramsBody.bottomMargin = margin;
                break;
            case DIRECTION_LEFT:
                paramsBody.rightMargin = margin;
                break;
            case DIRECTION_RIGHT:
                paramsBody.leftMargin = margin;
                break;
        }
        mBodyView.setLayoutParams(paramsBody);
    }

    public int getMaxLength() {
        switch (mDirection) {
            case DIRECTION_BOTTOM:
            case DIRECTION_TOP:
                return getHeight() - mVisibleLength;
            default:
                return getWidth() - mVisibleLength;

        }
    }


    class MoreGestureListener implements GestureDetector.OnGestureListener {

        @Override
        public boolean onDown(MotionEvent ev) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            calAnimation();
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //当头部显示并且向上fling时候关闭头部（惯性视觉）
            switch (mDirection) {
                case DIRECTION_BOTTOM:
                    mIsAllShow = velocityY < 0;
                    break;
                case DIRECTION_TOP:
                    mIsAllShow = velocityY > 0;
                    break;
                case DIRECTION_LEFT:
                    mIsAllShow = velocityX > 0;
                    break;
                case DIRECTION_RIGHT:
                    mIsAllShow = velocityX < 0;
                    break;
            }
            calAnimation();
            return true;
        }
    }

    /**
     * 设置可视长度/高度
     *
     * @param visibleLength
     */
    public void setVisibleLength(int visibleLength) {
        mVisibleLength = visibleLength;
    }

    /**
     * 关闭
     */
    public void hide() {
        mIsAllShow = false;
        calAnimation();
    }

    /**
     * 显示
     */
    public void show() {
        mIsAllShow = true;
        calAnimation();
    }

    public boolean getmIsAllShow(){
        return mIsAllShow;
    }

    public boolean isShowing() {
        LayoutParams paramsBody = (LayoutParams) mBodyView.getLayoutParams();
        return paramsBody.topMargin <= 0;
    }

    /**
     * 判断触摸的点是否在view范围内
     */
    private boolean isInView(View v, MotionEvent event) {
        Rect frame = new Rect();
        v.getHitRect(frame);
        float eventX = event.getX();
        float eventY = event.getY();
        return frame.contains((int) eventX, (int) eventY);
    }
}