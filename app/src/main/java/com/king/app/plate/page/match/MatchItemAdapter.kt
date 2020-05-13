package com.king.app.plate.page.match

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.king.app.plate.R
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.databinding.AdapterMatchItemBinding
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.utils.RippleUtil

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/17 14:07
 */
class MatchItemAdapter: BaseBindingAdapter<AdapterMatchItemBinding, Match>() {

    var onActionListener:OnActionListener? = null

    override fun onCreateBind(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): AdapterMatchItemBinding =
    // 必须有后两个，否则宽度只能占recyclerview的一半
    AdapterMatchItemBinding.inflate(inflater, parent, false)

    override fun onBindItem(binding: AdapterMatchItemBinding?, position: Int, bean: Match) {
        binding!!.clGroup.background = RippleUtil.getRippleBackground(Color.WHITE
            , binding.clGroup.resources.getColor(R.color.ripple_color))
        binding.bean = bean
        if (bean.level == AppConstants.matchLevelFinal) {
            binding.ivCup.visibility = View.VISIBLE
            binding.tvFinal.visibility = View.VISIBLE
        }
        else {
            binding.ivCup.visibility = View.GONE
            binding.tvFinal.visibility = View.GONE
        }
        binding.ivDelete.setOnClickListener { onActionListener?.onDeleteItem(position, bean) }
        binding.ivEdit.setOnClickListener { onActionListener?.onEditItem(position, bean) }
    }

    interface OnActionListener {
        fun onDeleteItem(position: Int, bean: Match)
        fun onEditItem(position: Int, bean: Match)
    }
}