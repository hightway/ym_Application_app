package com.example.myapplication.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapter.More_Radio_Adapter;
import com.example.myapplication.adapter.Play_History_Adapter;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.His_DateBean;
import com.example.myapplication.swipeDrawer_view.Common;
import com.example.myapplication.swipeDrawer_view.OnDrawerChange;
import com.example.myapplication.swipeDrawer_view.SwipeDrawer;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class More_Radio_Fragment extends BaseLazyFragment {

    @BindView(R.id.mainList)
    RecyclerView mainList;
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

    private List<His_DateBean> fruitList = new ArrayList<>();
    private More_Radio_Adapter listAdapter;

    @Override
    protected int setLayout() {
        return R.layout.more_radio_lay;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        //初始化布局
        initData();
    }


    private void initData() {
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
                fruitList.add(new His_DateBean("白噪音", R.mipmap.wx_icon));
                fruitList.add(new His_DateBean("黑噪音", R.mipmap.msm_icon));
            }
            listAdapter.notifyDataSetChanged();
        } else {
            fruitList.clear();
            listAdapter.notifyDataSetChanged();
            for (int i = 0; i < 20; i++) {
                His_DateBean orange = new His_DateBean("绿噪音", R.mipmap.wx_icon);
                fruitList.add(orange);
                His_DateBean waterMelon = new His_DateBean("红噪音", R.mipmap.msm_icon);
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
            His_DateBean orange = new His_DateBean("绿噪音", R.mipmap.wx_icon);
            fruitList.add(orange);
            His_DateBean waterMelon = new His_DateBean("红噪音", R.mipmap.msm_icon);
            fruitList.add(waterMelon);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mainList.setLayoutManager(layoutManager);
        listAdapter = new More_Radio_Adapter(fruitList, getActivity());
        mainList.setAdapter(listAdapter);
    }
}
