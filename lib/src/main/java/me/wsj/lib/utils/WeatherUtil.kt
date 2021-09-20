package me.wsj.lib.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import me.wsj.lib.R

object WeatherUtil {

    fun getWarningRes(context: Context, level: String): Pair<Drawable, Int> {
        val result: Pair<Drawable, Int>
        val res = context.resources
        when (level) {
            "黄色", "Yellow" -> {
                result =
                    res.getDrawable(R.drawable.shape_yellow_alarm) to res.getColor(
                        R.color.white
                    )
            }
            "橙色", "Orange" -> {
                result =
                    res.getDrawable(R.drawable.shape_orange_alarm) to res.getColor(
                        R.color.white
                    )
            }
            "红色", "Red" -> {
                result =
                    res.getDrawable(R.drawable.shape_red_alarm) to res.getColor(
                        R.color.white
                    )
            }
            "白色", "White" -> {
                result =
                    res.getDrawable(R.drawable.shape_white_alarm) to res.getColor(
                        R.color.black
                    )
            }
            else -> {
                result =
                    res.getDrawable(R.drawable.shape_blue_alarm) to res.getColor(
                        R.color.white
                    )
            }
        }
        return result
    }

    @JvmStatic
    fun getF(value: String): Long {
        return try {
            var i = value.toInt().toLong()
            i = Math.round(i * 1.8 + 32)
            i
        } catch (e: Exception) {
            0
        }
    }

    fun getAirColor(context: Context, aqi: String): Int {
        val num = aqi.toInt()
        val flag = when {
            num <= 50 -> 1
            num <= 100 -> 2
            num <= 150 -> 3
            num <= 200 -> 4
            num <= 300 -> 5
            else -> 6
        }
        val id = context.resources
            .getIdentifier("color_air_leaf_$flag", "color", context.packageName)
        return context.resources.getColor(id)
    }

    @JvmStatic
    fun convert(code: Int): Int {
        var result = 0
        when (code) {
            100, 150 ->
                result = 1  // 晴
            101, 102, 103, 153 ->
                result = 2  // 多云
            104, 154 ->
                result = 3  // 阴天
            300, 301, 306, 313, 315, 350, 399 ->
                result = 4  // 雨
            305, 309, 314 ->
                result = 40  // 小雨
            307, 308, 310, 311, 312, 316, 317, 318, 351 ->
                result = 42  // 大雨
            302, 303, 304 ->
                result = 5  // 雷雨
            in 400..457 ->
                result = 6  // 雪
            500, 501, 509, 510, 514, 515 ->
                result = 7  // 雾
            502, 511, 512, 513 ->
                result = 8  // 霾
            in 503..508 ->
                result = 9  // 沙尘
            else -> result = 0
        }
        return result
    }

    /**
     * 将图片压缩到指定大小
     *
     * @param w
     * @param h
     * @return
     */
    @JvmStatic
    fun getScaledIcon(drawable: Drawable, w: Float, h: Float): Bitmap {
        val bd = drawable as BitmapDrawable
        val matrix = Matrix()
        val src = bd.bitmap
        matrix.postScale(w / src.width, h / src.height)
        return Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
    }

    /**
     * 获取星期
     *
     * @param num 0-6
     * @return 星期
     */
    fun getWeek(num: Int): String {
        var week = " "
        when (num) {
            1 -> week = "周一"
            2 -> week = "周二"
            3 -> week = "周三"
            4 -> week = "周四"
            5 -> week = "周五"
            6 -> week = "周六"
            7 -> week = "周日"
        }
        return week
    }
}
