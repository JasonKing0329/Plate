package com.king.app.plate.page.glory

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.app.plate.base.BaseFragment
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.adapter.BaseBindingAdapter
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.databinding.FragmentGloryRoundBinding
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.repo.RecordRepository
import com.king.app.plate.page.player.page.PlayerPageActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/9/27 16:26
 */
class RoundFragment: BaseFragment<FragmentGloryRoundBinding, RoundViewModel>() {

    private var currentPeriod = 0
    private var maxPeriod = 0
    private var minPeriod = 1

    var adapter = RoundAdapter()

    override fun getBinding(inflater: LayoutInflater): FragmentGloryRoundBinding = FragmentGloryRoundBinding.inflate(inflater)

    override fun createViewModel(): RoundViewModel = generateViewModel(RoundViewModel::class.java)

    override fun initView(view: View) {
        mBinding.rvItem.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        mBinding.spResultType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                mModel.periodType = position
                if (position == AppConstants.PERIOD_SPECIFIC) {
                    var lastMatch = mModel.getLastMatch()// 取最近一站，不需要完赛
                    if (lastMatch != null) {
                        maxPeriod = lastMatch.period!!
                        currentPeriod = maxPeriod
                    }
                }
                updatePeriod()
                mModel.onPeriodChanged(currentPeriod)
            }
        }

        mBinding.ivLast.setOnClickListener {
            currentPeriod --
            updatePeriod()
            mModel.onPeriodChanged(currentPeriod)
        }
        mBinding.ivNext.setOnClickListener {
            currentPeriod ++
            updatePeriod()
            mModel.onPeriodChanged(currentPeriod)
        }

        updatePeriod()

        mBinding.tvChampion.setOnClickListener { adapter.sortByChampion() }
        mBinding.tvRunnerUp.setOnClickListener { adapter.sortByRunnerUp() }
        mBinding.tvSf.setOnClickListener { adapter.sortBySf() }
        mBinding.tvQf.setOnClickListener { adapter.sortByQf() }
        mBinding.tvR16.setOnClickListener { adapter.sortByR16() }
        mBinding.tvR32.setOnClickListener { adapter.sortByR32() }
    }

    override fun initData() {
        mModel.roundList.observe(this, Observer { showResults(it) })
        mModel.loadData();
    }

    private fun showResults(it: List<RoundItem>) {
        adapter.list = it
        if (mBinding.rvItem.adapter == null) {
            mBinding.rvItem.adapter = adapter
            adapter.setOnItemClickListener(object : BaseBindingAdapter.OnItemClickListener<RoundItem> {
                override fun onClickItem(view: View, position: Int, data: RoundItem) {
                    var bundle = Bundle()
                    bundle.putLong(PlayerPageActivity.EXTRA_PLAYER_ID, data.player.id)
                    startPage(PlayerPageActivity::class.java, bundle)
                }
            })
        }
        else {
            adapter.notifyDataSetChanged()
        }
    }

    private fun updatePeriod() {
        if (mModel.periodType == AppConstants.PERIOD_SPECIFIC) {
            mBinding.tvPeriod.text = "Period $currentPeriod"
            mBinding.tvPeriod.visibility = View.VISIBLE
            mBinding.ivLast.visibility = if (currentPeriod > minPeriod) View.VISIBLE else View.INVISIBLE
            mBinding.ivNext.visibility = if (currentPeriod < maxPeriod) View.VISIBLE else View.INVISIBLE
        }
        else {
            mBinding.tvPeriod.visibility = View.INVISIBLE
            mBinding.ivLast.visibility = View.INVISIBLE
            mBinding.ivNext.visibility = View.INVISIBLE
        }
    }

}

class RoundViewModel(application: Application): BaseViewModel(application) {

    var recordRepository = RecordRepository()

    var periodType = AppConstants.PERIOD_CURRENT

    var specificPeriod = 0

    var roundList = MutableLiveData<List<RoundItem>>()

    fun loadData() {
        viewModelScope.launch {
            var list = try {
                withContext(Dispatchers.IO) {
                    loadResults()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                listOf<RoundItem>()
            }
            roundList.value = list
        }
    }

    private suspend fun loadResults(): List<RoundItem> {
        var list = mutableListOf<RoundItem>()
        var players = getDatabase().getPlayerDao().getPlayers();
        for (player in players) {
            var item = RoundItem(player)
            list.add(item)
            var allRecords = recordRepository.getPlayerResultRecords(player.id, periodType, specificPeriod)
            for (record in allRecords) {
                when(record.round) {
                    AppConstants.ROUND_F -> {
                        if (record.winnerId == player.id) {
                            item.champion ++
                        }
                        else {
                            item.runnerUp ++
                        }
                    }
                    AppConstants.ROUND_SF -> item.sf ++
                    AppConstants.ROUND_QF -> item.qf ++
                    AppConstants.ROUND_SECOND -> item.r16 ++
                    AppConstants.ROUND_FIRST -> item.r32 ++
                }
            }
        }
        return list
    }

    fun getLastMatch(): Match {
        return getDatabase().getMatchDao().getLastMatch()
    }


    fun onPeriodChanged(specificPeriod: Int) {
        this.specificPeriod = specificPeriod
        loadData()
    }
}