package com.king.app.plate.model.bean

import com.king.app.plate.model.db.entity.RecordPlayer

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/4/16 16:06
 */
class DrawBody {
    var bodyData: MutableList<MutableList<BodyCell>> = mutableListOf()
}
class BodyCell {
    var text: String = ""
    var type: Int = 0
    var pack: RecordPack = RecordPack(null, null, null)
    var player: RecordPlayer? = null
    var isModified: Boolean = false
}
class RecordCells {
    var bodyCells = mutableListOf<MutableList<BodyCell>>()
}