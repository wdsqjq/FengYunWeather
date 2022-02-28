package me.wsj.fengyun.utils

import com.tencent.tauth.Tencent
import me.wsj.lib.BaseApp

object TencentUtil {
    val sTencent by lazy {
        Tencent.createInstance(
            ContentUtil.TC_APP_ID,
            BaseApp.context,
            "${BaseApp.context}.fileprovider"
        )
    }
}