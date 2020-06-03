package com.king.app.plate.model.repo

import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.model.db.entity.Score
import com.king.app.plate.page.player.page.ScoreItem
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/20 11:38
 */
class ScoreRepository: BaseRepository() {

    private val SCORE_NORMAL_STEP = listOf(10, 45, 90, 180, 300, 500)

    // Final采用累积方法：RR每赢一场+100，输+40，SF胜出+200，F胜出+300
    private val SCORE_FINAL_STEP = listOf(40, 100, 200, 300)

    fun createMatchScores(matchId: Long): Observable<Boolean> = Observable.create{
        var scores = mutableListOf<Score>()
        var recordList = getDatabase().getRecordDao().getRecordsByMatch(matchId)
        for (record in recordList) {
            var players = getDatabase().getRecordPlayerDao().getPlayersByRecord(record.id)
            var scoreBean: Score? = null
            for (player in players) {
                // 决赛之前，只有输的一方可判定积分
                if (record.round < AppConstants.round - 1) {
                    if (player.playerId != record.winnerId) {
                        scoreBean = defineScore(player, record.round, false)
                        if (scoreBean != null) {
                            scoreBean.matchId = matchId
                            scores.add(scoreBean)
                        }
                        break;
                    }
                }
                // 决赛，胜负方都判定
                else{
                    scoreBean = defineScore(player, record.round, player.playerId == record.winnerId)
                    if (scoreBean != null) {
                        scoreBean.matchId = matchId
                        scores.add(scoreBean)
                    }
                }
            }
        }

        var match = getDatabase().getMatchDao().getMatchById(matchId)

        getDatabase().getScoreDao().deleteMatchScore(matchId)
        match.isScoreCreated = false
        if (scores.size > 0) {
            match.isScoreCreated = true
            getDatabase().getScoreDao().insertAll(scores)
        }

        // update match info
        getDatabase().getMatchDao().update(match)

        it.onNext(true)
        it.onComplete()
    }

    private fun defineScore(player: RecordPlayer, round: Int, isWinner: Boolean): Score {
        // 第二轮负，如果是种子（轮空），只有第一轮负的分。其他第二轮负的分
        var nScore = if (round == 1 && player.playerSeed!! > 0) {
            SCORE_NORMAL_STEP[0]
        }
        // 决赛，胜者
        else if (round == AppConstants.round - 1 && isWinner) {
            SCORE_NORMAL_STEP[SCORE_NORMAL_STEP.size - 1]
        }
        // 其他情况
        else{
            SCORE_NORMAL_STEP[round]
        }
        return Score(0, 0, player.playerId, nScore)
    }

    /**
     * Final采用累积方法：RR每赢一场+100，输+40，SF胜出+200，F胜出+300
     */
    fun createFinalScores(matchId: Long): Observable<Boolean> = Observable.create{
        var scores = mutableListOf<Score>()
        var recordList = getDatabase().getRecordDao().getRecordsByMatch(matchId)
        var scoreMap = mutableMapOf<Long, Score>()
        for (record in recordList) {
            var players = getDatabase().getRecordPlayerDao().getPlayersByRecord(record.id)
            for (player in players) {
                var scoreBean = scoreMap[player.playerId]
                if (scoreBean == null) {
                    scoreBean = Score(0, matchId, player.playerId, 0)
                    scoreMap[player.playerId] = scoreBean
                    scores.add(scoreBean)
                }
                var score = defineFinalScore(record.round, record.winnerId == player.playerId)
                scoreBean.score = scoreBean.score!! + score
            }
        }

        var match = getDatabase().getMatchDao().getMatchById(matchId)

        getDatabase().getScoreDao().deleteMatchScore(matchId)
        match.isScoreCreated = false
        if (scores.size > 0) {
            match.isScoreCreated = true
            getDatabase().getScoreDao().insertAll(scores)
        }

        // update match info
        getDatabase().getMatchDao().update(match)

        it.onNext(true)
        it.onComplete()
    }

    private fun defineFinalScore(round: Int, isWinner: Boolean): Int {
        return when(round) {
            AppConstants.ROUND_ROBIN -> {
                if (isWinner) SCORE_FINAL_STEP[1]
                else SCORE_FINAL_STEP[0]
            }
            AppConstants.ROUND_SF -> {
                if (isWinner) SCORE_FINAL_STEP[2]
                else 0
            }
            AppConstants.ROUND_F -> {
                if (isWinner) SCORE_FINAL_STEP[3]
                else 0
            }
            else -> 0
        }
    }

    fun sumPlayerScore(playerId: Long): Int {
        var lastMatch = getDatabase().getMatchDao().getLastRankMatch()
        var orderMin = lastMatch.order - AppConstants.PERIOD_TOTAL_MATCH_NUM + 1
        return getDatabase().getScoreDao().sumRankScore(playerId, orderMin, lastMatch.order)
    }

    /**
     * 从最近一站创建过积分的赛事开始往前PERIOD_TOTAL站内有效积分
     */
    fun getRankScoreList(playerId: Long): Observable<MutableList<ScoreItem>> = Observable.create {
        var list = mutableListOf<ScoreItem>()
        var lastMatch = getDatabase().getMatchDao().getLastRankMatch()
        var orderMin = lastMatch.order - AppConstants.PERIOD_TOTAL_MATCH_NUM + 1
        var scores = getDatabase().getScoreDao().getRankScoreList(playerId, orderMin, lastMatch.order)
        for (score in scores) {
            var match = getDatabase().getMatchDao().getMatchById(score.matchId)
            list.add(ScoreItem(score, match))
        }
        it.onNext(list)
        it.onComplete()
    }

    /**
     * Period内赛事积分
     */
    fun getPeriodScoreList(playerId: Long, period: Int): Observable<MutableList<ScoreItem>> = Observable.create {
        var list = mutableListOf<ScoreItem>()
        var scores = getDatabase().getScoreDao().getPeriodScoreList(playerId, period)
        for (score in scores) {
            var match = getDatabase().getMatchDao().getMatchById(score.matchId)
            list.add(ScoreItem(score, match))
        }
        it.onNext(list)
        it.onComplete()
    }
}