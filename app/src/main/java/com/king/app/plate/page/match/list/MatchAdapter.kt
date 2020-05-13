package com.king.app.plate.page.match.list

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.king.app.plate.R
import com.king.app.plate.base.adapter.HeadChildBindingAdapter
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.databinding.AdapterMatchItemBinding
import com.king.app.plate.databinding.AdapterMatchPeriodBinding
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil
import com.king.app.plate.utils.RippleUtil

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/13 15:44
 */
class MatchAdapter: HeadChildBindingAdapter<AdapterMatchPeriodBinding, AdapterMatchItemBinding, MatchPeriodTitle, MatchItemBean>() {

    override val itemClass: Class<*> get() = MatchItemBean::class.java

    var onActionListener: OnActionListener? = null

    override fun onCreateHeadBind(
        from: LayoutInflater,
        parent: ViewGroup
    ): AdapterMatchPeriodBinding = AdapterMatchPeriodBinding.inflate(from, parent, false)

    override fun onCreateItemBind(
        from: LayoutInflater,
        parent: ViewGroup
    ): AdapterMatchItemBinding = AdapterMatchItemBinding.inflate(from, parent, false)

    override fun onBindHead(binding: AdapterMatchPeriodBinding?, position: Int, head: MatchPeriodTitle) {
        binding!!.bean = head
    }

    override fun onBindItem(binding: AdapterMatchItemBinding?, position: Int, bean: MatchItemBean) {
        binding!!.clGroup.background = RippleUtil.getRippleBackground(
            Color.WHITE
            , binding.clGroup.resources.getColor(R.color.ripple_color))
        binding.bean = bean.match
        binding.player = bean.winner
        if (bean.match.level == AppConstants.matchLevelFinal) {
            binding.ivCup.visibility = View.VISIBLE
            binding.tvFinal.visibility = View.VISIBLE
        }
        else {
            binding.ivCup.visibility = View.GONE
            binding.tvFinal.visibility = View.GONE
        }
        binding.ivDelete.setOnClickListener { onActionListener?.onDeleteItem(position, bean.match) }
        binding.ivEdit.setOnClickListener { onActionListener?.onEditItem(position, bean.match) }

        if (bean.winner != null) {
            var color = if (bean.winner!!.player!!.defColor == null) ColorUtils.randomWhiteTextBgColor()
            else bean.winner!!.player!!.defColor!!
            DrawableUtil.setGradientColor(binding!!.tvWinner, color)
        }
    }

    interface OnActionListener {
        fun onDeleteItem(position: Int, bean: Match)
        fun onEditItem(position: Int, bean: Match)
    }
}