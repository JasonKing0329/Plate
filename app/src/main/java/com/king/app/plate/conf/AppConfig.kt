package com.king.app.plate.conf

import android.os.Environment

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 15:36
 */
class AppConfig {

    companion object {
        var sdcardPath = Environment.getExternalStorageDirectory().path
        var appRootPath = "$sdcardPath/fileencrypt"
        var exportPath = "$appRootPath/export"
        var confPath = "$appRootPath/conf"
        var prefPath = "$confPath/conf"
        var prefDefPath = "$prefPath/conf"

        val prefName = "com.king.app.plate_preferences"

        var appDirectories = arrayOf(
            appRootPath,
            exportPath,
            confPath,
            prefPath,
            prefDefPath
        )

    }

}