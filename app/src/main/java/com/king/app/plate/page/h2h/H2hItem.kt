package com.king.app.plate.page.h2h

import com.king.app.plate.model.bean.RecordPack
import com.king.app.plate.model.db.entity.Match

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/21 13:10
 */
data class H2hItem(
    var recordPack: RecordPack,
    var match: Match,
    var date: String,
    var level: String,
    var round: String,
    var result: String,
    var score: String
)