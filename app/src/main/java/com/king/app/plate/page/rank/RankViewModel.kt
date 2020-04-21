package com.king.app.plate.page.rank

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.repo.RankRepository
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 9:17
 */
class RankViewModel(application: Application): BaseViewModel(application) {

    var rank2Visibility = ObservableInt(View.INVISIBLE)
    var lastArrowVisibility = ObservableInt(View.INVISIBLE)
    var nextArrowVisibility = ObservableInt(View.INVISIBLE)
    var rank1Title = ObservableField<String>()
    var rank2Title = ObservableField<String>()

    var rankList1 = MutableLiveData<List<RankItem>>()
    var rankList2 = MutableLiveData<List<RankItem>>()

    lateinit var matchList: MutableList<Match>

    var rankRepository = RankRepository()

    var matchIndex = 0

    fun loadData() {
        matchList = getDatabase().getMatchDao().getRankMatches()
        loadRanks()
    }

    fun lastRanks() {
        matchIndex -= 1;
        loadRanks()
    }

    fun nextRanks() {
        matchIndex += 1;
        loadRanks()
    }

    private fun loadRanks() {
        getRanks()
            .compose(applySchedulers())
            .subscribe(object: NextErrorObserver<Boolean>(getComposite()) {
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = "error: $e"
                }

                override fun onNext(t: Boolean) {

                }
            })
        nextArrowVisibility.set(if (matchIndex + 2 >= matchList.size) View.INVISIBLE else View.VISIBLE)
        lastArrowVisibility.set(if (matchIndex - 1 < 0) View.INVISIBLE else View.VISIBLE)
    }

    private fun getRanks(): Observable<Boolean> = Observable.create {
        rank1Title.set(matchList[matchIndex].date)
        rankList1.postValue(getRankList(matchIndex))
        if (matchIndex + 1 < matchList.size) {
            rank2Visibility.set(View.VISIBLE)
            rank2Title.set(matchList[matchIndex + 1].date)
            rankList2.postValue(getRankList(matchIndex + 1))
        }
        else{
            rank2Visibility.set(View.INVISIBLE)
            rank2Title.set("")
            rankList2.postValue(mutableListOf())
        }
        it.onNext(true)
        it.onComplete()
    }

    private fun getRankList(index: Int): MutableList<RankItem> {
        var list = mutableListOf<RankItem>()
        var thisMatch = matchList[index]
        var lastMatchId = if (index + 1 < matchList.size) matchList[index + 1].id else 0.toLong()
        var ranks = getDatabase().getRankDao().getMatchRanks(thisMatch.id)
        for (rank in ranks) {
            var player = getDatabase().getPlayerDao().getPlayerById(rank.playerId)
            var score = getDatabase().getScoreDao().sumScoreUntilMatch(thisMatch.period!!, thisMatch.orderInPeriod!!, rank.playerId)
            var change = 0
            if (lastMatchId != 0.toLong()) {
                var lastRank = getDatabase().getRankDao().getPlayerRank(rank.playerId, lastMatchId)
                if (lastRank != null) {
                    change = lastRank.rank - rank.rank
                }
            }
            var item = RankItem(rank, change, player, score)
            list.add(item)
        }
        return list
    }
}