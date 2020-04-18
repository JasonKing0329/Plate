package com.king.app.plate.page.match

import com.king.app.plate.model.bean.BodyCell
import com.king.app.plate.view.draw.AbsDrawAdapter

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 16:49
 */
class DrawsAdapter : AbsDrawAdapter() {

    private var data: DrawData? = null

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

    fun updateText(x: Int, y: Int, text: String?) {
        try {
            var colList: MutableList<BodyCell> = data?.body?.bodyData!![x]
            colList[y].text = text!!
            colList[y].isModified = true
        } catch (e: Exception) {
        }
    }
}