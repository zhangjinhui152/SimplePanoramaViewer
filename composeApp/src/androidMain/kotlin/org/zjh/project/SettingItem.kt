package org.zjh.project

import android.content.Context


open class SettingItem {
    companion object {
        val darkMode = "darkMode"
        val plType = "plType"

    }

    open var plType: Int = 0
    open var darkMode: Boolean = false
    fun initSetting(context: Context) {
        val sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        plType = sharedPreferences.getInt("plType", 0)
        darkMode = sharedPreferences.getBoolean("darkMode", false)
    }

    fun setItem(context: Context, key: String, value: Any) {
        val sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        when (value) {
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            else -> throw IllegalArgumentException("不支持的类型")
        }

        editor.apply()
    }

    fun setDark(value: Boolean) {
        darkMode = value
    }
}