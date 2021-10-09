package me.wsj.fengyun

import me.wsj.lib.BaseApp
import me.wsj.lib.utils.SpUtil
import me.wsj.plugin_lib.SkinManager

//@HiltAndroidApp
open class MyApp : BaseApp() {

    override fun onCreate() {
        super.onCreate()
        SkinManager.init(this, object : SkinManager.OnPluginCallback {
            override fun setSkin(skinPath: String?) {
                SpUtil.setPluginPath(context, skinPath)
            }

            override fun reset() {
                SpUtil.setThemeFlag(context, 0)
            }

            override fun getSkin(): String {
                return SpUtil.getPluginPath(context)
            }
        })
    }
}