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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.myapplication.R;
import com.example.myapplication.adapter.FruitAdapter;
import com.example.myapplication.aliyun_oss.AliyunOSSUtils;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.bean.Fruit;
import com.example.myapplication.custom.DownLoadProgressbar;
import com.example.myapplication.custom.WrapSlidingDrawer;
import com.example.myapplication.swipeDrawer_view.Common;
import com.example.myapplication.swipeDrawer_view.OnDrawerChange;
import com.example.myapplication.swipeDrawer_view.SwipeDrawer;
import com.example.myapplication.tools.MMAlert;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hollowsoft.slidingdrawer.OnDrawerCloseListener;
import hollowsoft.slidingdrawer.OnDrawerOpenListener;
import hollowsoft.slidingdrawer.OnDrawerScrollListener;

public class TabFragment extends BaseLazyFragment implements AliyunOSSUtils.UploadListener,AliyunOSSUtils.Upload_ParListener,
        OnDrawerScrollListener, OnDrawerOpenListener, OnDrawerCloseListener {

    @BindView(R.id.tx_upload)
    TextView tx_upload;
    @BindView(R.id.tx_upload_video)
    TextView tx_upload_video;
    @BindView(R.id.dp_game_progress)
    DownLoadProgressbar dp_game_progress;
    @BindView(R.id.drawer)
    WrapSlidingDrawer drawer;
    @BindView(R.id.handle)
    TextView handle;

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

    private AliyunOSSUtils ossUtils;
    private ActivityResultLauncher launcher;

    private static final int GETICON_LOCAL = 2;
    private long max = 100; //总的大小
    private long current = 0; //当前下载大小
    Handler handler = new Handler();

    private List<Fruit> fruitList = new ArrayList<>();
    private FruitAdapter listAdapter;

    @Override
    protected int setLayout() {
        return R.layout.tab_fragment_lay;
    }

    @Override
    public boolean onBackPressed() {
        if(drawer != null){
            if(drawer.isOpened()){
                drawer.animateClose();
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
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
                if(result_code == -1){
                    doChoose(true, intent);
                }
            }
        });

        drawer.setOnDrawerScrollListener(this);
        drawer.setOnDrawerOpenListener(this);
        drawer.setOnDrawerCloseListener(this);

        //content.setScanScrollChangedListener(this);




        /*initFruit();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycle_view.setLayoutManager(layoutManager);
        FruitAdapter fruitAdapter = new FruitAdapter(fruitList);
        recycle_view.setAdapter(fruitAdapter);*/

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

                /*reTopText.setText("刷新完成");
                // 0.6秒后关闭
                reTopText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        content.closeDrawer();
                    }
                }, 300);*/
                content.closeDrawer();
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
                    case SwipeDrawer.STATE_PROGRESS : // 移动，progress 获取进度
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
                    case SwipeDrawer.STATE_OPEN : // 打开
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
                    case SwipeDrawer.STATE_DRAG_INTO : // 拖拽超过 shrinkRange 距离
                        if (!view.getShow()) {
                            if (isTop) {
                                reTopText.setText("松开刷新");
                            } else if (isBottom) {
                                reBottomText.setText("松开加载");
                            }
                        }
                        break;
                    case SwipeDrawer.STATE_DRAG_OUT : // 拖拽未超过 shrinkRange 距离
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
     * @param num 更新条数
     */
    private void SetList(int num) {
        if (num > 0) {
            for (int i = 0; i < num; i++) {
                fruitList.add(new Fruit("orange", R.mipmap.wx_icon));
                fruitList.add(new Fruit("waterMelon", R.mipmap.msm_icon));
            }
            listAdapter.notifyDataSetChanged();
        } else {
            fruitList.clear();
            listAdapter.notifyDataSetChanged();
            for (int i = 0; i < 20; i++) {
                Fruit orange = new Fruit("orange", R.mipmap.wx_icon);
                fruitList.add(orange);
                Fruit waterMelon = new Fruit("waterMelon", R.mipmap.msm_icon);
                fruitList.add(waterMelon);
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 给 RecyclerView 填充数据
     */
    private void ListData() {
        for(int i =20; i>0; i--){
            Fruit orange = new Fruit("orange",R.mipmap.wx_icon);
            fruitList.add(orange);
            Fruit waterMelon = new Fruit("waterMelon",R.mipmap.msm_icon);
            fruitList.add(waterMelon);
        }

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycle_view.setLayoutManager(layoutManager);
        listAdapter = new FruitAdapter(fruitList);
        recycle_view.setAdapter(listAdapter);
    }







    /*private void initFruit() {
        for(int i =20; i>0; i--){
            Fruit orange = new Fruit("orange",R.mipmap.wx_icon);
            fruitList.add(orange);
            Fruit waterMelon = new Fruit("waterMelon",R.mipmap.msm_icon);
            fruitList.add(waterMelon);
        }
    }*/



    private void doChoose(boolean b, Intent intent) {
        Uri uri = intent.getData();
        if(uri != null){
            String path = uri.getPath();
            String authority = uri.getAuthority();

            String pic_path = getRealPathFromUriAboveApi19(getActivity(), uri);

            if(!TextUtils.isEmpty(pic_path)){
                File file = new File(pic_path);
                String name = file.getName();

                //上传图片
                ossUtils.upLoadMultipleFile(name, pic_path);

                //分片上传视频
                //ossUtils.MultipartUpload(name, pic_path);
            }
        }
    }


    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    public static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())){
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
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



    //上传文件
    @OnClick(R.id.tx_upload)
    public void tx_upload(){

        ossUtils = AliyunOSSUtils.getInstance(getActivity());
        ossUtils.setUpLoadListener(this);

        //先拿到本地图片
        selectImg();
    }


    //上传视频
    @OnClick(R.id.tx_upload_video)
    public void tx_upload_video(){
        //初始化OSS
        ossUtils = AliyunOSSUtils.getInstance(getActivity());
        ossUtils.setUpLoad_PartListener(this);
        //访问本地视频

        Intent intentFromGallery = new Intent();
        /* 开启Pictures画面Type设定为image */
        //intentFromGallery .setType("image/*");
        //intentFromGallery .setType("audio/*"); //选择音频
        //intentFromGallery .setType("video/*"); //选择视频(mp4 3gp 是android支持的视频格式)
        intentFromGallery .setType("video/*;image/*");//同时选择视频和图片
        /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        launcher.launch(intentFromGallery);
    }




    @Override
    public void onUpLoadComplete(String url) {
        toast("上传成功 url：" + url);
    }

    @Override
    public void onUpLoad_Pro(long part, long total) {
        max = total;
        current = part;
        start();
    }


    private void selectImg() {
        MMAlert.showAlert(getActivity(), "照片", getResources().getStringArray(R.array.camer_item), null,
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


    //相册中获取
    protected void getImageFromGallery() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");//相片类型
            launcher.launch(intent);
        } catch (Exception e) {
            toast("调用相册失败");
        }
    }

    //分片上传的进度条
    @Override
    public void onUpLoad_PartComplete(long part, long total) {
        max = total;
        current = part;
        start();
    }


    public void start() {
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
            tx_upload.setText("上传完成，大小：" + max);

            dp_game_progress.setCurrentValue(max);
            handler.removeCallbacks(runnable);
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //current += 6;
            start();
        }
    };


    @Override
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

    }


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
