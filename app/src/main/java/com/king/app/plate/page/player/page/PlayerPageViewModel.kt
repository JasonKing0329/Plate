package com.king.app.plate.page.player.page

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.king.app.plate.base.BaseViewModel
import com.king.app.plate.base.observer.NextErrorObserver
import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.db.entity.Player
import com.king.app.plate.model.repo.RankRepository
import com.king.app.plate.model.repo.RecordRepository
import com.king.app.plate.model.repo.ScoreRepository
import com.king.app.plate.view.widget.chart.adapter.LineData
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/23 10:34
 */
class PlayerPageViewModel(application: Application): BaseViewModel(application) {

    var periodType = AppConstants.PERIOD_CURRENT

    var playerName = ObservableField<String>()
    var playerRank = ObservableField<String>()
    var playerScore = ObservableField<String>()
    var highestRank = ObservableField<String>()
    var lowestRank = ObservableField<String>()
    var top8Weeks = ObservableField<String>()
    var bestResult = ObservableField<String>()
    var recordText = ObservableField<String>()
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
    var scoreBody = ObservableField<String>()

    var rankChartData = MutableLiveData<RankChartData>()
    var playerObserver = MutableLiveData<Player>()

    private var rankRepository = RankRepository()
    private var scoreRepository = ScoreRepository()
    private var recordRepository = RecordRepository()

    lateinit var player: Player

