package me.wsj.fengyun.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.wsj.fengyun.db.AppRepo
import me.wsj.fengyun.db.entity.CityEntity
import me.wsj.fengyun.ui.base.BaseViewModel

class CityManagerViewModel(private val app: Application) : BaseViewModel(app) {

    val cities = MutableLiveData<List<CityEntity>>()

    fun getCities() {
        launch {
            val results = AppRepo.getInstance().getCities()
            cities.postValue(results)
        }
    }

    fun removeCity(cityId: String) {
        launch {
            AppRepo.getInstance().removeCity(cityId)
        }
    }

    fun updateCities(it: List<CityEntity>) {
        launch {
            AppRepo.getInstance().removeAllCity()
            it.forEach {
                AppRepo.getInstance().addCity(it)
            }
        }
    }
}