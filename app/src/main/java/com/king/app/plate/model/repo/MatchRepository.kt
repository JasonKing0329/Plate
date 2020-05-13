package com.king.app.plate.model.repo

import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.bean.RankPlayer
import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.db.entity.RecordPlayer
import java.lang.Exception

/**
 * @description:
 * @author：Jing
 * @date: 2020/4/18 19:50
 */
class MatchRepository: BaseRepository() {

    fun getMatches(): MutableList<Match> {
        return getDatabase().getMatchDao().getMatches()
    }

    fun deleteMatch(bean: Match) {
        deleteMatchDetails(bean.id)
        getDatabase().getMatchDao().delete(bean)
    }

    fun deleteMatchDetails(matchId: Long) {
        var records = getDatabase().getRecordDao().getRecordsByMatch(matchId)
        for (record in records) {
            getDatabase().getRecordPlayerDao().deletePlayersByRecord(record.id)
            getDatabase().getRecordScoreDao().deleteByRecord(record.id)
        }
        getDatabase().getRecordDao().deleteByMatch(matchId)
        getDatabase().getScoreDao().deleteMatchScore(matchId)
        getDatabase().getRankDao().deleteMatchRank(matchId)
    }

    fun getWinner(match: Match): RankPlayer? {
        var record = getDatabase().getRecordDao().getFinalRecord(match.id, AppConstants.ROUND_F)
        return try {
            var player = getDatabase().getPlayerDao().getPlayerById(record.winnerId!!)
            // recordPlayer的playerRank字段没有做存储，通过matchId上一个match对应的rank来确定
//            var recordPlayer = getDatabase().getRecordPlayerDao().getRecordPlayer(record.id, record.winnerId!!)
            var period = match.period!!
            var orderInPeriod = match.orderInPeriod!! - 1
            if (orderInPeriod < 1) {
                period --
                orderInPeriod = getDatabase().getMatchDao().getMaxOrderInPeriod(period)
            }
            var rank = try {
                if (period < 1) 0
                else {
                    var previousMatch = getDatabase().getMatchDao().getMatchByOrder(period, orderInPeriod)
                    getDatabase().getRankDao().getPlayerRank(player.id, previousMatch.id).rank
                }
            } catch (ee: Exception) {0}
            RankPlayer(player, rank, 0)
        } catch (e: Exception) {
            null
        }
    }
}