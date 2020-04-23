package com.king.app.plate.page.player.page

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.model.repo.RankRepository
import com.king.app.plate.model.repo.RecordRepository
import com.king.app.plate.model.repo.ScoreRepository
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/23 10:34
 */
class PlayerPageViewModel(application: Application): BaseViewModel(application) {

    var playerName = ObservableField<String>()
    var playerRank = ObservableField<String>()
    var playerScore = ObservableField<String>()
    var highestRank = ObservableField<String>()
    var lowestRank = ObservableField<String>()
    var bestResult = ObservableField<String>()
    var championTimesVisibility = ObservableInt()
    var championTimes = ObservableField<String>()
    var ruTimesVisibility = ObservableInt()
    var ruTimes = ObservableField<String>()
    var sfTimesVisibility = ObservableInt()
    var sfTimes = ObservableField<String>()
    var qfTimesVisibility = ObservableInt()
    var qfTimes = ObservableField<String>()
    var r16TimesVisibility = ObservableInt()
    var r16Times = ObservableField<String>()
    var r32TimesVisibility = ObservableInt()
    var r32Times = ObservableField<String>()
    var h2hPlayers = ObservableField<String>()

    private var rankRepository = RankRepository()
    private var scoreRepository = ScoreRepository()
    private var recordRepository = RecordRepository()

    lateinit var player: Player

    fun loadPlayer(playerId: Long) {
        player = getDatabase().getPlayerDao().getPlayerById(playerId)

        playerName.set(player.name)
        loadDetails()
            .compose(applySchedulers())
            .subscribe(object : NextErrorObserver<Boolean>(getComposite()) {
                override fun onNext(t: Boolean?) {

                }

                override fun onError(e: Throwable?) {
                    e?.printStackTrace()
                    messageObserver.value = "error: $e"
                }

            })
    }

    private fun loadDetails(): Observable<Boolean> = Observable.create {
        // rank and score
        loadRankScorePart()
        // match result
        loadResultPart()
        // h2h
        loadH2hPart()

        it.onNext(true)
        it.onComplete()
    }

    private fun loadH2hPart() {
        var playerCount = getDatabase().getRecordDao().getPlayerCountByH2h(player.id)
        var recordCount = getDatabase().getRecordDao().getPlayerRecordCount(player.id)
        h2hPlayers.set("$playerCount players, $recordCount records")
    }

    private fun setRoundText(text: ObservableField<String>, visibility: ObservableInt, round: String, times: Int): String {
        var result: String
        if (times > 0) {
            visibility.set(View.VISIBLE)
            result = "$round ($times times)"
            text.set(result)
        }
        else{
            visibility.set(View.GONE)
            result = ""
        }
        return result
    }

    private fun loadResultPart() {
        var records = recordRepository.getPlayerResultRecords(player.id)

        val step = IntArray(AppConstants.round + 1)
        for (record in records) {
            if (record.round < AppConstants.round - 1) {
                step[record.round] ++
            }
            else {
                if (record.winnerId == player.id) step[AppConstants.round] ++ else step[AppConstants.round - 1] ++
            }
        }
        var bestText: String? = null
        for (i in step.size - 1 downTo 0) {
            var result = when(i) {
                AppConstants.round -> setRoundText(championTimes, championTimesVisibility, "Champion", step[i])
                AppConstants.round - 1 -> setRoundText(ruTimes, ruTimesVisibility, "Runner-up", step[i])
                AppConstants.round - 2 -> setRoundText(sfTimes, sfTimesVisibility, "SF", step[i])
                AppConstants.round - 3 -> setRoundText(qfTimes, qfTimesVisibility, "QF", step[i])
                AppConstants.round - 4 -> setRoundText(r16Times, r16TimesVisibility, "R16", step[i])
                AppConstants.round - 5 -> setRoundText(r32Times, r32TimesVisibility, "R32", step[i])
                else -> ""
            }
            if (result.isNotEmpty() && bestText == null) {
                bestText = result
                bestResult.set(bestText)
            }
        }
    }

    private fun loadRankScorePart() {
        var curRank = rankRepository.getPlayerCurrentRank(player.id)
        playerRank.set(curRank.toString())
        var score = scoreRepository.sumPlayerScore(player.id)
        playerScore.set(score.toString())
        var high = rankRepository.getPlayerHighRank(player.id)
        // pick the first time
        var matchId = getDatabase().getRankDao().getRankFirstMatch(high, player.id)
        var highDate = getDatabase().getMatchDao().getMatchById(matchId).date
        highestRank.set("$high ($highDate)")
        var low = rankRepository.getPlayerLowRank(player.id)
        // pick the first time
        matchId = getDatabase().getRankDao().getRankFirstMatch(low, player.id)
        var lowDate = getDatabase().getMatchDao().getMatchById(matchId).date
        lowestRank.set("$low ($lowDate)")
    }
}