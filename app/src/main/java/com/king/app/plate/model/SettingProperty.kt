package com.king.app.plate.model

import com.google.gson.Gson
import com.king.app.plate.model.bean.DrawColors
import java.lang.Exception

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 15:56
 */
class SettingProperty: BaseProperty() {

    companion object {

        fun isEnableFingerPrint(): Boolean = getBoolean("pref_safety_fingerprint")

        fun setDrawColors(drawColors: DrawColors){
            setString("draw_colors", Gson().toJson(drawColors))
        }

        fun getDrawColors(): DrawColors? {
            var json = getString("draw_colors")
            return try {
                Gson().fromJson(json, DrawColors::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }

}