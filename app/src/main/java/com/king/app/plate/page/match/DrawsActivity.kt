package com.king.app.plate.page.match

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import androidx.lifecycle.Observer
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityMatchDrawBinding
import com.king.app.plate.page.player.PlayerActivity
import com.king.app.plate.view.dialog.AlertDialogFragment
import com.king.app.plate.view.dialog.SimpleDialogs
import com.king.app.plate.view.draw.DrawKeyboard
import com.king.app.plate.view.draw.DrawsView

/**
 * @author Jing
 * @description:
 * @date :2020/1/24 0024 11:25
 */
class DrawsActivity: BaseActivity<ActivityMatchDrawBinding, DrawViewModel>() {

    companion object {
        var EXTRA_MATCH_ID: String = "match_id"
    }

    val REQUEST_PLAYER = 1

    private var adapter: DrawsAdapter = DrawsAdapter()

    private var popupKeyboard = PopupKeyboard()

    override fun getContentView(): Int = R.layout.activity_match_draw

    override fun createViewModel(): DrawViewModel = generateViewModel(DrawViewModel::class.java)

    override fun initView() {
        mBinding.draws.setAdapter(adapter)
        mBinding.draws.setOnClickDrawItemListener(object : DrawsView.OnClickDrawItemListener {
            override fun onClickDrawItem(x: Int, y: Int) {
                onClickPlayerCell(x, y)
            }

            override fun onClickScoreItem(x: Int, y: Int, round: Int) {
                popupEditScore(x, y)
            }
        })

        popupKeyboard.onKeyActionListener = object: DrawKeyboard.OnClickKeyListener{
            override fun onKey(key: String?) {
                var point: Point? = mBinding.draws.focusPoint
                if (point != null) {
                    adapter.updateText(point.x, point.y, key)
                    mBinding.draws.invalidate()
                }
            }

            override fun onClear() {
                var point: Point? = mBinding.draws.focusPoint
                if (point != null) {
                    adapter.clearText(point.x, point.y)
                    mBinding.draws.invalidate()
                }
            }

            override fun onDelete() {
                var point: Point? = mBinding.draws.focusPoint
                if (point != null) {
                    adapter.deleteText(point.x, point.y)
                    mBinding.draws.invalidate()
                }
            }
        }

        mBinding.actionBar.setOnMenuItemListener { menuId ->
            when (menuId) {
                R.id.menu_create -> showConfirmCancelMessage("Create draw will clear all existed data of current match, continue?"
                    , DialogInterface.OnClickListener { dialogInterface, i ->  mModel.createNewDraw()}
                    , null)
                R.id.menu_save -> mModel.saveDraw()
                R.id.menu_create_score -> {
                    if (mModel.isMatchCompleted()) {
                        if (mModel.match.isScoreCreated) {
                            showConfirmCancelMessage("Create score will clear all existed scores of current match, continue?"
                                , DialogInterface.OnClickListener { dialogInterface, i ->  mModel.createScore()}
                                , null)
                        }
                        else{
                            mModel.createScore()
                        }
                    }
                    else{
                        showMessageShort("Current match is not completed")
                    }
                }
                R.id.menu_create_rank -> {
                    if (mModel.isMatchCompleted()) {
                        if (mModel.match.isRankCreated) {
                            showConfirmCancelMessage("Create rank will clear all existed ranks of current match, continue?"
                                , DialogInterface.OnClickListener { dialogInterface, i ->  mModel.createRank()}
                                , null)
                        }
                        else{
                            mModel.createRank()
                        }
                    }
                    else{
                        showMessageShort("Current match is not completed")
                    }
                }
            }
        }
    }

    private fun popupEditScore(x: Int, y: Int) {
        if (mBinding.draws.focusRect != null) {
            var scrollX = mBinding.scrollView.scrollX
            popupKeyboard.show(this, mBinding.draws.focusRect, scrollX, mBinding.draws)
        }
    }

    private fun onClickPlayerCell(x: Int, y: Int) {
        var items = arrayOf<CharSequence>("Select player", "Set seed", "Remove", "Get winner")
        AlertDialogFragment()
            .setItems(items, DialogInterface.OnClickListener { dialogInterface, i ->
                when(i) {
                    0 -> selectPlayer(x, y)
                    1 -> setPlayerSeed(x, y)
                    2 -> {
                        mModel.deletePlayer(x, y)
                        mBinding.draws.invalidate()
                    }
                    3 -> {
                        mModel.getWinnerFor(x, y)
                        mBinding.draws.invalidate()
                    }
                }
            })
            .show(supportFragmentManager, "AlertDialogFragment")

    }

    private fun setPlayerSeed(x: Int, y: Int) {
        SimpleDialogs().openInputDialog(this, "input seed") { mModel.updatePlayerSeed(x, y, it) }
    }

    private fun selectPlayer(x: Int, y: Int) {
        mModel.setForResultBodyCell(x, y)
        var bundle = Bundle()
        bundle.putBoolean(PlayerActivity.EXTRA_SELECT_MODE, true)
        startPageForResult(PlayerActivity::class.java, bundle, REQUEST_PLAYER)
    }

    override fun initData() {
        mModel.dataObserver.observe(this, Observer { showDrawData(it) })
        mModel.loadData(getMatchId())
    }

    private fun getMatchId(): Long {
        var bundle = getIntentBundle()
        if (bundle != null) {
            return bundle.getLong(EXTRA_MATCH_ID)
        }
        return 0
    }

    private fun showDrawData(it: DrawData?) {
        adapter.setData(it)
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_PLAYER -> {
                if (resultCode == Activity.RESULT_OK) {
                    var playerId = data?.getLongExtra(PlayerActivity.RESP_PLAYER_ID, 0)
                    mModel.updateForResultPlayer(playerId!!)
                    mBinding.draws.invalidate()
                }
            }
        }
    }
}