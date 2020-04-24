package com.king.app.plate.page.player.record

import android.view.View
import android.widget.TextView
import com.king.app.plate.R
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/24 9:57
 */
class HeadAdapter: AbstractExpandableAdapterItem() {

    private lateinit var tvName: TextView
    private lateinit var tvResult: TextView

    override fun onExpansionToggled(expanded: Boolean) {

    }

    override fun getLayoutResId(): Int = R.layout.adapter_record_match

    override fun onBindViews(root: View?) {
        root!!.setOnClickListener { doExpandOrUnexpand() }
        tvName = root.findViewById(R.id.tv_date)
        tvResult = root.findViewById(R.id.tv_result)
    }

    override fun onSetViews() {
    }

    override fun onUpdateViews(model: Any?, position: Int) {
        super.onUpdateViews(model, position)
        var item = model as HeadItem
        tvName.text = item.match!!.name
        tvResult.text = item.result
    }
}