package com.example.myapplication.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
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
import com.example.myapplication.bean.Audio_Detail_Bean;
import com.example.myapplication.bean.Hor_DateBean;
import com.example.myapplication.bean.Noise_Bean;
import com.example.myapplication.bean.Video_Detail_Bean;
import com.example.myapplication.bean.White_Noise_Bean;
import com.example.myapplication.custom.DialogFragment;
import com.example.myapplication.custom.FloatWindow_View;
import com.example.myapplication.custom.ImageRound;
import com.example.myapplication.custom.MyCircleProgress;
import com.example.myapplication.custom.VoisePlayingIcon;
import com.example.myapplication.http.Api;
import com.example.myapplication.plmd.AliPlayer_Noise_Callback;
import com.example.myapplication.plmd.Radio_Click;
import com.example.myapplication.plmd.Radio_Click_Set;
import com.example.myapplication.plmd.White_Noise_Cliack;
import com.example.myapplication.plmd.White_Noise_Cliack_Set;
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

public class Video_Detail_Activity extends BaseActivity implements VideoViewPagerAdapter.ExoPlayerView_Click, White_Noise_Cliack, Radio_Click {

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
    private int current_noise_max;
    private DialogFragment dialogFragment;
    private String current_type;

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

        //init viewpager2
        //initViewPage2(urls);

        if (detail_bean != null) {
            initViewPage2(detail_bean.data);
            add_VideoData_Id(detail_bean.data.detail.id);
            //拿到初始白噪音
            white_noises.clear();
            white_noises.addAll(detail_bean.data.white_noises);
        }

        //设置白噪音点击监听事件
        White_Noise_Cliack_Set.setWhite_Noise_Cliack(this);
        //设置电台点击事件
        Radio_Click_Set.setRadio_Cliack(this);
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
        mViewPager2.setOffscreenPageLimit(1);
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

        //如果播放是电台，不切换
        if(!TextUtils.isEmpty(current_type) && current_type.equals("radio_stations")){
            //获取当前播放的电台
            Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean = white_noises.get(current_noise_index);
            white_noises.clear();

            current_noise_index = 0;
            white_noises.add(whiteNoisesBean);
            //重置白噪音
            List<Video_Detail_Bean.DataBean.WhiteNoisesBean> noisesBeans = (List<Video_Detail_Bean.DataBean.WhiteNoisesBean>) img_video_pic.getTag();
            if (noisesBeans != null && noisesBeans.size() > 0) {
                white_noises.addAll(noisesBeans);
            }
        }else{
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
        current_noise_max = 0;
        current_pro = 0;
        threadFlag = true;
        lin_roll.setVisibility(View.GONE);
        circle_progress.SetCurrent(0);
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
        //initView_FloatWindow();
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

    /*private void initView_FloatWindow() {
        if(MyApp.floatWindow_view == null){
            MyApp.floatWindow_view = new FloatWindow_View(getApplicationContext());
            MyApp.floatWindow_view.showFloatWindow();
        }
    }*/

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

        dialogFragment = new DialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "ss");
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
                Video_Detail_Bean.DataBean.WhiteNoisesBean bean = white_noises.get(current_noise_index);
                if(bean != null){
                    if(bean.resource_type.equals("radio_stations")){
                        //电台
                        getradio_station_detail(bean);
                    }else{
                        getwhite_noise_detail(bean);
                    }
                }
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
                Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean = white_noises.get(current_noise_index);
                init_noise_data(whiteNoisesBean);

                if(!TextUtils.isEmpty(whiteNoisesBean.resource_url)){
                    //直接播放
                    current_noise_url = whiteNoisesBean.resource_url;
                    current_noise_max = whiteNoisesBean.resource_duration;
                    current_type = whiteNoisesBean.resource_type;
                    if(!TextUtils.isEmpty(whiteNoisesBean.resource_type) && whiteNoisesBean.resource_type.equals("radio_stations")){
                        start_noise_play(current_noise_url, whiteNoisesBean.draft_url, current_type);
                    }else{
                        start_noise_play(current_noise_url, "", current_type);
                    }
                }else{
                    if(whiteNoisesBean.resource_type.equals("radio_stations")){
                        //电台
                        getradio_station_detail(whiteNoisesBean);
                    }else{
                        getwhite_noise_detail(whiteNoisesBean);
                    }
                }
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
                Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean = white_noises.get(current_noise_index);
                init_noise_data(whiteNoisesBean);

