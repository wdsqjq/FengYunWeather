package me.wsj.fengyun.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import me.wsj.fengyun.service.WidgetService

class BootCompleteReceiver : BroadcastReceiver() {

    private val BOOT_ACTION = "android.intent.action.BOOT_COMPLETED"

    override fun onReceive(context: Context, intent: Intent) {
        if (BOOT_ACTION == intent.action) {
            //开启Service
            openService(context)
        }
    }

    /***
     * 启动Service的方法
     *
     * @param context
     */
    fun openService(context: Context) {
        val newIntent = Intent(context, WidgetService::class.java)
        //判断当前编译的版本是否高于等于 Android8.0 或 26 以上的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(newIntent)
        } else {
            context.startService(newIntent)
        }
    }
}