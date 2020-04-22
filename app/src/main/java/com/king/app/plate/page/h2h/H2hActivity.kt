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

    companion object {
        var EXTRA_PLAYER1_ID: String = "player1_id"
        var EXTRA_PLAYER2_ID: String = "player2_id"
    }

    private val REQUEST_PLAYER = 1

    var adapter = H2hItemAdapter()

    override fun getContentView(): Int = R.layout.activity_h2h

    override fun createViewModel(): H2hViewModel = generateViewModel(H2hViewModel::class.java)

    override fun initView() {
        mBinding.model = mModel

        mBinding.actionbar.setOnBackListener { onBackPressed() }
        mBinding.tvAll.setOnClickListener { startPage(H2hTableActivity::class.java) }

        mBinding.tvPlayer1.setOnClickListener {selectPlayer(1)}
        mBinding.tvPlayer2.setOnClickListener {selectPlayer(2)}
        mBinding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun initData() {
        mModel.player1CircleColor.observe(this, Observer { DrawableUtil.setGradientColor(mBinding.tvPlayer1, it) })
        mModel.player2CircleColor.observe(this, Observer { DrawableUtil.setGradientColor(mBinding.tvPlayer2, it) })
        mModel.h2hItems.observe(this, Observer { showH2hItems(it) })

        init(intent)
    }

    private fun init(intent: Intent) {
        var player1Id = getIntentPlayerId(getIntentBundle(intent), EXTRA_PLAYER1_ID)
        var player2Id = getIntentPlayerId(getIntentBundle(intent), EXTRA_PLAYER2_ID)
        if (player1Id != null && player2Id != null) {
            mModel.initPlayers(player1Id, player2Id)
        }
        else {
            mModel.showLastH2h()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            init(intent)
        }
    }

    private fun getIntentPlayerId(bundle: Bundle?, key: String): Long? {
        return bundle?.getLong(key)
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