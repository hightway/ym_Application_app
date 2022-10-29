package com.example.myapplication.tools;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.myapplication.R;

public class PopWindowUtil {

    private static PopWindowUtil instance;

    private PopupWindow mPopupWindow;

    // 私有化构造方法，变成单例模式
    private PopWindowUtil() {

    }

    // 对外提供一个该类的实例，考虑多线程问题，进行同步操作
    public static PopWindowUtil getInstance() {
        if (instance == null) {
            synchronized (PopWindowUtil.class) {
                if (instance == null) {
                    instance = new PopWindowUtil();
                }
            }
        }
        return instance;
    }

    /**
     * @param cx
     *            activity
     * @param view
     *            传入需要显示在什么控件下
     * @param view1
     *            传入内容的view
     * @return
     */
    public PopWindowUtil makePopupWindow(Context cx, View view, View view1, int color) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wmManager=(WindowManager) cx.getSystemService(Context.WINDOW_SERVICE);
        wmManager.getDefaultDisplay().getMetrics(dm);
        int Hight = dm.heightPixels;

        mPopupWindow = new PopupWindow(cx);

        mPopupWindow.setBackgroundDrawable(new ColorDrawable(color));
        view1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        // 设置PopupWindow的大小（宽度和高度）
        mPopupWindow.setWidth(view.getWidth());
        mPopupWindow.setHeight((Hight - view.getBottom()) * 2 / 3);
        // 设置PopupWindow的内容view
        mPopupWindow.setContentView(view1);
        mPopupWindow.setFocusable(true); // 设置PopupWindow可获得焦点
        mPopupWindow.setTouchable(true); // 设置PopupWindow可触摸
        mPopupWindow.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸

        return instance;
    }

    /**
     *
     * @param cx 此处必须为Activity的实例
     * @param view 显示在该控件之下
     * @param xOff 距离view的x轴偏移量
     * @param yOff 距离view的y轴偏移量
     * @param anim 弹出及消失动画
     * @return
     */
    public PopupWindow showLocationWithAnimation(final Context cx, View view,
                                                 int xOff, int yOff, int anim) {
        // 弹出动画
        mPopupWindow.setAnimationStyle(anim);

        // 弹出PopupWindow时让后面的界面变暗
        WindowManager.LayoutParams parms = ((Activity) cx).getWindow().getAttributes();
        parms.alpha =0.5f;
        ((Activity) cx).getWindow().setAttributes(parms);

        //int[] positon = new int[2];
        //view.getLocationOnScreen(positon);
        // 弹窗的出现位置，在指定view之下
        //mPopupWindow.showAsDropDown(view, positon[0] + xOff, positon[1] + yOff);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // PopupWindow消失后让后面的界面变亮
                WindowManager.LayoutParams parms = ((Activity) cx).getWindow().getAttributes();
                parms.alpha =1.0f;
                ((Activity) cx).getWindow().setAttributes(parms);

                if (mListener != null) {
                    mListener.dissmiss();
                }
            }
        });

        return mPopupWindow;
    }


    public PopupWindow getPopupWindow(final Context cx, View view, int xOff, int yOff, int anim) {
        View inflate = LayoutInflater.from(cx).inflate(R.layout.layout_pop, null, false);
        final PopupWindow popupWindow = new PopupWindow(inflate, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //点击非菜单部分退出
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        popupWindow.setBackgroundDrawable(null);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(anim);

        //相对于屏幕的位置
        int[] position = new int[2];
        view.getLocationOnScreen(position);

        /*TextView txt = inflate.findViewById(R.id.txt);
        //获取状态栏高度
        int result = 0;
        int resourceId = cx.getResources().getIdentifier("status_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = cx.getResources().getDimensionPixelSize(resourceId);
        }
        LinearLayout.LayoutParams par = (LinearLayout.LayoutParams) txt.getLayoutParams();
        par.setMargins(position[0], position[1] - result, 0, 0);*/

        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        return mPopupWindow;
    }

    private interface OnDissmissListener{

        void dissmiss();

    }

    private OnDissmissListener mListener;

    public void setOnDissmissListener(OnDissmissListener listener) {
        mListener = listener;
    }
}

