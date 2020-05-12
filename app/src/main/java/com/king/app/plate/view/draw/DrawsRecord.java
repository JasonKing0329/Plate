package com.king.app.plate.view.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.king.app.plate.conf.AppConstants;

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/5/11 10:11
 */
public class DrawsRecord extends View {

    private int set = AppConstants.Companion.getSet();

    public DrawsRecord(Context context) {
        super(context);
    }

    public DrawsRecord(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawsRecord(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
