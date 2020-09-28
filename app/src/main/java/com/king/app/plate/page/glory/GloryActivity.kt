package com.king.app.plate.page.glory

import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.base.EmptyViewModel
import com.king.app.plate.databinding.ActivityGloryBinding
import com.king.app.plate.utils.DebugLog


/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/9/27 15:53
 */
class GloryActivity: BaseActivity<ActivityGloryBinding, EmptyViewModel>() {

    private val titles = arrayOf(
        "Round", "Champions"
    )
    private val PAGE_ROUND = 0
    private val PAGE_CHAMPION = 1

    override fun getContentView(): Int = R.layout.activity_glory

    override fun createViewModel(): EmptyViewModel = generateViewModel(EmptyViewModel::class.java)

    override fun initView() {
        mBinding.actionbar.setOnBackListener{ onBackPressed() }
    }

    override fun initData() {

        var list = listOf(RoundFragment(), ChampionFragment())
        var adapter = GloryPageAdapter(this)
        adapter.list = list
        mBinding.viewpager.adapter = adapter

        TabLayoutMediator(mBinding.tabLayout, mBinding.viewpager, true,
            TabLayoutMediator.TabConfigurationStrategy { tab, position -> tab.text = titles[position] }).attach()
    }
}