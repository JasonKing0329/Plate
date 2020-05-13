package com.king.app.plate.page.match.list

import com.king.app.plate.model.bean.RankPlayer
import com.king.app.plate.model.db.entity.Match

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/13 16:25
 */
data class MatchItemBean (
    var match: Match,
    var winner: RankPlayer?
)