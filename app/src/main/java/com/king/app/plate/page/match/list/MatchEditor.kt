package com.king.app.plate.page.match.list

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import com.king.app.plate.base.EmptyViewModel
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.databinding.FragmentMatchEditorBinding
import com.king.app.plate.model.db.AppDatabase
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.view.dialog.PopupContent
import com.king.app.plate.view.share.DateManager
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
        mBinding.spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == AppConstants.matchLevelFinal) {
                    mBinding.tvTypeDetail.text = "Draws 8, set 3"
                    match!!.draws = 8
                }
                else {
                    mBinding.tvTypeDetail.text = "Draws 32, set 3"
                    match!!.draws = 32
                }
            }

        }
    }

    override fun initData() {
        if (match == null) {
            // 新增match，自动生成period, order与orderInPeriod
            var period = 0
            var order = 0
            var orderInPeriod = 0
            var lastMatch = AppDatabase.instance.getMatchDao().getLastMatch()
            if (lastMatch != null) {
                order = lastMatch.order + 1
                if (lastMatch.orderInPeriod == AppConstants.PERIOD_TOTAL_MATCH_NUM) {
                    period = lastMatch.period!! + 1
                    orderInPeriod = 1
                }
                else {
                    period = lastMatch.period!!
                    orderInPeriod = lastMatch.orderInPeriod!! + 1
                }
            }
            date = dateManager.dateFormat.format(Date())
            match = Match(0, period, order, orderInPeriod, date, date, 0, AppConstants.draws, AppConstants.bye, 0,
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
        mBinding.tvOrderFull.text = "The ${match!!.order} match"
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
        match!!.order = (period - 1) * AppConstants.PERIOD_TOTAL_MATCH_NUM + order
        match!!.date = date
        match!!.level = mBinding.spType.selectedItemPosition

        onMatchListener?.onMatchUpdated(match!!)
        dismissAllowingStateLoss()
    }

    interface OnMatchListener {
        fun onMatchUpdated(match: Match)
    }
}