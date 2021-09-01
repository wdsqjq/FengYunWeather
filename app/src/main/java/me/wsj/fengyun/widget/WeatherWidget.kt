package me.wsj.fengyun.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import me.wsj.fengyun.service.WidgetService
import per.wsj.commonlib.utils.LogUtil


/**
 * Implementation of App Widget functionality.
 */
class WeatherWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, WidgetService::class.java))
        } else {
            context.startService(Intent(context, WidgetService::class.java))
        }
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
    }

    override fun onEnabled(context: Context) {
        // todo add tip: add boot start
        LogUtil.e("widget enable-------------------")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, WidgetService::class.java))
        } else {
            context.startService(Intent(context, WidgetService::class.java))
        }
    }

    override fun onDisabled(context: Context) {
        context.stopService(Intent(context, WidgetService::class.java))
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    /**
     * 这里基本没用了，都放在service中即可
     */
    // Construct the RemoteViews object
    /*val views = RemoteViews(context.packageName, R.layout.weather_widget)
//    views.setTextViewText(R.id.tvLunarDate, Lunar(Calendar.getInstance()).toString())

    val calendarIntent = Intent()
    val pkg =
        if (Build.VERSION.SDK_INT >= 8) "com.android.calendar" else "com.google.android.calendar"
    calendarIntent.component = ComponentName(pkg, "com.android.calendar.LaunchActivity")

    val calendarPI = PendingIntent.getActivity(context, 0, calendarIntent, 0)
    views.setOnClickPendingIntent(R.id.llCalendar, calendarPI)
    views.setOnClickPendingIntent(R.id.tvLunarDate, calendarPI)

    val clockIntent = Intent()
    clockIntent.component =
        ComponentName("com.android.deskclock", "com.android.deskclock.DeskClock")
    val timePI = PendingIntent.getActivity(context, 0, clockIntent, 0)
    views.setOnClickPendingIntent(R.id.clockTime, timePI)

    val weatherIntent = Intent(context, HomeActivity::class.java)
    val weatherPI = PendingIntent.getActivity(context, 0, weatherIntent, 0)
    views.setOnClickPendingIntent(R.id.llWeather, weatherPI)

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)*/
}