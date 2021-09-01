package me.wsj.fengyun.utils

import androidx.preference.PreferenceManager
import me.wsj.fengyun.MyApp.Companion.context
import me.wsj.fengyun.MyApp

/**
 * Created by niuchong on 2019/4/7.
 */
object ContentUtil {
    //应用设置里的文字
    //    public static String SYS_LANG = "zh";
    @JvmField
    var APP_SETTING_UNIT =
        PreferenceManager.getDefaultSharedPreferences(context).getString("unit", "she")
    @JvmField
    var UNIT_CHANGE = false
    @JvmField
    var CITY_CHANGE = false
    @JvmField
    var visibleHeight = 0
}