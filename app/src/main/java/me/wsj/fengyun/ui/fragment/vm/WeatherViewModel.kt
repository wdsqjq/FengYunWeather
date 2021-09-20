package me.wsj.fengyun.ui.fragment.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.wsj.fengyun.BuildConfig
import me.wsj.fengyun.bean.*
import me.wsj.fengyun.db.AppRepo
import me.wsj.fengyun.ui.base.BaseViewModel
import me.wsj.lib.net.HttpUtils

const val CACHE_WEATHER_NOW = "now_"

class WeatherViewModel(val app: Application) : BaseViewModel(app) {

    val weatherNow = MutableLiveData<Now>()
    val warnings = MutableLiveData<List<Warning>>()
    val airNow = MutableLiveData<Air>()
    val forecast = MutableLiveData<List<Daily>>()
    val hourly = MutableLiveData<List<Hourly>>()

    fun loadCache(cityId: String) {
        launchSilent {
            var cache = AppRepo.getInstance().getCache<Now>(CACHE_WEATHER_NOW + cityId)
            cache?.let {
                weatherNow.postValue(it)
            }
        }
    }

    fun loadData(cityId: String) {
        val param = HashMap<String, Any>()
        param["location"] = cityId
        param["key"] = BuildConfig.HeFengKey

        // 实时天气
        launch {
            val url = "https://devapi.qweather.com/v7/weather/now"
            val result = HttpUtils.get<WeatherNow>(url, param)
            result?.let {
                weatherNow.postValue(it.now)
                AppRepo.getInstance().saveCache(CACHE_WEATHER_NOW + cityId, it.now)
            }
        }

        // 预警
        launch {
            val url = "https://devapi.qweather.com/v7/warning/now"
            val result = HttpUtils.get<WarningBean>(url, param)
            result?.let {
                if (it.warning.isNotEmpty()) {
                    warnings.postValue(result.warning)
                }
            }
        }

        // 实时空气
        launch {
            val url = "https://devapi.qweather.com/v7/air/now"
            val result = HttpUtils.get<AirNow>(url, param)
            result?.let {
                airNow.postValue(it.now)
            }
        }

        // 15天 天气预报
        launch {
            val url = "https://devapi.qweather.com/v7/weather/15d"
            val result = HttpUtils.get<ForestBean>(url, param)
            result?.let {
                forecast.postValue(it.daily)
            }
        }

        // 逐小时天气预报
        launch {
            val url = "https://devapi.qweather.com/v7/weather/24h"
            val result = HttpUtils.get<WeatherHourly>(url, param)
            result?.let {
                hourly.postValue(it.hourly)
            }
        }

    }

}