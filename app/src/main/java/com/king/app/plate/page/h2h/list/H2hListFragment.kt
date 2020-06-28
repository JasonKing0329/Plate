package com.king.app.plate.page.h2h.list

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.king.app.plate.base.BaseFragment
import com.king.app.plate.base.EmptyViewModel
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.databinding.FragmentH2hListBinding
import com.king.app.plate.page.h2h.H2hTableViewModel
import com.king.app.plate.utils.ScreenUtils

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/6/28 14:18
 */
class H2hListFragment: BaseFragment<FragmentH2hListBinding, EmptyViewModel>() {

    private var list: MutableList<H2hListItem>? = null
    private var adapter = H2hListAdapter()

    override fun getBinding(inflater: LayoutInflater): FragmentH2hListBinding = FragmentH2hListBinding.inflate(inflater)

    override fun createViewModel(): EmptyViewModel = generateViewModel(EmptyViewModel::class.java)

    override fun initView(view: View) {
        mBinding.rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.rvList.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
                outRect.top = ScreenUtils.dp2px(10f)
            }
        })
    }

    override fun initData() {
        if (list != null) {
            adapter.list = list
            adapter.setOnItemClickListener(object : BaseBindingAdapter.OnItemClickListener<H2hListItem> {
                override fun onClickItem(view: View, position: Int, data: H2hListItem) {
                    getActivityViewModel(H2hTableViewModel::class.java).showH2hDetail.value = data
                }
            })
            mBinding.rvList.adapter = adapter
        }
    }

    fun setList(it: MutableList<H2hListItem>) {
        list = it
    }
}