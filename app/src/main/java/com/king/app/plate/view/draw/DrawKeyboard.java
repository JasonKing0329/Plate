package com.king.app.plate.view.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.king.app.plate.utils.DebugLog;
import com.king.app.plate.utils.ScreenUtils;

/**
 * @author Jing
 * @description:
 * @date :2020/1/25 0025 13:16
 */
public class DrawKeyboard extends View implements View.OnTouchListener {

    private int keyHeight = ScreenUtils.dp2px(40);
    private int textColor = Color.parseColor("#333333");
    private int keyBg = Color.WHITE;
    private int dividerColor = Color.parseColor("#cecece");
    private int dividerWidth = ScreenUtils.dp2px(1);
    private int textSize = ScreenUtils.dp2px(20);

    private Paint paint = new Paint();

    private Paint textPaint = new Paint();

    private Rect[][] keyRects;

    public static final String KEY_DEL = "Del";

    private String keyTexts[][] = new String[][] {
            {"4", "5", "6", "7", "8", "9"},
            {"0", "1", "2", "3", "(", ")", KEY_DEL}
    };

    private OnClickKeyListener onClickKeyListener;

    public DrawKeyboard(Context context) {
        super(context);
        init(null);
    }

    public DrawKeyboard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DrawKeyboard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setOnTouchListener(this);
    }

    public void setOnClickKeyListener(OnClickKeyListener onClickKeyListener) {
        this.onClickKeyListener = onClickKeyListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        createKeys();
        drawKeys(canvas);
        drawLines(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 布局宽度为自适应wrap
        int minimumHeight = getSuggestedMinimumHeight();
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, height);
    }

    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        DebugLog.e("---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                DebugLog.e("---speMode = AT_MOST");
                defaultHeight = measureDefaultWidth(defaultHeight);
                break;
            case MeasureSpec.EXACTLY:
                DebugLog.e("---speMode = EXACTLY");
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                DebugLog.e("---speMode = UNSPECIFIED");
//                defaultWidth = Math.max(defaultWidth, specSize);
                defaultHeight = measureDefaultWidth(defaultHeight);
        }
        DebugLog.e("---defaultWidth = " + defaultHeight + "");
        return defaultHeight;
    }

    private int measureDefaultWidth(int defaultHeight) {
        return keyTexts.length * keyHeight + (keyTexts.length - 1) * dividerWidth;
    }

    private void createKeys() {
        keyRects = new Rect[keyTexts.length][];
        for (int i = 0; i < keyTexts.length; i ++) {
            int keyNum = keyTexts[i].length;
            int validWidth = getWidth() - (keyNum - 1) * dividerWidth;
            int width = validWidth / keyNum;
            int extra = validWidth % keyNum;
            keyRects[i] = new Rect[keyNum];
            for (int j = 0; j < keyTexts[i].length; j ++) {
                Rect rect = new Rect();
                rect.top = i * (keyHeight + dividerWidth);
                rect.bottom = rect.top + keyHeight;
                if (j < extra) {
                    width += 1;
                }
                if (j == 0) {
                    rect.left = 0;
                }
                else {
                    rect.left = keyRects[i][j - 1].right + dividerWidth;
                }
                rect.right = rect.left + width;
                keyRects[i][j] = rect;
            }
        }
    }

    private void drawKeys(Canvas canvas) {
        for (int i = 0; i < keyTexts.length; i ++) {
            String[] arrays = keyTexts[i];
            for (int j = 0; j < arrays.length; j ++) {
                drawKey(i, j, arrays[j], canvas);
            }
        }
    }

    private void drawKey(int i, int j, String text, Canvas canvas) {
        paint.setColor(keyBg);
        canvas.drawRect(keyRects[i][j], paint);
        drawText(text, keyRects[i][j], canvas);
    }

    private void drawText(String text, Rect rect, Canvas canvas) {
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setStyle(Paint.Style.FILL);
        //设置基线上点对其方式
        textPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离
        int baseLineY = (int) (rect.centerY() - top / 2 - bottom / 2);//基线中间点的y轴计算公式

        canvas.drawText(text, rect.centerX(), baseLineY, textPaint);
    }

    private void drawLines(Canvas canvas) {
        paint.setColor(dividerColor);
        // row
        for (int i = 0; i < keyTexts.length; i ++) {
            if (i > 0) {
                Rect firstRect = keyRects[i][0];
                Rect endRect = keyRects[i][keyRects[i].length - 1];
                int y = firstRect.top - dividerWidth;
                canvas.drawLine(firstRect.left, y, endRect.right, y, paint);
            }
            // col
            for (int j = 0; j < keyTexts[i].length; j ++) {
                if (j > 0) {
                    Rect rect = keyRects[i][j];
                    int x = rect.left - dividerWidth;
                    canvas.drawLine(x, rect.top, x, rect.bottom, paint);
                }
            }
        }
    }

    private long startTime;
    private float startX, startY;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 相对控件原点的位置（能超过屏幕）
//        DebugLog.e("getX=" + event.getX() + ", getY=" + event.getY());
        // 相对屏幕原点的位置（不超过屏幕）
//        DebugLog.e("getRawX=" + event.getRawX() + ", getRawY=" + event.getRawY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTime = System.currentTimeMillis();
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                long time = System.currentTimeMillis();
                float x = event.getX();
                float y = event.getY();
                if (time - startTime < 500 && Math.abs(x - startX) < 30 && Math.abs(y - startY) < 30) {
                    doClick(x, y);
                }
                break;
        }
        return true;
    }

    private void doClick(float x, float y) {
        for (int i = 0; i < keyRects.length; i++) {
            Rect rect = keyRects[i][0];
            if (y <= rect.bottom && y >= rect.top) {
                for (int j = 0; j < keyRects[i].length; j++) {
                    rect = keyRects[i][j];
                    if (x >= rect.left && x <= rect.right) {
                        onClick(i, j);
                        break;
                    }
                }
            }
        }
    }

    private void onClick(int x, int y) {
        DebugLog.e(x + ", " + y);
        if (onClickKeyListener != null) {
            onClickKeyListener.onClickKey(keyTexts[x][y]);
        }
    }

    public interface OnClickKeyListener {
        void onClickKey(String key);
    }
}
