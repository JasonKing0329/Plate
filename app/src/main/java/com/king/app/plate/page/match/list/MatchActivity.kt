package com.king.app.plate.page.match.list

import android.content.DialogInterface
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.king.app.plate.R
import com.king.app.plate.base.BaseActivity
import com.king.app.plate.base.adapter.HeadChildBindingAdapter
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.databinding.ActivityMatchBinding
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.page.match.DrawsActivity
import com.king.app.plate.page.match.FinalDrawActivity
import com.king.app.plate.utils.ScreenUtils
import com.king.app.plate.view.dialog.PopupDialog

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/17 11:02
 */
class MatchActivity: BaseActivity<ActivityMatchBinding, MatchViewModel>() {

    private var adapter = MatchAdapter()

    override fun getContentView(): Int = R.layout.activity_match

    override fun createViewModel(): MatchViewModel = generateViewModel(
        MatchViewModel::class.java)

    override fun initView() {
        mBinding.actionbar.setOnBackListener { onBackPressed() }
        mBinding.actionbar.setOnMenuItemListener {
            when (it) {
                R.id.menu_add -> editMatch(null)
            }
        }

        mBinding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mBinding.rvList.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                outRect.top = ScreenUtils.dp2px(8f)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (mModel != null) {
            mModel.loadMatches()
        }
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

    private fun showMatches(list: MutableList<Any>?) {
        adapter.list = list
        if (mBinding.rvList.adapter == null) {
            adapter.onActionListener = object : MatchAdapter.OnActionListener {
                override fun onDeleteItem(position: Int, bean: Match) {
                    showConfirmCancelMessage("Are you sure to delete this match?"
                        , DialogInterface.OnClickListener { dialog, which ->  mModel.deleteMatch(bean)}
                        , null)
                }

                override fun onEditItem(position: Int, bean: Match) {
                    editMatch(bean)
                }
            }
            adapter.setOnItemClickListener(object : HeadChildBindingAdapter.OnItemClickListener<MatchItemBean> {
                override fun onClickItem(view: View, position: Int, match: MatchItemBean) {
                    var bundle = Bundle()
                    if (match.match.level == AppConstants.matchLevelFinal) {
                        bundle.putLong(FinalDrawActivity.EXTRA_MATCH_ID, match.match.id)
                        startPage(FinalDrawActivity::class.java, bundle)
                    }
                    else {
                        bundle.putLong(DrawsActivity.EXTRA_MATCH_ID, match.match.id)
                        startPage(DrawsActivity::class.java, bundle)
                    }
                }
            })
            mBinding.rvList.adapter = adapter
        }
        else {
            adapter.notifyDataSetChanged()
        }
    }
}