package com.example.myapplication.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.example.myapplication.R;

import java.util.List;

import okhttp3.internal.Util;

public class MyKeyboardView extends KeyboardView {

    private Context context;

    public MyKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        //init(context);
    }

    private void init(Context context) {
        Keyboard WordKeyboard = new Keyboard(context, R.xml.stock_word_keyboard);
        this.setKeyboard(WordKeyboard);
    }


    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Keyboard keyboard = getKeyboard();
        if (keyboard == null) return;
        List<Keyboard.Key> keys = keyboard.getKeys();
        if (keys != null && keys.size() > 0) {
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            Typeface font = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
            paint.setTypeface(font);
            paint.setAntiAlias(true);
            for (Keyboard.Key key : keys) {
                if (key.codes[0] == -3) {
                    Drawable dr = getContext().getResources().getDrawable(R.drawable.key_bg);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                } else {
                    Drawable dr = getContext().getResources().getDrawable(R.drawable.key_bg);
                    dr.setBounds(key.x, key.y, key.x + key.width, key.y + key.height);
                    dr.draw(canvas);
                }
                if (key.label != null) {
                    if (key.codes[0] == -4 ||
                            key.codes[0] == -5) {
                        paint.setTextSize(17 * 2);
                    } else {
                        paint.setTextSize(20 * 2);
                    }
                    if (key.codes[0] == -4) {
                        paint.setColor(getContext().getResources().getColor(R.color.white));
                    } else {
                        paint.setColor(getContext().getResources().getColor(R.color.white));
                    }
                    Rect rect = new Rect(key.x, key.y, key.x + key.width, key.y + key.height);
                    Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
                    int baseline = (rect.bottom + rect.top - fontMetrics.bottom - fontMetrics.top) / 2;
                    // 下面这行是实现水平居中，drawText对应改为传入targetRect.centerX()
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(key.label.toString(), rect.centerX(), baseline, paint);
                }
            }
        }
    }
}
