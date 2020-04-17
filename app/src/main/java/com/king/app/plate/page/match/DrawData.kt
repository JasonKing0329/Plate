package com.king.app.plate.page.match

import com.king.app.plate.model.bean.DrawBody
import com.king.app.plate.model.db.entity.Match

/**
 * @author Jing
 * @description:
 * @date :2020/1/25 0025 18:12
 */
class DrawData {
    var match: Match? = null
    var roundList: List<DrawRound> = arrayListOf()
    var body: DrawBody? = null
}