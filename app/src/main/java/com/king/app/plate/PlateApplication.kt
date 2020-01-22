package com.king.app.plate

import android.app.Application

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 10:21
 */
class PlateApplication:Application() {

    companion object {
        lateinit var instance:PlateApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}