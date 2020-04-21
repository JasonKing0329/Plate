package com.king.app.plate.page.h2h

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityH2hBinding
import com.king.app.plate.page.player.PlayerActivity
import com.king.app.plate.utils.ColorUtils
import com.king.app.plate.utils.DrawableUtil

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 14:16
 */
class H2hActivity: BaseActivity<ActivityH2hBinding, H2hViewModel>() {

    private val REQUEST_PLAYER = 1

    var adapter = H2hItemAdapter()

    override fun getContentView(): Int = R.layout.activity_h2h

    override fun createViewModel(): H2hViewModel = generateViewModel(H2hViewModel::class.java)

    override fun initView() {
        mBinding.model = mModel

        mBinding.actionbar.setOnBackListener { onBackPressed() }

        mBinding.tvPlayer1.setOnClickListener {selectPlayer(1)}
        mBinding.tvPlayer2.setOnClickListener {selectPlayer(2)}
        mBinding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun initData() {
        mModel.h2hItems.observe(this, Observer { showH2hItems(it) })
    }

    private fun selectPlayer(i: Int) {
        mModel.indexToReceivePlayer = i
        var bundle = Bundle()
        bundle.putBoolean(PlayerActivity.EXTRA_SELECT_MODE, true)
        startPageForResult(PlayerActivity::class.java, bundle, REQUEST_PLAYER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_PLAYER -> {
                if (resultCode == Activity.RESULT_OK) {
                    var playerId = data?.getLongExtra(PlayerActivity.RESP_PLAYER_ID, 0)
                    mModel.loadReceivePlayer(playerId!!)
                    if (mModel.indexToReceivePlayer == 1) {
                        DrawableUtil.setGradientColor(mBinding.tvPlayer1, ColorUtils.randomWhiteTextBgColor());
                    }
                    else if (mModel.indexToReceivePlayer == 2) {
                        DrawableUtil.setGradientColor(mBinding.tvPlayer2, ColorUtils.randomWhiteTextBgColor());
                    }
                }
            }
        }
    }

    private fun showH2hItems(it: List<H2hItem>?) {
        adapter.list = it
        if (mBinding.rvList.adapter == null) {
            mBinding.rvList.adapter = adapter
        }
        else{
            adapter.notifyDataSetChanged()
        }
    }
}