package com.example.myapplication.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.MyApp;
import com.example.myapplication.R;
import com.example.myapplication.adapter.VideoViewPagerAdapter;
import com.example.myapplication.base.BaseActivity;
import com.example.myapplication.bean.Noise_Bean;
import com.example.myapplication.bean.Video_Detail_Bean;
import com.example.myapplication.custom.DialogFragment;
import com.example.myapplication.custom.Fglass;
import com.example.myapplication.custom.ImageRound;
import com.example.myapplication.custom.MyCircleProgress;
import com.example.myapplication.custom.VoisePlayingIcon;
import com.example.myapplication.http.Api;
import com.example.myapplication.tools.DialogUtils;
import com.example.myapplication.tools.ExpandOrCollapse;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.tools.Utils;
import com.example.myapplication.videoplayTool.AppUtil;
import com.example.myapplication.videoplayTool.VideoPlayManager;
import com.example.myapplication.videoplayTool.VideoPlayTask;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Video_Detail_Activity extends BaseActivity implements VideoViewPagerAdapter.ExoPlayerView_Click {

    private Video_Detail_Activity instance;
    @BindView(R.id.image_round)
    ImageRound image_round;
    @BindView(R.id.image_round_1)
    ImageRound image_round_1;
    @BindView(R.id.rel_round)
    RelativeLayout rel_round;
    @BindView(R.id.rel_root)
    RelativeLayout rel_root;
    @BindView(R.id.voise_icon)
    VoisePlayingIcon voise_icon;
    @BindView(R.id.circle_progress)
    MyCircleProgress circle_progress;

    @BindView(R.id.viewpager2)
    ViewPager2 mViewPager2;
    @BindView(R.id.img_4)
    ImageView img_4;
    /*@BindView(R.id.layout_point)
    LinearLayout layout_point;*/
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.lin_roll)
    LinearLayout lin_roll;
    @BindView(R.id.img_back)
    ImageView img_back;
    @BindView(R.id.img_more)
    ImageView img_more;
    @BindView(R.id.img_2)
    ImageView img_2;
    @BindView(R.id.img_3)
    ImageView img_3;
    @BindView(R.id.img_1)
    ImageView img_1;
    @BindView(R.id.tx_name)
    TextView tx_name;
    @BindView(R.id.tx_long)
    TextView tx_long;

    private boolean btn_open = true;
    private int x;
    private ExpandOrCollapse mAnimationManager;
    private int prevWidth = 0;
    private boolean threadFlag = false;
    private VideoViewPagerAdapter mVideoViewPagerAdapter;
    //private int pos = 0;
    private List<Video_Detail_Bean.DataBean.DetailBean> detailBeanList = new ArrayList<>();
    private List<Integer> data_id = new ArrayList<>();
    private int current_id;
    private String current_url;
    private int current_position;
    private List<Video_Detail_Bean.DataBean.WhiteNoisesBean> white_noises = new ArrayList<>();
    private int current_noise_index = 0;
    private String current_noise_url;
    private boolean is_noise_play = false;

    @Override
    protected int getLayoutID() {
        instance = this;
        setBar_color_transparent(R.color.transparent);
        return R.layout.video_detail_lay;
    }

    @Override
    public void viewClick(View v) {

    }

    @Override
    protected void initView() {
        ButterKnife.bind(this);
        Video_Detail_Bean detail_bean = (Video_Detail_Bean) getIntent().getSerializableExtra("detail_bean");

        //音乐栏动画
        mAnimationManager = new ExpandOrCollapse();

        //进度圆环
        circle_progress.SetMax(500);
        Thread thread = new Thread(() -> {
            int j = 0;
            while (!threadFlag && j < 500) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                j++;
                int finalJ = j;
                runOnUiThread(() -> circle_progress.SetCurrent(finalJ));
            }
        });
        thread.start();


        //init viewpager2
        //initViewPage2(urls);

        if (detail_bean != null) {
            initViewPage2(detail_bean.data);
            add_VideoData_Id(detail_bean.data.detail.id);
            //拿到初始白噪音
            white_noises.clear();
            white_noises.addAll(detail_bean.data.white_noises);
        }
    }

    private void add_VideoData_Id(Integer id) {
        if (!data_id.contains(id)) {
            data_id.add(id);
        }
    }


    private void initViewPage2(Video_Detail_Bean.DataBean dataBean) {
        detailBeanList.add(dataBean.detail);
        if (dataBean.next_list != null && dataBean.next_list.size() > 0) {
            detailBeanList.addAll(dataBean.next_list);
        }

        mVideoViewPagerAdapter = new VideoViewPagerAdapter(instance);
        mVideoViewPagerAdapter.setExoPlayerView_Click(this);
        mVideoViewPagerAdapter.setDataList(detailBeanList);
        mViewPager2.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        mViewPager2.setAdapter(mVideoViewPagerAdapter);
        mViewPager2.setOffscreenPageLimit(2);
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);


                if (!TextUtils.isEmpty(mVideoViewPagerAdapter.getUrlByPos(position))) {
                    if (position == 0) {
                        View itemView = mViewPager2.findViewWithTag(position);
                        ImageView img_video_pic = itemView.findViewById(R.id.img_video_pic);
                        img_video_pic.setTag(dataBean.white_noises);
                    }
                    start_play(mVideoViewPagerAdapter.get_id(position), mVideoViewPagerAdapter.getUrlByPos(position), position, null);
                } else {
                    //请求数据
                    get_Video_Detail(mVideoViewPagerAdapter.get_Bean(position), position);
                }



                /*if(TextUtils.isEmpty(mVideoViewPagerAdapter.getUrlByPos(position))){
                    return;
                }
                View itemView = mViewPager2.findViewWithTag(position);
                SimpleExoPlayerView simpleExoPlayerView = itemView.findViewById(R.id.video_view);
                ImageView img_video_pic = itemView.findViewById(R.id.img_video_pic);
                ImageView start = itemView.findViewById(R.id.start);
                //VideoPlayManager.getInstance(AppUtil.getApplicationContext()).setCurVideoPlayTask(new VideoPlayTask(simpleExoPlayerView, img_video_pic, start, mVideoViewPagerAdapter.getUrlByPos(position)));
                VideoPlayManager.getInstance(AppUtil.getApplicationContext()).setCurVideoPlayTask(new VideoPlayTask(simpleExoPlayerView, img_video_pic, start,
                        mVideoViewPagerAdapter.getUrlByPos(position)));
                //开始播放
                VideoPlayManager.getInstance(AppUtil.getApplicationContext()).startPlay();*/



                /*for(int i=0; i<layout_point.getChildCount(); i++){
                    if(i == position){
                        layout_point.getChildAt(i).setBackgroundResource(R.drawable.point_enable);
                    }else{
                        layout_point.getChildAt(i).setBackgroundResource(R.drawable.point_disable);
                    }
                }

                int l_height = layout_point.getHeight();
                int s_height = scrollView.getHeight();
                if(l_height > s_height){
                    View view = layout_point.getChildAt(position);
                    int top = view.getTop();
                    int height = view.getHeight();
                    if(pos < position){
                        if((top + height) >= s_height){
                            scrollView.scrollBy(0, height+14);
                        }
                    }else{
                        if(l_height - top >= s_height){
                            scrollView.scrollBy(0, -(height+14));
                        }
                    }
                }
                pos = position;*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });


        /*for(int i=0; i<urls.size(); i++){
            //创建下标小白点，然后用LinearLayout作为父容器，把小白点加进去
            View view = new View(instance);
            view.setBackgroundResource(R.drawable.point_disable);
            LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(8, 28);
            if(i!=0){
                lp.topMargin = 14;
            }
            layout_point.addView(view,lp);
        }
        //设置小白点的默认位置0是选中状态的白点。
        layout_point.getChildAt(0).setBackgroundResource(R.drawable.point_enable);*/
    }


    private void start_play(int id, String url, int position, List<Video_Detail_Bean.DataBean.DetailBean> list) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        //如果id和url都相同，不播放
        if (current_id == id && current_url.equals(url) && current_position == position) {
            return;
        }
        current_id = id;
        current_url = url;
        current_position = position;

        //添加到exclude
        add_VideoData_Id(id);

        View itemView = mViewPager2.findViewWithTag(position);
        SimpleExoPlayerView simpleExoPlayerView = itemView.findViewById(R.id.video_view);
        ImageView img_video_pic = itemView.findViewById(R.id.img_video_pic);
        ImageView start = itemView.findViewById(R.id.start);
        //VideoPlayManager.getInstance(AppUtil.getApplicationContext()).setCurVideoPlayTask(new VideoPlayTask(simpleExoPlayerView, img_video_pic, start, mVideoViewPagerAdapter.getUrlByPos(position)));
        VideoPlayManager.getInstance(AppUtil.getApplicationContext()).setCurVideoPlayTask(new VideoPlayTask(simpleExoPlayerView, img_video_pic, start, url));

        //开始播放
        VideoPlayManager.getInstance(AppUtil.getApplicationContext()).startPlay();

        if (list != null && list.size() > 0) {
            //添加下一个bean
            mVideoViewPagerAdapter.addDataList(list);
        }


        //停止上一个白噪音播放
        current_noise_index = 0;
        stop_noise_play();
        //重置白噪音
        List<Video_Detail_Bean.DataBean.WhiteNoisesBean> noisesBeans = (List<Video_Detail_Bean.DataBean.WhiteNoisesBean>) img_video_pic.getTag();
        if (noisesBeans != null && noisesBeans.size() > 0) {
            white_noises.clear();
            white_noises.addAll(noisesBeans);
            init_noises();
        } else {
            image_round.setImageResource(R.mipmap.loading_icon);
        }
    }

    private void init_noises() {
        if (white_noises.size() > 0) {
            Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean = white_noises.get(0);
            init_noise_data(whiteNoisesBean);
        }
    }

    private void init_noise_data(Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean) {
        if (whiteNoisesBean != null) {
            tx_name.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tx_name.setText(whiteNoisesBean.title);
                    if (whiteNoisesBean.resource_duration > 0) {
                        tx_long.setText(Utils.transfom(whiteNoisesBean.resource_duration));
                    } else {
                        tx_long.setText("00:00");
                    }
                }
            }, 200);
            Glide.with(instance)
                    .load(whiteNoisesBean.icon)
                    .apply(RequestOptions
                            .bitmapTransform(new CircleCrop())
                            .error(R.mipmap.loading_icon)
                            .placeholder(R.mipmap.loading_icon))
                    .into(image_round);
        }
    }

    /**
     *  停止白噪音播放
     * */
    private void stop_noise_play() {
        current_noise_url = "";
        is_noise_play = false;
        voise_icon.setVisibility(View.GONE);
        voise_icon.stop();
        img_2.setImageResource(R.mipmap.img_play_2);
        if (MyApp.app_mAliPlayer != null) {
            MyApp.app_mAliPlayer.release();
            MyApp.app_mAliPlayer = null;
        }
    }


    private void get_Video_Detail(Video_Detail_Bean.DataBean.DetailBean detail, int position) {
        DialogUtils.getInstance().showDialog(instance, "加载中...");
        HashMap<String, String> map = new HashMap<>();
        map.put("detail_id", String.valueOf(detail.id));
        if (data_id.size() > 0) {
            /*String code = String.valueOf(data_id);
            String bb = code.replace("[", "");
            String cc = bb.replace("]", "");*/
            map.put("exclude[]", Utils.listToString(data_id, ","));
        }
        OkHttpUtil.postRequest(Api.HEAD + "featured_video/detail", map, new OkHttpUtil.OnRequestNetWorkListener() {
            @Override
            public void notOk(String err) {
                new Throwable("请求失败");
                if (!TextUtils.isEmpty(err) && err.contains("401")) {
                    //未登录，看详情需要登录
                    //Aliyun_Login_Util.getInstance().initSDK((Activity) instance);
                }
            }

            @Override
            public void un_login_err() {
                //未登录，看详情需要登录
                //Aliyun_Login_Util.getInstance().initSDK((Activity) instance);
            }

            @Override
            public void ok(String response, JSONObject jsonObject) {
                try {
                    int code = jsonObject.getInt("errCode");
                    if (code == 200) {
                        Video_Detail_Bean video_detail_bean = mGson.fromJson(response, Video_Detail_Bean.class);
                        if (video_detail_bean != null) {
                            detail.resource_url = video_detail_bean.data.detail.resource_url;
                            //tag
                            View itemView = mViewPager2.findViewWithTag(position);
                            ImageView img_video_pic = itemView.findViewById(R.id.img_video_pic);
                            img_video_pic.setTag(video_detail_bean.data.white_noises);
                            //播放
                            start_play(detail.id, detail.resource_url, position, video_detail_bean.data.next_list);
                        }
                    } else {
                        toast(jsonObject.getString("errMsg"));
                        if (code == 6451) {
                            //停止上一个白噪音播放
                            stop_noise_play();
                            Video_Detail_Bean video_detail_bean = mGson.fromJson(response, Video_Detail_Bean.class);
                            if (video_detail_bean != null) {
                                //播放
                                if (video_detail_bean.data.next_list != null && video_detail_bean.data.next_list.size() > 0) {
                                    //添加下一个bean
                                    current_id = 0;
                                    current_url = "";
                                    current_position = 0;
                                    current_noise_index = 0;
                                    //添加到exclude
                                    add_VideoData_Id(video_detail_bean.data.next_list.get(0).id);
                                    //清空白噪音
                                    white_noises.clear();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            image_round.setImageResource(R.mipmap.loading_icon);
                                            tx_name.setText(getString(R.string.noise_no));
                                            tx_long.setText("00:00");
                                        }
                                    });

                                    //停止视频播放
                                    VideoPlayManager.getInstance(AppUtil.getApplicationContext()).stopPlay();
                                    mVideoViewPagerAdapter.addDataList(video_detail_bean.data.next_list);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        VideoPlayManager.getInstance(AppUtil.getApplicationContext()).resumePlay();
    }

    @Override
    public void onPause() {
        super.onPause();
        VideoPlayManager.getInstance(AppUtil.getApplicationContext()).pausePlay();
    }


    @Override
    protected void onDestroy() {
        //终止循环
        threadFlag = true;
        VideoPlayManager.getInstance(AppUtil.getApplicationContext()).stopPlay();
        stop_noise_play();
        super.onDestroy();
    }

    @OnClick(R.id.img_back)
    public void img_back() {
        //终止循环
        threadFlag = true;
        VideoPlayManager.getInstance(AppUtil.getApplicationContext()).stopPlay();
        stop_noise_play();
        finish();
    }

    @OnClick(R.id.img_more)
    public void img_more() {
        showMorePoprWindow();
    }

    public void showMorePoprWindow() {
        View inflate = LayoutInflater.from(instance).inflate(R.layout.more_pop_lay, null, false);
        PopupWindow mPopupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout lin_1 = inflate.findViewById(R.id.lin_1);
        LinearLayout lin_2 = inflate.findViewById(R.id.lin_2);
        lin_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        lin_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // 弹出动画
        //mPopupWindow.setAnimationStyle(anim);
        // 设置点击窗口外边窗口消失，必须在ShowAtLocation方法之前调用
        mPopupWindow.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        mPopupWindow.setFocusable(true);

        mPopupWindow.setBackgroundDrawable(null);
        //mPopupWindow.showAsDropDown(img_more);
        mPopupWindow.showAsDropDown(img_more, -(Utils.dp2px(instance, 86)), 0);
        //popup和dialog的不同就是必须要有依赖的布局控件，设置在这个布局的上下左右等位置
        //mPopupWindow.showAtLocation(img_more, Gravity.BOTTOM, 0, 0); //设置pop在控件rl_parent底部显示
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.image_round)
    public void image_round() {
        //只获取一次
        if (x == 0) {
            x = Utils.get_X(rel_round, image_round_1);
        }

        if (prevWidth == 0) {
            prevWidth = rel_root.getWidth();
        }

        if (btn_open) {
            mAnimationManager.collapse(rel_root, 900, Utils.dp2px(instance, 84));

            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rel_round, "translationX", 0f, x);
            ObjectAnimator rotation_animator = ObjectAnimator.ofFloat(rel_round, "rotation", 0f, 360f);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(objectAnimator, rotation_animator);
            animSet.setDuration(900);
            animSet.start();
            animSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if(is_noise_play){
                        voise_icon.setVisibility(View.VISIBLE);
                        voise_icon.start();
                    }else{
                        voise_icon.setVisibility(View.GONE);
                        voise_icon.stop();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

        } else {
            mAnimationManager.expand(rel_root, 900, prevWidth);

            voise_icon.setVisibility(View.GONE);
            voise_icon.stop();
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(rel_round, "translationX", x, 0f);
            ObjectAnimator rotation_animator = ObjectAnimator.ofFloat(rel_round, "rotation", 360f, 0f);
            AnimatorSet animSet = new AnimatorSet();
            animSet.playTogether(objectAnimator, rotation_animator);
            animSet.setDuration(900);
            animSet.start();
        }
        btn_open = !btn_open;
    }


    @OnClick(R.id.img_4)
    public void img_4() {
        //getPopupWindow(instance, R.style.showPopupAnimation);

        /*MyDialogFragment customLoseDialog = new MyDialogFragment();
        customLoseDialog.show(getSupportFragmentManager(), "lose");*/

        DialogFragment dialogFragment = new DialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "ss");

        /*BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(instance);
        //BottomSheetDialogFragment mBottomSheetDialog = new BottomSheetDialogFragment();

        View view1 = getLayoutInflater().inflate(R.layout.layout_meua, null);
        view1.setBackgroundResource(R.color.transparent);
        mBottomSheetDialog.setContentView(view1);

        TabLayout tl_tabs = mBottomSheetDialog.getWindow().findViewById(R.id.meua_tabs);
        ViewPager meua_viewpage = mBottomSheetDialog.getWindow().findViewById(R.id.meua_viewpage);
        List<String> tab_name = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        tab_name.add(getString(R.string.meua_1));
        tab_name.add(getString(R.string.meua_2));
        tab_name.add(getString(R.string.meua_3));
        fragmentList.add(new White_Noise_Fragment());
        fragmentList.add(new Anchor_Radio_Fragment());
        fragmentList.add(new Play_History_Fragment());
        ViewPage_Meua_Adapter adapter = new ViewPage_Meua_Adapter(getSupportFragmentManager(), fragmentList, tab_name);
        meua_viewpage.setAdapter(adapter);
        tl_tabs.setupWithViewPager(meua_viewpage);

        mBottomSheetDialog.show();*/

    }


    public void getPopupWindow(Activity mContext, int anim) {
        WindowManager wm = getWindowManager();
        Display d = wm.getDefaultDisplay();
        int d_height = d.getHeight();

        View inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_meua, null, false);
        PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(d_height * 4 / 5);

        //点击非菜单部分退出
        /*inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });*/

        /*TextView img_miss = inflate.findViewById(R.id.tx_ok);
        img_miss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });*/

        popupWindow.setBackgroundDrawable(null);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(anim);

        //解决软件盘 "adjust_Pan"在使用时获取焦点的控件下边的View将会被软键盘覆盖
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        popupWindow.showAtLocation(inflate, Gravity.NO_GRAVITY, 0, d_height * 4 / 5);
    }

    @Override
    public void view_vlick() {
        VideoPlayTask videoPlayTask = VideoPlayManager.getInstance(AppUtil.getApplicationContext()).getCurVideoPlayTask();
        if (videoPlayTask != null) {
            SimpleExoPlayerView playerView = videoPlayTask.getSimpleExoPlayerView();
            if (playerView != null) {
                if (playerView.getPlayer().isPlaying()) {
                    VideoPlayManager.getInstance(AppUtil.getApplicationContext()).pausePlay();
                } else {
                    VideoPlayManager.getInstance(AppUtil.getApplicationContext()).resumePlay();
                }
            }
        }
    }

    @OnClick(R.id.img_2)
    public void img_2() {
        if (TextUtils.isEmpty(current_noise_url)) {
            //请求白噪音资源
            if (white_noises.size() > 0 && current_noise_index < white_noises.size()) {
                getwhite_noise_detail(white_noises.get(current_noise_index).id);
            }
        } else {
            if (MyApp.app_mAliPlayer == null) {
                return;
            }

            if (is_noise_play) {
                MyApp.app_mAliPlayer.pause();
                img_2.setImageResource(R.mipmap.img_play_2);
            } else {
                MyApp.app_mAliPlayer.start();
                img_2.setImageResource(R.mipmap.img_play_pause);
            }
            is_noise_play = !is_noise_play;
        }
    }

    /**
     * 下一首
     */
    @OnClick(R.id.img_3)
    public void img_3() {
        if (white_noises.size() > 0) {
            if(white_noises.size() == 1){
                toast(getString(R.string.noise_no_more));
                return;
            }

            stop_noise_play();
            current_noise_index++;
            if (current_noise_index >= white_noises.size()) {
                current_noise_index = current_noise_index % white_noises.size();
            }
            //请求白噪音资源
            if (current_noise_index < white_noises.size()) {
                init_noise_data(white_noises.get(current_noise_index));
                getwhite_noise_detail(white_noises.get(current_noise_index).id);
            }
        }
    }

    /**
     * 上一首
     */
    @OnClick(R.id.img_1)
    public void img_1() {
        if (white_noises.size() > 0) {
            if(white_noises.size() == 1){
                toast(getString(R.string.noise_no_more));
                return;
            }

            stop_noise_play();
            current_noise_index--;
            if (current_noise_index < 0) {
                current_noise_index = white_noises.size() - 1;
            }
            //请求白噪音资源
            if (current_noise_index < white_noises.size()) {
                init_noise_data(white_noises.get(current_noise_index));
                getwhite_noise_detail(white_noises.get(current_noise_index).id);
            }
        }
    }


    private void getwhite_noise_detail(Integer id) {
        HashMap<String, String> map = new HashMap<>();
        map.put("detail_id", String.valueOf(id));
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "white_noise/detail", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                        Noise_Bean video_detail_bean = mGson.fromJson(response, Noise_Bean.class);
                        Noise_Bean.DataBean dataBean = video_detail_bean.data;
                        if (dataBean != null) {
                            current_noise_url = dataBean.resource_url;
                            if (!TextUtils.isEmpty(current_noise_url)) {
                                start_noise_play(current_noise_url);
                            }
                        }
                    } else {
                        toast(jsonObject.getString("errMsg"));
                        /*if(code == 6451){
                            //Video_Detail_Bean video_detail_bean = mGson.fromJson(response, Video_Detail_Bean.class);
                        }*/
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void start_noise_play(String url) {
        //开始播放白噪音
        is_noise_play = true;
        img_2.setImageResource(R.mipmap.img_play_pause);
        Utils.init_Aliyun(MyApp.get_app_mAliPlayer(), MyApp.Aapp_context, lin_roll, url, "");
    }

}
