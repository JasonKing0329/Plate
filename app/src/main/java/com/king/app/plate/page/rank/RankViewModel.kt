package com.king.app.plate.page.rank

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.db.AppDatabase
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.db.entity.Rank
import com.king.app.plate.model.repo.RankRepository
import com.king.app.plate.model.repo.ScoreRepository
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
    var scoreRepository = ScoreRepository()

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

    fun resetRanks() {
        matchIndex = 0
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
            var minOrder = thisMatch.order - AppConstants.PERIOD_TOTAL_MATCH_NUM + 1
            // final之前最后一站算积分时不计入上个period的final积分
            if (thisMatch.order % AppConstants.PERIOD_TOTAL_MATCH_NUM == AppConstants.PERIOD_TOTAL_MATCH_NUM - 1) {
                minOrder ++;
            }
            var score = getDatabase().getScoreDao().sumRankScore(player.id, minOrder, thisMatch.order)
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

    fun loadRaceToFinalRanks() {
        rank1Title.set("Race to final")
        rank2Visibility.set(View.INVISIBLE)
        nextArrowVisibility.set(View.INVISIBLE)
        lastArrowVisibility.set(View.INVISIBLE)
        getRaceToFinalRanks()
            .compose(applySchedulers())
            .subscribe(object: NextErrorObserver<MutableList<RankItem>>(getComposite()) {
                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    messageObserver.value = "error: $e"
                }

                override fun onNext(t: MutableList<RankItem>) {
                    rankList1.value = t
                }
            })
    }

    private fun getRaceToFinalRanks(): Observable<MutableList<RankItem>> = Observable.create {
        var list = mutableListOf<RankItem>()
        var lastMatch = getDatabase().getMatchDao().getLastMatch()// 取最近一站，不需要完赛
        if (lastMatch != null) {
            var period = lastMatch.period!!
            var players = getDatabase().getPlayerDao().getPlayers();
            for (player in players) {
                var scoreList = scoreRepository.getPeriodScores(player.id, period)
                var sum = 0
                for (score in scoreList) {
                    sum += score.bean.score!!
                }
                var rank = Rank(0, lastMatch.id, player.id, 0)
                var item = RankItem(rank, 0, player, sum)
                list.add(item)
            }
            list.sortByDescending { it -> it.score }
            for (i in 0 until list.size) {
                list[i].bean.rank = i + 1
            }
        }
        it.onNext(list)
        it.onComplete()
    }

}