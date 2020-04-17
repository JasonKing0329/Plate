package com.king.app.plate.model.repo

import com.king.app.plate.model.bean.RankPlayer
import io.reactivex.Observable

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/14 15:56
 */
class PlayerRepository : BaseRepository() {

    fun getRankPlayers(): Observable<List<RankPlayer>> = Observable.create {
        var list = arrayListOf<RankPlayer>()
        var players = getDatabase().getPlayerDao().getPlayers()
        var matchId = getLatestMatchId()
        for (player in players) {
            var rank =
                try {
                    getDatabase().getRankDao().getPlayerRank(player.id, matchId).rank
                } catch (e: Exception) {
                    0
                }
            var rp = RankPlayer(player, rank!!)
            list.add(rp)
        }
        it.onNext(list)
        it.onComplete()
    }

    private fun getLatestMatchId(): Int? {
        return 0;
    }
}