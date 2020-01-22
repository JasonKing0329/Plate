package com.king.app.plate.model

/**
 * Desc:
 * @author：Jing Yang
 * @date: 2020/1/21 15:56
 */
class SettingProperty: BaseProperty() {

    companion object {
        fun isEnableFingerPrint(): Boolean = getBoolean("pref_safety_fingerprint")
    }

}