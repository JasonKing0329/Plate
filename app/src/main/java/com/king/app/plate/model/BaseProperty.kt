package com.king.app.plate.model

import android.preference.PreferenceManager
import com.king.app.plate.PlateApplication
import com.king.app.plate.model.db.AppDatabase

/**
 * Desc:
 *
 * @author：Jing Yang
 * @date: 2020/1/21 9:19
 */
abstract class BaseProperty {

    companion object {
        val PREF_NAME = "com.king.app.plate_preferences.xml"
        fun getPrefFolder():String {
            return PlateApplication.instance.cacheDir.parent + "/shared_prefs/"
        }
        fun getPrefPath(): String {
            return "${getPrefFolder()}${PREF_NAME}"
        }

        fun getString(key: String): String {
            val sp = PreferenceManager.getDefaultSharedPreferences(PlateApplication.instance)
            return sp.getString(key, "")
        }

        fun setString(key: String, value: String) {
            val sp = PreferenceManager.getDefaultSharedPreferences(PlateApplication.instance)
            val editor = sp.edit()
            editor.putString(key, value)
            editor.commit()
        }

        fun getInt(key: String): Int {
            val sp = PreferenceManager.getDefaultSharedPreferences(PlateApplication.instance)
            return sp.getInt(key, -1)
        }

        fun getInt(key: String, defaultValue: Int): Int {
            val sp = PreferenceManager.getDefaultSharedPreferences(PlateApplication.instance)
            return sp.getInt(key, defaultValue)
        }

        fun setInt(key: String, value: Int) {
            val sp = PreferenceManager.getDefaultSharedPreferences(PlateApplication.instance)
            val editor = sp.edit()
            editor.putInt(key, value)
            editor.commit()
        }

        fun getLong(key: String): Long {
            val sp = PreferenceManager.getDefaultSharedPreferences(PlateApplication.instance)
            return sp.getLong(key, -1)
        }

        fun setLong(key: String, value: Long) {
            val sp = PreferenceManager.getDefaultSharedPreferences(PlateApplication.instance)
            val editor = sp.edit()
            editor.putLong(key, value)
            editor.commit()
        }

        fun getBoolean(key: String): Boolean {
            val sp = PreferenceManager.getDefaultSharedPreferences(PlateApplication.instance)
            return sp.getBoolean(key, false)
        }

        fun setBoolean(key: String, value: Boolean) {
            val sp = PreferenceManager.getDefaultSharedPreferences(PlateApplication.instance)
            val editor = sp.edit()
            editor.putBoolean(key, value)
            editor.commit()
        }

    }
}
