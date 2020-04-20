package com.king.app.plate.page.match

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.king.app.plate.base.EmptyViewModel
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.databinding.FragmentMatchEditorBinding
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.view.dialog.PopupContent
import com.king.app.plate.view.share.DateManager
import java.text.SimpleDateFormat
import java.util.*

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/17 11:17
 */
class MatchEditor: PopupContent<FragmentMatchEditorBinding, EmptyViewModel>() {

    var match: Match? = null

    var onMatchListener: OnMatchListener? = null

    var date: String? = null

    var dateManager = DateManager()

    override fun getBinding(inflater: LayoutInflater): FragmentMatchEditorBinding = FragmentMatchEditorBinding.inflate(inflater)

    override fun createViewModel(): EmptyViewModel = generateViewModel(EmptyViewModel::class.java)

    override fun initView(view: View) {
        mBinding.tvOk.setOnClickListener { onConfirm() }
        mBinding.btnDate.setOnClickListener {
            dateManager.date = dateManager.dateFormat.parse(date)
            dateManager.pickDate(context, object : DateManager.OnDateListener {
                override fun onDateSet() {
                    date = dateManager.dateStr
                    mBinding.btnDate.text = date
                    mBinding.etName.setText(date)
                }
            })
        }
    }

    override fun initData() {
        if (match == null) {
            date = dateManager.dateFormat.format(Date())
            match = Match(0, 0, 0, 0, date, date, 0, AppConstants.draws, AppConstants.bye, 0,
                isRankCreated = false,
                isScoreCreated = false
            )
        }
        date = match!!.date
        mBinding.etName.setText(match!!.name)
        mBinding.btnDate.text = match!!.date
        mBinding.etPeriod.setText(match!!.period.toString())
        mBinding.etOrder.setText(match!!.orderInPeriod.toString())
        mBinding.tvTypeDetail.text = "Draws ${match!!.draws}, Set ${AppConstants.set}"
        mBinding.spType.setSelection(match!!.level)
    }

    private fun onConfirm() {
        var name = mBinding.etName.text.trim()
        if (TextUtils.isEmpty(name)) {
            showMessageShort("Name can't be empty")
            return
        }
        var period: Int
        try {
            period = mBinding.etPeriod.text.toString().toInt()
        } catch (e: Exception){
            showMessageShort("Error period")
            return
        }
        var order: Int
        try {
            order = mBinding.etOrder.text.toString().toInt()
        } catch (e: Exception){
            showMessageShort("Error order")
            return
        }
        match!!.name = name.toString();
        match!!.period = period
        match!!.orderInPeriod = order
        match!!.date = date
        match!!.level = mBinding.spType.selectedItemPosition

        onMatchListener?.onMatchUpdated(match!!)
        dismissAllowingStateLoss()
    }

    interface OnMatchListener {
        fun onMatchUpdated(match: Match)
    }
}