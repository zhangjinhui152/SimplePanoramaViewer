package org.zjh.project

import android.content.Context

class SettingItem {

     var plType:Int = 0
     var darkMode:Boolean = false
     fun initSetting(context: Context){
          var sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE)
          plType = sharedPreferences.getInt("plType",0)
          darkMode = sharedPreferences.getBoolean("darkMode",false)
     }
}