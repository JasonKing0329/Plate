package com.king.app.plate.page.match

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import com.king.app.plate.R
import com.king.app.plate.utils.ScreenUtils
import com.king.app.plate.view.draw.DrawKeyboard

/**
 * @description:
 * @author：Jing
 * @date: 2020/4/20 19:43
 */
class PopupKeyboard {

    var window: PopupWindow? = null
    var onKeyActionListener: DrawKeyboard.OnClickKeyListener? = null
    var popupWidth = 0

    fun show(context: Context, focusRect: Rect, scrollX: Int, parentView: View) {
        popupWidth = DrawKeyboard.keyHeight * DrawKeyboard.keyTexts.size + context.resources.getDimensionPixelSize(R.dimen.popup_keyboard_margin) * 2
        var view = LayoutInflater.from(context).inflate(R.layout.layout_popup_keyboard, null)
        view.findViewById<DrawKeyboard>(R.id.keyboard).setOnClickKeyListener(onKeyActionListener)
        if (window == null) {
            window = PopupWindow(view, popupWidth, popupWidth)
            window!!.isOutsideTouchable = true
        }

        var point = calculatePoint(parentView, focusRect, scrollX)
        // 虽然参数1是parent，但是后面的坐标还是以activity的整屏为view参照
        window!!.showAtLocation(parentView, Gravity.LEFT.or(Gravity.TOP), point.x, point.y)
    }

    /**
     * 以弹框左侧贴紧在单元格右侧并顶端对齐为基准
     * 若右侧区域不足，弹框整体右侧贴紧单元格左侧
     * 若底部区域不足，向上平移相应的位置
     * PopupWindow自带超出屏幕自动往回平移的功能，所以为了不遮住单元格并贴紧单元格，
     * 垂直方向只需对齐单元格顶部，水平方向需额外处理左右方向
     */
    private fun calculatePoint(parentView: View, focusRect: Rect, scrollX: Int): Point {
        // showAtLocation的参考基准是整个屏幕，所以要计算出DrawView的垂直偏移距离
        var array = IntArray(2)
        parentView.getLocationOnScreen(array)
        var offsetY = array[1]

        // 单元格实际原点（在屏幕上的位置）
        var realX = focusRect.left - scrollX// 减去滑动的距离
        var realY = focusRect.top + offsetY// 加上偏移量

        var screenWidth = ScreenUtils.getScreenWidth()

        var resultX = realX
        var resultY = realY
        // 以弹框左侧贴紧在单元格右侧并顶端对齐为基准
        // 若右侧区域不足，弹框整体右侧贴紧单元格左侧
        resultX = if (realX + focusRect.width() + popupWidth > screenWidth) {
            realX - popupWidth
        } else{
            realX + focusRect.width()
        }
        // 若底部区域不足，向上平移相应的位置
        // popupWindow自带超出边界往回自动平移的功能，不需要再处理垂直方向
        return Point(resultX, resultY)
    }
}