package com.example.myapplication.fragment;

import android.app.Activity;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.Play_History_Adapter;
import com.example.myapplication.adapter.Video_History_Adapter;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.His_DateBean;
import com.example.myapplication.bean.Video_History_Bean;
import com.example.myapplication.http.Api;
import com.example.myapplication.swipeDrawer_view.Common;
import com.example.myapplication.swipeDrawer_view.OnDrawerChange;
import com.example.myapplication.swipeDrawer_view.SwipeDrawer;
import com.example.myapplication.tools.Aliyun_Login_Util;
import com.example.myapplication.tools.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class History_Video_Fragment extends BaseLazyFragment {

    @BindView(R.id.content)
    SwipeDrawer content;
    @BindView(R.id.mainList)
    RecyclerView recycle_view;
    @BindView(R.id.reTopIcon)
    ImageView reTopIcon;
    @BindView(R.id.reBottomIcon)
    ImageView reBottomIcon;
    @BindView(R.id.reTopText)
    TextView reTopText;
    @BindView(R.id.reBottomText)
    TextView reBottomText;

    private int page = 1;
    private int pageSize = 20;
    private List<Video_History_Bean.DataBean> dataBeanList = new ArrayList<>();
    private Video_History_Adapter listAdapter;


    @Override
    protected int setLayout() {
        return R.layout.frag_video_lay;
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
                page=1;
                ListData();
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
                page++;
                ListData();
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

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycle_view.setLayoutManager(layoutManager);
        listAdapter = new Video_History_Adapter(getActivity());
        recycle_view.setAdapter(listAdapter);

        ListData();
    }

    /**
     * 给 RecyclerView 填充数据
     */
    private void ListData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("size", String.valueOf(pageSize));
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "history/featured_video", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
                if(!TextUtils.isEmpty(err) && err.contains("401")){
                    //未登录，看详情需要登录
                    Aliyun_Login_Util.getInstance().initSDK(getActivity());
                }
            }

            @Override
            public void un_login_err() {
                //未登录，看详情需要登录
                Aliyun_Login_Util.getInstance().initSDK(getActivity());
            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        Video_History_Bean his_dateBean = mgson.fromJson(response, Video_History_Bean.class);
                        List<Video_History_Bean.DataBean> listBeans = his_dateBean.data;

                        if(listBeans != null && listBeans.size() > 0){
                            if (page == 1) {
                                dataBeanList = listBeans;
                                listAdapter.setGridDataList(dataBeanList);
                            } else {
                                reBottomText.setText(getString(R.string.load_more_success));
                                dataBeanList.addAll(listBeans);
                                listAdapter.notifyItemRangeInserted(listAdapter.getItemCount(), listBeans.size());
                            }
                        }else{
                            if(page == 1){
                                listAdapter.clean();
                            }else{
                                reBottomText.setText(getString(R.string.load_more_null));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
