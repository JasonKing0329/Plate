package com.king.app.plate.page.h2h

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.ActivityH2hAllBinding
import com.king.app.plate.model.bean.H2hResultPack
import com.king.app.plate.page.player.PlayerItem
import com.king.app.plate.page.player.page.PlayerPageActivity
import com.king.app.plate.utils.ScreenUtils

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/22 11:19
 */
class H2hTableActivity: BaseActivity<ActivityH2hAllBinding, H2hTableViewModel>() {

    companion object {
        const val EXTRA_PLAYER_ID = "player_id"
    }

    private var playerAdapter = H2hTableItemAdapter()
    private var h2hAdapter = H2hTableItemAdapter()

    override fun getContentView(): Int = R.layout.activity_h2h_all

    override fun createViewModel(): H2hTableViewModel = generateViewModel(H2hTableViewModel::class.java)

    override fun initView() {
        mBinding.actionbar.setOnBackListener { onBackPressed() }

        var row = mModel.getRow()
        var manager = GridLayoutManager(this, row)
        manager.orientation = GridLayoutManager.HORIZONTAL
        mBinding.rvList.layoutManager = manager
        mBinding.rvList.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                outRect.top = ScreenUtils.dp2px(1f)
                outRect.left = ScreenUtils.dp2px(1f)
            }
        })

        manager = GridLayoutManager(this, row)
        manager.orientation = GridLayoutManager.HORIZONTAL
        mBinding.rvPlayers.layoutManager = manager
        mBinding.rvPlayers.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                outRect.top = ScreenUtils.dp2px(1f)
            }
        })
    }

    private fun getPlayerId(): Long {
        return if (getIntentBundle() == null) 0
        else getIntentBundle()!!.getLong(PlayerPageActivity.EXTRA_PLAYER_ID)
    }

    override fun initData() {
        mModel.playerList.observe(this, Observer { showPlayers(it) })
        mModel.h2hList.observe(this, Observer { showH2h(it) })
        mModel.loadData(getPlayerId())
    }

    private fun showPlayers(it: MutableList<H2hTableItem>?) {
        playerAdapter.list = it
        if (mBinding.rvPlayers.adapter == null) {
            playerAdapter!!.setOnItemClickListener(object : BaseBindingAdapter.OnItemClickListener<H2hTableItem>{
                override fun onClickItem(view: View, position: Int, data: H2hTableItem) {
                    if (data != null) {
                        mModel.focusRow(position)
                        h2hAdapter.notifyDataSetChanged()
                    }
                }
            })
            mBinding.rvPlayers.adapter = playerAdapter
        }
        else{
            playerAdapter.notifyDataSetChanged()
        }
    }

    private fun showH2h(it: MutableList<H2hTableItem>?) {
        h2hAdapter.list = it
        if (mBinding.rvList.adapter == null) {
            h2hAdapter!!.setOnItemClickListener(object : BaseBindingAdapter.OnItemClickListener<H2hTableItem>{
                override fun onClickItem(view: View, position: Int, data: H2hTableItem) {
                    if (mModel.isH2hPlayer(position)) {
                        mModel.focusCol(position)
                        h2hAdapter.notifyDataSetChanged()
                    }
                    else if (mModel.isH2hResult(position)) {
                        showH2hPage(data.bean as H2hResultPack)
                        data.isFocus = true
                        h2hAdapter.notifyItemChanged(position)
                    }
                }
            })
            mBinding.rvList.adapter = h2hAdapter
        }
        else{
            h2hAdapter.notifyDataSetChanged()
        }
    }

    private fun showH2hPage(pack: H2hResultPack) {
        var bundle = Bundle()
        bundle.putLong(H2hActivity.EXTRA_PLAYER1_ID, pack.player1Id)
        bundle.putLong(H2hActivity.EXTRA_PLAYER2_ID, pack.player2Id)
        startPage(H2hActivity::class.java, bundle)
    }
}