package com.king.app.plate.page.match

import android.content.DialogInterface
import android.graphics.Point
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityFinalDrawBinding
import com.king.app.plate.model.bean.RankPlayer
import com.king.app.plate.page.h2h.H2hActivity
import com.king.app.plate.page.player.page.PlayerPageActivity
import com.king.app.plate.view.dialog.AlertDialogFragment
import com.king.app.plate.view.draw.DrawKeyboard
import com.king.app.plate.view.draw.DrawsView

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/11 10:44
 */
class FinalDrawActivity: BaseActivity<ActivityFinalDrawBinding, FinalDrawViewModel>() {

    companion object {
        var EXTRA_MATCH_ID: String = "match_id"
    }

    private var popupKeyboard = PopupKeyboard()

    var playerRedAdapter = FinalPlayerAdapter()
    var playerBlueAdapter = FinalPlayerAdapter()
    var scoreRedAdapter = FinalScoreAdapter()
    var scoreBlueAdapter = FinalScoreAdapter()
    var groupRedDrawAdapter = DrawsAdapter()
    var groupBlueDrawAdapter = DrawsAdapter()
    var restDrawAdapter = DrawsAdapter()

    private var focusDraw: DrawsView? = null
    private var focusDrawAdapter: DrawsAdapter? = null

    override fun getContentView(): Int = R.layout.activity_final_draw

    override fun createViewModel(): FinalDrawViewModel = generateViewModel(FinalDrawViewModel::class.java)

