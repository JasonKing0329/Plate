package com.king.app.plate.page.home

import android.content.DialogInterface
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.app.plate.R
import com.king.app.plate.base.EmptyViewModel
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.conf.AppConfig
import com.king.app.plate.databinding.AdapterItemLoadfromBinding
import com.king.app.plate.databinding.FragmentContentLoadfromBinding
import com.king.app.plate.utils.FileUtil
import com.king.app.plate.view.dialog.AlertDialogFragment
import com.king.app.plate.view.dialog.PopupContent
import java.io.File

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/20 9:35
 */
class LoadFromDialog: PopupContent<FragmentContentLoadfromBinding, EmptyViewModel>() {

    private lateinit var list: List<File>

    private var adapter = ItemAdapter()

    var onDatabaseChangedListener: OnDatabaseChangedListener? = null

    override fun getBinding(inflater: LayoutInflater): FragmentContentLoadfromBinding = FragmentContentLoadfromBinding.inflate(inflater)

    override fun createViewModel(): EmptyViewModel = generateViewModel(EmptyViewModel::class.java)

    override fun initView(view: View) {
        mBinding.rvList.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        mBinding.tvConfirm.setOnClickListener { onSave() }
    }

    override fun initData() {
        var file = File(AppConfig.historyPath)
        list = listOf(*file.listFiles())

        adapter.list = list
        mBinding.rvList.adapter = adapter
    }

    private fun onSave() {
        if (adapter.selection != -1) {
            val file = list[adapter.selection]
            AlertDialogFragment()
                .setMessage(getString(R.string.load_from_warning_msg))
                .setPositiveText(getString(R.string.ok))
                .setPositiveListener(DialogInterface.OnClickListener{ dialogInterface, i ->
                    FileUtil.replaceDatabase(file)
                    if (onDatabaseChangedListener != null) {
                        onDatabaseChangedListener!!.onDatabaseChanged()
                    }
                    dismissAllowingStateLoss()
                })
                .setNegativeText(getString(R.string.cancel))
                .show(childFragmentManager, "AlertDialogFragment")
        }
    }

    inner class ItemAdapter: BaseBindingAdapter<AdapterItemLoadfromBinding, File>() {

        var selection = -1

        override fun onCreateBind(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): AdapterItemLoadfromBinding = AdapterItemLoadfromBinding.inflate(inflater, parent, false)

        override fun onBindItem(binding: AdapterItemLoadfromBinding?, position: Int, bean: File) {
            binding!!.tvName.text = bean.name
            if (position == selection) {
                binding.groupItem.setBackgroundColor(resources.getColor(R.color.yellowF7D23E))
            } else {
                binding.groupItem.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        override fun onClickItem(v: View, position: Int) {
            var lastPosition = selection
            selection = position
            if (lastPosition != -1) {
                notifyItemChanged(lastPosition)
            }
            notifyItemChanged(selection)
        }
    }

    interface OnDatabaseChangedListener {
        fun onDatabaseChanged()
    }
}