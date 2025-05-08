package me.wsj.fengyun.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import me.wsj.fengyun.BuildConfig
import me.wsj.fengyun.bean.TempUnit
import me.wsj.fengyun.bean.VersionBean
import me.wsj.fengyun.db.AppRepo
import me.wsj.fengyun.db.entity.CityEntity
import me.wsj.fengyun.ui.base.BaseViewModel
import me.wsj.fengyun.utils.ContentUtil
import me.wsj.lib.net.HttpUtils

class MainViewModel(val app: Application) : BaseViewModel(app) {

    /******************************HomeActivity******************************/

    val mCities = MutableLiveData<List<CityEntity>>()

    val mCurCondCode = MutableLiveData<String>()

    val newVersion = MutableLiveData<VersionBean>()

    fun setCondCode(condCode: String) {
        mCurCondCode.postValue(condCode)
    }

    fun getCities() {
        launchSilent {
            val cities = AppRepo.getInstance().getCities()
            mCities.postValue(cities)
        }
    }

    fun checkVersion() {
        launchSilent {
            val url = ContentUtil.BASE_URL + "api/check_version2"
            val param = HashMap<String, Any>()
//            param["app_code"] = CommonUtil.getVersionCode(app)
            param["key"] = BuildConfig.HeFengKey
            param["build_type"] = BuildConfig.BUILD_TYPE

            val result = HttpUtils.post<VersionBean>(url, param)

            result?.let {
                newVersion.postValue(it)
            }
        }
    }


    fun changeUnit(unit: TempUnit) {
//        ContentUtil.UNIT_CHANGE = true
        ContentUtil.APP_SETTING_UNIT = unit.tag

        PreferenceManager.getDefaultSharedPreferences(app).edit().apply {
            putString("unit", unit.tag)
            apply()
        }
    }


    /******************************HomeActivity******************************/
}