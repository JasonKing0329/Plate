package com.king.app.plate

import android.app.Application
import kotlin.properties.Delegates

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 10:21
 */
class PlateApplication:Application() {

    companion object {
        var instance:PlateApplication by Delegates.notNull()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}