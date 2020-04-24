package com.king.app.plate.page.player.record

import android.view.View

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/24 14:42
 */
interface OnRecordPlayerListener {
    fun onClickItem(view: View, position: Int, childItem: ChildItem)
}