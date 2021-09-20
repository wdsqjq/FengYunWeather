package me.wsj.fengyun.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import me.wsj.fengyun.bean.AirNow
import me.wsj.fengyun.bean.VersionBean
import me.wsj.fengyun.db.AppRepo
import me.wsj.fengyun.db.entity.CityEntity
import me.wsj.fengyun.ui.base.BaseViewModel
import me.wsj.lib.net.HttpUtils
import per.wsj.commonlib.utils.CommonUtil
import per.wsj.commonlib.utils.LogUtil

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
            val url = "http://fengyun.icu/api/check_version"
            val param = HashMap<String, Any>()
            param["app_code"] = CommonUtil.getVersionCode(app)
            val result = HttpUtils.post<VersionBean>(url, param)

            result?.let {
                newVersion.postValue(it)
            }
        }
    }

    /******************************HomeActivity******************************/
}