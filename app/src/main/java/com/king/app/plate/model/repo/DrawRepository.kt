package com.king.app.plate.model.repo

import com.king.app.plate.model.bean.RecordPack
import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.page.match.DrawData
import kotlin.math.ln

/**
 * @author Jing
 * @description:
 * @date :2020/1/25 0025 19:41
 */
class DrawRepository: BaseRepository() {

    fun getDrawTree(matchId: Int): DrawData {
        var match = getDatabase().getMatchDao().getMatchById(matchId)
        var recordList = getDatabase().getRecordDao().getRecordsByMatch(matchId)
        var recordPackList = arrayListOf<RecordPack>()
        for (record in recordList) {
            var players = getDatabase().getRecordPlayerDao().getPlayersByRecord(record.id!!)
            var scores = getDatabase().getRecordScoreDao().getScoresByRecord(record.id!!)
            var player1: RecordPlayer? = null
            var player2: RecordPlayer? = null
            for (index in 0..players.size) {
                var player = getDatabase().getPlayerDao().getPlayerById(players[index].playerId!!)
                players[index].player = player
                if (index == 0) {
                    player1 = players[index]
                }
                else if (index == 1) {
                    player2 = players[index]
                }
            }
            var pack = RecordPack(record, player1, player2, scores)
            recordPackList.add(pack)
        }
        return DrawData(match, recordPackList, null)
    }

    fun parseDrawText(drawData: DrawData) {
        var drawText: Array<Array<String>> = arrayOf()
        if (drawData.recordList != null) {
            for (pack in drawData.recordList!!) {
//                toDrawText(drawData, pack)
            }
        }
    }

    private fun formatRoundIndex(round: Int, draws: Int): Int? {
        var tr = ln(draws.toDouble()) / ln(2.0)
        return tr.toInt() - 1 - round
    }
}