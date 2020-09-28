package com.king.app.plate.page.glory

import android.view.LayoutInflater
import android.view.View
import com.king.app.plate.base.BaseFragment
import com.king.app.plate.base.EmptyViewModel
import com.king.app.plate.databinding.FragmentGloryChampionBinding

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/9/27 16:26
 */
class ChampionFragment: BaseFragment<FragmentGloryChampionBinding, EmptyViewModel>() {

    override fun getBinding(inflater: LayoutInflater): FragmentGloryChampionBinding = FragmentGloryChampionBinding.inflate(inflater)

    override fun createViewModel(): EmptyViewModel = generateViewModel(EmptyViewModel::class.java)

    override fun initView(view: View) {

    }

    override fun initData() {

    }
}