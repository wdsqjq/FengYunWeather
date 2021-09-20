package me.wsj.lib.plugin

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import per.wsj.commonlib.utils.LogUtil
import java.io.File
import java.lang.Exception
import java.lang.reflect.Method

object PluginUtil {

    var mResourceWrapper: ResourceWrapper? = null

    fun getPluginRes(context: Context, resName: String, pluginPath: String): Drawable? {
        return try {
            val resourceWrapper = loadResource(context, pluginPath)
            val resId = resourceWrapper.resources
                .getIdentifier(resName, "drawable", resourceWrapper.packageName)
            resourceWrapper.resources.getDrawable(resId)
        } catch (e: Exception) {
            null
        }
    }

    fun loadResource(context: Context, skinPath: String): ResourceWrapper {
        if (mResourceWrapper != null) {
            return mResourceWrapper!!
        } else {
            synchronized(PluginUtil::class.java) {
                // 创建AssetManager
                val assetManagerClass = AssetManager::class.java
                val assetManager: AssetManager = assetManagerClass.newInstance()
                //资源路径设置 目录或压缩包
                val addAssetPath: Method =
                    assetManagerClass.getMethod("addAssetPath", String::class.java)
                addAssetPath.invoke(assetManager, skinPath)
                // 构造新的Resource
                val skinRes = Resources(
                    assetManager,
                    context.resources.displayMetrics,
                    context.resources.configuration
                )
                // 获取apk资源包的包名
                val packageInfo = context.packageManager
                    .getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES)
                val packageName = packageInfo!!.packageName
                return ResourceWrapper(skinRes, packageName)
            }
        }
    }
}