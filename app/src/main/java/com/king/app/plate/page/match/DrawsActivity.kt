package com.king.app.plate.page.match

import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityMatchDrawBinding

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 11:25
 */
class DrawsActivity: BaseActivity<ActivityMatchDrawBinding, DrawViewModel>() {

    override fun getContentView(): Int = R.layout.activity_match_draw

    override fun createViewModel(): DrawViewModel = generateViewModel(DrawViewModel::class.java)

    override fun initView() {

    }

    override fun initData() {

    }
}