package com.king.app.plate.model.bean

import com.king.app.plate.model.db.entity.Record
import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.model.db.entity.RecordScore

/**
 * @author Jing
 * @description:
 * @date :2020/1/25 0025 18:14
 */
data class RecordPack (
    var record: Record?,
    var playerList: List<RecordPlayer>?,
    var scoreList: List<RecordScore>?
)