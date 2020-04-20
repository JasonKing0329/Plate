package com.king.app.plate.page.player

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.king.app.jactionbar.PopupMenuProvider
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.databinding.ActivityPlayerBinding
import com.king.app.plate.utils.ScreenUtils

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
        mBinding.rvList.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                outRect.top = ScreenUtils.dp2px(8f)
            }
        })

        mBinding.actionbar.setOnBackListener { onBackPressed() }
        mBinding.actionbar.registerPopupMenu(R.id.menu_sort)
        mBinding.actionbar.setPopupMenuProvider { iconMenuId, anchorView ->
            when(iconMenuId) {
                R.id.menu_sort -> getSortPopup(anchorView!!)
                else -> null
            }
        }
    }

    private fun getSortPopup(anchorView: View): PopupMenu? {
        val menu = PopupMenu(this, anchorView)
        menu.menuInflater.inflate(R.menu.player_sort, menu.menu)
        menu.setOnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.menu_sort_name -> mModel.onSortTypeChanged(AppConstants.playerSortName)
                R.id.menu_sort_rank -> mModel.onSortTypeChanged(AppConstants.playerSortRank)
            }
            true
        }
        return menu
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

