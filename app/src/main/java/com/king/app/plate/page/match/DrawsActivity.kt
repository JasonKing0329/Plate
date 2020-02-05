package com.king.app.plate.page.match

import android.graphics.Point
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityMatchDrawBinding
import com.king.app.plate.view.draw.DrawsView

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 11:25
 */
class DrawsActivity: BaseActivity<ActivityMatchDrawBinding, DrawViewModel>() {

    companion object {
        var EXTRA_MATCH_ID: String = "match_id"
    }

    private var adapter: DrawsAdapter = DrawsAdapter()

    override fun getContentView(): Int = R.layout.activity_match_draw

    override fun createViewModel(): DrawViewModel = generateViewModel(DrawViewModel::class.java)

    override fun initView() {
        mBinding.draws.setAdapter(adapter)
        mBinding.draws.setOnClickDrawItemListener(object : DrawsView.OnClickDrawItemListener {
            override fun onClickDrawItem(x: Int, y: Int) {

            }

            override fun onClickScoreItem(x: Int, y: Int, round: Int) {
                editScore(round)
            }
        })

        mBinding.keyboard.setOnClickKeyListener {
            var point: Point? = mBinding.draws.focusPoint
            if (point != null) {
                adapter.updateText(point.x, point.y, it)
            }
        }
    }

    private fun editScore(round: Int) {
        
    }

    override fun initData() {
        mModel.loadData(intent.getIntExtra(EXTRA_MATCH_ID, -1))
    }
}