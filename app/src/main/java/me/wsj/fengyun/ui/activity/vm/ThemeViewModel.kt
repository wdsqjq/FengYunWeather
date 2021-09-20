package me.wsj.fengyun.ui.activity.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import me.wsj.fengyun.db.AppRepo
import me.wsj.fengyun.db.entity.CityEntity
import me.wsj.fengyun.ui.base.BaseViewModel
import me.wsj.lib.net.OkHttpUtils
import me.wsj.lib.utils.SpUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import per.wsj.commonlib.utils.FileUtil
import per.wsj.commonlib.utils.LogUtil
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

class ThemeViewModel(private val app: Application) : BaseViewModel(app) {

    val downloadStatus = MutableLiveData<Boolean>()

    fun downPlugin() {
        val path =
            "https://wangsj.oss-cn-shanghai.aliyuncs.com/fengyun/plugin/plugin-colorful.apk"
        val destFileName = FileUtil.getNameFromPath(path)
        val destFileDir = app.externalCacheDir!!.absolutePath + File.separator + "plugin"
        //储存下载文件的目录
        val dir = File(destFileDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, destFileName)
        launch {
            flow {
                val request = Request.Builder().url(path).build()
                OkHttpUtils.getClient().newCall(request).execute().use {
                    val buf = ByteArray(1024)
                    var len = 0

                    val inputStream = it.body()!!.byteStream()
                    val total = it.body()!!.contentLength()
                    val fos = FileOutputStream(file)
                    var sum: Long = 0
                    while (inputStream.read(buf).also { len = it } != -1) {
                        fos.write(buf, 0, len)
                        sum += len.toLong()
                        val progress = (sum * 1.0f / total * 100).toInt()
                        emit(progress)
                    }
                    fos.flush()
                    fos.close()
                    downloadStatus.postValue(true)
                    //下载完成
                    LogUtil.e("下载完成。。。")
                    SpUtil.setPluginPath(app, file.absolutePath)
                }
            }.collectLatest {
//                delay(400)
                LogUtil.e("progress: $it")
            }
        }
    }
}