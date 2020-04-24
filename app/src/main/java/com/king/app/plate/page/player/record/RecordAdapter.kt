package com.king.app.plate.page.player.record

import android.view.View
import com.zaihuishou.expandablerecycleradapter.adapter.BaseExpandableAdapter
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractAdapterItem

/**
 * Desc:BaseExpandableAdapter 会把注入的list的结构改变（以现在的二级为例，一级list本来只有HeadItem，最终HeadItem里的ChildItem list会被加入到一级list中）
 * @author：Jing Yang
 * @date: 2020/4/24 10:30
 */
class RecordAdapter(data: MutableList<HeadItem>) : BaseExpandableAdapter(data) {

    private val ITEM_TYPE_HEAD = 1
    private val ITEM_TYPE_CHILD = 0

    var listener: OnRecordPlayerListener? = null

    override fun getItemView(type: Any?): AbstractAdapterItem<Any> {
        return when(type as Int) {
            ITEM_TYPE_HEAD -> HeadAdapter()
            else -> {
                var adapter = ChildAdapter()
                adapter.listener = listener
                adapter
            }
        }
    }

    override fun getItemViewType(t: Any?): Any {
        return when (t) {
            is HeadItem -> ITEM_TYPE_HEAD
            else -> ITEM_TYPE_CHILD
        }
    }
}