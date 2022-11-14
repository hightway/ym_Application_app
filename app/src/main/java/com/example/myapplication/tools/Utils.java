package com.example.myapplication.tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;

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

}
