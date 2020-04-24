package com.king.app.plate.page.player.record

import com.king.app.plate.model.db.entity.Match
import com.zaihuishou.expandablerecycleradapter.model.ExpandableListItem

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/24 10:12
 */
class HeadItem: ExpandableListItem {

    var match: Match? = null
    var result = ""
    var childList = mutableListOf<ChildItem>()
    var isExpand: Boolean = false

    override fun getChildItemList(): MutableList<*> = childList

    override fun isExpanded(): Boolean = isExpand

    override fun setExpanded(isExpanded: Boolean) {
        this.isExpand = isExpanded
    }
}