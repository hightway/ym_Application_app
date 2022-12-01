package com.example.myapplication.tools;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.example.myapplication.MyApp;

import java.util.List;

public class AppUtils {

    /**
     * 将app从后台唤醒到前台
     */
    public static void moveToFront(final Class Class) {
        ActivityManager activityManager = (ActivityManager) MyApp.instance.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfoList = activityManager.getRunningTasks(20);
        if (taskInfoList == null) {
            return;
        }
        for (ActivityManager.RunningTaskInfo taskInfo : taskInfoList) {
            if (taskInfo.baseActivity.getPackageName().equals(MyApp.instance.getPackageName())) {
                activityManager.moveTaskToFront(taskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                Intent intent = new Intent(MyApp.instance, Class);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.setAction(Intent.ACTION_MAIN);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                MyApp.instance.startActivity(intent);
                break;
            }
        }
    }

    /**
     * 判断app是否在后台
     */
    public static boolean isAppIsInBackground() {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) MyApp.instance.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        if (runningProcesses == null) {
            return true;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    if (activeProcess.equals(MyApp.instance.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }
        return isInBackground;
    }




    public static final float DENSITY = Resources.getSystem()
            .getDisplayMetrics().density;
    @SuppressLint("StaticFieldLeak")
    private static final Context mContext = MyApp.instance;

    /**
     * dp 转 px
     *
     * @param dpValue 以 dp 为单位的值
     * @return px value
     */
    public static int dp2px(int dpValue) {
        return (int) (dpValue * DENSITY + 0.5f);
    }

    /**
     * 获取屏幕真实宽度（去除虚拟按键和状态栏）
     *
     * @return px
     */
    public static int getRealWidth() {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int screenWidth = 0;
        DisplayMetrics dm = new DisplayMetrics();
        display.getRealMetrics(dm);
        Point size = new Point();
        display.getRealSize(size);
        screenWidth = size.x;
        return screenWidth;
    }

    /**
     * 获取设备高度（px）
     *
     * @return
     */
    public static int deviceHeight() {
        return mContext.getResources().getDisplayMetrics().heightPixels;
    }


}
