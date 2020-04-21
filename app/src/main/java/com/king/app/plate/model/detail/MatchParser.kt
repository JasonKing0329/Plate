package com.king.app.plate.model.detail

import com.king.app.plate.conf.AppConstants

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 14:10
 */
class MatchParser {
    companion object {
        fun getLevelText(level: Int): String {
            return AppConstants.MATCH_LEVEL[level]
        }
    }
}