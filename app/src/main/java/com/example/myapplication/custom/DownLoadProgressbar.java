package com.example.myapplication.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.TextView;

import com.example.myapplication.R;

public class DownLoadProgressbar extends View {
    private Paint paint = new Paint(); // 绘制背景灰色线条画笔

    private Paint paintText = new Paint(); // 绘制下载进度画笔

    private float offset = 0f; // 下载偏移量

    private float maxvalue = 0f; // 下载的总大小

    private float currentValue = 0f; // 下载了多少

    private Rect mBound = new Rect(); // 获取百分比数字的长宽

    private String percentValue = "0%"; // 要显示的现在百分比

    private float offsetRight = 0f; // 灰色线条距离右边的距离

    private int textSize = 25; // 百分比的文字大小

    private float offsetTop = 18f; // 距离顶部的偏移量

    public DownLoadProgressbar(Context context) {

        this(context, null);

        // TODO Auto-generated constructor stub

    }

    public DownLoadProgressbar(Context context, AttributeSet attribute) {

        this(context, attribute, 0);

    }

    public DownLoadProgressbar(Context context, AttributeSet attrs,

                               int defStyleAttr) {

        super(context, attrs, defStyleAttr);

        // TODO Auto-generated constructor stub

        // 获取自定义属性，给textsize赋初始值
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.downloadProgressBar);
        textSize = (int) t.getDimension(R.styleable.downloadProgressBar_dptextsize, 30);
        getTextWidth();

    }

    @Override
    protected void onDraw(Canvas canvas) {

        // TODO Auto-generated method stub

        super.onDraw(canvas);

        // 绘制底色
        paint.setColor(getResources().getColor(R.color.gray_del));

        paint.setStrokeWidth(1);

        canvas.drawLine(0, offsetTop, getWidth(), offsetTop, paint);

        // 绘制进度条颜色

        paint.setColor(getResources().getColor(R.color.navColor));

        paint.setStrokeWidth(2);

        canvas.drawLine(0, offsetTop, offset, offsetTop, paint);

        // 绘制白色区域及百分比

        paint.setColor(getResources().getColor(R.color.layout_gray_bg));

        paint.setStrokeWidth(1);

        paintText.setColor(getResources().getColor(R.color.navColor));

        paintText.setTextSize(textSize);

        paintText.setAntiAlias(true);

        paintText.getTextBounds(percentValue, 0, percentValue.length(), mBound);

        canvas.drawLine(offset, offsetTop, offset + mBound.width() + 4, offsetTop, paint);

        canvas.drawText(percentValue, offset, offsetTop + mBound.height() / 2 - 2, paintText);

    }

    /**

     * 设置当前进度值

     *

     * @param currentValue

     */

    public void setCurrentValue(float currentValue) {

        this.currentValue = currentValue;

        int value = (int) (this.currentValue / maxvalue * 100);

        if (value < 100) {

            percentValue = value + "%";

        } else {

            percentValue = "100%";

        }


        initCurrentProgressBar();

        invalidate();

    }

    /**

     * 设置最大值

     *

     * @param maxValue

     */

    public void setMaxValue(float maxValue) {

        this.maxvalue = maxValue;

    }

    /**

     * 获取当前进度条长度

     *

     * @param

     * @param

     * @return

     */

    public void initCurrentProgressBar() {
        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                if (currentValue < maxvalue) {
                    offset = (getWidth() - offsetRight) * currentValue / maxvalue;
                } else {
                    offset = getWidth() - offsetRight;
                }

                //节约资源
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    /**

     * 获取“100%”的宽度

     */

    public void getTextWidth() {

        Paint paint = new Paint();

        Rect rect = new Rect();

        paint.setTextSize(textSize);

        paint.setAntiAlias(true);

        paint.getTextBounds("100%", 0, "100%".length(), rect);

        offsetRight = rect.width() + 5;

    }

}
