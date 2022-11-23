package com.example.myapplication.custom;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class FillImageView extends AppCompatImageView {
    private String TAG = FillImageView.class.getSimpleName();
    private Matrix matrix;
    private Paint paint;
    private Rect srcR;
    private RectF dstR;
    RectF deviceR;

    public FillImageView(Context context) {
        super(context);
        init();
    }

    public FillImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FillImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        matrix = new Matrix();
        paint = new Paint();
        srcR = new Rect();
        dstR = new RectF();
        deviceR = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //TODO 还原matrix中的缩放比例
        matrix.reset();
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) {
            return;
        }
        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();

        if (null == b) {
            return;
        }
        // 获得图片的宽高
        int width = b.getWidth();
        int height = b.getHeight();
        // 计算缩放比例
        float scaleWidth = (w * 1f) / (width * 1f);
        float scaleHeight = (h * 1f) / (height * 1f);
        // 取得想要缩放的matrix参数
        matrix.postScale(scaleWidth, scaleHeight);

        boolean transformed = !matrix.rectStaysRect();
        if (transformed) {
            paint.setAntiAlias(true);
        }
        srcR.set(0, 0, width, height);
        dstR.set(0, 0, width, height);
        canvas.concat(matrix);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(b, srcR, dstR, paint);
    }
}