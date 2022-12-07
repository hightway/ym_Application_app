package com.example.myapplication.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;
import com.aliyun.subtitle.SubtitleView;
import com.example.myapplication.plmd.AliPlayer_Noise_Callback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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


    public static File saveSong(Context context, int song) {
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
        //return Uri.fromFile(new File(path + filename));
        File song_file = new File(path + filename);
        return song_file;
    }


    public static void init_Aliyun(AliPlayer mAliPlayer, Context instance, LinearLayout lin_roll, String url, String srt, AliPlayer_Noise_Callback callback) {
        //字幕
        if (!TextUtils.isEmpty(srt)) {
            lin_roll.setVisibility(View.VISIBLE);
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
            mAliPlayer.addExtSubtitle(srt);
        } else {
            lin_roll.setVisibility(View.GONE);
        }

        /*mAliPlayer.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                //播放完成事件
                callback.play_loop();
            }
        });*/

        mAliPlayer.setOnStateChangedListener(new IPlayer.OnStateChangedListener() {
            @Override
            public void onStateChanged(int newState) {
                //播放器状态改变事件
                /*int idle = 0;
                  int initalized = 1;
                  int prepared = 2;
                  int started = 3;
                  int paused = 4;
                  int stopped = 5;
                  int completion = 6;
                  int error = 7;*/
                if(newState == IPlayer.prepared){
                    callback.play_start();
                }else if(newState == IPlayer.paused){
                    callback.play_pause();
                }else if(newState == IPlayer.started){
                    callback.play_reStart();
                }/*else if(newState == IPlayer.completion){
                    callback.play_loop();
                }*/
            }
        });

        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        //设置播放源
        mAliPlayer.setLoop(true);
        mAliPlayer.setDataSource(urlSource);
        mAliPlayer.setAutoPlay(true);
        mAliPlayer.prepare();
    }


    /**
     * list拼接成字符串
     */
    public static String listToString(List list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i < list.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }


    public static String transfom(int time) {
        long shi = time / 3600;
        long fen = (time % 3600) / 60;
        long miao = (time % 3600) % 60;
        //shi< 10 ? ("0" + shi) : shi)判断时否大于10时的话就执行shi,否则执行括号中的
        String str;
        if (shi >= 1) {
            str = (shi < 10 ? ("0" + shi) : shi) + ":" + (fen < 10 ? ("0" + fen) : fen) + ":" + (miao < 10 ? ("0" + miao) : miao);
        } else {
            str = (fen < 10 ? ("0" + fen) : fen) + ":" + (miao < 10 ? ("0" + miao) : miao);
        }
        return str;
    }

    public static String transfom_min(int time) {
        String str;
        if(time >= 60){
            long shi = time / 60;
            long fen = time % 60;
            str = (shi + "小时" + (fen > 0 ? (fen + "分") : ""));
        }else{
            str = (time + "分钟");
        }
        return str;
    }


    //days * (1000 * 60 * 60 * 24)
    public static String transfom_time(int s_h, int s_m, int e_h, int e_m) {
        String code;
        if(s_h == 0){
            s_h = 24;
        }
        if(e_h == 0){
            e_h = 24;
        }
        if(s_m == 0){
            s_m = 60;
        }
        if(e_m == 0){
            e_m = 60;
        }
        if(e_h > s_h){
            //当天
            int data_min;
            int data_hour = e_h - s_h;
            if(e_m > s_m){
                //当天
                data_min = e_m - s_m;
            }else if(e_m == s_m){
                data_min = 0;
            }else{
                data_min = 60 - s_m + e_m;
                data_hour--;
            }
            code = (data_hour > 0 ? (data_hour+"小时") : "") + (data_min > 0 ? (data_min+"分") : "");
        }else if(s_h == e_h){
            //判断分钟
            if(e_m > s_m){
                //当天
                int data_min = e_m - s_m;
                code = data_min + "分";
            }else if(s_m == e_m){
                code = "24小时";
            }else{
                int data_min = 60 - s_m + e_m;
                code = "23小时" + data_min + "分";
            }
        }else{
            //第二天
            int data_min;
            int d = s_h - e_h;
            int data_hour = 24 - d;
            if(e_m > s_m){
                //当天
                data_min = e_m - s_m;
            }else if(e_m == s_m){
                data_min = 0;
            }else{
                data_min = 60 - s_m + e_m;
                data_hour--;
            }
            code = (data_hour > 0 ? (data_hour+"小时") : "") + (data_min > 0 ? (data_min+"分") : "");
        }
        return code;
    }





}
