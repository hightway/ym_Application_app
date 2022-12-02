package com.example.myapplication.custom;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import android.widget.Toast;

import com.example.myapplication.activity.Sleep_Time_Set_Activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AudioRecordDemo {

    private static final String TAG = "AudioRecord";
    static final int SAMPLE_RATE_IN_HZ = 8000;
    static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
    private final Random random;
    AudioRecord mAudioRecord;
    boolean isGetVoiceRun;
    Object mLock;
    private WeakReference<Sleep_Time_Set_Activity> mActivity;
    private List<Integer> db_List = new ArrayList<>();

    public AudioRecordDemo(Sleep_Time_Set_Activity activity) {
        mLock = new Object();
        mActivity = new WeakReference<>(activity);
        random = new Random();
    }

    public void sotp() {
        this.isGetVoiceRun = false;
    }

    @SuppressLint("MissingPermission")
    public void getNoiseLevel() {
        if (isGetVoiceRun) {
            return;
        }
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_DEFAULT,
                AudioFormat.ENCODING_PCM_16BIT, BUFFER_SIZE);
        if (mAudioRecord == null) {
        }
        isGetVoiceRun = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                mAudioRecord.startRecording();
                short[] buffer = new short[BUFFER_SIZE];
                final Sleep_Time_Set_Activity activity = mActivity.get();
                while (isGetVoiceRun) {
                    int r = mAudioRecord.read(buffer, 0, BUFFER_SIZE);
                    long v = 0;
                    for (int i = 0; i < buffer.length; i++) {
                        v += buffer[i] * buffer[i];
                    }
                    double mean = v / (double) r;
                    final double volume = 10 * Math.log10(mean);
                    Log.d(TAG, "db value:" + volume);
                    if (null != activity) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(activity, volume + "分贝", Toast.LENGTH_SHORT).show();
                                if(volume > 0){
                                    int db = (int)volume;
                                    activity.tx_db.setText(db + "db");


                                    db_List.add(db);
                                    if(db_List.size()>10){
                                        db_List.remove(0);
                                    }
                                    int all = 0;
                                    for(int num : db_List){
                                        all+=num;
                                    }
                                    int num = (int)(all/db_List.size());
                                    int randomInt = random.nextInt(5);
                                    if(num > randomInt){
                                        activity.tx_noise.setText(String.valueOf(num-randomInt));
                                    }else{
                                        activity.tx_noise.setText(String.valueOf(num));
                                    }

                                    /*int randomInt = random.nextInt(5);
                                    if(db > randomInt){
                                        activity.tx_noise.setText(String.valueOf(db-randomInt));
                                    }else{
                                        activity.tx_noise.setText(String.valueOf(db));
                                    }*/
                                }
                            }
                        });
                    }
                    // 大概一秒1次
                    synchronized (mLock) {
                        try {
                            mLock.wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
                db_List.clear();
                db_List = null;
            }
        }).start();
    }

}
