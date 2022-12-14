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

        //???????????????
        initData();
    }


    private void initData() {
        // ?????? SwipeDrawer ??????
        content.setOnDrawerChange(new OnDrawerChange() {
            // ????????????
            private void topOver() {
                // ????????????????????????
                page=1;
                ListData();
                reTopIcon.clearAnimation();
                reTopIcon.setRotation(0);
                reTopIcon.setVisibility(View.GONE);

                reTopText.setText("????????????");
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
                page++;
                ListData();
                reBottomIcon.clearAnimation();
                reBottomIcon.setRotation(0);
                reBottomIcon.setVisibility(View.GONE);

                reBottomText.setText("????????????");
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
        listAdapter = new Video_History_Adapter(getActivity());
        recycle_view.setAdapter(listAdapter);

        ListData();
    }

    /**
     * ??? RecyclerView ????????????
     */
    private void ListData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("size", String.valueOf(pageSize));
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "history/featured_video", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("????????????");
                if(!TextUtils.isEmpty(err) && err.contains("401")){
                    //?????????????????????????????????
                    Aliyun_Login_Util.getInstance().initSDK(getActivity());
                }
            }

            @Override
            public void un_login_err() {
                //?????????????????????????????????
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