                if(!TextUtils.isEmpty(whiteNoisesBean.resource_url)){
                    //直接播放
                    current_noise_url = whiteNoisesBean.resource_url;
                    current_noise_max = whiteNoisesBean.resource_duration;
                    current_type = whiteNoisesBean.resource_type;
                    if(!TextUtils.isEmpty(whiteNoisesBean.resource_type) && whiteNoisesBean.resource_type.equals("radio_stations")){
                        start_noise_play(current_noise_url, whiteNoisesBean.draft_url, current_type);
                    }else{
                        start_noise_play(current_noise_url, "", current_type);
                    }
                }else{
                    if(whiteNoisesBean.resource_type.equals("radio_stations")){
                        //电台
                        getradio_station_detail(whiteNoisesBean);
                    }else{
                        getwhite_noise_detail(whiteNoisesBean);
                    }
                }
            }
        }
    }


    /**
     *  获取白噪音详情
     * */
    private void getwhite_noise_detail(Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("detail_id", String.valueOf(whiteNoisesBean.id));
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
                            //设置url
                            whiteNoisesBean.resource_url = dataBean.resource_url;

                            current_noise_url = dataBean.resource_url;
                            current_noise_max = dataBean.resource_duration;
                            if (!TextUtils.isEmpty(current_noise_url)) {
                                current_type = dataBean.resource_type;
                                start_noise_play(current_noise_url, "", current_type);
                            }
                        }
                    } else {
                        current_type = "white_noises";
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

    private void start_noise_play(String url, String draft_url, String current_type) {
        //终止循环
        threadFlag = true;
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //开始播放白噪音
        is_noise_play = true;
        img_2.setImageResource(R.mipmap.img_play_pause);
        Utils.init_Aliyun(MyApp.get_app_mAliPlayer(), MyApp.Aapp_context, lin_roll, url, draft_url, new AliPlayer_Noise_Callback(){
            @Override
            public void play_start() {
                current_pro = 0;
                threadFlag = false;
                setProgress();
            }

            @Override
            public void play_pause() {
                threadFlag = true;
            }

            @Override
            public void play_reStart() {
                if(threadFlag){
                    threadFlag = false;
                    setProgress();
                }
            }
        });
    }


    /**
     *  进度圆环
     * */
    private int current_pro = 0;
    public void setProgress(){
        if(current_noise_max <= 0){
            return;
        }

        int max = current_noise_max*1000;
        circle_progress.SetMax(max);
        Thread thread = new Thread(() -> {
            while (!threadFlag && current_pro < max) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                current_pro+=200;
                int finalJ = current_pro;
                runOnUiThread(() -> circle_progress.SetCurrent(finalJ));

                //循环播放，重置
                if(current_pro >= max){
                    current_pro = 0;
                }
            }
        });
        thread.start();
    }

    @Override
    public void noise_click(White_Noise_Bean.DataBean dataBean) {
        if(dialogFragment != null){
            dialogFragment.dismiss();
        }
        if(dataBean == null){
            return;
        }
        //去重
        boolean is_same = false;
        Video_Detail_Bean.DataBean.WhiteNoisesBean move_nioseBean = null;
        if(white_noises.size() > 0){
            for(Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean : white_noises){
                if(whiteNoisesBean.id.equals(dataBean.id) && whiteNoisesBean.resource_type.equals(dataBean.resource_type)){
                    //相同资源，不加入
                    is_same = true;
                    move_nioseBean = whiteNoisesBean;
                    if(!TextUtils.isEmpty(whiteNoisesBean.resource_url)){
                        dataBean.resource_url = whiteNoisesBean.resource_url;
                    }
                    break;
                }
            }
        }

        Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean = getBean(dataBean);
        //插入白噪音，并播放
        if(white_noises.size() == 0){
            white_noises.add(whiteNoisesBean);
        }else{
            white_noises.add(current_noise_index+1, whiteNoisesBean);
        }

        current_noise_index = white_noises.indexOf(whiteNoisesBean);
        init_noise_data(whiteNoisesBean);

        if(!TextUtils.isEmpty(whiteNoisesBean.resource_url)){
            //直接播放
            current_noise_url = whiteNoisesBean.resource_url;
            current_noise_max = whiteNoisesBean.resource_duration;
            current_type = whiteNoisesBean.resource_type;
            start_noise_play(current_noise_url, "", current_type);
        }else{
            getwhite_noise_detail(whiteNoisesBean);
        }

        //判断是否有相同的资源，有则移除
        if(is_same && move_nioseBean != null){
            white_noises.remove(move_nioseBean);
        }
    }

    private Video_Detail_Bean.DataBean.WhiteNoisesBean getBean(White_Noise_Bean.DataBean dataBean) {
        Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean = new Video_Detail_Bean.DataBean.WhiteNoisesBean();
        whiteNoisesBean.id = dataBean.id;
        whiteNoisesBean.title = dataBean.title;
        whiteNoisesBean.free = dataBean.free;
        whiteNoisesBean.play_volume = dataBean.play_volume;
        whiteNoisesBean.resource_duration = dataBean.resource_duration;
        whiteNoisesBean.resource_type = dataBean.resource_type;
        whiteNoisesBean.icon = dataBean.icon;
        whiteNoisesBean.updated_at = dataBean.updated_at;
        return whiteNoisesBean;
    }


    private Video_Detail_Bean.DataBean.WhiteNoisesBean getRadioBean(Hor_DateBean.DataBean.ListBean dataBean) {
        Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean = new Video_Detail_Bean.DataBean.WhiteNoisesBean();
        whiteNoisesBean.id = dataBean.id;
        whiteNoisesBean.title = dataBean.title;
        whiteNoisesBean.free = dataBean.free;
        whiteNoisesBean.play_volume = dataBean.play_volume;
        whiteNoisesBean.resource_duration = dataBean.resource_duration;
        whiteNoisesBean.resource_type = dataBean.resource_type;
        whiteNoisesBean.icon = dataBean.icon;
        whiteNoisesBean.updated_at = dataBean.updated_at;
        return whiteNoisesBean;
    }

    @Override
    public void audio_click(Hor_DateBean.DataBean.ListBean dataBean) {
        if(dialogFragment != null){
            dialogFragment.set_dismiss();
            dialogFragment.dismiss();
        }
        if(dataBean == null){
            return;
        }

        //去重
        boolean is_same = false;
        Video_Detail_Bean.DataBean.WhiteNoisesBean move_nioseBean = null;
        if(white_noises.size() > 0){
            for(Video_Detail_Bean.DataBean.WhiteNoisesBean whiteNoisesBean : white_noises){
                if(whiteNoisesBean.id.equals(dataBean.id) && whiteNoisesBean.resource_type.equals(dataBean.resource_type)){
                    //相同资源，不加入
                    is_same = true;
                    move_nioseBean = whiteNoisesBean;
                    if(!TextUtils.isEmpty(whiteNoisesBean.resource_url)){
                        dataBean.resource_url = whiteNoisesBean.resource_url;
                    }
                    if(!TextUtils.isEmpty(whiteNoisesBean.draft_url)){
                        dataBean.draft_url = whiteNoisesBean.draft_url;
                    }
                    break;
                }
            }
        }

        Video_Detail_Bean.DataBean.WhiteNoisesBean radio_bean = getRadioBean(dataBean);
        //插入电台，并播放
        if(white_noises.size() == 0){
            white_noises.add(radio_bean);
        }else{
            white_noises.add(current_noise_index+1, radio_bean);
        }

        current_noise_index = white_noises.indexOf(radio_bean);
        init_noise_data(radio_bean);

        if(!TextUtils.isEmpty(radio_bean.resource_url)){
            //直接播放
            current_noise_url = radio_bean.resource_url;
            current_noise_max = radio_bean.resource_duration;
            current_type = radio_bean.resource_type;
            start_noise_play(current_noise_url, radio_bean.draft_url, current_type);
        }else{
            //获取电台详情
            lin_roll.setVisibility(View.GONE);
            getradio_station_detail(radio_bean);
        }

        //判断是否有相同的资源，有则移除
        if(is_same && move_nioseBean != null){
            white_noises.remove(move_nioseBean);
        }
    }


    /**
     *  获取电台详情
     * */
    private void getradio_station_detail(Video_Detail_Bean.DataBean.WhiteNoisesBean radio_bean) {
        HashMap<String, String> map = new HashMap<>();
        map.put("detail_id", String.valueOf(radio_bean.id));
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "radio_station/detail", map, new OkHttpUtil.OnRequestNetWorkListener() {
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
                        Audio_Detail_Bean audio_detail_bean = mGson.fromJson(response, Audio_Detail_Bean.class);
                        Audio_Detail_Bean.DataBean dataBean = audio_detail_bean.data;
                        if (dataBean != null) {
                            //设置url
                            radio_bean.resource_url = dataBean.resource_url;
                            radio_bean.draft_url = dataBean.draft_url;

                            current_noise_url = dataBean.resource_url;
                            current_noise_max = dataBean.resource_duration;
                            if (!TextUtils.isEmpty(current_noise_url)) {
                                current_type = dataBean.resource_type;
                                start_noise_play(current_noise_url, dataBean.draft_url, current_type);
                            }
                        }
                    } else {
                        toast(jsonObject.getString("errMsg"));
                        stop_noise_play();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
