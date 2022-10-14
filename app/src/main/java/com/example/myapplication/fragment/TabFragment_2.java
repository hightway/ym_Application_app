package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.widget.NestedScrollView;

import com.example.myapplication.R;
import com.example.myapplication.base.BaseLazyFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TabFragment_2 extends BaseLazyFragment {

    boolean isHide = true;
    int curMarginTop = 0;
    int dp200;

    @BindView(R.id.scollview)
    NestedScrollView scollview;
    @BindView(R.id.up_tv)
    TextView up_tv;

    @Override
    protected int setLayout() {
        return R.layout.tab_2_fragment_lay;
    }


    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        curMarginTop = Dp2Px(getActivity(), 200);
        dp200 = curMarginTop/2 -20;//向上还是向下的界线
        initMyView();

        /*scroll_down_layout.setOnScrollChangedListener(mOnScrollChangedListener);
        scroll_down_layout.getBackground().setAlpha(0);
        scroll_down_layout.setExitOffset(280);*/
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initMyView() {
        scollview.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (oldScrollY < scrollY) {// 向上
                    curMarginTop = curMarginTop - (scrollY - oldScrollY);
                } else if (oldScrollY > scrollY) {// 向下
                    curMarginTop = curMarginTop + (oldScrollY - scrollY);
                }
            }
        });

        scollview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    /*if (curMarginTop < -300) {
                        //缩进去
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                isHide = true;
                                scollview.smoothScrollTo(0, 0);
                            }
                        });
                    }

                    if(curMarginTop > 300){
                        //衍生上去
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                isHide = false;
                                scollview.smoothScrollTo(0, Dp2Px(getActivity(), 600));
                            }
                        });
                    }*/


                    if (curMarginTop < dp200) {
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                isHide = false;
                                scollview.smoothScrollTo(0, Dp2Px(getActivity(), 600));
                            }
                        });
                    } else {
                        //缩进去
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                isHide = true;
                                scollview.smoothScrollTo(0, 0);
                            }
                        });
                    }
                }
                return false;
            }
        });
    }


    public int Dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density; //当前屏幕密度因子
        return (int) (dp * scale + 0.5f);
    }


    @OnClick(R.id.up_tv)
    public void up_tv(){
        if (!isHide) {
            scollview.smoothScrollTo(0, 0);
        } else {
            scollview.smoothScrollTo(0, Dp2Px(getActivity(), 600));
        }
        isHide = !isHide;
    }


    /*private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
                float precent = 280 * currentProgress;
                if (precent > 280) {
                    precent = 280;
                } else if (precent < 0) {
                    precent = 0;
                }
                scroll_down_layout.getBackground().setAlpha(255 - (int) precent);
            }
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                //finish();
            }
        }

        @Override
        public void onChildScroll(int top) {
        }

    };*/

}