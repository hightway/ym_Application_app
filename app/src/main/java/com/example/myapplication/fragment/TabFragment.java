package com.example.myapplication.fragment;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.R;
import com.example.myapplication.activity.Video_More_Activity;
import com.example.myapplication.adapter.FruitAdapter;
import com.example.myapplication.adapter.ViewPage_Adapter;
import com.example.myapplication.aliyun_oss.AliyunOSSUtils;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.bean.User_Msg_Bean;
import com.example.myapplication.bean.Video_Info_Bean;
import com.example.myapplication.bean.Weather_Bean;
import com.example.myapplication.bean.Weather_Video_Bean;
import com.example.myapplication.custom.SlidingDrawerLayout;
import com.example.myapplication.http.Api;
import com.example.myapplication.http.UserConfig;
import com.example.myapplication.swipeDrawer_view.Common;
import com.example.myapplication.swipeDrawer_view.OnDrawerChange;
import com.example.myapplication.swipeDrawer_view.SwipeDrawer;
import com.example.myapplication.tools.MMAlert;
import com.example.myapplication.tools.OkHttpUtil;
import com.example.myapplication.tools.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hollowsoft.slidingdrawer.OnDrawerCloseListener;
import hollowsoft.slidingdrawer.OnDrawerOpenListener;
import hollowsoft.slidingdrawer.OnDrawerScrollListener;

//OnDrawerScrollListener, OnDrawerOpenListener, OnDrawerCloseListener
public class TabFragment extends BaseLazyFragment implements AliyunOSSUtils.UploadListener, AliyunOSSUtils.Upload_ParListener {

    @BindView(R.id.tx_upload)
    TextView tx_upload;
    @BindView(R.id.tx_upload_video)
    TextView tx_upload_video;
    //@BindView(R.id.dp_game_progress)
    //DownLoadProgressbar dp_game_progress;
    /*@BindView(R.id.drawer)
    WrapSlidingDrawer drawer;*/

    @BindView(R.id.sliding_lay)
    SlidingDrawerLayout sliding_lay;
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
    @BindView(R.id.video_viewpage)
    ViewPager video_viewpage;
    @BindView(R.id.tx_time)
    TextView tx_time;
    @BindView(R.id.tx_temperature)
    TextView tx_temperature;
    @BindView(R.id.name_hi)
    TextView name_hi;
    @BindView(R.id.tx_day)
    TextView tx_day;
    @BindView(R.id.tx_year)
    TextView tx_year;
    @BindView(R.id.rel_all_msg)
    RelativeLayout rel_all_msg;
    @BindView(R.id.layout_point)
    LinearLayout layout_point;
    @BindView(R.id.tx_more)
    TextView tx_more;

    private AliyunOSSUtils ossUtils;
    private ActivityResultLauncher launcher;
    private static final int GETICON_LOCAL = 2;
    private long max = 100; //????????????
    private long current = 0; //??????????????????

    private List<Fruit> fruitList = new ArrayList<>();
    private FruitAdapter listAdapter;
    private int page = 1;
    private int pageSize = 20;
    private List<Video_Info_Bean.DataBean> dataBeanList = new ArrayList<>();

    @Override
    protected int setLayout() {
        return R.layout.tab_fragment_lay;
    }

