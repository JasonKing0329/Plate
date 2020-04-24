package com.king.app.plate.page.player.record

import android.view.View
import android.widget.TextView
import com.king.app.plate.R
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil
import com.zaihuishou.expandablerecycleradapter.viewholder.AbstractExpandableAdapterItem

/**
 * Desc: AbstractAdapterItem的作用仅仅是用来描述一个item的对象，所以在这里面只能描述当前position的item
 * @author：Jing Yang
 * @date: 2020/4/24 10:22
 */
class ChildAdapter: AbstractExpandableAdapterItem() {

    private lateinit var tvName: TextView
    private lateinit var tvWinner: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvRound: TextView
    private lateinit var root: View

    var listener: OnRecordPlayerListener? = null

    override fun onExpansionToggled(expanded: Boolean) {

    }

    override fun getLayoutResId(): Int = R.layout.adapter_record_player

    override fun onBindViews(root: View) {
        this.root = root
        tvName = root.findViewById(R.id.tv_name)
        tvWinner = root.findViewById(R.id.tv_winner)
        tvScore = root.findViewById(R.id.tv_score)
        tvRound = root.findViewById(R.id.tv_round)
    }

    override fun onSetViews() {

    }

    override fun onUpdateViews(model: Any?, position: Int) {
        super.onUpdateViews(model, position)
        var item = model as ChildItem
        item.itemPosition = position
        tvName.text = item.player.name
        tvWinner.text = item.winner
        tvScore.text = item.score
        tvRound.text = item.round
        if (item.isWinner) {
            tvWinner.setTextColor(tvWinner.resources.getColor(R.color.red))
        }
        else{
            tvWinner.setTextColor(tvWinner.resources.getColor(R.color.text_sub))
        }
        DrawableUtil.setGradientColor(tvName, item.playerColor)
        root.setOnClickListener{ listener?.onClickItem(it, position, item) }
    }
}