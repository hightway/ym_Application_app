package com.example.myapplication.fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donkingliang.headerviewadapter.view.HeaderRecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.adapter.Anchor_Radio_CommonAdapter;
import com.example.myapplication.adapter.HosGridViewAdapter;
import com.example.myapplication.adapter.Radio_CommonAdapter;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.Hor_DateBean;
import com.example.myapplication.custom.More_DialogFragment;
import com.example.myapplication.http.Api;
import com.example.myapplication.plmd.Radio_Click;
import com.example.myapplication.plmd.Radio_Click_Set;
import com.example.myapplication.swipeDrawer_view.Common;
import com.example.myapplication.swipeDrawer_view.OnDrawerChange;
import com.example.myapplication.swipeDrawer_view.SwipeDrawer;
import com.example.myapplication.tools.OkHttpUtil;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Anchor_Radio_Fragment extends BaseLazyFragment {

    @BindView(R.id.mainList)
    HeaderRecyclerView mainList;
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

    private List<Hor_DateBean.DataBean.ListBean> dataBeanList = new ArrayList<>();
    private Anchor_Radio_CommonAdapter listAdapter;
    private HeaderAndFooterWrapper headerFooterWrapper;
    private int page = 1;
    private int pageSize = 20;
    private RecyclerView hor_recycleview;
    private Radio_CommonAdapter radio_commonAdapter;
    private More_DialogFragment dialogFragment;


    @Override
    protected int setLayout() {
        return R.layout.anchor_radio_lay;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        //初始化布局
        initData();

        //设置电台点击事件
        //Radio_Click_Set.setRadio_Cliack(this);
    }


    private void initData() {
        // 监听 SwipeDrawer 改变
        content.setOnDrawerChange(new OnDrawerChange() {
            // 刷新完毕
            private void topOver() {
                // 显示刷新完成状态
                //SetList(0);
                page=1;
                ListData();
                reTopIcon.clearAnimation();
                reTopIcon.setRotation(0);
                reTopIcon.setVisibility(View.GONE);

                reTopText.setText(getString(R.string.load_more_success));
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
                //SetList(20);
                page++;
                ListData();
                reBottomIcon.clearAnimation();
                reBottomIcon.setRotation(0);
                reBottomIcon.setVisibility(View.GONE);

                //reBottomText.setText("加载完成");
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

        /*LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mainList.setLayoutManager(layoutManager);
        listAdapter = new Anchor_Radio_CommonAdapter(getActivity(), R.layout.item_radio_item, dataBeanList);
        mainList.setAdapter(listAdapter);

        //添加header—装饰者模式
        headerFooterWrapper = new HeaderAndFooterWrapper(listAdapter);
        View headerView = View.inflate(getContext(), R.layout.layout_radio_header,null);
        hor_recycleview = headerView.findViewById(R.id.rv_view);
        TextView tx_more = headerView.findViewById(R.id.tx_more);
        tx_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more_audio();
            }
        });
        //init_hor(hor_recycleview);
        headerFooterWrapper.addHeaderView(headerView);
        //添加头部
        mainList.setAdapter(headerFooterWrapper);


        //设置条目点击监听了--获取到点击条目的位置
        listAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            //当条目点击的时候执行
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                //因为有头布局所以应用的条目索引从1开始
                //然而集合的索引是从0开始
                Hor_DateBean.DataBean.ListBean appInfo = dataBeanList.get(position - 1);
            }

            //当长按条目的时候执行---按下不放超过400ms就算长按
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });*/


        radio_commonAdapter = new Radio_CommonAdapter(getActivity());
        radio_commonAdapter.setGridDataList(dataBeanList);
        mainList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //解决数据加载完成后, 没有停留在顶部的问题
        mainList.setFocusable(false);
        mainList.setHasFixedSize(true);
        mainList.setAdapter(radio_commonAdapter);

        View view_head = LayoutInflater.from(getActivity()).inflate(R.layout.layout_radio_header, mainList, false);
        hor_recycleview = view_head.findViewById(R.id.rv_view);
        TextView tx_more = view_head.findViewById(R.id.tx_more);
        tx_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more_audio();
            }
        });
        mainList.addHeaderView(view_head);

        ListData();
    }


    //水平滑动布局
    private void init_hor(List<Hor_DateBean.DataBean.ListBean> topBeans) {
        hor_recycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        HosGridViewAdapter hosGridViewAdapter = new HosGridViewAdapter(topBeans, getActivity());
        hor_recycleview.setAdapter(hosGridViewAdapter);
    }


    /**
     * 给 RecyclerView 填充数据
     */
    private void ListData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("size", String.valueOf(pageSize));
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "radio_stations", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
            }

            @Override
            public void un_login_err() {

            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        Hor_DateBean white_noise_bean = mgson.fromJson(response, Hor_DateBean.class);
                        Hor_DateBean.DataBean dataBean = white_noise_bean.data;
                        List<Hor_DateBean.DataBean.ListBean> topBeans = dataBean.top;
                        if(page == 1 && topBeans != null && topBeans.size() > 0){
                            init_hor(topBeans);
                        }

                        List<Hor_DateBean.DataBean.ListBean> listBeans = dataBean.list;
                        if(listBeans != null && listBeans.size() > 0){
                            if (page == 1) {
                                dataBeanList = listBeans;
                                radio_commonAdapter.setGridDataList(dataBeanList);
                            } else {
                                reBottomText.setText(getString(R.string.load_more_success));
                                dataBeanList.addAll(listBeans);
                                radio_commonAdapter.notifyItemRangeInserted(radio_commonAdapter.getItemCount() + 1, listBeans.size());
                            }
                        }else{
                            if(page == 1){
                                listAdapter.clean();
                            }else{
                                reBottomText.setText(getString(R.string.load_more));
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void more_audio() {
        dialogFragment = new More_DialogFragment();
        dialogFragment.show(getChildFragmentManager(), "ss");
    }


    public void set_dismiss(){
        if(dialogFragment != null){
            dialogFragment.dismiss();
        }
    }

    /*@Override
    public void audio_click(Hor_DateBean.DataBean.ListBean dataBean) {
        if(dialogFragment != null){
            dialogFragment.dismiss();
        }
    }*/
}
