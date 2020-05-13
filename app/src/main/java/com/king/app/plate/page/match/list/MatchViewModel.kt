package com.king.app.plate.page.match.list

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.repo.MatchRepository
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/17 11:03
 */
class MatchViewModel(application: Application): BaseViewModel(application) {

    var matchesObserver = MutableLiveData<MutableList<Any>>()
    var repository = MatchRepository()

    fun loadMatches() {
        var list = repository.getMatches()
        toClassifyMatches(list)
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<MutableList<Any>>(getComposite()){
                override fun onNext(t: MutableList<Any>?) {
                    matchesObserver.value = t
                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    messageObserver.value = "error: $e"
                }

            })
    }

    fun insertOrUpdate(match: Match) {
        if (match.id == 0.toLong()) {
            getDatabase().getMatchDao().insert(match)
        }
        else{
            getDatabase().getMatchDao().update(match)
        }
        loadMatches()
    }

    fun deleteMatch(bean: Match) {
        repository.deleteMatch(bean)
        loadMatches()
    }

    /**
     * @param list 已按period, orderInPeriod降序排列
     */
    private fun toClassifyMatches(list: MutableList<Match>): Observable<MutableList<Any>> = Observable.create {
        var result = mutableListOf<Any>()
        var lastPeriod: MatchPeriodTitle? = null
        for (match in list) {
            var title = lastPeriod
            if (title == null || match.period!! != title.period) {
                title =
                    MatchPeriodTitle(match.period!!)
                title.endDate = match.date!!
                result.add(title)

                lastPeriod = title
            }
            // 确保startDate肯定会最后会设置为该period的第一个
            title.startDate = match.date!!

            // load winner
            var winner = repository.getWinner(match)

            result.add(MatchItemBean(match, winner))
        }

        it.onNext(result)
        it.onComplete()
    }
}