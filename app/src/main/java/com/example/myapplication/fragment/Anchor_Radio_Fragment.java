package com.example.myapplication.fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        //???????????????
        initData();

        //????????????????????????
        //Radio_Click_Set.setRadio_Cliack(this);
    }


    private void initData() {
        // ?????? SwipeDrawer ??????
        content.setOnDrawerChange(new OnDrawerChange() {
            // ????????????
            private void topOver() {
                // ????????????????????????
                //SetList(0);
                page=1;
                ListData();
                reTopIcon.clearAnimation();
                reTopIcon.setRotation(0);
                reTopIcon.setVisibility(View.GONE);

                reTopText.setText(getString(R.string.load_more_success));
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
                //SetList(20);
                page++;
                ListData();
                reBottomIcon.clearAnimation();
                reBottomIcon.setRotation(0);
                reBottomIcon.setVisibility(View.GONE);

                //reBottomText.setText("????????????");
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

        /*LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mainList.setLayoutManager(layoutManager);
        listAdapter = new Anchor_Radio_CommonAdapter(getActivity(), R.layout.item_radio_item, dataBeanList);
        mainList.setAdapter(listAdapter);

        //??????header??????????????????
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
        //????????????
        mainList.setAdapter(headerFooterWrapper);


        //???????????????????????????--??????????????????????????????
        listAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
            //??????????????????????????????
            @Override
            public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                //????????????????????????????????????????????????1??????
                //???????????????????????????0??????
                Hor_DateBean.DataBean.ListBean appInfo = dataBeanList.get(position - 1);
            }

            //??????????????????????????????---??????????????????400ms????????????
            @Override
            public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                return false;
            }
        });*/


        radio_commonAdapter = new Radio_CommonAdapter(getActivity());
        radio_commonAdapter.setGridDataList(dataBeanList);
        mainList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //???????????????????????????, ??????????????????????????????
        mainList.setFocusable(false);
        mainList.setHasFixedSize(true);
        mainList.setAdapter(radio_commonAdapter);

        View view_head = LayoutInflater.from(getActivity()).inflate(R.layout.layout_radio_header, mainList, false);
        hor_recycleview = view_head.findViewById(R.id.rv_view);
        LinearLayout lin_refresh = view_head.findViewById(R.id.lin_refresh);
        TextView tx_more = view_head.findViewById(R.id.tx_more);
        tx_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                more_audio();
            }
        });
        lin_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page=1;
                ListData();
            }
        });

        mainList.addHeaderView(view_head);

        ListData();
    }


    //??????????????????
    private void init_hor(List<Hor_DateBean.DataBean.ListBean> topBeans) {
        hor_recycleview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        HosGridViewAdapter hosGridViewAdapter = new HosGridViewAdapter(topBeans, getActivity());
        hor_recycleview.setAdapter(hosGridViewAdapter);
    }


    /**
     * ??? RecyclerView ????????????
     */
    private void ListData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("size", String.valueOf(pageSize));
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "radio_stations", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
