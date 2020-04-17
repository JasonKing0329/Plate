package com.king.app.plate.model.bean

import com.king.app.plate.model.db.entity.RecordPlayer
import com.king.app.plate.model.db.entity.RecordScore

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/16 16:06
 */
class DrawBody {
    var bodyData: MutableList<MutableList<BodyData>> = mutableListOf()
}
class BodyData {
    var text: String = ""
    var type: Int = 0
    var pack: RecordPack = RecordPack(null, null, null)
    var player: RecordPlayer? = null
    var score: RecordScore? = null
    var scoreIndex: Int = 0
}