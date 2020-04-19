package com.king.app.plate.view.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.king.app.plate.conf.AppConstants;
import com.king.app.plate.utils.DebugLog;
import com.king.app.plate.utils.ScreenUtils;

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 9:55
 */
public class DrawsView extends View implements View.OnTouchListener {

    private Paint paint = new Paint();

    private Paint textPaint = new Paint();

    private int draws = AppConstants.Companion.getDraws();
    private int set = AppConstants.Companion.getSet();
    private int cellWidth = ScreenUtils.dp2px(36);
    private int divider = ScreenUtils.dp2px(1);
    private int focusBorderWidth = ScreenUtils.dp2px(2);
    private int colorWinner = Color.CYAN;
    private int colorFocus = Color.BLACK;
    private int colorText = Color.parseColor("#333333");
    private int textSize = ScreenUtils.dp2px(14);

    private int[] cellColors = new int[]{
            Color.parseColor("#89DAE9"),
            Color.parseColor("#E49F80"),
            Color.parseColor("#89E98B"),
            Color.parseColor("#F8EB75"),
            Color.parseColor("#F0F0F0")
    };

    private int round;

    private Rect[][] drawsMap;

    private Point focusPoint;

    private boolean isEnableFocusItem = true;

    private OnClickDrawItemListener onClickDrawItemListener;

    private AbsDrawAdapter adapter;

    public void setOnClickDrawItemListener(OnClickDrawItemListener onClickDrawItemListener) {
        this.onClickDrawItemListener = onClickDrawItemListener;
    }

    public void setEnableFocusItem(boolean enableFocusItem) {
        isEnableFocusItem = enableFocusItem;
    }

    public DrawsView(Context context) {
        super(context);
        init(null);
    }

