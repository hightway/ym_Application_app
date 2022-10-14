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
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityOptionsCompat;

import com.example.myapplication.R;
import com.example.myapplication.activity.Password_Register_Activity;
import com.example.myapplication.aliyun_oss.AliyunOSSUtils;
import com.example.myapplication.base.BaseLazyFragment;
import com.example.myapplication.custom.DownLoadProgressbar;
import com.example.myapplication.custom.SmartScrollView;
import com.example.myapplication.custom.WrapSlidingDrawer;
import com.example.myapplication.tools.MMAlert;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hollowsoft.slidingdrawer.OnDrawerCloseListener;
import hollowsoft.slidingdrawer.OnDrawerOpenListener;
import hollowsoft.slidingdrawer.OnDrawerScrollListener;
import hollowsoft.slidingdrawer.SlidingDrawer;

public class TabFragment extends BaseLazyFragment implements AliyunOSSUtils.UploadListener,AliyunOSSUtils.Upload_ParListener,
        OnDrawerScrollListener, OnDrawerOpenListener, OnDrawerCloseListener, SmartScrollView.ISmartScrollChangedListener {

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
    SmartScrollView content;

    private AliyunOSSUtils ossUtils;
    private ActivityResultLauncher launcher;

    private static final int GETICON_LOCAL = 2;
    private long max = 100; //总的大小
    private long current = 0; //当前下载大小
    Handler handler = new Handler();

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
                if(result_code == -1){
                    doChoose(true, intent);
                }
            }
        });

        drawer.setOnDrawerScrollListener(this);
        drawer.setOnDrawerOpenListener(this);
        drawer.setOnDrawerCloseListener(this);

        content.setScanScrollChangedListener(this);
    }

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
        toast("1");
    }

    @Override
    public void onDrawerOpened() {
        toast("2");
    }

    @Override
    public void onScrollStarted() {

    }

    @Override
    public void onScrollEnded() {

    }


    //SmartScrollView
    @Override
    public void onScrolledToBottom() {

    }

    @Override
    public void onScrolledToTop() {
        if(drawer.isOpened()){
            drawer.animateClose();
        }
    }
}
