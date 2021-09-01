package me.wsj.fengyun.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.wsj.fengyun.db.AppRepo
import me.wsj.fengyun.db.entity.CityEntity
import me.wsj.fengyun.ui.base.BaseViewModel

class MainViewModel(val app: Application) : BaseViewModel(app) {

    /******************************HomeActivity******************************/

    val mCities = MutableLiveData<List<CityEntity>>()

    val mCurCondCode = MutableLiveData<String>()

    fun setCondCode(condCode: String) {
        mCurCondCode.postValue(condCode)
    }

    fun getCities() {
        launchSilent {
            val cities = AppRepo.getInstance().getCities()
            mCities.postValue(cities)
        }
    }

    /******************************HomeActivity******************************/
}