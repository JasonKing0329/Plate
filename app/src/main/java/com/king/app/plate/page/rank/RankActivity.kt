package com.king.app.plate.page.rank

import android.graphics.Rect
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityRankBinding
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil
import com.king.app.plate.utils.ScreenUtils

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 9:18
 */
class RankActivity: BaseActivity<ActivityRankBinding, RankViewModel>() {

    private var adapter1 = RankItemAdapter()
    private var adapter2 = RankItemAdapter()

    override fun getContentView(): Int = R.layout.activity_rank

    override fun createViewModel(): RankViewModel = generateViewModel(RankViewModel::class.java)

    override fun initView() {
        mBinding.model = mModel

        mBinding.actionbar.setOnBackListener { onBackPressed() }
        mBinding.rvRank1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvRank1.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                outRect.top = ScreenUtils.dp2px(1f)
            }
        })
        mBinding.rvRank2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvRank2.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                outRect.top = ScreenUtils.dp2px(1f)
            }
        })

        ColorUtils.updateIconColor(mBinding.ivLast, resources.getColor(R.color.colorPrimary))
        ColorUtils.updateIconColor(mBinding.ivNext, resources.getColor(R.color.colorPrimary))
        mBinding.ivLast.setOnClickListener { v -> mModel.lastRanks() }
        mBinding.ivNext.setOnClickListener { v -> mModel.nextRanks() }
    }

    override fun initData() {
        mModel.rankList1.observe(this, Observer { showRankList1(it) })
        mModel.rankList2.observe(this, Observer { showRankList2(it) })

        mModel.loadData()
    }

    private fun showRankList1(it: List<RankItem>) {
        adapter1.list = it
        if (mBinding.rvRank1.adapter == null) {
            mBinding.rvRank1.adapter = adapter1
        }
        else{
            adapter1.notifyDataSetChanged()
        }
    }

    private fun showRankList2(it: List<RankItem>) {
        adapter2.list = it
        if (mBinding.rvRank2.adapter == null) {
            mBinding.rvRank2.adapter = adapter2
        }
        else{
            adapter2.notifyDataSetChanged()
        }
    }
}