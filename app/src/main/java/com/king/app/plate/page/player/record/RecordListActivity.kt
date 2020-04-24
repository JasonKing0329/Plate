package com.king.app.plate.page.player.record

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.databinding.ActivityRecordListBinding
import com.king.app.plate.page.h2h.H2hActivity
import com.king.app.plate.page.player.page.PlayerPageActivity

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/24 10:41
 */
class RecordListActivity: BaseActivity<ActivityRecordListBinding, RecordViewModel>() {

    companion object {
        const val EXTRA_PLAYER_ID = "player_id"
    }

    private lateinit var adapter: RecordAdapter

    override fun getContentView(): Int = R.layout.activity_record_list

    override fun createViewModel(): RecordViewModel = generateViewModel(RecordViewModel::class.java)

    override fun initView() {
        mBinding.model = mModel
        mBinding.actionbar.setOnBackListener { onBackPressed() }
        mBinding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        mBinding.actionbar.setOnMenuItemListener {
            when(it) {
                // 由于BaseExpandableAdapter对list在构造方法中做了数据重装，所以notify无效，只能重新加载
                R.id.menu_expand -> mModel.loadData(getPlayerId(), true)
                R.id.menu_collapse -> mModel.loadData(getPlayerId(), false)
            }
        }
    }

    override fun initData() {
        mModel.headList.observe(this, Observer { showRecords(it) })
        mModel.loadData(getPlayerId(), true)
    }

    private fun showRecords(it: MutableList<HeadItem>) {
        adapter = RecordAdapter(it)
        adapter.listener = object : OnRecordPlayerListener {
            override fun onClickItem(view: View, position: Int, childItem: ChildItem) {
                showH2h(childItem)
            }
        }
        mBinding.rvList.adapter = adapter
    }

    private fun showH2h(childItem: ChildItem) {
        var bundle = Bundle()
        bundle.putLong(H2hActivity.EXTRA_PLAYER1_ID, mModel.player.id)
        bundle.putLong(H2hActivity.EXTRA_PLAYER2_ID, childItem.player.id)
        startPage(H2hActivity::class.java, bundle)
    }

    private fun getPlayerId(): Long {
        return getIntentBundle()?.getLong(PlayerPageActivity.EXTRA_PLAYER_ID)!!
    }

}