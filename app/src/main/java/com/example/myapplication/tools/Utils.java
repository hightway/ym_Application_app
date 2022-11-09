package com.example.myapplication.tools;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;

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
        int x=addressitTeaddX-lldownX;
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


}
