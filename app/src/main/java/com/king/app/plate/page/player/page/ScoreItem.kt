package com.king.app.plate.page.player.page

import com.king.app.plate.model.db.entity.Match
import com.king.app.plate.model.db.entity.Score

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/5/14 14:43
 */
data class ScoreItem (
    var bean: Score,
    var match: Match
)