    @Override
    protected void initView(View view) {
        ButterKnife.bind(this, view);
        mActivity = getActivity();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent = result.getData();
                int result_code = result.getResultCode();
                if (result_code == -1) {
                    doChoose(true, intent);
                }
            }
        });

        initData();

        //???????????????
        if (mhandler != null) {
            mhandler.postDelayed(time_Runnable, 1000 * 60 * 5);
        }
    }


    public void setVideo_data(Weather_Video_Bean weather_video_bean) {
        Weather_Video_Bean.DataBean dataBean = weather_video_bean.data;
        List<Weather_Video_Bean.DataBean.VideoListBean> list = dataBean.video_list;
        if (list != null && list.size() > 0) {
            initVideo_viewpage(list);
        }

        //dataBean.
        tx_day.setText(String.valueOf(dataBean.day));
        tx_year.setText(dataBean.month);
        if(dataBean.daily_pick != null){
            tx_temperature.setText(dataBean.daily_pick.content);
        }

        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                /*Animation animation_1 = AnimationUtils.loadAnimation(getActivity(), R.anim.push_bottom_out);
                tx_time.startAnimation(animation_1);
                tx_temperature.startAnimation(animation_1);
                tx_time.setVisibility(View.GONE);
                tx_temperature.setVisibility(View.GONE);*/

                Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.push_bottom_in);
                tx_time.startAnimation(animation);
                tx_day.startAnimation(animation);
                tx_year.startAnimation(animation);
                name_hi.startAnimation(animation);
                tx_temperature.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        tx_time.setVisibility(View.VISIBLE);
                        tx_day.setVisibility(View.VISIBLE);
                        tx_year.setVisibility(View.VISIBLE);
                        name_hi.setVisibility(View.VISIBLE);
                        tx_temperature.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        //rel_all_msg.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                //????????????
                sliding_lay.setVisibility(View.VISIBLE);

            }
        }, 1000);
    }

    public void setWeather_data(Weather_Bean.DataBean.ResultBean resultBean) {

    }


    @Override
    public void onResume() {
        super.onResume();
        //???????????????
        if (tx_time != null) {
            tx_time.setText(getString(getTodayFlag()));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mhandler != null) {
            mhandler.removeCallbacks(time_Runnable);
        }
    }

    Runnable time_Runnable = new Runnable() {
        @Override
        public void run() {
            //???????????????
            if (tx_time != null) {
                tx_time.setText(getString(getTodayFlag()));
            }

            mhandler.postDelayed(time_Runnable, 1000 * 60 * 5);
        }
    };

    public static int getTodayFlag() {
        // ??????????????????
        Calendar c = Calendar.getInstance();
        // ????????????????????????int???
        int hour = c.get(Calendar.HOUR_OF_DAY);
        if (hour < 7) {
            return R.string.date_7;
        } else if (hour < 9) {
            return R.string.date_8;
        } else if (hour < 12) {
            return R.string.date_2;
        } else if (hour < 14) {
            return R.string.date_3;
        } else if (hour < 18) {
            return R.string.date_4;
        } else if (hour < 19) {
            return R.string.date_5;
        } else if (hour < 24) {
            return R.string.date_6;
        } else {
            return R.string.date_1;
        }
    }


    private boolean mIsChanged = false;
    private int mCurrentPagePosition = FIRST_ITEM_INDEX;
    private static int POINT_LENGTH = 0;
    private static int FIRST_ITEM_INDEX = 1;

    private void initVideo_viewpage(List<Weather_Video_Bean.DataBean.VideoListBean> list) {
        List<Fragment> fragments = new ArrayList<>();

        if (list.size() <= 1) {
            Video_Fragment video_fragment = new Video_Fragment(list.get(0));
            fragments.add(video_fragment);
        } else {
            POINT_LENGTH = list.size();
            Video_Fragment video_fragment_max = new Video_Fragment(list.get(list.size() - 1));
            fragments.add(video_fragment_max);

            for (Weather_Video_Bean.DataBean.VideoListBean videoListBean : list) {
                Video_Fragment video_fragment = new Video_Fragment(videoListBean);
                fragments.add(video_fragment);
            }

            Video_Fragment video_fragment_0 = new Video_Fragment(list.get(0));
            fragments.add(video_fragment_0);

            video_viewpage.setAdapter(new ViewPage_Adapter(getChildFragmentManager(), fragments));
            video_viewpage.setCurrentItem(1, false);
            video_viewpage.setOffscreenPageLimit(1);//????????????????????????


            for (int i = 0; i < POINT_LENGTH; i++) {
                //?????????????????????????????????LinearLayout?????????????????????5?????????????????????
                View view = new View(getActivity());
                view.setBackgroundResource(R.drawable.point_disable);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(32, 8);
                if (i != 0) {
                    lp.leftMargin = 16;
                }
                layout_point.addView(view, lp);
            }
            //??????????????????????????????0???????????????????????????
            layout_point.getChildAt(0).setBackgroundResource(R.drawable.point_enable);


            video_viewpage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int pPosition) {
                    mIsChanged = true;
                    if (pPosition > POINT_LENGTH) {// ?????????????????????????????????1???
                        mCurrentPagePosition = FIRST_ITEM_INDEX;
                    } else if (pPosition < FIRST_ITEM_INDEX) {// ?????????????????????????????????N???
                        mCurrentPagePosition = POINT_LENGTH;
                    } else {
                        mCurrentPagePosition = pPosition;
                    }

                    for (int i = 1; i <= layout_point.getChildCount(); i++) {
                        if (i == mCurrentPagePosition) {
                            layout_point.getChildAt(i - 1).setBackgroundResource(R.drawable.point_enable);
                        } else {
                            layout_point.getChildAt(i - 1).setBackgroundResource(R.drawable.point_disable);
                        }
                    }
                }


                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }


                @Override
                public void onPageScrollStateChanged(int pState) {
                    if (ViewPager.SCROLL_STATE_IDLE == pState) {
                        if (mIsChanged) {
                            mIsChanged = false;
                            video_viewpage.setCurrentItem(mCurrentPagePosition, false);

                            Video_Fragment video_fragment = (Video_Fragment) fragments.get(mCurrentPagePosition);
                            video_fragment.start_Video();

                            if (FIRST_ITEM_INDEX < mCurrentPagePosition && mCurrentPagePosition < POINT_LENGTH) {
                                Video_Fragment fragment_top = (Video_Fragment) fragments.get(mCurrentPagePosition - 1);
                                Video_Fragment fragment_btm = (Video_Fragment) fragments.get(mCurrentPagePosition + 1);
                                fragment_top.stop_Video();
                                fragment_btm.stop_Video();
                            } else if (mCurrentPagePosition == FIRST_ITEM_INDEX) {
                                Video_Fragment fragment_top = (Video_Fragment) fragments.get(POINT_LENGTH);
                                Video_Fragment fragment_btm = (Video_Fragment) fragments.get(mCurrentPagePosition + 1);
                                fragment_top.stop_Video();
                                fragment_btm.stop_Video();
                            } else if (mCurrentPagePosition == POINT_LENGTH) {
                                Video_Fragment fragment_top = (Video_Fragment) fragments.get(FIRST_ITEM_INDEX);
                                Video_Fragment fragment_btm = (Video_Fragment) fragments.get(mCurrentPagePosition - 1);
                                fragment_top.stop_Video();
                                fragment_btm.stop_Video();
                            }

                        }
                    }
                }
            });
        }
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
        listAdapter = new FruitAdapter(getActivity());
        recycle_view.setAdapter(listAdapter);

        ListData();
    }


    /**
     * ??? RecyclerView ????????????
     */
    private void ListData() {
        OkHttpUtil.postRequestNoDialog(Api.HEAD + "video_list", new OkHttpUtil.OnRequestNetWorkListener() {
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
                        Video_Info_Bean video_info_bean = mgson.fromJson(response, Video_Info_Bean.class);
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


    private void doChoose(boolean b, Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            String path = uri.getPath();
            String authority = uri.getAuthority();

            String pic_path = getRealPathFromUriAboveApi19(getActivity(), uri);

            if (!TextUtils.isEmpty(pic_path)) {
                File file = new File(pic_path);
                String name = file.getName();

                //????????????
                ossUtils.upLoadMultipleFile(name, pic_path);

                //??????????????????
                //ossUtils.MultipartUpload(name, pic_path);
            }
        }
    }


    /**
     * ??????api19?????????,??????uri???????????????????????????
     *
     * @param context ???????????????
     * @param uri     ?????????Uri
     * @return ??????Uri?????????????????????, ????????????????????????????????????, ????????????null
     */
    @SuppressLint("NewApi")
    public static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // ?????????document????????? uri, ?????????document id???????????????
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // ??????':'??????
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // ????????? content ????????? Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // ????????? file ????????? Uri,?????????????????????????????????
            filePath = uri.getPath();
        }
        return filePath;
    }


    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }


    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    //????????????
    @OnClick(R.id.tx_upload)
    public void tx_upload() {

        ossUtils = AliyunOSSUtils.getInstance(getActivity());
        ossUtils.setUpLoadListener(this);

        //?????????????????????
        selectImg();
    }


    //????????????
    @OnClick(R.id.tx_upload_video)
    public void tx_upload_video() {
        //?????????OSS
        ossUtils = AliyunOSSUtils.getInstance(getActivity());
        ossUtils.setUpLoad_PartListener(this);
        //??????????????????

        Intent intentFromGallery = new Intent();
        /* ??????Pictures??????Type?????????image */
        //intentFromGallery .setType("image/*");
        //intentFromGallery .setType("audio/*"); //????????????
        //intentFromGallery .setType("video/*"); //????????????(mp4 3gp ???android?????????????????????)
        intentFromGallery.setType("video/*;image/*");//???????????????????????????
        /* ??????Intent.ACTION_GET_CONTENT??????Action */
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intentFromGallery);
    }


    @Override
    public void onUpLoadComplete(String url) {
        toast("???????????? url???" + url);
    }

    @Override
    public void onUpLoad_Pro(long part, long total) {
        max = total;
        current = part;
        //start();
    }


    private void selectImg() {
        MMAlert.showAlert(getActivity(), "??????", getResources().getStringArray(R.array.camer_item), null,
                new MMAlert.OnAlertSelectId() {
                    @Override
                    public void onClick(int whichButton) {
                        switch (whichButton) {
                            case 0:
                                getImageFromGallery();
                                break;
                            case 1:
                                //getImageFromCamera();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }


    //???????????????
    protected void getImageFromGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");//????????????
            launcher.launch(intent);
        } catch (Exception e) {
            toast("??????????????????");
        }
    }

    //????????????????????????
    @Override
    public void onUpLoad_PartComplete(long part, long total) {
        max = total;
        current = part;
        //start();
    }


    public SlidingDrawerLayout getWrapSlidingDrawer() {
        return sliding_lay;
    }

    @OnClick(R.id.tx_more)
    public void tx_more() {
        startActivity(new Intent(getActivity(), Video_More_Activity.class));
    }


    /*public void start() {
        if (current <= max) {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tx_upload.setText(current + "/" + max);
                }
            });

            dp_game_progress.setMaxValue(max);
            dp_game_progress.setCurrentValue(current);
            handler.postDelayed(runnable, 500);
        } else {
            tx_upload.setText("????????????????????????" + max);

            dp_game_progress.setCurrentValue(max);
            handler.removeCallbacks(runnable);
        }
    }*/

    /*Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //start();
        }
    };*/


    /*@Override
    public void onDrawerClosed() {
        //toast("1");
    }

    @Override
    public void onDrawerOpened() {
        //toast("2");
    }

    @Override
    public void onScrollStarted() {

    }

    @Override
    public void onScrollEnded() {

    }*/


    //SmartScrollView
    /*@Override
    public void onScrolledToBottom() {

    }

    @Override
    public void onScrolledToTop() {
        if(drawer.isOpened()){
            drawer.animateClose();
        }
    }*/

}
