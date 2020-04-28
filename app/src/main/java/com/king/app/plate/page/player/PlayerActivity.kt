package com.king.app.plate.page.player

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.king.app.jactionbar.PopupMenuProvider
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.databinding.ActivityPlayerBinding
import com.king.app.plate.page.player.page.PlayerPageActivity
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

    private var adapter: PlayerAdapter = PlayerAdapter()

    private var isSetColor = false

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
        mBinding.actionbar.setOnMenuItemListener {
            when(it) {
                R.id.menu_color -> {
                    mBinding.actionbar.showConfirmStatus(it, true, "Cancel");
                    isSetColor = true
                }
            }
        }
        mBinding.actionbar.setOnConfirmListener {
            when(it) {
                R.id.menu_color -> isSetColor = false
            }
            true
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
        adapter!!.list = it
        if (mBinding.rvList.adapter == null) {
            adapter!!.setOnItemClickListener(object : BaseBindingAdapter.OnItemClickListener<PlayerItem>{
                override fun onClickItem(view: View, position: Int, data: PlayerItem) {
                    if (isSetColor) {
                        setColorForPlayer(position, data)
                    }
                    else {
                        if (isSelectMode()) {
                            var intent = Intent()
                            intent.putExtra(RESP_PLAYER_ID, data.bean!!.id)
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        }
                        else {
                            var bundle = Bundle()
                            bundle.putLong(PlayerPageActivity.EXTRA_PLAYER_ID, data.bean!!.id)
                            startPage(PlayerPageActivity::class.java, bundle)
                        }
                    }
                }

            })
            mBinding.rvList.adapter = adapter
        }
        else {
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun setColorForPlayer(position: Int, data: PlayerItem) {
        var builder = ColorPickerDialogBuilder.with(this)
            .setTitle("Pick color")
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .density(12)
            .setOnColorSelectedListener {  }
            .setPositiveButton("Ok"
            ) { d, lastSelectedColor, allColors ->
                mModel.updatePlayerColor(data.bean!!, lastSelectedColor)
                adapter.notifyItemChanged(position)
            }
            .setNegativeButton("Cancel", null);
        if (data.bean!!.defColor != null) {
            builder.initialColor(data.bean!!.defColor!!)
        }
        builder.build().show()
    }
}