    fun loadPlayer(playerId: Long) {
        player = getDatabase().getPlayerDao().getPlayerById(playerId)
        playerObserver.value = player

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
        loadScorePart()
        loadRankPart()
        // match result
        loadResultPart(0)
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

    private fun loadResultPart(specificPeriod: Int) {
        var win = 0
        var lose = 0
        var allRecords = recordRepository.getPlayerRecords(player.id, periodType, specificPeriod)
        for (record in allRecords) {
            if (record.winnerId == player.id) {
                win ++
            }
            // don't count record not completed
            else if (record.winnerId != null) {
                lose ++
            }
        }
        recordText.set("Win $win, Lose $lose")

        // count result
        var records = recordRepository.getPlayerResultRecords(player.id, periodType, specificPeriod)
        val step = IntArray(AppConstants.round + 1)
        for (record in records) {
            if (record.round < AppConstants.ROUND_F) {
                // 总决赛小组赛不计入
                if (record.round == AppConstants.ROUND_ROBIN) {
                    var match = getDatabase().getMatchDao().getMatchById(record.matchId)
                    if (match.level == AppConstants.matchLevelFinal) {
                        continue
                    }
                }
                step[record.round] ++
            }
            else {
                if (record.winnerId == player.id) step[AppConstants.round] ++ else step[AppConstants.round - 1] ++
            }
        }

        var bestText: String? = null
        for (i in step.size - 1 downTo 0) {
            var result = when(i) {
                AppConstants.round -> setRoundText(championTimes, championTimesVisibility, AppConstants.RESULT_NORMAL[AppConstants.round], step[i])
                AppConstants.round - 1 -> setRoundText(ruTimes, ruTimesVisibility, AppConstants.RESULT_NORMAL[AppConstants.round - 1], step[i])
                AppConstants.round - 2 -> setRoundText(sfTimes, sfTimesVisibility, AppConstants.RESULT_NORMAL[AppConstants.round - 2], step[i])
                AppConstants.round - 3 -> setRoundText(qfTimes, qfTimesVisibility, AppConstants.RESULT_NORMAL[AppConstants.round - 3], step[i])
                AppConstants.round - 4 -> setRoundText(r16Times, r16TimesVisibility, AppConstants.RESULT_NORMAL[AppConstants.round - 4], step[i])
                AppConstants.round - 5 -> setRoundText(r32Times, r32TimesVisibility, AppConstants.RESULT_NORMAL[AppConstants.round - 5], step[i])
                else -> ""
            }
            if (result.isNotEmpty() && bestText == null) {
                bestText = result
                bestResult.set(bestText)
            }
        }
    }

    private fun loadScorePart() {
        var score = scoreRepository.sumPlayerScore(player.id)
        playerScore.set(score.toString())
        var scores = getDatabase().getScoreDao().getScoreList(player.id)
        var buffer = StringBuffer()
        var lastScore = 0
        for (s in scores) {
            if (s.score != lastScore) {
                buffer.append("\n")
            }
            var match = getDatabase().getMatchDao().getMatchById(s.matchId)
            buffer.append(match.name).append("(").append(s.score).append(")  ")
            lastScore = s.score!!
        }
        var text = buffer.toString()
        if (text.length > 1) {
            text = text.substring(1)
        }
        scoreBody.set(text)
    }

    private fun loadRankPart() {
        var curRank = rankRepository.getPlayerCurrentRank(player.id)
        playerRank.set(curRank.toString())
        var high = rankRepository.getPlayerHighRank(player.id)
        var highWeek = rankRepository.getRankWeek(player.id, high)
        // pick the first time
        var matchId = getDatabase().getRankDao().getRankFirstMatch(high, player.id)
        var highDate = getDatabase().getMatchDao().getMatchById(matchId).date
        var weekTxt = if (highWeek > 1) "Weeks" else "Week"
        highestRank.set("$high ($highDate), $highWeek $weekTxt")
        var low = rankRepository.getPlayerLowRank(player.id)
        var lowWeek = rankRepository.getRankWeek(player.id, low)
        weekTxt = if (lowWeek > 1) "Weeks" else "Week"
        // pick the first time
        matchId = getDatabase().getRankDao().getRankFirstMatch(low, player.id)
        var lowDate = getDatabase().getMatchDao().getMatchById(matchId).date
        lowestRank.set("$low ($lowDate), $lowWeek $weekTxt")
        var top8week = rankRepository.getTop8RankWeek(player.id)
        weekTxt = if (top8week > 1) "Weeks" else "Week"
        top8Weeks.set("$top8week $weekTxt")

        var ranks = getDatabase().getRankDao().getPlayerRanks(player.id)
        var max = getDatabase().getPlayerDao().getCount() + 1// 避免x轴上连出线
        var lineData = LineData()
        lineData.startX = 0
        lineData.endX = ranks.size - 1
        lineData.values = mutableListOf()
        lineData.valuesText = mutableListOf()
        lineData.colors = mutableListOf()
        var lastRank = 0

        // 颜色采用按周期，3色交替的方案
        var colors = listOf(Color.parseColor("#3399ff"), Color.parseColor("#C93437"), Color.parseColor("#BDB626"))
        var lastPeriod = -1
        var colorPack = ColorPack(0, -1)
        for (i in ranks.indices) {
            // 线条颜色
            var match = getDatabase().getMatchDao().getMatchById(ranks[i].matchId)
            if (match.period != lastPeriod) {
                lastPeriod = match.period!!
                nextColor(colors, colorPack)
            }
            lineData.colors.add(colorPack.color)

            // 取反，排名高的权值大
            lineData.values.add(max - ranks[i].rank)
            if (ranks[i].rank != lastRank) {
                lineData.valuesText.add(ranks[i].rank.toString())
            }
            else{
                lineData.valuesText.add("")
            }
            lastRank = ranks[i].rank
        }
        var xText = arrayOfNulls<String>(ranks.size)
        for (i in xText.indices) {
            var step: Int = when {// x轴刻度文字，每隔多少个显示（下面的数值都是经调试在360dp的手机上比较合适的值）
                ranks.size < 16 -> 2
                ranks.size < 22 -> 3
                ranks.size < 32 -> 4
                ranks.size < 37 -> 5
                else -> 10
            }
            if (i % step == 0) {
                xText[i] = getDatabase().getMatchDao().getMatchById(ranks[i].matchId).date
            }
            else {
                xText[i] = ""
            }
        }
        rankChartData.postValue(RankChartData(ranks, max, lineData, xText))
    }

    data class ColorPack (
        var color: Int,
        var index: Int
    )

    private fun nextColor(colors: List<Int>, pack: ColorPack) {
        pack.index ++
        if (pack.index >= colors.size) {
            pack.index = 0
        }
        pack.color = colors[pack.index]
    }

    fun getLastMatch(): Match {
        return getDatabase().getMatchDao().getLastMatch()
    }

    fun onPeriodChanged(specificPeriod: Int) {
        loadResultPart(specificPeriod)
    }
}