    public DrawsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DrawsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        round = (int) (Math.log(draws) / Math.log(2));
        drawsMap = new Rect[round * getRoundTotalCol() + 1][];// 1是winner
        setOnTouchListener(this);
    }

    public void setAdapter(AbsDrawAdapter adapter) {
        this.adapter = adapter;
        if (adapter != null) {
            adapter.setNotifyObserver(new NotifyObserver() {
                @Override
                public void notifyDataSetChanged() {
                    invalidate();
                }
            });
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 布局宽度为自适应wrap
        int minimumWidth = getSuggestedMinimumWidth();
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        setMeasuredDimension(width, heightMeasureSpec);
    }

    /**
     * specMode判断控件设置的layout_width
     * 1. 本view layout_width指定为固定值，specMode=固定值
     * 2. 本view嵌套在HorizontalScrollView中，HorizontalScrollView作用于横向滚动
     * --> 无论本view layout_width设置的是match_parent还是wrap_content，specMode=UNSPECIFIED
     * 为支持嵌入HorizontalScrollView滚动视图，在UNSPECIFIED里计算本view应该有的宽度
     * 3. 本view嵌套在其他没有横向滚动功能的ViewGroup中
     * --> ViewGroup宽度已知（指定过大小，或match_parent，parent已知大小，比如整个屏幕）
     * --> 无论本view layout_width设置的是match_parent还是wrap_content，specMode=AT_MOST
     * 所以这里选择在AT_MOST也运用本view应该有的宽度，也可以改为运用parent的宽度
     * --> ViewGroup宽度未知（不是说设置为wrap_content就是未知，而是比如嵌套在HorizontalScrollView中，导致ViewGroup的宽度也未知）
     * --> 同第2条
     * <p>
     * measureHeight同理，考虑layout_height与是否嵌入ScrollView
     *
     * @param defaultWidth
     * @param measureSpec
     * @return
     */
    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        DebugLog.e("---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                DebugLog.e("---speMode = AT_MOST");
                defaultWidth = measureDefaultWidth(defaultWidth);
                break;
            case MeasureSpec.EXACTLY:
                DebugLog.e("---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                DebugLog.e("---speMode = UNSPECIFIED");
//                defaultWidth = Math.max(defaultWidth, specSize);
                defaultWidth = measureDefaultWidth(defaultWidth);
        }
        DebugLog.e("---defaultWidth = " + defaultWidth + "");
        return defaultWidth;
    }

    /**
     * 总宽度等于 两端延长线的宽度+Y轴显示刻度文字的宽度+X轴所有刻度总宽度
     *
     * @param defaultWidth
     * @return
     */
    private int measureDefaultWidth(int defaultWidth) {
        return cellWidth * (getRoundTotalCol() * round + 1) + (round * getRoundTotalCol() - 1) * divider;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawTable(canvas);
        drawTableText(canvas);
        if (isEnableFocusItem) {
            drawFocusCell(canvas);
        }
        paint.reset();
        textPaint.reset();
        super.onDraw(canvas);
    }

    private void drawTable(Canvas canvas) {
        createDrawMap();
        for (int i = 0; i < drawsMap.length; i++) {
            for (int j = 0; j < drawsMap[i].length; j++) {
                drawCell(i, j, drawsMap[i][j], canvas);
            }
        }
    }

    private void createDrawMap() {
        int cell = draws;
        // 创建第一轮
        for (int i = 0; i < getRoundTotalCol(); i++) {
            drawsMap[i] = new Rect[cell];
        }
        HeightValue height = calcHeight(cell);
        for (int row = 0; row < cell; row++) {
            int h = height.average;
            // extra匀到尽可能多的cell
            if (row < height.extra) {
                h += 1;
            }
            for (int index = 0; index < getRoundTotalCol(); index++) {
                int left = index * (cellWidth + divider);
                if (row > 0) {
                    Rect last = drawsMap[index][row - 1];
                    drawsMap[index][row] = new Rect(left, last.bottom + divider, left + cellWidth, last.bottom + divider + h);
                } else {
                    drawsMap[index][row] = new Rect(left, 0, left + cellWidth, h);
                }
            }
        }
        // 因为第一轮均摊了extra，所以后面的轮次需要在第一轮的基础上计算位置，否则会出现无法对齐的问题
        for (int r = 1; r < round; r++) {
            cell /= 2;
            int startCol = r * getRoundTotalCol();
            for (int i = startCol; i < (r + 1) * getRoundTotalCol(); i++) {
                drawsMap[i] = new Rect[cell];
            }
            for (int row = 0; row < cell; row++) {
                Rect alignTopCell = drawsMap[r * getRoundTotalCol() - 1][row * 2];
                Rect alignBottomCell = drawsMap[r * getRoundTotalCol() - 1][row * 2 + 1];
                for (int index = 0; index < getRoundTotalCol(); index++) {
                    int top = alignTopCell.top;
                    int bottom = alignBottomCell.bottom;
                    int left = alignTopCell.right + divider + index * (cellWidth + divider);
                    int right = left + cellWidth;
                    drawsMap[startCol + index][row] = new Rect(left, top, right, bottom);
                }
            }
        }

        // winner
        drawsMap[round * getRoundTotalCol()] = new Rect[1];
        int left = drawsMap[drawsMap.length - 2][0].right + divider;
        drawsMap[round * getRoundTotalCol()][0] = new Rect(left, 0, left + cellWidth, getHeight());
    }

    private class HeightValue {
        int average;
        int extra;
    }

    private HeightValue calcHeight(int cell) {
        int validHeight = getHeight() - (cell - 1) * divider;
        HeightValue value = new HeightValue();
        // 取整，取出取整后多余的
        value.extra = validHeight % cell;
        value.average = validHeight / cell;
        return value;
    }

    private void drawCell(int row, int col, Rect rect, Canvas canvas) {
        paint.setColor(getColor(row, col));
        canvas.drawRect(rect, paint);
    }

    private void drawFocusCell(Canvas canvas) {
        if (focusPoint != null) {
            Rect rect = drawsMap[focusPoint.x][focusPoint.y];
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(colorFocus);
            paint.setStrokeWidth(focusBorderWidth);
            Path path = new Path();
            path.moveTo(rect.left, rect.top);
            path.lineTo(rect.right, rect.top);
            path.lineTo(rect.right, rect.bottom);
            path.lineTo(rect.left, rect.bottom);
            path.lineTo(rect.left, rect.top);
            path.close();
            canvas.drawPath(path, paint);
        }
    }

    private int getColor(int hor, int ver) {
        // 最后一列，winner
        if (hor == round * getRoundTotalCol()) {
            return colorWinner;
        } else {
            int index = ver / 2;
            if (index % 2 == 0) {
                return cellColors[hor / getRoundTotalCol()];
            } else {
                return cellColors[cellColors.length - 1];
            }
        }
    }

    private int getRoundTotalCol() {
        return set + 1;
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
        for (int i = 0; i < drawsMap.length; i++) {
            Rect rect = drawsMap[i][0];
            if (x >= rect.left && x <= rect.right) {
                for (int j = 0; j < drawsMap[i].length; j++) {
                    rect = drawsMap[i][j];
                    if (y <= rect.bottom && y >= rect.top) {
                        if (isScoreCell(i, j)) {
                            onClickScore(i, j);
                        }
                        else {
                            onClickDraw(i, j);
                        }
                        break;
                    }
                }
            }
        }
    }

    private boolean isScoreCell(float x, float y) {
        if (x % (set + 1) == 0) {
            return false;
        }
        return true;
    }

    private void onClickScore(int x, int y) {
        focusCell(x, y);
        if (onClickDrawItemListener != null) {
            onClickDrawItemListener.onClickScoreItem(x, y, x - 1);
        }
    }

    private void onClickDraw(int x, int y) {
        focusCell(x, y);
        if (onClickDrawItemListener != null) {
            onClickDrawItemListener.onClickDrawItem(x, y);
        }
    }

    private void focusCell(int x, int y) {
        DebugLog.e(x + ", " + y);
        // 已聚焦，取消聚焦
        if (focusPoint != null && focusPoint.x == x && focusPoint.y == y) {
            focusPoint = null;
        } else {
            focusPoint = new Point();
            focusPoint.x = x;
            focusPoint.y = y;
        }

        if (isEnableFocusItem) {
            invalidate();
        }
    }

    public Point getFocusPoint() {
        return focusPoint;
    }

    private void drawTableText(Canvas canvas) {
        if (adapter != null) {
            for (int i = 0; i < drawsMap.length; i++) {
                for (int j = 0; j < drawsMap[i].length; j++) {
                    String text = getTextFromAdapter(i, j);
                    int color = getTextColorFromAdapter(i, j);
                    drawText(text, color, drawsMap[i][j], canvas);
                }
            }
        }
    }

    private int getTextColorFromAdapter(int x, int y) {
        return adapter.getTextColor(x, y);
    }

    private String getTextFromAdapter(int x, int y) {
        return adapter.getText(x, y);
    }

    private void drawText(String text, int color, Rect rect, Canvas canvas) {
        textPaint.setColor(color);
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

    public interface OnClickDrawItemListener {
        void onClickDrawItem(int x, int y);

        void onClickScoreItem(int x, int y, int round);
    }
}
