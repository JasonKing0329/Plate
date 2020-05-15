package com.king.app.plate.page.player.page

import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import com.king.app.plate.base.EmptyViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.databinding.FragmentScoreStructBinding
import com.king.app.plate.model.db.AppDatabase
import com.king.app.plate.model.repo.ScoreRepository
import com.king.app.plate.view.dialog.PopupContent
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableSource

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/14 14:21
 */
class ScoreStructDialog: PopupContent<FragmentScoreStructBinding, EmptyViewModel>() {

    var playerId: Long = 0

    var adapter = ScoreAdapter()

    var repository = ScoreRepository()

    private val SORT_DATE = 0
    private val SORT_SCORE = 1

    private val TYPE_RANK = 0
    private val TYPE_PERIOD = 1

    private var sortType = SORT_DATE
    private var listType = TYPE_RANK

    private var currentPeriod = 0
    private var maxPeriod = 0
    private var minPeriod = 1

    var sumScoreText = ""

    override fun getBinding(inflater: LayoutInflater): FragmentScoreStructBinding = FragmentScoreStructBinding.inflate(inflater)

    override fun createViewModel(): EmptyViewModel = generateViewModel(EmptyViewModel::class.java)

    override fun initView(view: View) {
        mBinding.rvList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                listType = position
                mBinding.llPeriod.visibility = if (listType == TYPE_PERIOD) View.VISIBLE else View.GONE
                getScores()
            }
        }
        mBinding.spSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sortType = position
                getScores()
            }
        }
        mBinding.ivLast.setOnClickListener {
            currentPeriod --
            updatePeriod()
            getScores()
        }
        mBinding.ivNext.setOnClickListener {
            currentPeriod ++
            updatePeriod()
            getScores()
        }
    }

    override fun initData() {
        var lastMatch = AppDatabase.instance.getMatchDao().getLastMatch()// 取最近一站，不需要完赛
        if (lastMatch != null) {
            maxPeriod = lastMatch.period!!
            currentPeriod = maxPeriod
            updatePeriod()
        }
        getScores()
    }

    private fun updatePeriod() {
        mBinding.tvPeriod.text = "Period $currentPeriod"
        mBinding.ivLast.visibility = if (currentPeriod > minPeriod) View.VISIBLE else View.INVISIBLE
        mBinding.ivNext.visibility = if (currentPeriod < maxPeriod) View.VISIBLE else View.INVISIBLE
    }

    private fun getScores() {
        getList()
            .flatMap { list -> sortScores(list) }
            .compose(mModel.applySchedulers())
            .subscribe(object : NextErrorObserver<MutableList<ScoreItem>>(compositeDisposable) {
                override fun onNext(t: MutableList<ScoreItem>?) {
                    showList(t!!)
                    mBinding.tvScoreSum.text = sumScoreText
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    showMessageShort("error: $e")
                }

            })
    }

    private fun getList(): Observable<MutableList<ScoreItem>> {
        return if (listType == TYPE_PERIOD) {
            repository.getPeriodScoreList(playerId, currentPeriod)
        } else {
            repository.getRankScoreList(playerId)
        }
    }

    private fun showList(t: MutableList<ScoreItem>) {
        adapter.list = t
        if (mBinding.rvList.adapter == null) {
            mBinding.rvList.adapter = adapter
        }
        else {
            adapter.notifyDataSetChanged()
        }
    }

    private fun sortScores(list: MutableList<ScoreItem>): ObservableSource<MutableList<ScoreItem>> = ObservableSource {
        when(sortType) {
            SORT_DATE -> list.sortByDescending { it.match.order }
            SORT_SCORE -> list.sortByDescending { it.bean.score }
        }
        var sum = 0
        for (item in list) {
            sum += item.bean.score!!
        }
        sumScoreText = "Total: $sum"

        it.onNext(list)
        it.onComplete()
    }
}