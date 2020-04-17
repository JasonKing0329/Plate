package com.king.app.plate.page.match

import com.king.app.plate.model.bean.RecordPack

/**
 * @author Jing
 * @description:
 * @date :2020/2/2 0002 17:36
 */
data class DrawRound (
    var round: Int,
    var recordList: MutableList<RecordPack>
)