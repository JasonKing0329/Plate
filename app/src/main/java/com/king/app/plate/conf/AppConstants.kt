package com.king.app.plate.conf

import android.graphics.Color

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/14 15:53
 */
class AppConstants {
    companion object {

        var PERIOD_TOTAL = 21 // 20 Normal + 1 Final

        var draws = 32
        var set = 3
        var bye = 8
        var round = 5

        var draws_final_group = 12
        var draws_final_rest = 4
        var round_final_rest = 2

        var cellTypePlayer = 0
        var cellTypeScore = 1

        const val matchLevelNormal = 0
        const val matchLevelFinal = 1

        var playerSortName = 0
        var playerSortRank = 1

        /**
         * 0: no retire
         * 1: retire with score
         * 2: retire before match(W/0)
         */
        const val RETIRE_NONE = 0
        const val RETIRE_WITH_SCORE = 1
        const val RETIRE_WO = 2

        const val SCORE_RETIRE = "W/O"
        const val SCORE_RETIRE_NORMAL = "(对手退赛)"

        val ROUND_NORMAL = arrayOf("R32", "R16", "QF", "SF", "Final")
        val RESULT_NORMAL = arrayOf("R32", "R16", "QF", "SF", "Runner-up", "Champion")
        val ROUND_FINAL = arrayOf("RR", "SF", "Final")
        val RESULT_FINAL = arrayOf("RR", "SF", "Runner-up", "Champion")
        var ROUND_FIRST = 0
        var ROUND_SECOND = 1
        var ROUND_QF = 2
        var ROUND_SF = 3
        var ROUND_F = 4
        var ROUND_ROBIN = 0

        val MATCH_LEVEL = arrayOf("Normal", "Final")

        var DRAW_TEXT_DEF = Color.parseColor("#333333")
        var DRAW_TEXT_MODIFY = Color.parseColor("#ff0000")
    }
}