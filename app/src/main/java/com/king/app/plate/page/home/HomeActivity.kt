package com.king.app.plate.page.home

import android.content.Intent
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityHomeBinding
import com.king.app.plate.page.SettingsActivity
import com.king.app.plate.utils.RippleUtil

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 13:43
 */
class HomeActivity: BaseActivity<ActivityHomeBinding, HomeViewModel>() {

    override fun getContentView() = R.layout.activity_home

    override fun createViewModel(): HomeViewModel = generateViewModel(HomeViewModel::class.java)

    override fun initView() {
        mBinding.tvPlayer.setOnClickListener {  }
        mBinding.tvH2h.setOnClickListener {  }
        mBinding.tvMatch.setOnClickListener {  }
        mBinding.tvRank.setOnClickListener {  }
        mBinding.vTemp.setOnClickListener {  }

        RippleUtil.setRippleBackground(mBinding.tvPlayer, resources.getColor(R.color.home_sec_player), resources.getColor(R.color.ripple_gray))
        RippleUtil.setRippleBackground(mBinding.tvMatch, resources.getColor(R.color.home_sec_match), resources.getColor(R.color.ripple_gray))
        RippleUtil.setRippleBackground(mBinding.tvRank, resources.getColor(R.color.home_sec_rank), resources.getColor(R.color.ripple_gray))
        RippleUtil.setRippleBackground(mBinding.tvH2h, resources.getColor(R.color.home_sec_h2h), resources.getColor(R.color.ripple_gray))

        mBinding.actionbar.setOnMenuItemListener { menuId ->
            when (menuId) {
                R.id.menu_setting -> startActivity(Intent().setClass(this, SettingsActivity::class.java))
            }
        }
    }

    override fun initData() {

    }

}