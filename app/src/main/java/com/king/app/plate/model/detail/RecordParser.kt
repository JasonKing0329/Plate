package com.king.app.plate.model.detail

import com.king.app.plate.conf.AppConstants
import com.king.app.plate.model.bean.RecordPack
import com.king.app.plate.model.db.entity.RecordScore

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 13:39
 */
class RecordParser {

    companion object {
        fun getRoundSimpleText(round: Int, matchLevel: Int): String {
            return AppConstants.ROUND_NORMAL[round]
        }

        /**
         * 形式为 [1]A d. [2]B
         * @param recordPack
         */
        fun getH2hResult(recordPack: RecordPack): String {
            var player1Id = recordPack.playerList[0].playerId
            var player1 = if (recordPack.playerList[0].playerSeed!! > 0) "[${recordPack.playerList[0].playerSeed}]${recordPack.playerList[0].player!!.name}"
            else recordPack.playerList[0].player!!.name
            var player2 = if (recordPack.playerList[1].playerSeed!! > 0) "[${recordPack.playerList[1].playerSeed}]${recordPack.playerList[1].player!!.name}"
            else recordPack.playerList[1].player!!.name
            return if (recordPack.record!!.winnerId == player1Id) {
                "$player1 d. $player2"
            }
            else {
                "$player2 d. $player1"
            }
        }

        /**
         * 解析比分，形式为 6-3 6(5)-7 7-6(4)，winnerId决定谁的比分在前
         * @param recordPack
         * @return
         */
        fun getScoreText(recordPack: RecordPack): String {
            val buffer = StringBuffer()
            var player1Id = recordPack.playerList[0].playerId
            for (score in recordPack.scoreList) {
                // 默认score1在前
                var score1 = score.score1
                var score2 = score.score2
                var scoreTie1 = score.scoreTie1
                var scoreTie2 = score.scoreTie2
                // score2在前
                if (player1Id != recordPack.record!!.winnerId) {
                    score1 = score.score2
                    score2 = score.score1
                    scoreTie1 = score.scoreTie2
                    scoreTie2 = score.scoreTie1
                }

                buffer.append(" ")
                if (score.isTiebreak) {
                    if (score1 == 6) {
                        buffer.append("${score1}(${scoreTie1})-${score2}")
                    }
                    else{
                        buffer.append("${score1}-${score2}(${scoreTie2})")
                    }
                }
                else{
                    buffer.append("${score1}-${score2}")
                }
            }
            var text = buffer.toString()
            if (text.length > 1) {
                text = text.substring(1)
            }
            return text
        }
    }
}