    override fun initView() {
        mBinding.actionbar.setOnBackListener { onBackPressed() }
        mBinding.rvPlayerBlue.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvPlayerRed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding.rvScoreBlue.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvScoreRed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.drawGroupRed.setAdapter(groupRedDrawAdapter)
        mBinding.drawGroupBlue.setAdapter(groupBlueDrawAdapter)
        mBinding.drawWin.setAdapter(restDrawAdapter)

        mBinding.actionbar.setOnMenuItemListener {
            when (it) {
                R.id.menu_create -> mModel.createDraw()
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

        mBinding.drawGroupRed.setOnClickDrawItemListener(object : DrawsView.OnClickDrawItemListener {
            override fun onClickDrawItem(x: Int, y: Int) {
                focusDraw = mBinding.drawGroupRed
                focusDrawAdapter = groupRedDrawAdapter
                onClickPlayerCell(x, y, FinalDrawViewModel.DRAW_RED)
            }

            override fun onClickScoreItem(x: Int, y: Int, round: Int) {
                focusDraw = mBinding.drawGroupRed
                focusDrawAdapter = groupRedDrawAdapter
                popupEditScore(x, y)
            }
        })
        mBinding.drawGroupBlue.setOnClickDrawItemListener(object : DrawsView.OnClickDrawItemListener {
            override fun onClickDrawItem(x: Int, y: Int) {
                focusDraw = mBinding.drawGroupBlue
                focusDrawAdapter = groupBlueDrawAdapter
                onClickPlayerCell(x, y, FinalDrawViewModel.DRAW_BLUE)
            }

            override fun onClickScoreItem(x: Int, y: Int, round: Int) {
                focusDraw = mBinding.drawGroupBlue
                focusDrawAdapter = groupBlueDrawAdapter
                popupEditScore(x, y)
            }
        })
        mBinding.drawWin.setOnClickDrawItemListener(object : DrawsView.OnClickDrawItemListener {
            override fun onClickDrawItem(x: Int, y: Int) {
                focusDraw = mBinding.drawWin
                focusDrawAdapter = restDrawAdapter
                onClickPlayerCell(x, y, FinalDrawViewModel.DRAW_WIN)
            }

            override fun onClickScoreItem(x: Int, y: Int, round: Int) {
                focusDraw = mBinding.drawWin
                focusDrawAdapter = restDrawAdapter
                popupEditScore(x, y)
            }
        })

        popupKeyboard.onKeyActionListener = object: DrawKeyboard.OnClickKeyListener{
            override fun onKey(key: String?) {
                var point: Point? = getFocusDraw().focusPoint
                if (point != null) {
                    var isFirst = getFocusDraw().focusPoint != getFocusDraw().lastFocusPoint
                    getFocusAdapter().updateText(point.x, point.y, key, isFirst, popupKeyboard.isTieBreaking())
                    // 只要发生了输入就同步当前游标，以便下一次是继续输入
                    getFocusDraw().syncLastFocusPoint()

                    getFocusDraw().invalidate()
                }
            }

            override fun onClear() {
                var point: Point? = getFocusDraw().focusPoint
                if (point != null) {
                    getFocusAdapter().clearText(point.x, point.y)
                    getFocusDraw().invalidate()
                }
            }

            override fun onTieBreak(isOn: Boolean) {
                if (isOn) {
                    onKey("6()")
                }
            }

            override fun onDelete() {
                var point: Point? = getFocusDraw().focusPoint
                if (point != null) {
                    getFocusAdapter().deleteText(point.x, point.y)
                    getFocusDraw().invalidate()
                }
            }
        }

    }

    private fun popupEditScore(x: Int, y: Int) {
        if (getFocusDraw().focusRect != null) {
            popupKeyboard.show(this, getFocusDraw().focusRect, 0, getFocusDraw(), getFocusDraw().left)
        }
    }

    private fun onClickPlayerCell(x: Int, y: Int, drawType: Int) {
        var items = if(drawType == FinalDrawViewModel.DRAW_WIN) arrayOf<CharSequence>("Player page", "View H2H", "Remove", "Get winner")
        else arrayOf<CharSequence>("Player page", "View H2H", "Remove")
        AlertDialogFragment()
            .setItems(items, DialogInterface.OnClickListener { dialogInterface, i ->
                when(i) {
                    0 -> playerPage(x, y, drawType)
                    1 -> viewH2h(x, y, drawType)
                    2 -> {
                        mModel.deletePlayer(x, y, drawType)
                        getFocusDraw().invalidate()
                    }
                    3 -> {
                        mModel.getWinnerFor(x, y)
                        getFocusDraw().invalidate()
                    }
                }
            })
            .show(supportFragmentManager, "AlertDialogFragment")

    }

    private fun playerPage(x: Int, y: Int, drawType: Int) {
        var player = mModel.getRecordPlayer(x, y, drawType)
        if (player != null) {
            var bundle = Bundle()
            bundle.putLong(PlayerPageActivity.EXTRA_PLAYER_ID, player.playerId)
            startPage(PlayerPageActivity::class.java, bundle)
        }
    }

    private fun viewH2h(x: Int, y: Int, drawType: Int) {
        var player1 = mModel.getRecordPlayer(x, y, 0, drawType)
        var player2 = mModel.getRecordPlayer(x, y, 1, drawType)
        if (player1 != null && player2 != null) {
            var bundle = Bundle()
            bundle.putLong(H2hActivity.EXTRA_PLAYER1_ID, player1.playerId)
            bundle.putLong(H2hActivity.EXTRA_PLAYER2_ID, player2.playerId)
            startPage(H2hActivity::class.java, bundle)
        }
    }

    private fun getFocusDraw(): DrawsView {
        return focusDraw!!
    }

    private fun getFocusAdapter(): DrawsAdapter {
        return focusDrawAdapter!!
    }

    private fun getMatchId(): Long {
        var bundle = getIntentBundle()
        if (bundle != null) {
            return bundle.getLong(EXTRA_MATCH_ID)
        }
        return 0
    }

    override fun initData() {
        mModel.redDataObserver.observe(this, Observer { showDrawData(groupRedDrawAdapter, it) })
        mModel.blueDataObserver.observe(this, Observer { showDrawData(groupBlueDrawAdapter, it) })
        mModel.restDataObserver.observe(this, Observer { showDrawData(restDrawAdapter, it) })
        mModel.showGroupRedPlayers.observe(this, Observer { showRedPlayers(it) })
        mModel.showGroupBluePlayers.observe(this, Observer { showBluePlayers(it) })
        mModel.groupRedResults.observe(this, Observer { showRedResult(it) })
        mModel.groupBlueResults.observe(this, Observer { showBlueResult(it) })

        mModel.loadData(getMatchId())
    }

    private fun showRedResult(list: MutableList<FinalPlayerScore>) {
        scoreRedAdapter.list = list
        if (mBinding.rvScoreRed.adapter == null) {
            mBinding.rvScoreRed.adapter = scoreRedAdapter
        }
        else {
            scoreRedAdapter.notifyDataSetChanged()
        }
    }

    private fun showBlueResult(list: MutableList<FinalPlayerScore>) {
        scoreBlueAdapter.list = list
        if (mBinding.rvScoreBlue.adapter == null) {
            mBinding.rvScoreBlue.adapter = scoreBlueAdapter
        }
        else {
            scoreBlueAdapter.notifyDataSetChanged()
        }
    }

    private fun showRedPlayers(list: MutableList<RankPlayer>?) {
        playerRedAdapter.list = list
        if (mBinding.rvPlayerRed.adapter == null) {
            mBinding.rvPlayerRed.adapter = playerRedAdapter
        }
        else {
            playerRedAdapter.notifyDataSetChanged()
        }
    }

    private fun showBluePlayers(list: MutableList<RankPlayer>?) {
        playerBlueAdapter.list = list
        if (mBinding.rvPlayerBlue.adapter == null) {
            mBinding.rvPlayerBlue.adapter = playerBlueAdapter
        }
        else {
            playerBlueAdapter.notifyDataSetChanged()
        }
    }

    private fun showDrawData(adapter: DrawsAdapter, it: DrawData?) {
        adapter.setData(it)
        adapter.notifyDataSetChanged()
    }

}