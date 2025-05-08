package me.wsj.fengyun.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.APSService
import me.wsj.fengyun.BuildConfig
import me.wsj.fengyun.R
import me.wsj.fengyun.bean.CityBean
import me.wsj.fengyun.bean.Location
import me.wsj.fengyun.bean.SearchCity
import me.wsj.fengyun.db.AppRepo
import me.wsj.fengyun.db.entity.CityEntity
import me.wsj.fengyun.ui.base.BaseViewModel
import me.wsj.fengyun.utils.ContentUtil
import me.wsj.lib.net.HttpUtils
import me.wsj.lib.net.LoadState
import java.util.*
import kotlin.collections.HashMap

const val LAST_LOCATION = "LAST_LOCATION"

class SearchViewModel(private val app: Application) : BaseViewModel(app) {

    val searchResult = MutableLiveData<List<Location>>()

    val curCity = MutableLiveData<Location>()

    val choosedCity = MutableLiveData<Location>()

    val topCity = MutableLiveData<List<String>>()

    val addFinish = MutableLiveData<Boolean>()


    /**
     * 搜索城市
     */
    fun searchCity(keywords: String) {
        launchSilent {
            val url = "https://geoapi.qweather.com/v2/city/lookup"
            val param = HashMap<String, Any>()
            param["location"] = keywords
            param["key"] = BuildConfig.HeFengKey

            val result = HttpUtils.get<SearchCity>(url, param)
            result?.let {
                searchResult.postValue(it.location)
            }
        }
    }

    /**
     * 获取城市数据
     */
    fun getCityInfo(cityInfo: Pair<String, String?>, save: Boolean = false) {
        launch {
            if (save) {
                // 缓存定位城市
                AppRepo.getInstance().saveCache(LAST_LOCATION, cityInfo.first)
            }

            val url = "https://geoapi.qweather.com/v2/city/lookup"
            val param = HashMap<String, Any>()
            param["location"] =
                if (cityInfo.second.isNullOrEmpty()) cityInfo.first else cityInfo.second!!
            param["key"] = BuildConfig.HeFengKey

            val result = HttpUtils.get<SearchCity>(url, param)
            result?.let {
                if (save) {
                    curCity.postValue(it.location[0])
                } else {
                    choosedCity.postValue(it.location[0])
                }
            }
        }
    }

    /**
     * 获取热门城市
     */
    fun getTopCity() {
        launch {
            val stringArray = app.resources.getStringArray(R.array.top_city)
            val cityList = stringArray.toList() as ArrayList<String>
            topCity.postValue(cityList)
        }
    }

    /**
     * 添加城市
     */
    fun addCity(it: CityBean, isLocal: Boolean = false, fromSplash: Boolean = false) {
        launch {
            // todo 排序
            if (isLocal) {
                AppRepo.getInstance().removeLocal(it.cityId)
//                AppRepo.getInstance().removeCity(it.cityId)
            }
            AppRepo.getInstance().addCity(CityEntity(it.cityId, it.cityName, isLocal))
            ContentUtil.CITY_CHANGE = true
            if (!isLocal) {
                addFinish.postValue(true)
            } else if (fromSplash) {
                addFinish.postValue(true)
            }
        }
    }

    val curLocation = MutableLiveData<Pair<String, String>>()

    fun getLocation() {
        loadState.postValue(LoadState.Start("正在获取位置..."))
        //初始化定位
        val mLocationClient = AMapLocationClient(app)
        //设置定位回调监听

        //声明AMapLocationClientOption对象
        val mLocationOption = AMapLocationClientOption()
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.interval = 10000
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.httpTimeOut = 20000
        mLocationClient.setLocationListener { aMapLocation ->
            if (aMapLocation.errorCode == 0) {
//                curLocation.value = "${aMapLocation.longitude},${aMapLocation.latitude}"
                curLocation.value = Pair(
                    aMapLocation.district,
                    "${aMapLocation.longitude},${aMapLocation.latitude}"
                )
            } else {
                loadState.value = LoadState.Error("获取定位失败,请重试")
            }
            loadState.value = LoadState.Finish
            mLocationClient.onDestroy()
        }
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption)
        //启动定位
        mLocationClient.startLocation()
    }
}