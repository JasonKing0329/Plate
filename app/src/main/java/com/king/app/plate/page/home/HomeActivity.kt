package com.king.app.plate.page.home

import android.os.Bundle
import androidx.lifecycle.Observer
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityHomeBinding
import com.king.app.plate.model.db.AppDatabase
import com.king.app.plate.page.SettingsActivity
import com.king.app.plate.page.h2h.H2hActivity
import com.king.app.plate.page.match.DrawsActivity
import com.king.app.plate.page.match.MatchActivity
import com.king.app.plate.page.player.PlayerActivity
import com.king.app.plate.page.rank.RankActivity
import com.king.app.plate.utils.DrawableUtil
import com.king.app.plate.utils.ScreenUtils
import com.king.app.plate.view.dialog.PopupDialog

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 13:43
 */
class HomeActivity: BaseActivity<ActivityHomeBinding, HomeViewModel>() {

    override fun getContentView() = R.layout.activity_home

    override fun createViewModel(): HomeViewModel = generateViewModel(HomeViewModel::class.java)

    override fun initView() {
        mBinding.tvPlayer.setOnClickListener { startPage(PlayerActivity::class.java) }
        mBinding.tvH2h.setOnClickListener { startPage(H2hActivity::class.java) }
        mBinding.tvMatch.setOnClickListener { startPage(MatchActivity::class.java) }
        mBinding.tvRank.setOnClickListener { startPage(RankActivity::class.java) }
        mBinding.vTemp.setOnClickListener { mModel.getLastMatch() }

        DrawableUtil.setRippleBackground(mBinding.tvPlayer, resources.getColor(R.color.home_sec_player), resources.getColor(R.color.ripple_gray))
        DrawableUtil.setRippleBackground(mBinding.tvMatch, resources.getColor(R.color.home_sec_match), resources.getColor(R.color.ripple_gray))
        DrawableUtil.setRippleBackground(mBinding.tvRank, resources.getColor(R.color.home_sec_rank), resources.getColor(R.color.ripple_gray))
        DrawableUtil.setRippleBackground(mBinding.tvH2h, resources.getColor(R.color.home_sec_h2h), resources.getColor(R.color.ripple_gray))

        mBinding.actionbar.setOnMenuItemListener { menuId ->
            when (menuId) {
                R.id.menu_save -> mModel.saveData()
                R.id.menu_load_from -> loadFrom()
                R.id.menu_setting -> startPage(SettingsActivity::class.java)
            }
        }

        mModel.showLastMatchDraw.observe(this, Observer { showLastDraw(it) })
    }

    private fun showLastDraw(matchId: Long) {
        var bundle = Bundle()
        bundle.putLong(DrawsActivity.EXTRA_MATCH_ID, matchId)
        startPage(DrawsActivity::class.java, bundle)
    }

    private fun loadFrom() {
        var content = LoadFromDialog()
        content.onDataChangedListener = object : LoadFromDialog.OnDataChangedListener {
            override fun onDatabaseChanged() {

            }
        }
        var dialog = PopupDialog()
        dialog.content = content
        dialog.title = "Load From"
        dialog.forceHeight = ScreenUtils.getScreenHeight() * 3 / 5
        dialog.show(supportFragmentManager, "LoadFromDialog")
    }

    override fun initData() {

    }

    override fun onDestroy() {
        AppDatabase.instance.close()
        super.onDestroy()
    }
}