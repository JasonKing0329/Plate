package com.king.app.plate.page.player

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.ActivityPlayerBinding

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/22 16:28
 */
class PlayerActivity: BaseActivity<ActivityPlayerBinding, PlayerViewModel>() {

    companion object {
        var EXTRA_SELECT_MODE: String = "select_mode"

        var RESP_PLAYER_ID: String = "resp_player_id"
    }

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

    private fun isSelectMode(): Boolean {
        if (getIntentBundle() != null) {
            return getIntentBundle()!!.getBoolean(EXTRA_SELECT_MODE)
        }
        return false
    }

    private fun showPlayers(it: List<PlayerItem>?) {
        if (adapter == null) {
            adapter = PlayerAdapter()
            adapter!!.list = it
            adapter!!.setOnItemClickListener(object : BaseBindingAdapter.OnItemClickListener<PlayerItem>{
                override fun onClickItem(view: View, position: Int, data: PlayerItem) {
                    if (isSelectMode()) {
                        var intent = Intent()
                        intent.putExtra(RESP_PLAYER_ID, data.bean!!.id)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }

            })
            mBinding.rvList.adapter = adapter
        }
        else {
            adapter!!.list = it
            adapter!!.notifyDataSetChanged()
        }
    }
}

