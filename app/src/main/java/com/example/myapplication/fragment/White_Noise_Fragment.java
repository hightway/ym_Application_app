package com.example.myapplication.fragment;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FruitAdapter;
import com.example.myapplication.adapter.White_Noise_Adapter;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.Video_Info_Bean;
import com.example.myapplication.bean.White_Noise_Bean;
import com.example.myapplication.http.Api;
import com.example.myapplication.plmd.Put_Top;
import com.example.myapplication.swipeDrawer_view.Common;
import com.example.myapplication.swipeDrawer_view.OnDrawerChange;
import com.example.myapplication.swipeDrawer_view.SwipeDrawer;
import com.example.myapplication.tools.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class White_Noise_Fragment extends BaseLazyFragment {

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

    private List<White_Noise_Bean.DataBean> dataBeanList = new ArrayList<>();
    private White_Noise_Adapter listAdapter;
    private int page = 1;
    private int pageSize = 999;


    /*public White_Noise_Fragment(Put_Top put_top){
        this.put_top = put_top;
    }*/


    @Override
    protected int setLayout() {
        return R.layout.white_noise_lay;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);

        initData();
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
                            // 1.2??????????????????
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

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        mainList.setLayoutManager(layoutManager);
        listAdapter = new White_Noise_Adapter(getActivity());
        mainList.setAdapter(listAdapter);

        ListData();
    }

    private void ListData() {
        HashMap<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));
        map.put("size", String.valueOf(pageSize));
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "white_noises", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                        White_Noise_Bean white_noise_bean = mgson.fromJson(response, White_Noise_Bean.class);
                        List<White_Noise_Bean.DataBean> dataBeans = white_noise_bean.data;
                        if(dataBeans != null && dataBeans.size() > 0){
                            //listAdapter.setDataList(dataBeans);
                            if (page == 1) {
                                dataBeanList = dataBeans;
                                listAdapter.setDataList(dataBeanList);
                            } else {
                                reBottomText.setText(getString(R.string.load_more_success));
                                dataBeanList.addAll(dataBeans);
                                listAdapter.notifyItemRangeInserted(listAdapter.getItemCount(), dataBeans.size());
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

}
