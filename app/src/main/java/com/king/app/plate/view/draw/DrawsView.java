package com.king.app.plate.view.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.king.app.plate.utils.DebugLog;
import com.king.app.plate.utils.ScreenUtils;

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 9:55
 */
public class DrawsView extends View {
    
    private Paint paint = new Paint();
    
    private int draws = 32;
    private int set = 3;
    private int cellWidth = ScreenUtils.dp2px(32);
    private int divider = ScreenUtils.dp2px(1);

    private int round;

    private Rect[][] drawsMap;

    private int[] colors = new int[] {
            Color.parseColor("#89DAE9"),
            Color.parseColor("#E49F80"),
            Color.parseColor("#89E98B"),
            Color.parseColor("#F8EB75"),
            Color.parseColor("#F0F0F0")
    };

    private int colorWinner = Color.CYAN;

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
        round = (int) (Math.log(draws)/Math.log(2));
        drawsMap = new Rect[round * getRoundTotalCol() + 1][];// 1是winner
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
     *      --> 无论本view layout_width设置的是match_parent还是wrap_content，specMode=UNSPECIFIED
     *          为支持嵌入HorizontalScrollView滚动视图，在UNSPECIFIED里计算本view应该有的宽度
     * 3. 本view嵌套在其他没有横向滚动功能的ViewGroup中
     *      --> ViewGroup宽度已知（指定过大小，或match_parent，parent已知大小，比如整个屏幕）
     *          --> 无论本view layout_width设置的是match_parent还是wrap_content，specMode=AT_MOST
     *              所以这里选择在AT_MOST也运用本view应该有的宽度，也可以改为运用parent的宽度
     *      --> ViewGroup宽度未知（不是说设置为wrap_content就是未知，而是比如嵌套在HorizontalScrollView中，导致ViewGroup的宽度也未知）
     *          --> 同第2条
     *
     *  measureHeight同理，考虑layout_height与是否嵌入ScrollView
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
     * @param defaultWidth
     * @return
     */
    private int measureDefaultWidth(int defaultWidth) {
        return cellWidth * (getRoundTotalCol() * round + 1) + (round * getRoundTotalCol() - 1) * divider;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawTable(canvas);
        paint.reset();
        super.onDraw(canvas);
    }

    private void drawTable(Canvas canvas) {
        createDrawMap();
        for (int i = 0; i < drawsMap.length; i ++) {
            for (int j = 0; j < drawsMap[i].length; j ++) {
                drawCell(i, j, drawsMap[i][j], canvas);
            }
        }
    }

    private void createDrawMap() {
        int cell = draws;
        // 创建第一轮
        for (int i = 0; i < getRoundTotalCol(); i ++) {
            drawsMap[i] = new Rect[cell];
        }
        HeightValue height = calcHeight(cell);
        for (int row = 0; row < cell; row ++) {
            int h = height.average;
            // extra匀到尽可能多的cell
            if (row < height.extra) {
                h += 1;
            }
            for (int index = 0; index < getRoundTotalCol(); index ++) {
                int left = index * (cellWidth + divider);
                if (row > 0) {
                    Rect last = drawsMap[index][row - 1];
                    drawsMap[index][row] = new Rect(left, last.bottom + divider , left + cellWidth, last.bottom + divider + h);
                }
                else {
                    drawsMap[index][row] = new Rect(left, 0, left + cellWidth,  h);
                }
            }
        }
        // 因为第一轮均摊了extra，所以后面的轮次需要在第一轮的基础上计算位置，否则会出现无法对齐的问题
        for (int r = 1; r < round; r ++) {
            cell /= 2;
            int startCol = r * getRoundTotalCol();
            for (int i = startCol; i < (r + 1) * getRoundTotalCol(); i ++) {
                drawsMap[i] = new Rect[cell];
            }
            for (int row = 0; row < cell; row ++) {
                Rect alignTopCell = drawsMap[r * getRoundTotalCol() - 1][row * 2];
                Rect alignBottomCell = drawsMap[r * getRoundTotalCol() - 1][row * 2 + 1];
                for (int index = 0; index < getRoundTotalCol(); index ++) {
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
        DebugLog.e("[" + rect.left + ", " + rect.top + ", " + rect.right + ", " + rect.bottom + "]");
        canvas.drawRect(rect, paint);
    }

    private int getColor(int hor, int ver) {
        // 最后一列，winner
        if (hor == round * getRoundTotalCol()) {
            return colorWinner;
        }
        else {
            int index = ver / 2;
            if (index % 2 == 0) {
                return colors[hor / getRoundTotalCol()];
            }
            else {
                return colors[colors.length - 1];
            }
        }
    }

    private int getRoundTotalCol() {
        return set + 1;
    }
}
