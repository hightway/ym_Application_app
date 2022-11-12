package com.example.myapplication.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FruitAdapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.custom.FabScrollListener;
import com.example.myapplication.custom.MingRecyclerView;
import com.example.myapplication.plmd.HideScrollListener;
import com.example.myapplication.swipeDrawer_view.Common;
import com.example.myapplication.swipeDrawer_view.OnDrawerChange;
import com.example.myapplication.swipeDrawer_view.SwipeDrawer;
import com.example.myapplication.tools.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Video_More_Activity extends BaseActivity {

    private Video_More_Activity instance;
    @BindView(R.id.mainList)
    MingRecyclerView recycle_view;
    @BindView(R.id.content)
    SwipeDrawer content;
    @BindView(R.id.reTopIcon)
    ImageView reTopIcon;
    @BindView(R.id.reBottomIcon)
    ImageView reBottomIcon;
    @BindView(R.id.reTopText)
    TextView reTopText;
    @BindView(R.id.reBottomText)
    TextView reBottomText;
    @BindView(R.id.query_button)
    ImageView query_button;

    private List<Fruit> fruitList = new ArrayList<>();
    private FruitAdapter listAdapter;
    /*private int mFirstY;
    private int mCurrentY;
    private boolean direction;
    private int marginTop;*/


    @Override
    protected int getLayoutID() {
        instance = this;
        return R.layout.video_more_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);

        initData_SwipeDrawer();
        //顶部搜索栏高度
        //marginTop = Utils.dp2px(this, 44);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    private void initData_SwipeDrawer() {
        // 监听 SwipeDrawer 改变
        content.setOnDrawerChange(new OnDrawerChange() {

            // 刷新完毕
            private void topOver() {
                // 显示刷新完成状态
                SetList(0);
                reTopIcon.clearAnimation();
                reTopIcon.setRotation(0);
                reTopIcon.setVisibility(View.GONE);

                reTopText.setText("刷新完成");
                // 0.6秒后关闭
                reTopText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        content.closeDrawer();
                    }
                }, 300);
                //content.closeDrawer();
            }

            // 加载完毕
            private void bottomOver() {
                // 显示加载完成状态
                SetList(20);
                reBottomIcon.clearAnimation();
                reBottomIcon.setRotation(0);
                reBottomIcon.setVisibility(View.GONE);

                reBottomText.setText("加载完成");
                // 0.6秒后关闭
                reBottomText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        content.closeDrawer();
                    }
                }, 300);
            }

            @Override
            public void onChange(final SwipeDrawer view, int state, float progress) {
                boolean isTop = view.getDirection() == SwipeDrawer.DIRECTION_TOP;
                boolean isBottom = view.getDirection() == SwipeDrawer.DIRECTION_BOTTOM;
                switch (state) {
                    case SwipeDrawer.STATE_START: // 拖拽开始
                    case SwipeDrawer.STATE_CALL_OPEN: // 调用 openDrawer 方法打开
                        //Common.animHide(gesture, 200); // 隐藏方向引导
                        break;
                    case SwipeDrawer.STATE_PROGRESS: // 移动，progress 获取进度
                        if (!view.getShow() && view.getIntercept()) { // 非开启状态，且是手动拖拽
                            if (progress > 2) progress = 2; // 限制进度最大2倍
                            //topBar.getTopRightIcon().setRotation(progress * 360); // 头部右边图标根据进度旋转
                            if (progress > 1) progress = 1;
                            if (isTop) {
                                reTopIcon.setRotation(progress * 360); // 顶部刷新图标根据进度旋转
                            } else if (isBottom) {
                                reBottomIcon.setRotation(progress * 360); // 底部加载图标根据进度旋转
                            }
                        }
                        break;
                    case SwipeDrawer.STATE_OPEN: // 打开
                        //Common.animRotate(topBar.getTopRightIcon(), 600); // 头部右边图标旋转动画
                        if (isTop) {
                            reTopText.setText("正在刷新");
                            reTopIcon.setImageResource(R.mipmap.icon_more);
                            Common.animRotate(reTopIcon, 800); // 顶部刷新图标旋转动画
                            // 1.5秒后结束刷新
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (view.getShow()) {
                                        topOver();
                                    }
                                }
                            }, 1200);
                        } else if (isBottom) {
                            reBottomText.setText("正在加载");
                            reBottomIcon.setImageResource(R.mipmap.icon_more);
                            Common.animRotate(reBottomIcon, 800); // 底部加载图标旋转动画
                            // 1.5秒后结束加载
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (view.getShow()) {
                                        bottomOver();
                                    }
                                }
                            }, 1200);
                        }
                        break;
                    case SwipeDrawer.STATE_ANIM_OVER: // 动画执行完毕
                        if (view.getShow()) {
                            //Common.animHide(gesture, 200); // 隐藏方向引导
                        } else {
                            //Common.animShow(gesture, 200); // 显示方向引导
                            if (isTop) {
                                // 刷新完毕初始化布局状态
                                reTopIcon.clearAnimation();
                                reTopIcon.setRotation(0);
                                reTopIcon.setVisibility(View.VISIBLE);
                                reTopIcon.setImageResource(R.mipmap.icon_down);
                            } else if (isBottom) {
                                // 加载完毕初始化布局状态
                                reBottomIcon.clearAnimation();
                                reBottomIcon.setRotation(0);
                                reBottomIcon.setVisibility(View.VISIBLE);
                                reBottomIcon.setImageResource(R.mipmap.icon_up);
                            }
                        }
                        break;
                    case SwipeDrawer.STATE_DRAG_INTO: // 拖拽超过 shrinkRange 距离
                        if (!view.getShow()) {
                            if (isTop) {
                                reTopText.setText("松开刷新");
                            } else if (isBottom) {
                                reBottomText.setText("松开加载");
                            }
                        }
                        break;
                    case SwipeDrawer.STATE_DRAG_OUT: // 拖拽未超过 shrinkRange 距离
                        if (!view.getShow()) {
                            if (isTop) {
                                reTopText.setText("下拉刷新");
                            } else if (isBottom) {
                                reBottomText.setText("上拉加载");
                            }
                        }
                        break;
                }
            }
        });

        ListData();
    }


    /**
     * 更新 list 数据
     *
     * @param num 更新条数
     */
    private void SetList(int num) {
        if (num > 0) {
            for (int i = 0; i < num; i++) {
                fruitList.add(new Fruit("orange", R.mipmap.wx_icon));
                fruitList.add(new Fruit("waterMelon", R.mipmap.msm_icon));
            }
            listAdapter.notifyDataSetChanged();
        } else {
            fruitList.clear();
            listAdapter.notifyDataSetChanged();
            for (int i = 0; i < 20; i++) {
                Fruit orange = new Fruit("orange", R.mipmap.wx_icon);
                fruitList.add(orange);
                Fruit waterMelon = new Fruit("waterMelon", R.mipmap.msm_icon);
                fruitList.add(waterMelon);
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 给 RecyclerView 填充数据
     */
    private void ListData() {
        for (int i = 20; i > 0; i--) {
            Fruit orange = new Fruit("orange", R.mipmap.wx_icon);
            fruitList.add(orange);
            Fruit waterMelon = new Fruit("waterMelon", R.mipmap.msm_icon);
            fruitList.add(waterMelon);
        }

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycle_view.setLayoutManager(layoutManager);
        listAdapter = new FruitAdapter(fruitList, instance);
        recycle_view.setAdapter(listAdapter);



        /*recycle_view.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mFirstY = recycle_view.getTouchPointY();
                        mCurrentY = (int) motionEvent.getY();
                        RelativeLayout.LayoutParams top = (RelativeLayout.LayoutParams) search_bar.getLayoutParams();
                        if (mCurrentY - mFirstY > 0) {
                            //向下滑动
                            direction = false;
                        } else {
                            direction = true; //向上滑动
                        }
                        if (direction) {
                            //Log.d("uuu","上滑");
                            if (top.topMargin > -marginTop) {
                                top.topMargin += mCurrentY - mFirstY;
                                if(top.topMargin<-marginTop)
                                    top.topMargin=-marginTop;
                                //Log.d("uuu","top2:"+top.topMargin);
                                search_bar.requestLayout();
                            }
                        } else {
                            if (top.topMargin < 0) {
                                top.topMargin += mCurrentY - mFirstY;
                                if(top.topMargin>0)top.topMargin=0;
                                search_bar.requestLayout();
                            }
                        }
                        break;
                }
                return false;
            }
        });*/
    }


    @OnClick(R.id.query_button)
    public void query_button(){
        finish();
    }

}
