package com.king.app.plate.page.player

import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityPlayerBinding

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 16:28
 */
class PlayerActivity: BaseActivity<ActivityPlayerBinding, PlayerViewModel>() {

    private var adapter: PlayerAdapter? = null

    override fun getContentView(): Int = R.layout.activity_player

    override fun createViewModel(): PlayerViewModel = generateViewModel(PlayerViewModel::class.java)

    override fun initView() {
        mBinding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }

    override fun initData() {
        mModel.playersObserver.observe(this, Observer { showPlayers(it) })

        mModel.loadPlayers()
    }

    private fun showPlayers(it: List<PlayerItem>?) {
        if (adapter == null) {
            adapter = PlayerAdapter()
            adapter!!.list = it
            mBinding.rvList.adapter = adapter
        }
        else {
            adapter!!.list = it
            adapter!!.notifyDataSetChanged()
        }
    }
}

