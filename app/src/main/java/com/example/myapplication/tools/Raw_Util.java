package com.example.myapplication.tools;

import static com.aliyun.player.nativeclass.NativePlayerBase.getContext;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;

import com.example.myapplication.R;
import com.example.myapplication.bean.Raw_Bean;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Raw_Util {

    private static MediaPlayer mediaPlayer;
    private static Raw_Bean audioInfo;

    public static List<Raw_Bean> getRaw_List(){
        List<Raw_Bean> audioInfos = new ArrayList<>();
        Field[] fields = R.raw.class.getDeclaredFields();
        int rawId;
        String rawName;

        for (int i = 0; i < fields.length; i++) {
            try {
                rawId = fields[i].getInt(R.raw.class);
                rawName = fields[i].getName();

                Uri uri = Uri.parse("android.resource://"+getContext().getPackageName()+"/"+ rawId);
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(getContext(), uri);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.prepare();
                int duration = mediaPlayer.getDuration();

                String time_duraton = "";
                if(duration > 0){
                    int d = duration/1000;
                    time_duraton = Utils.transfom(d);
                }
                audioInfo = new Raw_Bean(rawName, time_duraton, uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            audioInfos.add(audioInfo);
        }
        fields = null;
        return audioInfos;
    }

}
