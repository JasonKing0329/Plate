package com.king.app.plate.page.match

import com.king.app.plate.model.db.entity.Match

/**
 * @author Jing
 * @description:
 * @date :2020/1/25 0025 18:12
 */
data class DrawData (
    var match: Match?,
    var roundList: List<DrawRound>?
)