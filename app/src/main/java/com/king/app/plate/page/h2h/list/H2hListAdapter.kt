package com.king.app.plate.page.h2h.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.king.app.plate.R
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.AdapterH2hListItemBinding
import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/6/28 14:11
 */
class H2hListAdapter: BaseBindingAdapter<AdapterH2hListItemBinding, H2hListItem>() {
    override fun onCreateBind(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): AdapterH2hListItemBinding = AdapterH2hListItemBinding.inflate(inflater, parent, false)

    override fun onBindItem(binding: AdapterH2hListItemBinding?, position: Int, bean: H2hListItem) {
        binding!!.bean = bean
        updatePlayer(binding.tvPlayer1, bean.player1)
        updatePlayer(binding.tvPlayer2, bean.player2)
        if (bean.score1 > bean.score2) {
            binding.tvScore1.setTextColor(binding.tvScore1.resources.getColor(R.color.h2h_win))
            binding.tvScore2.setTextColor(binding.tvScore1.resources.getColor(R.color.h2h_lose))
        }
        else if (bean.score1 < bean.score2) {
            binding.tvScore1.setTextColor(binding.tvScore1.resources.getColor(R.color.h2h_lose))
            binding.tvScore2.setTextColor(binding.tvScore1.resources.getColor(R.color.h2h_win))
        }
        else {
            binding.tvScore1.setTextColor(binding.tvScore1.resources.getColor(R.color.h2h_tie))
            binding.tvScore2.setTextColor(binding.tvScore1.resources.getColor(R.color.h2h_tie))
        }
    }

    private fun updatePlayer(view: View, player: Player) {
        var color = if (player.defColor == null) ColorUtils.randomWhiteTextBgColor()
        else player.defColor!!
        DrawableUtil.setGradientColor(view, color)
    }
}