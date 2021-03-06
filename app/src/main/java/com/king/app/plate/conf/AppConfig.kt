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
        var appRootPath = "$sdcardPath/plate"
        var exportPath = "$appRootPath/export"
        var historyPath = "$appRootPath/history"
        var historyDbPath = "$historyPath/db"
        var historyPrefPath = "$historyPath/pref"
        var confPath = "$appRootPath/conf"

        var appDirectories = arrayOf(
            appRootPath,
            exportPath,
            historyPath,
            confPath,
            historyDbPath,
            historyPrefPath
        )

    }

}