package com.king.app.plate.page.player.page

import android.view.View
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityPlayerPageBinding

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
        mBinding.tvScoreBody.visibility = View.GONE
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
        mBinding.ivScoreExpand.setOnClickListener{
            if (mBinding.tvScoreBody.visibility == View.VISIBLE) {
                mBinding.tvScoreBody.visibility = View.GONE
                mBinding.ivResultExpand.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp)
            }
            else {
                mBinding.tvScoreBody.visibility = View.VISIBLE
                mBinding.ivResultExpand.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp)
            }
        }
    }

    override fun initData() {
        mModel.loadPlayer(getPlayerId())
    }

    private fun getPlayerId(): Long {
        return getIntentBundle()?.getLong(EXTRA_PLAYER_ID)!!
    }
}