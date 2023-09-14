package me.wsj.fengyun.utils

import androidx.preference.PreferenceManager
import me.wsj.lib.BaseApp.Companion.context

/**
 * Created by niuchong on 2019/4/7.
 */
object ContentUtil {
    //应用设置里的文字
    //    public static String SYS_LANG = "zh";
    @JvmField
    var APP_SETTING_UNIT =
        PreferenceManager.getDefaultSharedPreferences(context).getString("unit", "she")

    /*@JvmField
    var UNIT_CHANGE = false*/

    @JvmField
    @Volatile
    var CITY_CHANGE = false

    @JvmField
    var visibleHeight = 0

    @JvmField
    var screenHeight = 0

    val BASE_URL = "https://fengyun.icu/"

    val TC_APP_ID = "101991873"

//    val BASE_URL = "http://192.168.100.231:8000/"
}