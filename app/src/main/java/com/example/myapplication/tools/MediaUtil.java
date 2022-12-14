package com.example.myapplication.tools;

import static com.aliyun.player.nativeclass.NativePlayerBase.getContext;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.text.TextUtils;

import com.example.myapplication.R;
import com.example.myapplication.bean.Raw_Bean;
import com.example.myapplication.http.UserConfig;

public class MediaUtil {

    private static MediaPlayer mMediaPlayer;
    //渐强的时长，单位：毫秒；默认4秒
    static final long duration = 10000;
    //音量调节的时间间隔
    static long interval = duration / 10;

    //开始播放
    public static void playRing(Context context, int source, String painless_arousal) {
        try {

            //ADJUST_LOWER 降低音量
            //ADJUST_RAISE 升高音量
            //AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
            //am.setStreamVolume(AudioManager.STREAM_MUSIC, 20, 0);
            //am.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_);

            //用于本地资源
            mMediaPlayer = MediaPlayer.create(context, source); //无需再调用setDataSource
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();

            if(!TextUtils.isEmpty(painless_arousal) && painless_arousal.equals("1")){
                new CountDownTimer(duration, interval){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        float volume = 1f - millisUntilFinished * 1.0f / duration;
                        if(mMediaPlayer != null){
                            mMediaPlayer.setVolume(volume, volume);
                        }
                    }

                    @Override
                    public void onFinish() {
                        if(mMediaPlayer != null){
                            mMediaPlayer.setVolume(1f, 1f);
                        }
                    }
                }.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //开始播放
    public static void playRing(Context context, String name, Uri uri, String painless_arousal) {
        try {
            //用于本地资源
            mMediaPlayer = new MediaPlayer();
            Uri mUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/raw/" + name);

            mMediaPlayer.setDataSource(context, mUri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

            if(!TextUtils.isEmpty(painless_arousal) && painless_arousal.equals("1")){
                new CountDownTimer(duration, interval){
                    @Override
                    public void onTick(long millisUntilFinished) {
                        float volume = 1f - millisUntilFinished * 1.0f / duration;
                        if(mMediaPlayer != null){
                            mMediaPlayer.setVolume(volume, volume);
                        }
                    }

                    @Override
                    public void onFinish() {
                        if(mMediaPlayer != null){
                            mMediaPlayer.setVolume(1f, 1f);
                        }
                    }
                }.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //开始播放
    public static MediaPlayer playRing(Context context, String name) {
        try {
            //用于本地资源
            mMediaPlayer = new MediaPlayer();
            Uri mUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/raw/" + name);

            mMediaPlayer.setDataSource(context, mUri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(false);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mMediaPlayer;
    }

    //停止播放
    public static void stopRing() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        }
    }

}
