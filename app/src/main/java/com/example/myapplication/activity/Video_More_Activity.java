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
import com.example.myapplication.bean.Video_Info_Bean;
import com.example.myapplication.custom.FabScrollListener;
import com.example.myapplication.custom.MingRecyclerView;
import com.example.myapplication.http.Api;
import com.example.myapplication.plmd.HideScrollListener;
import com.example.myapplication.swipeDrawer_view.Common;
import com.example.myapplication.swipeDrawer_view.OnDrawerChange;
import com.example.myapplication.swipeDrawer_view.SwipeDrawer;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.tools.Utils;

import org.json.JSONException;
import org.json.JSONObject;

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
    private int page = 1;
    private int pageSize = 20;
    private List<Video_Info_Bean.DataBean> dataBeanList = new ArrayList<>();


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
        //?????????????????????
        //marginTop = Utils.dp2px(this, 44);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    private void initData_SwipeDrawer() {
        // ?????? SwipeDrawer ??????
        content.setOnDrawerChange(new OnDrawerChange() {

            // ????????????
            private void topOver() {
                // ????????????????????????
                page = 1;
                ListData();
                reTopIcon.clearAnimation();
                reTopIcon.setRotation(0);
                reTopIcon.setVisibility(View.GONE);

                reTopText.setText(getString(R.string.refresh_finish));
                // 0.6????????????
                reTopText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        content.closeDrawer();
                    }
                }, 300);
                //content.closeDrawer();
            }

            // ????????????
            private void bottomOver() {
                // ????????????????????????
                /*page++;
                ListData();*/
                reBottomIcon.clearAnimation();
                reBottomIcon.setRotation(0);
                reBottomIcon.setVisibility(View.GONE);

                reBottomText.setText(getString(R.string.load_more));
                // 0.6????????????
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
                    case SwipeDrawer.STATE_START: // ????????????
                    case SwipeDrawer.STATE_CALL_OPEN: // ?????? openDrawer ????????????
                        //Common.animHide(gesture, 200); // ??????????????????
                        break;
                    case SwipeDrawer.STATE_PROGRESS: // ?????????progress ????????????
                        if (!view.getShow() && view.getIntercept()) { // ????????????????????????????????????
                            if (progress > 2) progress = 2; // ??????????????????2???
                            //topBar.getTopRightIcon().setRotation(progress * 360); // ????????????????????????????????????
                            if (progress > 1) progress = 1;
                            if (isTop) {
                                reTopIcon.setRotation(progress * 360); // ????????????????????????????????????
                            } else if (isBottom) {
                                reBottomIcon.setRotation(progress * 360); // ????????????????????????????????????
                            }
                        }
                        break;
                    case SwipeDrawer.STATE_OPEN: // ??????
                        //Common.animRotate(topBar.getTopRightIcon(), 600); // ??????????????????????????????
                        if (isTop) {
                            reTopText.setText("????????????");
                            reTopIcon.setImageResource(R.mipmap.icon_more);
                            Common.animRotate(reTopIcon, 800); // ??????????????????????????????
                            // 1.5??????????????????
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (view.getShow()) {
                                        topOver();
                                    }
                                }
                            }, 1200);
                        } else if (isBottom) {
                            reBottomText.setText("????????????");
                            reBottomIcon.setImageResource(R.mipmap.icon_more);
                            Common.animRotate(reBottomIcon, 800); // ??????????????????????????????
                            // 1.5??????????????????
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
                    case SwipeDrawer.STATE_ANIM_OVER: // ??????????????????
                        if (view.getShow()) {
                            //Common.animHide(gesture, 200); // ??????????????????
                        } else {
                            //Common.animShow(gesture, 200); // ??????????????????
                            if (isTop) {
                                // ?????????????????????????????????
                                reTopIcon.clearAnimation();
                                reTopIcon.setRotation(0);
                                reTopIcon.setVisibility(View.VISIBLE);
                                reTopIcon.setImageResource(R.mipmap.icon_down);
                            } else if (isBottom) {
                                // ?????????????????????????????????
                                reBottomIcon.clearAnimation();
                                reBottomIcon.setRotation(0);
                                reBottomIcon.setVisibility(View.VISIBLE);
                                reBottomIcon.setImageResource(R.mipmap.icon_up);
                            }
                        }
                        break;
                    case SwipeDrawer.STATE_DRAG_INTO: // ???????????? shrinkRange ??????
                        if (!view.getShow()) {
                            if (isTop) {
                                reTopText.setText("????????????");
                            } else if (isBottom) {
                                reBottomText.setText("????????????");
                            }
                        }
                        break;
                    case SwipeDrawer.STATE_DRAG_OUT: // ??????????????? shrinkRange ??????
                        if (!view.getShow()) {
                            if (isTop) {
                                reTopText.setText("????????????");
                            } else if (isBottom) {
                                reBottomText.setText("????????????");
                            }
                        }
                        break;
                }
            }
        });

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycle_view.setLayoutManager(layoutManager);
        listAdapter = new FruitAdapter(instance);
        recycle_view.setAdapter(listAdapter);

        ListData();
    }


    /**
     * ??? RecyclerView ????????????
     */
    private void ListData() {
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "all_video_list", new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("????????????");
            }

            @Override
            public void un_login_err() {

            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        Video_Info_Bean video_info_bean = mGson.fromJson(response, Video_Info_Bean.class);
                        List<Video_Info_Bean.DataBean> dataBeans = video_info_bean.data;

                        if(dataBeans != null && dataBeans.size() > 0){
                            dataBeanList = dataBeans;
                            listAdapter.setDataList(dataBeanList);
                            /*if (page == 1) {
                                dataBeanList = dataBeans;
                                listAdapter.setDataList(dataBeanList);
                            } else {
                                dataBeanList.addAll(dataBeans);
                                listAdapter.notifyItemRangeInserted(listAdapter.getItemCount(), dataBeans.size());
                            }*/
                        } else {
                            if(page == 1){
                                listAdapter.clean();
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @OnClick(R.id.query_button)
    public void query_button(){
        finish();
    }

}
