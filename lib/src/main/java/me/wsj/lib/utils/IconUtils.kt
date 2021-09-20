package me.wsj.lib.utils

import android.content.Context
import android.graphics.drawable.Drawable
import me.wsj.lib.R
import me.wsj.lib.plugin.PluginUtil
import me.wsj.lib.utils.DateUtil.getNowHour
import me.wsj.lib.utils.WeatherUtil.convert
import per.wsj.commonlib.utils.LogUtil

object IconUtils {

    @JvmStatic
    fun getDayIcon(context: Context, weatherCode: String): Drawable? {
        return getIcon(context, weatherCode, "d")
    }

    @JvmStatic
    fun getNightIcon(context: Context, weatherCode: String): Drawable? {
        return getIcon(context, weatherCode, "n")
    }

    fun getIcon(context: Context, weatherCode: String, postFix: String): Drawable? {
        val isPlugin = SpUtil.getThemeFlag(context) == 1
        val code = parseCode(weatherCode, postFix)
        val resName = "icon_$code"
        if (isPlugin) {
            val pluginRes = PluginUtil.getPluginRes(context, resName, SpUtil.getPluginPath(context))
            if (pluginRes == null) {
                SpUtil.setPluginPath(context, "")
                LogUtil.e("res not found: $resName")
            }
            return pluginRes
                ?: context.resources.getDrawable(R.drawable.icon_100d)
        } else {
            val resId = getDrawableRes(context, resName, R.drawable.icon_100d)
            return context.resources.getDrawable(resId)
        }
    }

    /**
     * 获取白天深色天气图标
     */
    @JvmStatic
    fun getDayIconDark(context: Context, weather: String): Int {
        val code = parseCode(weather, "d")
        return getDrawableRes(context, "icon_$code", R.drawable.icon_100d)
    }

    /**
     * 获取白天深色天气图标
     */
    @JvmStatic
    fun getNightIconDark(context: Context, weather: String): Int {
        val code = parseCode(weather, "n")
        return getDrawableRes(context, "icon_$code", R.drawable.icon_100n)
    }

    private fun parseCode(weather: String, postFix: String): String {
        val code = (if (weather.isEmpty()) "0" else weather).toInt()
        return when (code) {
            in 150 until 199,
            in 350 until 399,
            in 450 until 499 -> {
                (code - 50).toString() + "n"
            }
            else -> {
                code.toString() + postFix
            }
        }
    }

    /**
     * 获取白天背景
     */
    val defaultBg: Int
        get() = if (isDay()) R.drawable.bg_0_d else R.drawable.bg_0_n

    @JvmStatic
    fun isDay(): Boolean {
        val now = getNowHour()
        return now in 7..18
    }

    /**
     * 获取白天背景
     */
    fun getBg(context: Context, code: Int): Int {
        return if (isDay()) getDayBg(context, code) else getNightBg(
            context,
            code
        )
    }

    /**
     * 获取白天背景
     */
    fun getDayBg(context: Context, code: Int): Int {
        var newCode = convert(code)
        if (newCode > 10) {
            newCode /= 10
        }
        return getDrawableRes(context, "bg_" + newCode + "_d", R.drawable.bg_0_d)
    }

    /**
     * 获取晚上背景
     */
    fun getNightBg(context: Context, code: Int): Int {
        var newCode = convert(code)
        if (newCode > 10) {
            newCode /= 10
        }
        return getDrawableRes(context, "bg_" + newCode + "_n", R.drawable.bg_0_n)
    }

    fun getDrawableRes(context: Context, weather: String, def: Int): Int {
        return getRes(context, "drawable", weather, def)
    }

    fun getRes(context: Context, type: String?, weather: String, def: Int): Int {
        return try {
            var id = context.resources.getIdentifier(weather, type, context.packageName)
            if (id == 0) {
                id = def
            }
            id
        } catch (e: Exception) {
            LogUtil.e("获取资源失败：$weather")
            def
        }
    }
}