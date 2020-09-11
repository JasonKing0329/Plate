package com.king.app.plate.page.match

import android.graphics.Color
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.bean.BodyCell
import com.king.app.plate.view.draw.AbsDrawAdapter

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 16:49
 */
class DrawsAdapter : AbsDrawAdapter() {

    private var data: DrawData? = null
    private val colorDefault = AppConstants.DRAW_TEXT_DEF
    private val colorModify = AppConstants.DRAW_TEXT_MODIFY

    fun setData(data: DrawData?) {
        this.data = data
    }

    override fun getText(x: Int, y: Int): String {
        try {
            return data?.body?.bodyData?.get(x)?.get(y)?.text!!
        } catch (e: Exception) {
        }
        return ""
    }

    override fun getTextColor(x: Int, y: Int): Int {
        try {
            if (data?.body?.bodyData?.get(x)?.get(y)?.isModified!!) {
                return colorModify
            }
        } catch (e: Exception) {
        }
        return colorDefault
    }

    fun updateText(x: Int, y: Int, text: String?, isFirst: Boolean, isTieBreak: Boolean) {
        try {
            var colList: MutableList<BodyCell> = data?.body?.bodyData!![x]
            if (isFirst) {
                colList[y].text = text!!
            }
            else {
                if (isTieBreak) {
                    var index = colList[y].text.lastIndexOf(")")
                    colList[y].text = StringBuffer(colList[y].text.substring(0, index)).append(text).append(")").toString()
                }
                else {
                    colList[y].text = colList[y].text + text!!
                }
            }
            colList[y].isModified = true
        } catch (e: Exception) {
        }
    }

    fun clearText(x: Int, y: Int) {
        try {
            var colList: MutableList<BodyCell> = data?.body?.bodyData!![x]
            colList[y].text = ""
            colList[y].isModified = true
        } catch (e: Exception) {
        }
    }

    fun deleteText(x: Int, y: Int) {
        try {
            var colList: MutableList<BodyCell> = data?.body?.bodyData!![x]
            colList[y].text = colList[y].text.substring(0, colList[y].text.length - 1)
            colList[y].isModified = true
        } catch (e: Exception) {
        }
    }
}