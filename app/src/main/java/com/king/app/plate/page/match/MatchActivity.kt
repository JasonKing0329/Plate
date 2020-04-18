package com.king.app.plate.page.match

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.ActivityMatchBinding
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.view.dialog.PopupDialog

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/17 11:02
 */
class MatchActivity: BaseActivity<ActivityMatchBinding, MatchViewModel>() {

    private var adapter:MatchItemAdapter = MatchItemAdapter()

    override fun getContentView(): Int = R.layout.activity_match

    override fun createViewModel(): MatchViewModel = generateViewModel(MatchViewModel::class.java)

    override fun initView() {
        mBinding.actionbar.setOnBackListener { onBackPressed() }
        mBinding.actionbar.setOnMenuItemListener {
            when (it) {
                R.id.menu_add -> editMatch(null)
            }
        }

        mBinding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun editMatch(match: Match?) {
        var content = MatchEditor()
        content.match = match
        content.onMatchListener = object : MatchEditor.OnMatchListener {
            override fun onMatchUpdated(match: Match) {
                mModel.insertOrUpdate(match)
            }
        }
        var dialog = PopupDialog()
        dialog.content = content
        dialog.title = if (match == null) "Add" else match.name
        dialog.show(supportFragmentManager, "MatchEditor")
    }

    override fun initData() {
        mModel.matchesObserver.observe(this, Observer { showMatches(it) })
        mModel.loadMatches()
    }

    private fun showMatches(list: List<Match>?) {
        adapter.list = list
        if (mBinding.rvList.adapter == null) {
            adapter.onActionListener = object : MatchItemAdapter.OnActionListener {
                override fun onDeleteItem(position: Int, bean: Match) {
                    showConfirmCancelMessage("Are you sure to delete this match?"
                        , DialogInterface.OnClickListener { dialog, which ->  mModel.deleteMatch(bean)}
                        , null)
                }

                override fun onEditItem(position: Int, bean: Match) {
                    editMatch(bean)
                }
            }
            adapter.setOnItemClickListener(object : BaseBindingAdapter.OnItemClickListener<Match> {
                override fun onClickItem(view: View, position: Int, data: Match) {
                    var bundle = Bundle()
                    bundle.putLong(DrawsActivity.EXTRA_MATCH_ID, data.id)
                    startPage(DrawsActivity::class.java, bundle)
                }
            })
            mBinding.rvList.adapter = adapter
        }
        else {
            adapter.notifyDataSetChanged()
        }
    }
}