package com.king.app.plate.model.repo

import com.king.app.plate.model.bean.RankPlayer
import com.king.app.plate.model.db.entity.Rank
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/20 14:23
 */
class RankRepository: BaseRepository() {

    fun createRank(matchId: Long): Observable<Boolean> = Observable.create {
        var players = getDatabase().getPlayerDao().getPlayers()
        var list = mutableListOf<RankPlayer>()
        for (player in players) {
            var score = getDatabase().getScoreDao().sumScore(player.id)
            var rp = RankPlayer(player, 0, score)
            list.add(rp)
        }
        list.sortWith(ScoreComparator())

        var rankList = mutableListOf<Rank>()
        var lastRank = 1;
        var lastScore = 0
        for (rp in list) {
            // 处理并列的情况
            var rank = lastRank
            if (rp.score != lastScore) {
                lastRank ++
            }
            var rankBean = Rank(0, matchId, rp.player!!.id, rank)
            rankList.add(rankBean)
        }

        var match = getDatabase().getMatchDao().getMatchById(matchId)

        getDatabase().getRankDao().deleteMatchRank(matchId)
        match.isRankCreated = false
        if (rankList.size > 0) {
            match.isRankCreated = true
            getDatabase().getRankDao().insertAll(rankList)
        }
        // update match info
        getDatabase().getMatchDao().update(match)

        it.onNext(true)
        it.onComplete()
    }

    fun getPlayerCurrentRank(playerId: Long): Int {
        var matchId = getDatabase().getMatchDao().getLastRankMatch()?.id
        return getPlayerRank(matchId, playerId)
    }

    private fun getPlayerRank(matchId: Long, playerId: Long): Int {
        var rank = getDatabase().getRankDao().getPlayerRank(playerId, matchId)
        return if (rank == null) {
            0
        } else{
            rank!!.rank
        }
    }

    fun getPlayerHighRank(playerId: Long): Int {
        return getDatabase().getRankDao().getPlayerHighRank(playerId)
    }

    fun getPlayerLowRank(playerId: Long): Int {
        return getDatabase().getRankDao().getPlayerLowRank(playerId)
    }

    class ScoreComparator: Comparator<RankPlayer> {
        override fun compare(o1: RankPlayer?, o2: RankPlayer?): Int {
            var result = o2?.score!! - o1?.score!!
            return when {
                result > 0 -> 1
                result < 0 -> -1
                else -> 0
            }
        }

    }
}