package me.wsj.fengyun.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import me.wsj.fengyun.ui.base.BaseViewModel
import me.wsj.fengyun.utils.ContentUtil
import me.wsj.lib.net.HttpUtils
import per.wsj.commonlib.utils.CommonUtil
import per.wsj.commonlib.utils.DeviceUtil

class FeedBackViewModel(val app: Application) : BaseViewModel(app) {

    val feedBackResult = MutableLiveData<String>()

    fun sendFeedBack(content: String) {
        val param = HashMap<String, Any>()
        param["content"] = content
//        param["app_version"] = CommonUtil.getVersionCode(app)
//        param["device_brand"] = DeviceUtil.getDeviceBrand()
//        param["system_model"] = DeviceUtil.getSystemModel()
//        param["system_version"] = DeviceUtil.getSystemVersion()

        val url = ContentUtil.BASE_URL + "api/feedback"

        launch {
            val result = HttpUtils.post<String>(url, param)
            result?.let {
                feedBackResult.postValue(it)
            }
        }
    }
}