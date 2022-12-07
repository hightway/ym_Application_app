package com.example.myapplication.tools;

import static com.aliyun.player.nativeclass.NativePlayerBase.getContext;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;

import com.example.myapplication.R;
import com.example.myapplication.bean.Raw_Bean;

public class MediaUtil {

    private static MediaPlayer mMediaPlayer;

    //开始播放
    public static void playRing(Context context, int source) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //开始播放
    public static void playRing(Context context, String name, Uri uri) {
        try {
            //用于本地资源
            mMediaPlayer = new MediaPlayer();
            Uri mUri = Uri.parse("android.resource://" + getContext().getPackageName() + "/raw/" + name);

            mMediaPlayer.setDataSource(context, mUri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //开始播放
    public static void playRing(Context context, String name) {
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
