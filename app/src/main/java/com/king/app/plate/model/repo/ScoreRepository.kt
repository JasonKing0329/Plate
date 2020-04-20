package com.king.app.plate.model.repo

import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.model.db.entity.Score
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/20 11:38
 */
class ScoreRepository: BaseRepository() {

    private val SCORE_STEP = listOf(10, 45, 90, 180, 250, 500)

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
                        break;
                    }
                }
                // 决赛，胜负方都判定
                else{
                    scoreBean = defineScore(player, record.round, player.playerId == record.winnerId)
                }
            }
            if (scoreBean != null) {
                scoreBean.matchId = matchId
                scores.add(scoreBean)
            }
        }

        getDatabase().getScoreDao().deleteAll()
        if (scores.size > 0) {
            getDatabase().getScoreDao().insertAll(scores)
        }

        it.onNext(true)
        it.onComplete()
    }

    private fun defineScore(player: RecordPlayer, round: Int, isWinner: Boolean): Score {
        // 第二轮负，如果是种子（轮空），只有第一轮负的分。其他第二轮负的分
        var nScore = if (round == 1 && player.playerSeed!! > 0) {
            SCORE_STEP[0]
        }
        // 决赛，胜者
        else if (round == AppConstants.round - 1 && isWinner) {
            SCORE_STEP[SCORE_STEP.size - 1]
        }
        // 其他情况
        else{
            SCORE_STEP[round]
        }
        return Score(0, 0, player.playerId, nScore)
    }

    fun sumPlayerScore(playerId: Long): Int {
        return getDatabase().getScoreDao().sumScore(playerId)
    }
}