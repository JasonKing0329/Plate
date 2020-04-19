package com.king.app.plate.page.match

import android.graphics.Color
import com.king.app.plate.model.bean.BodyCell
import com.king.app.plate.view.draw.AbsDrawAdapter

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 16:49
 */
class DrawsAdapter : AbsDrawAdapter() {

    private var data: DrawData? = null
    private val colorDefault = Color.parseColor("#333333")
    private val colorModify = Color.parseColor("#ff0000")

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

    fun updateText(x: Int, y: Int, text: String?) {
        try {
            var colList: MutableList<BodyCell> = data?.body?.bodyData!![x]
            colList[y].text = colList[y].text + text!!
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