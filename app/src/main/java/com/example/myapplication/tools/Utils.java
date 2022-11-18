package com.example.myapplication.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;
import com.aliyun.subtitle.SubtitleView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utils {

    public static int get_X(View view_1, View view_2) {

        int[] viewLocation = new int[2];
        view_1.getLocationInWindow(viewLocation);
        int lldownX = viewLocation[0]; // x 坐标
        int lldownY = viewLocation[1]; // y 坐标


        int[] view_Location = new int[2];
        view_2.getLocationInWindow(view_Location);
        int addressitTeaddX = view_Location[0]; // x 坐标
        int addressitTeaddY = view_Location[1]; // y 坐标

        //距离
        int x = addressitTeaddX - lldownX;
        return x;
    }


    /**
     * dp转px
     *
     * @param context
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources()
                .getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param context
     * @return
     */
    public static int sp2px(Context context, float spVal) {
//        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources()
//                .getDisplayMetrics());
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spVal * fontScale + 0.5f);
    }


    public static Uri saveSong(Context context, int song) {
        byte[] buffer = null;
        InputStream fIn = context.getResources().openRawResource(song);
        int size = 0;

        try {
            size = fIn.available();
            buffer = new byte[size];
            fIn.read(buffer);
            fIn.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }

        String path = Environment.getExternalStorageDirectory() + "/Ringtones/";
        String filename = "song" + ".mp3";

        boolean exists = (new File(path)).exists();
        if (!exists) {
            new File(path).mkdirs();
        }
        exists = (new File(path + filename)).exists();
        if (!exists) {
            FileOutputStream save;
            try {
                save = new FileOutputStream(path + filename);
                save.write(buffer);
                save.flush();
                save.close();

                //扫描文件
                //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(new File(path + filename))));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //如果是4.4及以上版本， 方法1
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri contentUri = Uri.fromFile(new File(path + filename));
                    mediaScanIntent.setData(contentUri);
                    context.sendBroadcast(mediaScanIntent);

                    //或者方法2
                    /*String[] paths = new String[]{Environment.getExternalStorageDirectory().toString()};
                      MediaScannerConnection.scanFile(mContext, paths, null, null);*/

                } else {
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(new File(path + filename))));
                }
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                return null;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return null;
            }
        }
        return Uri.fromFile(new File(path + filename));
    }



    public static void init_Aliyun(AliPlayer mAliPlayer, Context instance, LinearLayout lin_roll){
        //用于显示SRT和VTT字幕
        SubtitleView subtitleView = new SubtitleView(instance);

        SubtitleView.DefaultValueBuilder defaultValueBuilder = new SubtitleView.DefaultValueBuilder();
        defaultValueBuilder.setColor("#FFFFFFFF");
        defaultValueBuilder.setSize(45);
        subtitleView.setDefaultValue(defaultValueBuilder);
        //用于显示ASS和SSA字幕
        //AssSubtitleView assSubtitleView = new AssSubtitleView(instance);
        //将字幕View添加到布局视图中
        lin_roll.addView(subtitleView);
        mAliPlayer.setOnSubtitleDisplayListener(new IPlayer.OnSubtitleDisplayListener() {
            @Override
            public void onSubtitleExtAdded(int trackIndex, String url) {
                mAliPlayer.selectExtSubtitle(trackIndex, true);
            }

            @Override
            public void onSubtitleShow(int trackIndex, long id, String data) {
                // ass 字幕
                //assSubtitleView.show(id,data);

                // srt 字幕
                SubtitleView.Subtitle subtitle = new SubtitleView.Subtitle();

                subtitle.id = id + "";
                subtitle.content = data;
                subtitleView.show(subtitle);

                /*lin_roll.bringToFront();
                lin_roll.setStateListAnimator(null);*/
            }

            @Override
            public void onSubtitleHide(int trackIndex, long id) {
                // ass 字幕
                //assSubtitleView.dismiss(id);
                // srt 字幕
                subtitleView.dismiss(id + "");
            }

            @Override
            public void onSubtitleHeader(int trackIndex, String header) {

            }
        });

        mAliPlayer.addExtSubtitle("https://test-maoyu.oss-cn-guangzhou.aliyuncs.com/%E5%81%8F%E7%88%B1.srt");
        UrlSource urlSource = new UrlSource();
        urlSource.setUri("https://test-maoyu.oss-cn-guangzhou.aliyuncs.com/%E5%81%8F%E7%88%B1.mp3");
        //设置播放源
        mAliPlayer.setLoop(true);
        mAliPlayer.setDataSource(urlSource);
        mAliPlayer.setAutoPlay(true);
        mAliPlayer.prepare();
    }


}
