package com.king.app.plate.model.repo

import com.king.app.plate.model.bean.RankPlayer
import io.reactivex.rxjava3.core.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/14 15:56
 */
class PlayerRepository : BaseRepository() {

    fun getRankPlayers(): Observable<MutableList<RankPlayer>> = getRankPlayers(null, null)

    fun getRankPlayers(limitTop: Int?, limitLow: Int?): Observable<MutableList<RankPlayer>> = Observable.create {
        var list = mutableListOf<RankPlayer>()
        var players = getDatabase().getPlayerDao().getPlayers()
        var match = getDatabase().getMatchDao().getLastRankMatch()
        for (player in players) {
            var rank =
                try {
                    getDatabase().getRankDao().getPlayerRank(player.id, match.id).rank
                } catch (e: Exception) {
                    0
                }
            var rp = RankPlayer(player, rank!!, 0)
            if (limitTop != null && rank < limitTop) {
                continue
            }
            if (limitLow != null && rank > limitLow) {
                continue
            }
            list.add(rp)
        }
        it.onNext(list)
        it.onComplete()
    }

    fun getFinalGroupPlayers(matchId: Long, groupFlag: Int): MutableList<RankPlayer> {
        var players = mutableListOf<RankPlayer>()
        var list = getDatabase().getRecordPlayerDao().getMatchGroupPlayers(matchId, groupFlag)
        for (item in list) {
            var player = getDatabase().getPlayerDao().getPlayerById(item.playerId)
            var rp = RankPlayer(player, item.playerSeed!!, 0)
            players.add(rp)
        }
        return players
    }
}