package com.king.app.plate.model.bean

/**
 * @author Jing
 * @description:
 * @date :2020/1/27 0027 11:11
 */
class DrawTree<T> {
    var round: Int = 0
    var column: Int = 0
    var data: T? = null
    var text: String = ""
    var nextRoundTree: DrawTree<T>? = null
    var lastRoundTree: DrawTree<T>? = null
    var thisRoundTree: DrawTree<T>? = null
}