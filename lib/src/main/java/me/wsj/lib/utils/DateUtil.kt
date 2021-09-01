package me.wsj.lib.utils

import java.text.SimpleDateFormat
import java.util.*


object DateUtil {

    @JvmStatic
    fun getNowHour(): Int {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    }

    /**
     * HH:mm
     */
    fun getNowTime(): String {
        val simpleDateFormat = SimpleDateFormat("HH:mm")
        // 获取当前时间
        val date = Date(System.currentTimeMillis());
        return simpleDateFormat.format(date)
    }
}