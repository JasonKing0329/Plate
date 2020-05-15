package com.king.app.plate.page.player.page

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityPlayerPageBinding
import com.king.app.plate.page.h2h.H2hTableActivity
import com.king.app.plate.page.player.record.RecordListActivity
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil
import com.king.app.plate.utils.ScreenUtils
import com.king.app.plate.view.dialog.PopupDialog
import com.king.app.plate.view.widget.chart.adapter.IAxis
import com.king.app.plate.view.widget.chart.adapter.LineChartAdapter
import com.king.app.plate.view.widget.chart.adapter.LineData

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/23 13:09
 */
class PlayerPageActivity: BaseActivity<ActivityPlayerPageBinding, PlayerPageViewModel>() {

    companion object {
        const val EXTRA_PLAYER_ID = "player_id"
    }

    override fun getContentView(): Int = R.layout.activity_player_page

    override fun createViewModel(): PlayerPageViewModel = generateViewModel(PlayerPageViewModel::class.java)

    override fun initView() {
        mBinding.model = mModel
        mBinding.actionbar.setOnBackListener { onBackPressed() }

        mBinding.llResults.visibility = View.GONE
        mBinding.ivResultExpand.setOnClickListener{
            if (mBinding.llResults.visibility == View.VISIBLE) {
                mBinding.llResults.visibility = View.GONE
                mBinding.ivResultExpand.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp)
            }
            else {
                mBinding.llResults.visibility = View.VISIBLE
                mBinding.ivResultExpand.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp)
            }
        }
        mBinding.llScore.setOnClickListener{ showScoreDetails() }

        mBinding.llH2h.setOnClickListener{
            var bundle = Bundle()
            bundle.putLong(H2hTableActivity.EXTRA_PLAYER_ID, mModel.player.id)
            startPage(H2hTableActivity::class.java, bundle)
        }
        mBinding.llRecords.setOnClickListener {
            var bundle = Bundle()
            bundle.putLong(RecordListActivity.EXTRA_PLAYER_ID, mModel.player.id)
            startPage(RecordListActivity::class.java, bundle)
        }

    }

    private fun showScoreDetails() {
        var content = ScoreStructDialog()
        content.playerId = getPlayerId()
        var dialog = PopupDialog()
        dialog.content = content
        dialog.title = "Score Struct"
        dialog.forceHeight = ScreenUtils.getScreenHeight() * 3 / 5
        dialog.show(supportFragmentManager, "ScoreStructDialog")
    }

    override fun initData() {
        mModel.playerObserver.observe(this, Observer {
            var color = if (it.defColor == null) ColorUtils.randomWhiteTextBgColor()
            else it.defColor!!
            DrawableUtil.setGradientColor(mBinding.tvName, color)
        })
        mModel.rankChartData.observe(this, Observer { showChart(it) })
        mModel.loadPlayer(getPlayerId())
    }

    private fun getPlayerId(): Long {
        return getIntentBundle()?.getLong(EXTRA_PLAYER_ID)!!
    }

    private fun showChart(it: RankChartData) {
        if (it.ranks.isEmpty()) {
            mBinding.chartRank.visibility = View.GONE
            return
        }
        mBinding.chartRank.visibility = View.VISIBLE
        mBinding.chartRank.setDrawAxisY(true)
        mBinding.chartRank.setDegreeCombine(1)
        mBinding.chartRank.setAxisX(object :IAxis{
            override fun getTextAt(position: Int): String = it.xTexts[position]!!

            override fun isNotDraw(position: Int): Boolean = false

            override fun getTotalWeight(): Int = it.ranks.size

            override fun getDegreeCount(): Int = it.ranks.size

            override fun getWeightAt(position: Int): Int = position
        })
        mBinding.chartRank.setAxisY(object :IAxis {
            override fun getTextAt(position: Int): String = (it.max - position).toString()

            override fun isNotDraw(position: Int): Boolean = position % 2 == 1 || position == 0

            override fun getTotalWeight(): Int = it.max

            override fun getDegreeCount(): Int = it.max

            override fun getWeightAt(position: Int): Int = position
        })
        mBinding.chartRank.setAdapter(object : LineChartAdapter(){
            override fun getLineData(lineIndex: Int): LineData = it.lineData

            override fun getLineCount(): Int = 1

        })
    }

}